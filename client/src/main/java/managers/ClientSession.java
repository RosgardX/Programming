package managers;

import network.UserCredentials;

public class ClientSession {
    private static volatile UserCredentials credentials = null;

    private ClientSession() {}

    public static void setCredentials(UserCredentials c) {
        credentials = c;
    }

    public static UserCredentials getCredentials() {
        return credentials;
    }

    public static boolean isAuthorized() {
        return credentials != null
                && credentials.getLogin() != null && !credentials.getLogin().isBlank()
                && credentials.getPassword() != null && !credentials.getPassword().isBlank();
    }

    public static void clear() {
        credentials = null;
    }
}