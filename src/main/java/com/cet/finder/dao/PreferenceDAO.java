
package com.cet.finder.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PreferenceDAO extends BaseDAO {

    public void saveCollege(String choiceCode, int preferenceOrder)
            throws SQLException {

        String sql = """
            INSERT INTO saved_preferences
                (choice_code, preference_order)
            VALUES (?, ?)
            ON DUPLICATE KEY UPDATE
                preference_order = VALUES(preference_order)
            """;

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, choiceCode);
            ps.setInt(2, preferenceOrder);
            ps.executeUpdate();
        }
    }

    public List<String[]> getSavedColleges() throws SQLException {

        String sql = """
            SELECT
                sp.preference_id,
                sp.choice_code,
                sp.preference_order,
                COALESCE(c.institute_name, 'College details unavailable')
                    AS institute_name,
                COALESCE(co.course_name, 'Course details unavailable')
                    AS course_name,
                COALESCE(ci.city_name, 'Unknown')
                    AS city_name
            FROM saved_preferences sp
            LEFT JOIN college_courses cc
                ON sp.choice_code = cc.choice_code
            LEFT JOIN colleges c
                ON cc.institute_code = c.institute_code
            LEFT JOIN courses co
                ON cc.course_id = co.course_id
            LEFT JOIN cities ci
                ON c.city_id = ci.city_id
            ORDER BY sp.preference_order
            """;

        List<String[]> results = new ArrayList<>();

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                results.add(new String[]{
                        rs.getString("preference_id"),
                        rs.getString("choice_code"),
                        rs.getString("preference_order"),
                        rs.getString("institute_name"),
                        rs.getString("course_name"),
                        rs.getString("city_name")
                });
            }
        }

        return results;
    }

    public void removeCollege(long preferenceId) throws SQLException {

        String sql =
                "DELETE FROM saved_preferences WHERE preference_id = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, preferenceId);
            ps.executeUpdate();
        }
    }

    public void moveCollege(long preferenceId, int direction)
            throws SQLException {

        if (direction != -1 && direction != 1) {
            throw new IllegalArgumentException(
                    "Direction must be -1 or 1.");
        }

        String selectSql = """
            SELECT preference_id, preference_order
            FROM saved_preferences
            ORDER BY preference_order
            """;

        try (Connection con = getConnection()) {
            con.setAutoCommit(false);

            try {
                List<long[]> rows = new ArrayList<>();

                try (PreparedStatement ps =
                             con.prepareStatement(selectSql);
                     ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {
                        rows.add(new long[]{
                                rs.getLong("preference_id"),
                                rs.getLong("preference_order")
                        });
                    }
                }

                int current = -1;

                for (int i = 0; i < rows.size(); i++) {
                    if (rows.get(i)[0] == preferenceId) {
                        current = i;
                        break;
                    }
                }

                int target = current + direction;

                if (current >= 0 && target >= 0
                        && target < rows.size()) {

                    long currentId = rows.get(current)[0];
                    long currentOrder = rows.get(current)[1];
                    long targetId = rows.get(target)[0];
                    long targetOrder = rows.get(target)[1];

                    long maxOrder = 0;
                    for (long[] row : rows) {
                        maxOrder = Math.max(maxOrder, row[1]);
                    }

                    long temporaryOrder = maxOrder + 1;

                    try (PreparedStatement ps = con.prepareStatement(
                            "UPDATE saved_preferences " +
                                    "SET preference_order = ? WHERE preference_id = ?")) {

                        ps.setLong(1, temporaryOrder);
                        ps.setLong(2, currentId);
                        ps.executeUpdate();

                        ps.setLong(1, currentOrder);
                        ps.setLong(2, targetId);
                        ps.executeUpdate();

                        ps.setLong(1, targetOrder);
                        ps.setLong(2, currentId);
                        ps.executeUpdate();
                    }
                }

                con.commit();

            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }
}
