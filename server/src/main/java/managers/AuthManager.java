package managers;

import db.AuthRepository;
import network.UserCredentials;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class AuthManager {
    private final AuthRepository repo;

    public AuthManager(AuthRepository repo) {
        this.repo = repo;
    }

    public boolean register(UserCredentials cred) throws Exception {
        if (!valid(cred)) return false;
        String hash = sha256(cred.getPassword());
        return repo.insertUser(cred.getLogin(), hash);
    }

    public boolean authorize(UserCredentials cred) throws Exception {
        if (!valid(cred)) return false;
        String expected = repo.findPasswordHash(cred.getLogin());
        if (expected == null) return false;
        return expected.equals(sha256(cred.getPassword()));
    }

    private boolean valid(UserCredentials cred) {
        return cred != null
                && cred.getLogin() != null && !cred.getLogin().isBlank()
                && cred.getPassword() != null && !cred.getPassword().isBlank();
    }

    private static String sha256(String s) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] dig = md.digest(s.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder(dig.length * 2);
        for (byte b : dig) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}