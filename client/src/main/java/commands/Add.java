package commands;

import managers.ClientSession;
import managers.InputManager;
import models.*;
import network.*;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Scanner;

/**
 * add : добавить новый элемент в коллекцию (на сервере).
 * <p>
 * Клиент собирает поля MusicBand, отправляет на сервер.
 * Сервер:
 * - проверяет авторизацию
 * - пишет в БД (id генерируется sequence)
 * - при успехе обновляет коллекцию в памяти
 * </p>
 */
public class Add implements Command {
    private final UdpClient udpClient;
    private final Duration timeout;

    public Add(UdpClient udpClient, Duration timeout) {
        this.udpClient = udpClient;
        this.timeout = timeout;
    }

    @Override
    public void execute(String args) {
        if (!ClientSession.isAuthorized()) {
            System.out.println("Нужна авторизация: login/register");
            return;
        }

        Scanner scanner = InputManager.getScanner();

        try {
            System.out.println("Создание новой музыкальной группы:");

            String name = askString(scanner, "Введите имя группы: ", false);

            System.out.println("Введите координаты:");
            double x = askDouble(scanner, " -> X: ");
            Double y = askDoubleObj(scanner, " -> Y: ");
            Coordinates coordinates = new Coordinates(x, y);

            long participants = askLongPositive(scanner, "Введите количество участников (> 0): ");

            String description = askString(scanner, "Введите описание (можно пустую строку): ", true);

            ZonedDateTime establishmentDate = ZonedDateTime.now();
            System.out.println("Дата основания проставлена автоматически: " + establishmentDate);

            MusicGenre genre = askGenre(scanner, true);

            Album bestAlbum = askAlbum(scanner);

            MusicBand band = new MusicBand(
                    name,
                    coordinates,
                    participants,
                    description,
                    establishmentDate,
                    genre,
                    bestAlbum
            );

            Request req = new Request(CommandType.ADD, "", null, band, ClientSession.getCredentials());
            Response resp = udpClient.sendAndReceive(req, timeout);
            System.out.println(resp.getMessage());

        } catch (Exception e) {
            System.out.println("Ошибка add: " + e.getMessage());
        }
    }


    private String askString(Scanner scanner, String prompt, boolean canBeEmpty) {
        while (true) {
            System.out.print(prompt);
            String s = scanner.nextLine();
            if (s == null) s = "";
            s = s.trim();
            if (!canBeEmpty && s.isEmpty()) {
                System.out.println("Поле не может быть пустым.");
                continue;
            }
            return s;
        }
    }

    private double askDouble(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = scanner.nextLine().trim();
            try {
                return Double.parseDouble(s);
            } catch (NumberFormatException e) {
                System.out.println("Введите число (double).");
            }
        }
    }

    private Double askDoubleObj(Scanner scanner, String prompt) {
        while (true) {
            double d = askDouble(scanner, prompt);
            if (Double.isNaN(d)) {
                System.out.println("Y не может быть NaN.");
                continue;
            }
            return d;
        }
    }

    private long askLongPositive(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = scanner.nextLine().trim();
            try {
                long v = Long.parseLong(s);
                if (v <= 0) {
                    System.out.println("Число должно быть > 0.");
                    continue;
                }
                return v;
            } catch (NumberFormatException e) {
                System.out.println("Введите целое число (long).");
            }
        }
    }

    private MusicGenre askGenre(Scanner scanner, boolean allowNull) {
        while (true) {
            System.out.println("Выберите жанр" + (allowNull ? " (или пусто = null)" : "") + ":");
            for (MusicGenre g : MusicGenre.values()) {
                System.out.println("- " + g);
            }
            System.out.print("genre: ");
            String s = scanner.nextLine().trim();
            if (allowNull && s.isEmpty()) return null;

            try {
                return MusicGenre.valueOf(s.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Нет такого жанра.");
            }
        }
    }

    private Album askAlbum(Scanner scanner) {
        System.out.println("Введите данные о лучшем альбоме (или пусто = null).");
        System.out.print("album name (Enter = null): ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) return null;

        long tracks = askLongPositive(scanner, "tracks (>0): ");

        int length;
        while (true) {
            System.out.print("length (>0): ");
            String s = scanner.nextLine().trim();
            try {
                length = Integer.parseInt(s);
                if (length <= 0) {
                    System.out.println("length должен быть > 0.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Введите int.");
            }
        }

        float sales;
        while (true) {
            System.out.print("sales (>0): ");
            String s = scanner.nextLine().trim();
            try {
                sales = Float.parseFloat(s);
                if (sales <= 0) {
                    System.out.println("sales должен быть > 0.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Введите float.");
            }
        }

        return new Album(name, tracks, length, sales);
    }

    @Override
    public String getDescription() {
        return "add : добавить новый элемент в коллекцию";
    }

    @Override
    public String getName() {
        return "add";
    }
}