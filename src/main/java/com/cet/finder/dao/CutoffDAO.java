
package com.cet.finder.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CutoffDAO extends BaseDAO {

    public List<String[]> search(String filter) throws SQLException {
        String sql = """
            SELECT
                cutoff_id,
                choice_code,
                year,
                cap_round,
                NULL AS merit_exam,
                NULL AS allotment_type,
                NULL AS seat_type,
                category,
                merit_rank,
                NULL AS merit_score,
                percentile
            FROM cutoffs
            WHERE (? = ''
                OR choice_code LIKE ?
                OR category LIKE ?
                OR CAST(year AS CHAR) LIKE ?)
            ORDER BY year DESC, choice_code, cap_round
            LIMIT 2000
            """;

        List<String[]> rows = new ArrayList<>();

        try (Connection c = getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {

            String f = filter == null ? "" : filter.trim();

            p.setString(1, f);
            p.setString(2, "%" + f + "%");
            p.setString(3, "%" + f + "%");
            p.setString(4, "%" + f + "%");

            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    rows.add(new String[]{
                            r.getString("cutoff_id"),
                            r.getString("choice_code"),
                            r.getString("year"),
                            r.getString("cap_round"),
                            r.getString("merit_exam"),
                            r.getString("allotment_type"),
                            r.getString("seat_type"),
                            r.getString("category"),
                            r.getString("merit_rank"),
                            r.getString("merit_score"),
                            r.getString("percentile")
                    });
                }
            }
        }

        return rows;
    }

    public void insert(String choice, int year, int round,
                       String exam, String allotment, String seat,
                       String category, Integer rank,
                       Double score, Double percentile)
            throws SQLException {

        validate(year, round, rank, score, percentile);

        String sql = """
            INSERT INTO cutoffs
                (choice_code, year, cap_round, category, merit_rank, percentile)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (Connection c = getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {

            p.setString(1, choice.trim());
            p.setInt(2, year);
            p.setInt(3, round);
            p.setString(4, category.trim());
            setNullableInt(p, 5, rank);
            setNullableDouble(p, 6, percentile);

            p.executeUpdate();
        }
    }

    public void update(long id, String choice, int year, int round,
                       String exam, String allotment, String seat,
                       String category, Integer rank,
                       Double score, Double percentile)
            throws SQLException {

        validate(year, round, rank, score, percentile);

        String sql = """
            UPDATE cutoffs
            SET choice_code = ?, year = ?, cap_round = ?,
                category = ?, merit_rank = ?, percentile = ?
            WHERE cutoff_id = ?
            """;

        try (Connection c = getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {

            p.setString(1, choice.trim());
            p.setInt(2, year);
            p.setInt(3, round);
            p.setString(4, category.trim());
            setNullableInt(p, 5, rank);
            setNullableDouble(p, 6, percentile);
            p.setLong(7, id);

            if (p.executeUpdate() == 0) {
                throw new SQLException("No cutoff record found for ID " + id);
            }
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM cutoffs WHERE cutoff_id = ?";

        try (Connection c = getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {

            p.setLong(1, id);

            if (p.executeUpdate() == 0) {
                throw new SQLException("No cutoff record found for ID " + id);
            }
        }
    }

    private void setNullableInt(PreparedStatement p, int index,
                                Integer value) throws SQLException {
        if (value == null) {
            p.setNull(index, Types.INTEGER);
        } else {
            p.setInt(index, value);
        }
    }

    private void setNullableDouble(PreparedStatement p, int index,
                                   Double value) throws SQLException {
        if (value == null) {
            p.setNull(index, Types.DECIMAL);
        } else {
            p.setDouble(index, value);
        }
    }

    private void validate(int year, int round, Integer rank,
                          Double score, Double percentile) {

        if (year < 2000 || year > 2100) {
            throw new IllegalArgumentException(
                    "Year must be between 2000 and 2100.");
        }

        if (round < 1 || round > 10) {
            throw new IllegalArgumentException(
                    "CAP round must be between 1 and 10.");
        }

        if (rank != null && rank < 1) {
            throw new IllegalArgumentException(
                    "Merit rank must be positive.");
        }

        if (score != null && !Double.isFinite(score)) {
            throw new IllegalArgumentException(
                    "Merit score must be numeric.");
        }

        if (percentile != null &&
                (!Double.isFinite(percentile)
                        || percentile < 0 || percentile > 100)) {
            throw new IllegalArgumentException(
                    "Percentile must be between 0 and 100.");
        }
    }
}
