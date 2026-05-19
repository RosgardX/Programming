package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthRepository {
    private final DbManager db;

    public AuthRepository(DbManager db) {
        this.db = db;
    }

    public boolean insertUser(String login, String passwordHash) throws Exception {
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO users(login, password_hash) VALUES (?, ?)")) {
            ps.setString(1, login);
            ps.setString(2, passwordHash);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            // логин занят или другая ошибка
            return false;
        }
    }

    public String findPasswordHash(String login) throws Exception {
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT password_hash FROM users WHERE login = ?")) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return rs.getString(1);
            }
        }
    }
}