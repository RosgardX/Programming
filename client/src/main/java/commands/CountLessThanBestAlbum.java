package commands;

import managers.ClientSession;
import managers.InputManager;
import models.Album;
import network.*;

import java.time.Duration;
import java.util.Scanner;

/**
 * count_less_than_best_album : вывести количество элементов, bestAlbum которых меньше заданного.
 * Команда read-only => сервер считает по коллекции в памяти.
 */
public class CountLessThanBestAlbum implements Command {
    private final UdpClient udpClient;
    private final Duration timeout;

    public CountLessThanBestAlbum(UdpClient udpClient, Duration timeout) {
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
            System.out.println("Введите альбом-границу (для сравнения по sales):");
            String name = askString(scanner, "name: ", false);
            long tracks = askLongPositive(scanner, "tracks (>0): ");
            int length = askIntPositive(scanner, "length (>0): ");
            float sales = askFloatPositive(scanner, "sales (>0): ");

            Album album = new Album(name, tracks, length, sales);

            Request req = new Request(CommandType.COUNT_LESS_THAN_BEST_ALBUM, "", null, album, ClientSession.getCredentials());
            Response resp = udpClient.sendAndReceive(req, timeout);
            System.out.println(resp.getMessage());

        } catch (Exception e) {
            System.out.println("Ошибка count_less_than_best_album: " + e.getMessage());
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

    private long askLongPositive(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = scanner.nextLine().trim();
            try {
                long v = Long.parseLong(s);
                if (v <= 0) {
                    System.out.println("Должно быть > 0.");
                    continue;
                }
                return v;
            } catch (NumberFormatException e) {
                System.out.println("Введите long.");
            }
        }
    }

    private int askIntPositive(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = scanner.nextLine().trim();
            try {
                int v = Integer.parseInt(s);
                if (v <= 0) {
                    System.out.println("Должно быть > 0.");
                    continue;
                }
                return v;
            } catch (NumberFormatException e) {
                System.out.println("Введите int.");
            }
        }
    }

    private float askFloatPositive(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = scanner.nextLine().trim();
            try {
                float v = Float.parseFloat(s);
                if (v <= 0) {
                    System.out.println("Должно быть > 0.");
                    continue;
                }
                return v;
            } catch (NumberFormatException e) {
                System.out.println("Введите float.");
            }
        }
    }

    @Override
    public String getDescription() {
        return "count_less_than_best_album : вывести количество элементов, значение поля bestAlbum которых меньше заданного";
    }

    @Override
    public String getName() {
        return "count_less_than_best_album";
    }
}