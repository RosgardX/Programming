package db;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Мини-читалка ~/.pgpass (формат: host:port:db:user:password, поддерживает '*').
 * Нужна чтобы JDBC мог подключаться как psql "без пароля" (т.е. пароль берётся из ~/.pgpass).
 */
public final class PgPass {
    private PgPass() {}

    public static String findPassword(String host, int port, String db, String user) {
        try {
            String home = System.getProperty("user.home");
            Path pgpass = Path.of(home, ".pgpass");
            try (BufferedReader br = new BufferedReader(new FileReader(pgpass.toFile()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) continue;

                    String[] p = line.split(":", 5);
                    if (p.length != 5) continue;

                    if (match(p[0], host)
                            && match(p[1], String.valueOf(port))
                            && match(p[2], db)
                            && match(p[3], user)) {
                        return p[4];
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private static boolean match(String pattern, String value) {
        pattern = Objects.requireNonNullElse(pattern, "");
        value = Objects.requireNonNullElse(value, "");
        return pattern.equals("*") || pattern.equals(value);
    }
}