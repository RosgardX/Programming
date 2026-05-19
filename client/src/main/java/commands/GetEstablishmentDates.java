package commands;

import managers.ClientSession;
import network.*;

import java.time.Duration;

public class GetEstablishmentDates implements Command {
    private final UdpClient udpClient;
    private final Duration timeout;

    public GetEstablishmentDates(UdpClient udpClient, Duration timeout) {
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
            Request req = new Request(CommandType.PRINT_FIELD_DESCENDING_ESTABLISHMENT_DATE, "", null, null, ClientSession.getCredentials());
            Response resp = udpClient.sendAndReceive(req, timeout);
            System.out.println(resp.getMessage());
        } catch (Exception e) {
            System.out.println("Ошибка get_establishment_dates: " + e.getMessage());
        }
    }

    @Override
    public String getDescription() {
        return "get_establishment_dates : вывести establishmentDate по убыванию";
    }

    @Override
    public String getName() {
        return "get_establishment_dates";
    }
}