package commands;

import managers.ClientSession;
import network.*;

import java.time.Duration;

public class RemoveGreater implements Command {
    private final UdpClient udpClient;
    private final Duration timeout;

    public RemoveGreater(UdpClient udpClient, Duration timeout) {
        this.udpClient = udpClient;
        this.timeout = timeout;
    }

    @Override
    public void execute(String args) {
        if (!ClientSession.isAuthorized()) {
            System.out.println("Нужна авторизация: login/register");
            return;
        }
        if (args == null || args.isBlank()) {
            System.out.println("Использование: remove_greater <id>");
            return;
        }

        try {
            long id = Long.parseLong(args.trim());
            Request req = new Request(CommandType.REMOVE_GREATER, "", id, null, ClientSession.getCredentials());
            Response resp = udpClient.sendAndReceive(req, timeout);
            System.out.println(resp.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("id должен быть числом");
        } catch (Exception e) {
            System.out.println("Ошибка remove_greater: " + e.getMessage());
        }
    }

    @Override
    public String getDescription() {
        return "remove_greater id : удалить только свои элементы с id > заданного";
    }

    @Override
    public String getName() {
        return "remove_greater";
    }
}