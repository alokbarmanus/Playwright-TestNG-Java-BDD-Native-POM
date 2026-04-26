package com.automation.utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility for reading test data from a database table.
 */
public class DbDataReader {
    public static List<List<Object>> readDbTable(String jdbcUrl, String user, String password, String query) throws SQLException {
        List<List<Object>> data = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(jdbcUrl, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            int colCount = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                List<Object> row = new ArrayList<>();
                for (int i = 1; i <= colCount; i++) {
                    row.add(rs.getObject(i));
                }
                data.add(row);
            }
        }
        return data;
    }
}
