import commands.*;
import managers.CommandManager;
import managers.InputManager;
import network.UdpClient;

import java.time.Duration;
import java.util.Scanner;

public class ClientMain {
    public static void main(String[] args) {
        try {
            String host = "127.0.0.1";
            int port = 2222;

            if (args.length >= 1 && !args[0].isBlank()) host = args[0].trim();
            if (args.length >= 2 && !args[1].isBlank()) port = Integer.parseInt(args[1].trim());

            Duration timeout = Duration.ofSeconds(3);
            UdpClient udpClient = new UdpClient(host, port);

            CommandManager commandManager = new CommandManager();

            commandManager.register("help", new Help(commandManager));
            commandManager.register("history", new History(commandManager));
            commandManager.register("exit", new Exit());
            commandManager.register("register", new Register(udpClient, timeout));
            commandManager.register("login", new Login(udpClient, timeout));

            commandManager.register("info", new Info(udpClient, timeout));
            commandManager.register("show", new Show(udpClient, timeout));
            commandManager.register("clear", new Clear(udpClient, timeout));
            commandManager.register("shuffle", new Shuffle(udpClient, timeout));

            commandManager.register("add", new Add(udpClient, timeout));
            commandManager.register("update", new UpdateId(udpClient, timeout));
            commandManager.register("remove_by_id", new RemoveById(udpClient, timeout));
            commandManager.register("remove_greater", new RemoveGreater(udpClient, timeout));

            commandManager.register("count_less_than_best_album", new CountLessThanBestAlbum(udpClient, timeout));
            commandManager.register("filter_less_than_genre", new FilterLessThanGenre(udpClient, timeout));
            commandManager.register("get_establishment_dates", new GetEstablishmentDates(udpClient, timeout));

            commandManager.register("execute_script", new ExecuteScript(commandManager));

            Scanner scanner = InputManager.getScanner();
            System.out.println("Client started. Connected to " + host + ":" + port);

            while (true) {
                System.out.print("> ");
                if (!scanner.hasNextLine()) break;

                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                String name;
                String cmdArgs = "";
                int space = line.indexOf(' ');
                if (space == -1) {
                    name = line;
                } else {
                    name = line.substring(0, space);
                    cmdArgs = line.substring(space + 1).trim();
                }

                commandManager.execute(name, cmdArgs);
            }
        } catch (Exception e) {
            System.out.println("Client failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}