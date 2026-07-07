package network;

import java.io.Serializable;

public class Request implements Serializable {
    private static final long serialVersionUID = 2L;

    private final CommandType type;
    private final String args;

    private final Long id;
    private final Serializable payload;
    private final UserCredentials credentials;

    public Request(CommandType type, String args, Long id, Serializable payload, UserCredentials credentials) {
        this.type = type;
        this.args = args == null ? "" : args;
        this.id = id;
        this.payload = payload;
        this.credentials = credentials;
    }

    public Request(CommandType type, String args, UserCredentials credentials) {
        this(type, args, null, null, credentials);
    }

    public Request(CommandType type, UserCredentials credentials) {
        this(type, "", null, null, credentials);
    }

    public CommandType getType() { return type; }
    public String getArgs() { return args; }
    public Long getId() { return id; }
    public Serializable getPayload() { return payload; }
    public UserCredentials getCredentials() { return credentials; }
}