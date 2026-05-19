package commands;

import managers.ClientSession;
import network.*;

import java.time.Duration;

public class Shuffle implements Command {
    private final UdpClient udpClient;
    private final Duration timeout;

    public Shuffle(UdpClient udpClient, Duration timeout) {
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
            Request req = new Request(CommandType.SHUFFLE, "", null, null, ClientSession.getCredentials());
            Response resp = udpClient.sendAndReceive(req, timeout);
            System.out.println(resp.getMessage());
        } catch (Exception e) {
            System.out.println("Ошибка shuffle: " + e.getMessage());
        }
    }

    @Override
    public String getDescription() {
        return "shuffle : перемешать коллекцию (в памяти на сервере)";
    }

    @Override
    public String getName() {
        return "shuffle";
    }
}