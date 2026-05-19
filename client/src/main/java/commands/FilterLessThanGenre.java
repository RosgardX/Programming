package commands;

import managers.ClientSession;
import managers.InputManager;
import models.MusicGenre;
import network.*;

import java.time.Duration;
import java.util.Scanner;

/**
 * filter_less_than_genre : вывести элементы, genre которых меньше заданного.
 * Команда read-only => сервер фильтрует по коллекции в памяти.
 */
public class FilterLessThanGenre implements Command {
    private final UdpClient udpClient;
    private final Duration timeout;

    public FilterLessThanGenre(UdpClient udpClient, Duration timeout) {
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
            MusicGenre genre;

            if (args != null && !args.isBlank()) {
                try {
                    genre = MusicGenre.valueOf(args.trim().toUpperCase());
                } catch (IllegalArgumentException e) {
                    System.out.println("Жанр не распознан, выбери из списка:");
                    genre = askGenre(scanner);
                }
            } else {
                genre = askGenre(scanner);
            }

            Request req = new Request(CommandType.FILTER_LESS_THAN_GENRE, "", null, genre, ClientSession.getCredentials());
            Response resp = udpClient.sendAndReceive(req, timeout);
            System.out.println(resp.getMessage());

        } catch (Exception e) {
            System.out.println("Ошибка filter_less_than_genre: " + e.getMessage());
        }
    }

    private MusicGenre askGenre(Scanner scanner) {
        while (true) {
            System.out.println("Доступные жанры:");
            for (MusicGenre g : MusicGenre.values()) System.out.println("- " + g);
            System.out.print("genre: ");
            String s = scanner.nextLine().trim().toUpperCase();
            try {
                return MusicGenre.valueOf(s);
            } catch (IllegalArgumentException e) {
                System.out.println("Нет такого жанра.");
            }
        }
    }

    @Override
    public String getDescription() {
        return "filter_less_than_genre genre : вывести элементы, значение поля genre которых меньше заданного";
    }

    @Override
    public String getName() {
        return "filter_less_than_genre";
    }
}