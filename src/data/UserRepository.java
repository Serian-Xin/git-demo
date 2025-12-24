package data;

import model.User;
import util.PasswordUtil;

import java.sql.*;

public class UserRepository {

    // 根据用户名查找用户
    public User findByUsername(String username) {
        // 使用username字段，与实际数据库表结构匹配
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username")); // 使用username字段
                user.setPassword(rs.getString("password"));
                user.setName(rs.getString("name"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 注册新用户
    public boolean register(User user) {
        // 使用username字段，与实际数据库表结构匹配
        String sql = "INSERT INTO users (username, password, name) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, PasswordUtil.hashPassword(user.getPassword())); // 加密存储
            stmt.setString(3, user.getName());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // 更新用户密码
    public boolean updatePassword(int userId, String oldPassword, String newPassword) {
        // 先验证旧密码是否正确
        String verifySql = "SELECT password FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement verifyStmt = conn.prepareStatement(verifySql)) {
            verifyStmt.setInt(1, userId);
            ResultSet rs = verifyStmt.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                // 比较存储的密码哈希值与输入的旧密码哈希值
                if (storedPassword.equals(PasswordUtil.hashPassword(oldPassword))) {
                    // 旧密码正确，更新为新密码
                    String updateSql = "UPDATE users SET password = ? WHERE id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setString(1, PasswordUtil.hashPassword(newPassword));
                        updateStmt.setInt(2, userId);
                        return updateStmt.executeUpdate() > 0;//executeUpdate回返回一个整数值，通过比较受影响的行数是否大于 0，判断插入操作是否成功。
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}


