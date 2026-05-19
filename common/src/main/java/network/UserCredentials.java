package network;

import java.io.Serializable;

public class UserCredentials implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String login;
    private final String password;

    public UserCredentials(String login, String password) {
        this.login = login == null ? "" : login.trim();
        this.password = password == null ? "" : password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}