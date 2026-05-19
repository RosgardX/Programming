package network;

import java.io.Serializable;

public class Response implements Serializable {
    private static final long serialVersionUID = 2L;

    private final boolean success;
    private final String message;
    private final Serializable payload;

    public Response(boolean success, String message, Serializable payload) {
        this.success = success;
        this.message = message == null ? "" : message;
        this.payload = payload;
    }

    public Response(boolean success, String message) {
        this(success, message, null);
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Serializable getPayload() { return payload; }
}