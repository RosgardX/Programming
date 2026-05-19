package managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import models.MusicBand;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.ZonedDateTime;
import java.util.Vector;

/**
 * Серверный FileManager: чтение/запись коллекции в JSON-файл.
 */
public class FileManager {
    private final Path filePath;
    private final Gson gson;

    public FileManager(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("fileName is empty");
        }
        this.filePath = Paths.get(fileName);

        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(ZonedDateTime.class, new DateAdapter())
                .create();
    }

    public boolean writeToFile(Vector<MusicBand> collection) {
        try {
            Path parent = filePath.toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            try (Writer writer = Files.newBufferedWriter(
                    filePath,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE
            )) {
                gson.toJson(collection, writer);
            }

            return true;
        } catch (IOException e) {
            System.out.println("Ошибка при записи в файл: " + e.getMessage());
            return false;
        }
    }

    public Vector<MusicBand> readFromFile() {
        if (!Files.exists(filePath)) {
            System.out.println("Файл не найден. Будет создана новая коллекция: " + filePath.toAbsolutePath());
            return new Vector<>();
        }

        try {
            if (Files.size(filePath) == 0) {
                return new Vector<>();
            }

            Type collectionType = new TypeToken<Vector<MusicBand>>() {}.getType();

            try (Reader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
                Vector<MusicBand> loaded = gson.fromJson(reader, collectionType);
                return loaded == null ? new Vector<>() : loaded;
            }

        } catch (JsonSyntaxException e) {
            System.out.println("Ошибка формата JSON. Коллекция будет пустой: " + e.getMessage());
            return new Vector<>();
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла. Коллекция будет пустой: " + e.getMessage());
            return new Vector<>();
        }
    }
}