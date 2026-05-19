package db;

import java.sql.Connection;
import java.sql.Statement;

public class DbInit {
    private final DbManager db;

    public DbInit(DbManager db) {
        this.db = db;
    }

    public void init() throws Exception {
        try (Connection c = db.getConnection();
             Statement st = c.createStatement()) {

            st.execute("""
                CREATE TABLE IF NOT EXISTS users (
                  login TEXT PRIMARY KEY,
                  password_hash TEXT NOT NULL
                )
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS music_bands (
                  id BIGSERIAL PRIMARY KEY,
                  owner_login TEXT NOT NULL REFERENCES users(login),

                  name TEXT NOT NULL,

                  coord_x DOUBLE PRECISION NOT NULL,
                  coord_y DOUBLE PRECISION NOT NULL,

                  creation_date TIMESTAMP NOT NULL DEFAULT now(),

                  number_of_participants BIGINT NOT NULL CHECK (number_of_participants > 0),
                  description TEXT,

                  establishment_date TIMESTAMPTZ NOT NULL,

                  genre TEXT,
                  album_name TEXT,
                  album_tracks BIGINT,
                  album_length INT,
                  album_sales REAL,

                  CHECK (album_tracks IS NULL OR album_tracks > 0),
                  CHECK (album_length IS NULL OR album_length > 0),
                  CHECK (album_sales IS NULL OR album_sales > 0)
                )
            """);
        }
    }
}