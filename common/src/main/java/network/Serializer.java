package network;

import java.io.*;

public final class Serializer {

    public static byte[] toBytes(Serializable obj) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(obj);
        }
        return bos.toByteArray();
    }

    public static Object fromBytes(byte[] data) throws IOException {
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
            return ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("Класс не найден при десериализации: " + e.getMessage(), e);
        }
    }

    public static <T> T fromBytes(byte[] data, Class<T> expectedType) throws IOException {
        Object obj = fromBytes(data);
        if (!expectedType.isInstance(obj)) {
            throw new IOException("Unexpected object type: " + (obj == null ? "null" : obj.getClass())
                    + ", expected: " + expectedType);
        }
        return expectedType.cast(obj);
    }

    private Serializer() {}
}