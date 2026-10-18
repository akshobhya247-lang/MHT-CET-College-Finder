
package com.cet.finder.util;

import com.cet.finder.dao.CollegeDAO;
import com.cet.finder.util.DBConnection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CsvImportTask {

    private static final String HEADER =
            "choice_code,year,cap_round,category,merit_rank,merit_score,percentile";

    private static final String INSERT_SQL =
            "INSERT INTO cutoffs " +
                    "(choice_code, year, cap_round, category, merit_rank, percentile) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

    public static int importCutoffs(Path file)
            throws IOException, SQLException {

        try (Connection con = DBConnection.getConnection();
             BufferedReader reader = Files.newBufferedReader(
                     file, StandardCharsets.UTF_8)) {

            String header = reader.readLine();

            if (header == null) {
                throw new IOException("The CSV file is empty.");
            }

            header = header.replace("\uFEFF", "").trim();

            if (!header.equalsIgnoreCase(HEADER)) {
                throw new IOException(
                        "Expected CSV header: " + HEADER
                );
            }

            boolean oldAutoCommit = con.getAutoCommit();
            int count = 0;

            try {
                con.setAutoCommit(false);

                try (PreparedStatement ps =
                             con.prepareStatement(INSERT_SQL)) {

                    String line;
                    int lineNumber = 1;

                    while ((line = reader.readLine()) != null) {
                        lineNumber++;

                        if (line.isBlank()) {
                            continue;
                        }

                        String[] row = line.split(",", -1);

                        if (row.length != 7) {
                            throw new IOException(
                                    "Invalid number of columns on line "
                                            + lineNumber
                            );
                        }

                        String choiceCode = row[0].trim();
                        int year = Integer.parseInt(row[1].trim());
                        int capRound = Integer.parseInt(row[2].trim());
                        String category = row[3].trim();
                        String rankText = row[4].trim();
                        String percentileText = row[6].trim();

                        // Column 6 (merit_score) is accepted in the CSV
                        // for compatibility but is not stored in cutoffs.

                        if (choiceCode.isEmpty()
                                || choiceCode.length() > 15) {
                            throw new IOException(
                                    "Invalid choice_code on line " + lineNumber
                            );
                        }

                        if (year < 1 || year > 65535) {
                            throw new IOException(
                                    "Invalid year on line " + lineNumber
                            );
                        }

                        if (capRound < 0 || capRound > 255) {
                            throw new IOException(
                                    "Invalid CAP round on line " + lineNumber
                            );
                        }

                        if (category.isEmpty()
                                || category.length() > 20) {
                            throw new IOException(
                                    "Invalid category on line " + lineNumber
                            );
                        }

                        ps.setString(1, choiceCode);
                        ps.setInt(2, year);
                        ps.setInt(3, capRound);
                        ps.setString(4, category);

                        if (rankText.isEmpty()) {
                            ps.setNull(5, java.sql.Types.INTEGER);
                        } else {
                            int rank = Integer.parseInt(rankText);

                            if (rank < 0) {
                                throw new IOException(
                                        "Invalid merit_rank on line "
                                                + lineNumber
                                );
                            }

                            ps.setInt(5, rank);
                        }

                        if (percentileText.isEmpty()) {
                            ps.setNull(6, java.sql.Types.DECIMAL);
                        } else {
                            double percentile =
                                    Double.parseDouble(percentileText);

                            if (!Double.isFinite(percentile)
                                    || percentile < 0
                                    || percentile > 100) {
                                throw new IOException(
                                        "Percentile must be between 0 and 100 "
                                                + "on line " + lineNumber
                                );
                            }

                            ps.setDouble(6, percentile);
                        }

                        ps.addBatch();
                        count++;
                    }

                    ps.executeBatch();
                }

                con.commit();
                return count;

            } catch (Exception ex) {
                con.rollback();

                if (ex instanceof IOException io) {
                    throw io;
                }

                if (ex instanceof SQLException sql) {
                    throw sql;
                }

                if (ex instanceof RuntimeException runtime) {
                    throw runtime;
                }

                throw new IOException("CSV import failed.", ex);

            } finally {
                con.setAutoCommit(oldAutoCommit);
            }
        }
    }

    public static int exportSearch(
            Path file,
            String name,
            String code,
            String city,
            String course,
            String category,
            int round,
            int year) throws Exception {

        List<String[]> results =
                new CollegeDAO().searchColleges(
                        city, course, category, round
                );

        try (BufferedWriter writer = Files.newBufferedWriter(
                file, StandardCharsets.UTF_8)) {

            writer.write(
                    "choice_code,institute_code,institute_name,city_name,"
                            + "course_name,category,cap_round,year"
            );
            writer.newLine();

            int count = 0;

            for (String[] row : results) {
                // Expected search result:
                // choice_code, institute_code, institute_name,
                // city_name, course_name

                if (!contains(row[2], name)
                        || !contains(row[1], code)
                        || !contains(row[3], city)
                        || !contains(row[4], course)) {
                    continue;
                }

                writer.write(csv(row[0]) + ","
                        + csv(row[1]) + ","
                        + csv(row[2]) + ","
                        + csv(row[3]) + ","
                        + csv(row[4]) + ","
                        + csv(category) + ","
                        + round + ","
                        + year);
                writer.newLine();
                count++;
            }

            return count;
        }
    }

    private static boolean contains(String value, String filter) {
        return filter == null || filter.isBlank()
                || (value != null
                && value.toLowerCase().contains(
                filter.trim().toLowerCase()));
    }

    private static String csv(String value) {
        if (value == null) {
            return "";
        }

        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
