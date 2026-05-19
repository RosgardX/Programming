package Models;

/**
 * Модель альбома музыкальной группы.
 * <p>
 * Используется в поле {@code bestAlbum} у {@link MusicBand}.
 * Естественный порядок сравнения: по {@code sales}.
 * </p>
 */
public class Album implements Comparable<Album> {

    /** Название альбома (не {@code null}, не пустое). */
    private String name;

    /** Количество треков (не {@code null}, {@code > 0}). */
    private Long tracks;

    /** Длина/длительность ( {@code > 0}). */
    private int length;

    /** Продажи (не {@code null}, {@code > 0}). */
    private Float sales;

    /**
     * Создаёт альбом.
     *
     * @param name название
     * @param tracks количество треков
     * @param length длина/длительность
     * @param sales продажи
     * @throws IllegalArgumentException если значения не проходят проверку
     */
    public Album(String name, Long tracks, int length, Float sales) {
        setName(name);
        setTracks(tracks);
        setLength(length);
        setSales(sales);
    }

    /** @return название альбома */
    public String getName() {
        return name;
    }

    /** @param name название альбома */
    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        this.name = name;
    }

    /** @return количество треков */
    public Long getTracks() {
        return tracks;
    }

    /** @param tracks количество треков */
    public void setTracks(Long tracks) {
        if (tracks == null || tracks <= 0) {
            throw new IllegalArgumentException("Tracks should be greater than 0");
        }
        this.tracks = tracks;
    }

    /** @return длина/длительность */
    public int getLength() {
        return length;
    }

    /** @param length длина/длительность */
    public void setLength(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length should be greater than 0");
        }
        this.length = length;
    }

    /** @return продажи */
    public Float getSales() {
        return sales;
    }

    /** @param sales продажи */
    public void setSales(Float sales) {
        if (sales == null || sales <= 0) {
            throw new IllegalArgumentException("Sales should be greater than 0");
        }
        this.sales = sales;
    }

    /**
     * Сравнение альбомов по продажам.
     *
     * @param otherAlbum другой альбом
     * @return результат сравнения
     */
    @Override
    public int compareTo(Album otherAlbum) {
        return Float.compare(this.sales, otherAlbum.sales);
    }
}