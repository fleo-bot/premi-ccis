package dao;

import db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public static List<String[]> getAllUsers() {
        List<String[]> users = new ArrayList<>();
        String sql = "SELECT user_id, username, email, role FROM users WHERE deleted_at IS NULL";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(new String[]{
                    String.valueOf(rs.getInt("user_id")),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("role")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    public static boolean softDeleteUser(int userId) {
        String sql = "UPDATE users SET deleted_at = NOW() WHERE user_id = ? AND deleted_at IS NULL";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
