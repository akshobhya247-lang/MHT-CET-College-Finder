package com.cet.finder.dao;

import java.sql.*;
import java.util.*;

public class CutoffPredictorDAO extends BaseDAO {
    public List<String[]> predictColleges(double studentPercentile, String category, String course, int capRound) throws SQLException {
        return predictColleges(studentPercentile, category, "", course, 0, capRound);
    }
    public List<String[]> predictColleges(double studentPercentile, String category, String city, String course, int admissionYear, int capRound) throws SQLException {
        if (!Double.isFinite(studentPercentile) || studentPercentile < 0 || studentPercentile > 100) throw new IllegalArgumentException("Percentile must be between 0 and 100.");
        String sql = """
            SELECT c.institute_name, co.course_name, cu.year, cu.cap_round, cu.category, cu.percentile
            FROM cutoffs cu JOIN college_courses cc ON cu.choice_code=cc.choice_code
            JOIN colleges c ON cc.institute_code=c.institute_code
            JOIN courses co ON cc.course_id=co.course_id
            LEFT JOIN cities ci ON c.city_id=ci.city_id
            WHERE cu.percentile IS NOT NULL
              AND (?='' OR cu.category=?) AND (?='' OR ci.city_name LIKE ?)
              AND (?='' OR co.course_name LIKE ?) AND (?=0 OR cu.year=?) AND (?=0 OR cu.cap_round=?)
            ORDER BY cu.year DESC, ABS(? - cu.percentile), c.institute_name LIMIT 3000
            """;
        List<String[]> out=new ArrayList<>();
        try(Connection con=getConnection();PreparedStatement ps=con.prepareStatement(sql)){
            String cat=category==null?"":category.trim(), ct=city==null?"":city.trim(), cr=course==null?"":course.trim(); int i=1;
            ps.setString(i++,cat);ps.setString(i++,cat);ps.setString(i++,ct);ps.setString(i++,"%"+ct+"%");ps.setString(i++,cr);ps.setString(i++,"%"+cr+"%");ps.setInt(i++,admissionYear);ps.setInt(i++,admissionYear);ps.setInt(i++,capRound);ps.setInt(i++,capRound);ps.setDouble(i,studentPercentile);
            try(ResultSet rs=ps.executeQuery()){while(rs.next()){
                double cutoff=rs.getDouble("percentile"); double gap=studentPercentile-cutoff; String status;
                if(gap>=5.0)status="Safe"; else if(gap>=0)status="Target"; else if(gap>=-5.0)status="Dream"; else status="Unlikely";
                out.add(new String[]{rs.getString("institute_name"),rs.getString("course_name"),rs.getString("year"),rs.getString("cap_round"),rs.getString("category"),String.format(Locale.ROOT,"%.4f",cutoff),status});
            }}
        } return out;
    }
}
