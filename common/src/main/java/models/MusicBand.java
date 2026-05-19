package models;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * Модель музыкальной группы — элемент коллекции.
 * 7 лаба:
 * - id генерируется базой данных (sequence / identity)
 * - ownerLogin хранится для контроля прав
 */
public class MusicBand implements Comparable<MusicBand>, Serializable {
    private static final long serialVersionUID = 2L;

    private Long id;                // выставляется сервером после INSERT RETURNING id
    private String ownerLogin;      // выставляется сервером (логин пользователя)

    private String name;
    private Coordinates coordinates;
    private Date creationDate;      // выставляется сервером (или в БД default now())
    private long numberOfParticipants;
    private String description;
    private ZonedDateTime establishmentDate;
    private MusicGenre genre;
    private Album bestAlbum;

    public MusicBand(String name, Coordinates coordinates, long numberOfParticipants, String description,
                     ZonedDateTime establishmentDate, MusicGenre genre, Album bestAlbum) {
        setName(name);
        setCoordinates(coordinates);
        setNumberOfParticipants(numberOfParticipants);
        setDescription(description);
        setEstablishmentDate(establishmentDate);
        setGenre(genre);
        setBestAlbum(bestAlbum);

        // клиент при создании не задаёт id/owner/creationDate
        this.id = null;
        this.ownerLogin = null;
        this.creationDate = null;
    }

    // ===== server-side setters =====

    public void setIdServerSide(long id) {
        if (id <= 0) throw new IllegalArgumentException("id must be > 0");
        this.id = id;
    }

    public void setOwnerLoginServerSide(String ownerLogin) {
        if (ownerLogin == null || ownerLogin.isBlank()) {
            throw new IllegalArgumentException("ownerLogin cannot be empty");
        }
        this.ownerLogin = ownerLogin;
    }

    public void setCreationDateServerSide(Date creationDate) {
        if (creationDate == null) throw new IllegalArgumentException("creationDate cannot be null");
        this.creationDate = creationDate;
    }

    // ===== обычные setters/validation =====

    public void setName(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name cannot be empty");
        this.name = name;
    }

    public void setCoordinates(Coordinates coordinates) {
        if (coordinates == null) throw new IllegalArgumentException("Coordinates cannot be null");
        this.coordinates = coordinates;
    }

    public void setNumberOfParticipants(long numberOfParticipants) {
        if (numberOfParticipants <= 0) throw new IllegalArgumentException("Number of participants must be > 0");
        this.numberOfParticipants = numberOfParticipants;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEstablishmentDate(ZonedDateTime establishmentDate) {
        if (establishmentDate == null) throw new IllegalArgumentException("Establishment date cannot be null");
        this.establishmentDate = establishmentDate;
    }

    public void setGenre(MusicGenre genre) {
        this.genre = genre;
    }

    public void setBestAlbum(Album bestAlbum) {
        this.bestAlbum = bestAlbum;
    }

    // ===== getters =====

    public Long getId() { return id; }

    public String getOwnerLogin() { return ownerLogin; }

    public String getName() { return name; }

    public Coordinates getCoordinates() { return coordinates; }

    public Date getCreationDate() { return creationDate; }

    public long getNumberOfParticipants() { return numberOfParticipants; }

    public String getDescription() { return description; }

    public ZonedDateTime getEstablishmentDate() { return establishmentDate; }

    public MusicGenre getGenre() { return genre; }

    public Album getBestAlbum() { return bestAlbum; }

    @Override
    public int compareTo(MusicBand o) {
        if (this.id == null && o.id == null) return 0;
        if (this.id == null) return -1;
        if (o.id == null) return 1;
        return this.id.compareTo(o.id);
    }

    @Override
    public String toString() {
        return "MusicBand{" +
                "id=" + id +
                ", ownerLogin='" + ownerLogin + '\'' +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", numberOfParticipants=" + numberOfParticipants +
                ", description='" + description + '\'' +
                ", establishmentDate=" + establishmentDate +
                ", genre=" + genre +
                ", bestAlbum=" + bestAlbum +
                '}';
    }
}