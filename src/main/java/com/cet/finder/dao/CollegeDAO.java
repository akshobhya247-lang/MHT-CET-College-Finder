package com.cet.finder.dao;

import java.sql.*;
import java.util.*;

public class CollegeDAO extends BaseDAO {
    public List<String[]> searchColleges(String city, String course, String category, int capRound) throws SQLException {
        return searchColleges("", "", city, course, category, capRound, 0);
    }

    /** Returns identifiers, college/course details and matching historical cutoff fields. */
    public List<String[]> searchColleges(String collegeName, String instituteCode, String city,
                                         String course, String category, int capRound, int year) throws SQLException {
        String sql = """
            SELECT cc.choice_code, c.institute_code, c.institute_name,
                   COALESCE(ci.city_name, ''), co.course_name,
                   cu.year, cu.cap_round, cu.category, cu.merit_rank, cu.percentile
            FROM colleges c
            JOIN college_courses cc ON c.institute_code = cc.institute_code
            JOIN courses co ON cc.course_id = co.course_id
            LEFT JOIN cities ci ON c.city_id = ci.city_id
            LEFT JOIN cutoffs cu ON cu.choice_code = cc.choice_code
                AND (? = '' OR cu.category = ?)
                AND (? = 0 OR cu.cap_round = ?)
                AND (? = 0 OR cu.year = ?)
            WHERE (? = '' OR c.institute_name LIKE ?)
              AND (? = '' OR c.institute_code LIKE ?)
              AND (? = '' OR ci.city_name LIKE ?)
              AND (? = '' OR co.course_name LIKE ?)
              AND ((? = '' AND ? = 0 AND ? = 0) OR cu.cutoff_id IS NOT NULL)
            ORDER BY c.institute_name, co.course_name, cu.year DESC, cu.cap_round
            LIMIT 3000
            """;
        List<String[]> results = new ArrayList<>();
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            String name = norm(collegeName), code = norm(instituteCode), selectedCity = norm(city), selectedCourse = norm(course), selectedCategory = norm(category);
            int i = 1;
            ps.setString(i++, selectedCategory); ps.setString(i++, selectedCategory);
            ps.setInt(i++, capRound); ps.setInt(i++, capRound);
            ps.setInt(i++, year); ps.setInt(i++, year);
            ps.setString(i++, name); ps.setString(i++, "%" + name + "%");
            ps.setString(i++, code); ps.setString(i++, "%" + code + "%");
            ps.setString(i++, selectedCity); ps.setString(i++, "%" + selectedCity + "%");
            ps.setString(i++, selectedCourse); ps.setString(i++, "%" + selectedCourse + "%");
            ps.setString(i++, selectedCategory); ps.setInt(i++, capRound); ps.setInt(i++, year);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) results.add(new String[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),
                        rs.getString(6) == null ? "" : rs.getString(6), rs.getString(7) == null ? "" : rs.getString(7),
                        rs.getString(8) == null ? "" : rs.getString(8), rs.getString(9) == null ? "" : rs.getString(9),
                        rs.getString(10) == null ? "" : rs.getString(10)});
            }
        }
        return results;
    }
    private String norm(String s) { return s == null ? "" : s.trim(); }
}
