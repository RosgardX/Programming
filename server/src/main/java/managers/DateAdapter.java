package managers;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.ZonedDateTime;

/**
 * Gson-адаптер для сериализации/десериализации {@link ZonedDateTime}.
 */
public class DateAdapter implements JsonSerializer<ZonedDateTime>, JsonDeserializer<ZonedDateTime> {

    /**
     * Преобразует {@link ZonedDateTime} в JSON.
     *
     * @param src значение даты/времени
     * @param typeOfSrc тип
     * @param context контекст сериализации
     * @return JSON-представление даты/времени
     */
    @Override
    public JsonElement serialize(ZonedDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }

    /**
     * Преобразует JSON в {@link ZonedDateTime}.
     *
     * @param json JSON-значение
     * @param typeOfT тип
     * @param context контекст десериализации
     * @return распарсенный {@link ZonedDateTime}
     * @throws JsonParseException если значение не удаётся распарсить
     */
    @Override
    public ZonedDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        return ZonedDateTime.parse(json.getAsString());
    }
}