package network;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.time.Duration;

/**
 * UDP-клиент для отправки запросов на сервер и получения ответов.
 */
public class UdpClient implements Closeable {
    private final InetSocketAddress serverAddress;
    private final DatagramChannel channel;
    private final Selector selector;

    public UdpClient(String host, int port) throws IOException {
        this.serverAddress = new InetSocketAddress(host, port);

        this.channel = DatagramChannel.open();
        this.channel.configureBlocking(false);

        this.selector = Selector.open();
        this.channel.register(selector, SelectionKey.OP_READ);
    }

    public Response sendAndReceive(Request request, Duration timeout) throws IOException, TimeoutException {
        byte[] payload = Serializer.toBytes(request);

        channel.send(ByteBuffer.wrap(payload), serverAddress);

        long timeoutMs = timeout.toMillis();
        long start = System.currentTimeMillis();

        ByteBuffer in = ByteBuffer.allocate(65507);

        while (true) {
            long elapsed = System.currentTimeMillis() - start;
            long remaining = timeoutMs - elapsed;
            if (remaining <= 0) throw new TimeoutException("Сервер не ответил за " + timeoutMs + " мс");

            int ready = selector.select(remaining);
            if (ready == 0) continue;

            for (SelectionKey key : selector.selectedKeys()) {
                if (!key.isValid()) continue;

                if (key.isReadable()) {
                    in.clear();
                    channel.receive(in);
                    in.flip();

                    byte[] data = new byte[in.remaining()];
                    in.get(data);

                    Response response = Serializer.fromBytes(data, Response.class);
                    selector.selectedKeys().clear();
                    return response;
                }
            }
            selector.selectedKeys().clear();
        }
    }

    @Override
    public void close() throws IOException {
        try {
            selector.close();
        } finally {
            channel.close();
        }
    }

    public static class TimeoutException extends Exception {
        public TimeoutException(String message) {
            super(message);
        }
    }
}