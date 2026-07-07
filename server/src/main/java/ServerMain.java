import db.AuthRepository;
import db.DbInit;
import db.DbManager;
import db.MusicBandRepository;
import db.PgPass;
import managers.AuthManager;
import managers.CollectionManager;
import network.RequestHandler;
import network.UdpServer;

public class ServerMain {

    private static String env(String k) {
        String v = System.getenv(k);
        return v == null ? "" : v.trim();
    }

    public static void main(String[] args) {
        int port = 2222;

        String host = (args != null && args.length > 0 && args[0] != null && !args[0].isBlank()) ? args[0].trim() : env("PGHOST");
        String dbName = (args != null && args.length > 1 && args[1] != null && !args[1].isBlank()) ? args[1].trim() : env("PGDATABASE");
        String user = (args != null && args.length > 2 && args[2] != null && !args[2].isBlank()) ? args[2].trim() : env("PGUSER");
        String pass = (args != null && args.length > 3 && args[3] != null && !args[3].isBlank()) ? args[3].trim() : env("PGPASSWORD");

        if (host.isBlank()) host = "pg";
        if (dbName.isBlank()) dbName = "studs";

        if (user.isBlank()) {
            System.out.println("Не задан логин БД. Передай args: <host> <db> <user> <pass> или env PGUSER.");
            System.exit(1);
            return;
        }

        if (pass.isBlank()) {
            String pgpass = PgPass.findPassword(host, 5432, dbName, user);
            if (pgpass != null && !pgpass.isBlank()) {
                pass = pgpass;
                System.out.println("Пароль взят из ~/.pgpass");
            }
        }

        if (pass.isBlank()) {
            System.out.println("Не найден пароль БД. Задай env PGPASSWORD или настрой ~/.pgpass.");
            System.exit(1);
            return;
        }

        try {
            DbManager db = new DbManager(host, dbName, user, pass);

            new DbInit(db).init();

            AuthRepository authRepository = new AuthRepository(db);
            AuthManager authManager = new AuthManager(authRepository);

            MusicBandRepository bandRepository = new MusicBandRepository(db);
            CollectionManager collectionManager = new CollectionManager(bandRepository);

            collectionManager.loadFromDb();

            RequestHandler handler = new RequestHandler(collectionManager, authManager);

            System.out.println("DB: " + host + "/" + dbName + " user=" + user);
            new UdpServer(port, handler).run();

        } catch (Exception e) {
            System.out.println("Сервер не запустился: " + e.getMessage());
            e.printStackTrace();
        }
    }
}