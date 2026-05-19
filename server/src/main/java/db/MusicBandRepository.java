package db;

import models.*;

import java.sql.*;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MusicBandRepository {
    private final DbManager db;

    public MusicBandRepository(DbManager db) {
        this.db = db;
    }

    public List<MusicBand> loadAll() throws Exception {
        List<MusicBand> out = new ArrayList<>();

        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM music_bands ORDER BY id");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                long id = rs.getLong("id");
                String owner = rs.getString("owner_login");

                String name = rs.getString("name");
                double x = rs.getDouble("coord_x");
                double y = rs.getDouble("coord_y");

                Timestamp creationTs = rs.getTimestamp("creation_date");
                Date creationDate = creationTs == null ? null : new Date(creationTs.getTime());

                long participants = rs.getLong("number_of_participants");
                String description = rs.getString("description");

                OffsetDateTime est = rs.getObject("establishment_date", OffsetDateTime.class);
                ZonedDateTime establishmentDate = est.atZoneSameInstant(ZoneId.systemDefault());

                MusicGenre genre = null;
                String genreStr = rs.getString("genre");
                if (genreStr != null) genre = MusicGenre.valueOf(genreStr);

                Album album = null;
                String albumName = rs.getString("album_name");
                Long tracks = (Long) rs.getObject("album_tracks");
                Integer length = (Integer) rs.getObject("album_length");
                Float sales = (Float) rs.getObject("album_sales");
                if (albumName != null) {
                    album = new Album(albumName, tracks, length, sales);
                }

                MusicBand b = new MusicBand(name, new Coordinates(x, y), participants, description,
                        establishmentDate, genre, album);

                b.setIdServerSide(id);
                b.setOwnerLoginServerSide(owner);
                if (creationDate != null) b.setCreationDateServerSide(creationDate);

                out.add(b);
            }
        }
        return out;
    }

    public long insert(MusicBand band, String ownerLogin) throws Exception {
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement("""
                 INSERT INTO music_bands(
                   owner_login, name, coord_x, coord_y,
                   number_of_participants, description,
                   establishment_date, genre,
                   album_name, album_tracks, album_length, album_sales
                 )
                 VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                 RETURNING id, creation_date
             """)) {
            ps.setString(1, ownerLogin);
            ps.setString(2, band.getName());
            ps.setDouble(3, band.getCoordinates().getX());
            ps.setDouble(4, band.getCoordinates().getY());
            ps.setLong(5, band.getNumberOfParticipants());
            ps.setString(6, band.getDescription());

            ps.setObject(7, band.getEstablishmentDate().toOffsetDateTime());

            if (band.getGenre() == null) ps.setNull(8, Types.VARCHAR);
            else ps.setString(8, band.getGenre().name());

            Album a = band.getBestAlbum();
            if (a == null) {
                ps.setNull(9, Types.VARCHAR);
                ps.setNull(10, Types.BIGINT);
                ps.setNull(11, Types.INTEGER);
                ps.setNull(12, Types.REAL);
            } else {
                ps.setString(9, a.getName());
                ps.setLong(10, a.getTracks());
                ps.setInt(11, a.getLength());
                ps.setFloat(12, a.getSales());
            }

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                long id = rs.getLong("id");
                // creation_date можно потом прочитать из БД / вернуть отдельно; тут id обязателен
                return id;
            }
        }
    }

    public boolean update(long id, MusicBand band, String ownerLogin) throws Exception {
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement("""
                 UPDATE music_bands SET
                   name = ?, coord_x = ?, coord_y = ?,
                   number_of_participants = ?, description = ?,
                   establishment_date = ?, genre = ?,
                   album_name = ?, album_tracks = ?, album_length = ?, album_sales = ?
                 WHERE id = ? AND owner_login = ?
             """)) {
            ps.setString(1, band.getName());
            ps.setDouble(2, band.getCoordinates().getX());
            ps.setDouble(3, band.getCoordinates().getY());
            ps.setLong(4, band.getNumberOfParticipants());
            ps.setString(5, band.getDescription());
            ps.setObject(6, band.getEstablishmentDate().toOffsetDateTime());

            if (band.getGenre() == null) ps.setNull(7, Types.VARCHAR);
            else ps.setString(7, band.getGenre().name());

            Album a = band.getBestAlbum();
            if (a == null) {
                ps.setNull(8, Types.VARCHAR);
                ps.setNull(9, Types.BIGINT);
                ps.setNull(10, Types.INTEGER);
                ps.setNull(11, Types.REAL);
            } else {
                ps.setString(8, a.getName());
                ps.setLong(9, a.getTracks());
                ps.setInt(10, a.getLength());
                ps.setFloat(11, a.getSales());
            }

            ps.setLong(12, id);
            ps.setString(13, ownerLogin);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(long id, String ownerLogin) throws Exception {
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "DELETE FROM music_bands WHERE id = ? AND owner_login = ?")) {
            ps.setLong(1, id);
            ps.setString(2, ownerLogin);
            return ps.executeUpdate() > 0;
        }
    }
}