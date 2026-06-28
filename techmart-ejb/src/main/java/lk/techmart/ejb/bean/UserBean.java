package lk.techmart.ejb.bean;

import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import javax.sql.DataSource;
import lk.techmart.core.service.UserService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Stateless
public class UserBean implements UserService {

    @Resource(lookup = "jdbc/TechMart")
    private DataSource dataSource;

    @Override
    public boolean validateUser(String email, String password) {
        // 🔍 users table (email, password) එක චෙක් කරන SQL එක
        String query = "SELECT email FROM users WHERE email = ? AND password = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, email);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("✅ [UserBean] Authentication Successful for: " + email);
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ [UserBean] DB Error during authentication: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}