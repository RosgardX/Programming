package network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.*;

public class UdpServer {
    private final int port;
    private final RequestHandler handler;

    private final ExecutorService readPool = Executors.newFixedThreadPool(2);
    private final ExecutorService handlePool = Executors.newCachedThreadPool();
    private final ForkJoinPool sendPool = new ForkJoinPool();

    public UdpServer(int port, RequestHandler handler) {
        this.port = port;
        this.handler = handler;
    }

    public void run() throws IOException {
        try (DatagramChannel ch = DatagramChannel.open();
             Selector selector = Selector.open()) {

            ch.bind(new InetSocketAddress(port));
            ch.configureBlocking(false);
            ch.register(selector, SelectionKey.OP_READ);

            final DatagramChannel channel = ch;

            System.out.println("Server started on port " + port);

            while (true) {
                selector.select();

                Set<SelectionKey> selected = selector.selectedKeys();
                Iterator<SelectionKey> it = selected.iterator();

                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();

                    if (!key.isValid() || !key.isReadable()) continue;

                    readPool.execute(() -> {
                        try {
                            ByteBuffer buffer = ByteBuffer.allocate(65507);
                            SocketAddress clientAddr = channel.receive(buffer);
                            if (clientAddr == null) return;

                            buffer.flip();
                            byte[] inData = new byte[buffer.remaining()];
                            buffer.get(inData);

                            final SocketAddress client = clientAddr;
                            final byte[] data = inData;

                            handlePool.execute(() -> {
                                final Response response;
                                try {
                                    Request req = Serializer.fromBytes(data, Request.class);
                                    response = handler.handle(req);
                                } catch (Exception e) {
                                    Response r = new Response(false, "Ошибка чтения/обработки запроса: " + e.getMessage());
                                    sendPool.execute(() -> {
                                        try {
                                            byte[] out = Serializer.toBytes(r);
                                            channel.send(ByteBuffer.wrap(out), client);
                                        } catch (Exception ignored) {}
                                    });
                                    return;
                                }

                                sendPool.execute(() -> {
                                    try {
                                        byte[] out = Serializer.toBytes(response);
                                        channel.send(ByteBuffer.wrap(out), client);
                                    } catch (Exception ignored) {
                                    }
                                });
                            });

                        } catch (Exception ignored) {
                        }
                    });
                }
            }
        }
    }
}