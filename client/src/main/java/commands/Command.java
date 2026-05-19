package commands;

/**
 * Интерфейс для всех команд приложения.
 */
public interface Command {

    /**
     * Выполняет команду.
     *
     * @param args аргументы команды (в виде одной строки)
     */
    void execute(String args);

    /**
     * Возвращает описание команды (для вывода в справке).
     *
     * @return описание команды
     */
    String getDescription();

    /**
     * Возвращает имя команды.
     *
     * @return имя команды
     */
    String getName();
}