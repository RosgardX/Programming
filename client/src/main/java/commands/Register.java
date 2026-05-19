package commands;

import managers.ClientSession;
import managers.InputManager;
import network.*;

import java.time.Duration;
import java.util.Scanner;

public class Register implements Command {
    private final UdpClient udpClient;
    private final Duration timeout;

    public Register(UdpClient udpClient, Duration timeout) {
        this.udpClient = udpClient;
        this.timeout = timeout;
    }

    @Override
    public void execute(String args) {
        Scanner sc = InputManager.getScanner();

        System.out.print("login: ");
        String login = sc.nextLine().trim();

        System.out.print("password: ");
        String password = sc.nextLine(); // не trim, пусть будет как ввели

        try {
            UserCredentials cred = new UserCredentials(login, password);
            Request req = new Request(CommandType.REGISTER, "", null, null, cred);

            Response resp = udpClient.sendAndReceive(req, timeout);
            System.out.println(resp.getMessage());

            // как легче: если регистрация ок — сразу сохраняем cred как активные
            if (resp.isSuccess()) {
                ClientSession.setCredentials(cred);
            }

        } catch (Exception e) {
            System.out.println("Ошибка register: " + e.getMessage());
        }
    }

    @Override
    public String getDescription() {
        return "register : зарегистрироваться (логин+пароль)";
    }

    @Override
    public String getName() {
        return "register";
    }
}