package managers;

import db.MusicBandRepository;
import models.Album;
import models.MusicBand;
import models.MusicGenre;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class CollectionManager {
    private final MusicBandRepository repo;
    private final LocalDate initialDate = LocalDate.now();

    private final Vector<MusicBand> collection = new Vector<>();

    private final ReentrantLock lock = new ReentrantLock(true);

    public CollectionManager(MusicBandRepository repo) {
        this.repo = repo;
    }

    public int clearOwned(String ownerLogin) throws Exception {
        java.util.List<Long> ids;
        lock.lock();
        try {
            ids = collection.stream()
                    .filter(b -> b.getId() != null)
                    .filter(b -> ownerLogin.equals(b.getOwnerLogin()))
                    .map(models.MusicBand::getId)
                    .toList();
        } finally {
            lock.unlock();
        }

        int removed = 0;
        for (Long id : ids) {
            if (id != null && removeById(id, ownerLogin)) {
                removed++;
            }
        }
        return removed;
    }

    public void loadFromDb() throws Exception {
        lock.lock();
        try {
            collection.clear();
            collection.addAll(repo.loadAll());
        } finally {
            lock.unlock();
        }
    }

    public String getInfo() {
        lock.lock();
        try {
            return "Type: " + collection.getClass().getSimpleName() + "\n" +
                    "Collection Size: " + collection.size() + "\n" +
                    "Initial Date: " + initialDate + "\n";
        } finally {
            lock.unlock();
        }
    }

    public String show() {
        lock.lock();
        try {
            if (collection.isEmpty()) return "Коллекция пуста!";

            return collection.stream()
                    .sorted(Comparator.comparingLong(MusicBand::getNumberOfParticipants))
                    .map(MusicBand::toString)
                    .collect(Collectors.joining("\n"));
        } finally {
            lock.unlock();
        }
    }

    public MusicBand getById(long id) {
        lock.lock();
        try {
            return collection.stream()
                    .filter(b -> b.getId() != null && b.getId() == id)
                    .findFirst()
                    .orElse(null);
        } finally {
            lock.unlock();
        }
    }

    public boolean add(MusicBand incoming, String ownerLogin) throws Exception {
        if (incoming == null) throw new NullPointerException("Element cannot be null");

        long newId = repo.insert(incoming, ownerLogin);

        lock.lock();
        try {
            incoming.setIdServerSide(newId);
            incoming.setOwnerLoginServerSide(ownerLogin);
            incoming.setCreationDateServerSide(new Date());
            collection.add(incoming);
            return true;
        } finally {
            lock.unlock();
        }
    }

    public boolean updateById(long id, MusicBand newBand, String ownerLogin) throws Exception {
        if (newBand == null) throw new NullPointerException("newBand cannot be null");

        boolean ok = repo.update(id, newBand, ownerLogin);
        if (!ok) return false;

        lock.lock();
        try {
            MusicBand bandToUpdate = getById(id);
            if (bandToUpdate == null) return false;

            bandToUpdate.setName(newBand.getName());
            bandToUpdate.setCoordinates(newBand.getCoordinates());
            bandToUpdate.setNumberOfParticipants(newBand.getNumberOfParticipants());
            bandToUpdate.setDescription(newBand.getDescription());
            bandToUpdate.setEstablishmentDate(newBand.getEstablishmentDate());
            bandToUpdate.setGenre(newBand.getGenre());
            bandToUpdate.setBestAlbum(newBand.getBestAlbum());
            return true;
        } finally {
            lock.unlock();
        }
    }

    public boolean removeById(long id, String ownerLogin) throws Exception {
        boolean ok = repo.delete(id, ownerLogin);
        if (!ok) return false;

        lock.lock();
        try {
            return collection.removeIf(b -> b.getId() != null && b.getId() == id);
        } finally {
            lock.unlock();
        }
    }

    public int removeGreater(long id, String ownerLogin) throws Exception {
        List<Long> toDelete;
        lock.lock();
        try {
            toDelete = collection.stream()
                    .filter(b -> b.getId() != null && b.getId() > id)
                    .filter(b -> ownerLogin.equals(b.getOwnerLogin()))
                    .map(MusicBand::getId)
                    .toList();
        } finally {
            lock.unlock();
        }

        int removed = 0;
        for (Long bandId : toDelete) {
            if (bandId != null && removeById(bandId, ownerLogin)) removed++;
        }
        return removed;
    }

    public boolean shuffle() {
        lock.lock();
        try {
            if (collection.isEmpty()) return false;
            Collections.shuffle(collection);
            return true;
        } finally {
            lock.unlock();
        }
    }

    public List<ZonedDateTime> getEstablishmentDates() {
        lock.lock();
        try {
            return collection.stream()
                    .map(MusicBand::getEstablishmentDate)
                    .filter(Objects::nonNull)
                    .sorted(Comparator.reverseOrder())
                    .toList();
        } finally {
            lock.unlock();
        }
    }

    public String getEstablishmentDatesText() {
        List<ZonedDateTime> dates = getEstablishmentDates();
        if (dates.isEmpty()) return "Даты отсутствуют.";
        StringBuilder sb = new StringBuilder("Значения establishmentDate (по убыванию):\n");
        for (ZonedDateTime d : dates) sb.append(d).append("\n");
        return sb.toString();
    }

    public long countLessThanBestAlbum(Album limitAlbum) {
        if (limitAlbum == null) throw new NullPointerException("limitAlbum cannot be null");

        lock.lock();
        try {
            return collection.stream()
                    .map(MusicBand::getBestAlbum)
                    .filter(Objects::nonNull)
                    .filter(a -> a.compareTo(limitAlbum) < 0)
                    .count();
        } finally {
            lock.unlock();
        }
    }

    public String filterLessThanGenre(MusicGenre limitGenre) {
        if (limitGenre == null) throw new NullPointerException("limitGenre cannot be null");

        lock.lock();
        try {
            String result = collection.stream()
                    .filter(b -> b.getGenre() != null && b.getGenre().compareTo(limitGenre) < 0)
                    .map(MusicBand::toString)
                    .collect(Collectors.joining("\n"));

            return result.isBlank() ? "Подходящих элементов не найдено." : result;
        } finally {
            lock.unlock();
        }
    }
}