package commands;

import managers.ClientSession;
import network.*;

import java.time.Duration;

public class Info implements Command {
    private final UdpClient udpClient;
    private final Duration timeout;

    public Info(UdpClient udpClient, Duration timeout) {
        this.udpClient = udpClient;
        this.timeout = timeout;
    }

    @Override
    public void execute(String args) {
        if (!ClientSession.isAuthorized()) {
            System.out.println("Нужна авторизация: login/register");
            return;
        }

        try {
            Request req = new Request(CommandType.INFO, "", null, null, ClientSession.getCredentials());
            Response resp = udpClient.sendAndReceive(req, timeout);
            System.out.println(resp.getMessage());
        } catch (Exception e) {
            System.out.println("Ошибка info: " + e.getMessage());
        }
    }

    @Override
    public String getDescription() {
        return "info : вывести информацию о коллекции";
    }

    @Override
    public String getName() {
        return "info";
    }
}