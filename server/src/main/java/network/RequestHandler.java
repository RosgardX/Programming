package network;

import managers.AuthManager;
import managers.CollectionManager;
import models.Album;
import models.MusicBand;
import models.MusicGenre;

public class RequestHandler {
    private final CollectionManager collectionManager;
    private final AuthManager authManager;

    public RequestHandler(CollectionManager collectionManager, AuthManager authManager) {
        this.collectionManager = collectionManager;
        this.authManager = authManager;
    }

    public Response handle(Request request) {
        if (request == null) return new Response(false, "Request is null");

        try {
            if (request.getType() == CommandType.REGISTER) {
                boolean ok = authManager.register(request.getCredentials());
                return new Response(ok, ok ? "Регистрация успешна." : "Регистрация не удалась (логин занят?).");
            }

            if (request.getType() == CommandType.LOGIN) {
                boolean ok = authManager.authorize(request.getCredentials());
                return new Response(ok, ok ? "Авторизация успешна." : "Неверный логин/пароль.");
            }

            if (!authManager.authorize(request.getCredentials())) {
                return new Response(false, "Запрещено: пользователь не авторизован.");
            }

            String login = request.getCredentials().getLogin();

            return switch (request.getType()) {

                case INFO -> new Response(true, collectionManager.getInfo());
                case SHOW -> new Response(true, collectionManager.show());

                case COUNT_LESS_THAN_BEST_ALBUM -> {
                    if (!(request.getPayload() instanceof Album album)) {
                        yield new Response(false, "COUNT_LESS_THAN_BEST_ALBUM: payload должен быть Album");
                    }
                    long count = collectionManager.countLessThanBestAlbum(album);
                    yield new Response(true, "Количество групп: " + count);
                }

                case FILTER_LESS_THAN_GENRE -> {
                    if (!(request.getPayload() instanceof MusicGenre genre)) {
                        yield new Response(false, "FILTER_LESS_THAN_GENRE: payload должен быть MusicGenre");
                    }
                    yield new Response(true, collectionManager.filterLessThanGenre(genre));
                }

                case PRINT_FIELD_DESCENDING_ESTABLISHMENT_DATE ->
                        new Response(true, collectionManager.getEstablishmentDatesText());

                case ADD -> {
                    if (!(request.getPayload() instanceof MusicBand band)) {
                        yield new Response(false, "ADD: payload должен быть MusicBand");
                    }
                    boolean ok = collectionManager.add(band, login);
                    yield new Response(ok, ok ? "Элемент добавлен." : "Не удалось добавить элемент.");
                }

                case UPDATE -> {
                    if (request.getId() == null) yield new Response(false, "UPDATE: id is null");
                    if (!(request.getPayload() instanceof MusicBand band)) {
                        yield new Response(false, "UPDATE: payload должен быть MusicBand");
                    }
                    boolean ok = collectionManager.updateById(request.getId(), band, login);
                    yield new Response(ok, ok ? "Элемент обновлён." : "Обновление запрещено (не владелец?) или id не найден.");
                }

                case REMOVE_BY_ID -> {
                    if (request.getId() == null) yield new Response(false, "REMOVE_BY_ID: id is null");
                    boolean ok = collectionManager.removeById(request.getId(), login);
                    yield new Response(ok, ok ? "Элемент удалён." : "Удаление запрещено (не владелец?) или id не найден.");
                }

                case REMOVE_GREATER -> {
                    if (request.getId() == null) yield new Response(false, "REMOVE_GREATER: id is null");
                    int removed = collectionManager.removeGreater(request.getId(), login);
                    yield new Response(true, "Удалено элементов: " + removed);
                }

                case SHUFFLE -> {
                    boolean ok = collectionManager.shuffle();
                    yield new Response(ok, ok ? "Коллекция перемешана." : "Коллекция пуста, нечего перемешивать.");
                }

                case CLEAR -> {
                    int removed = collectionManager.clearOwned(login);
                    yield new Response(true, "Удалено своих элементов: " + removed);
                }
                default -> new Response(false, "Команда не поддерживается сервером: " + request.getType());
            };
        } catch (Exception e) {
            return new Response(false, "Ошибка на сервере: " + e.getMessage());
        }
    }
}