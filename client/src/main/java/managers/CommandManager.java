package managers;

import commands.Command;

import java.util.*;

/**
 * Менеджер команд: хранит зарегистриро��анные команды и выполняет их по имени.
 * <p>
 * Также хранит историю последних команд и отслеживает выполнение скриптов для защиты от рекурсии.
 * </p>
 */
public class CommandManager {
    private List<String> commandHistory = new ArrayList<>();
    private final Map<String, Command> commands = new HashMap<>();
    private final Set<String> scriptStack = new HashSet<>();

    /**
     * Регистрирует команду по имени.
     *
     * @param name имя команды
     * @param command реализация команды
     */
    public void register(String name, Command command) {
        commands.put(name, command);
    }

    /**
     * Возвращает карту зарегистрированных команд.
     *
     * @return команды
     */
    public Map<String, Command> getCommands() {
        return commands;
    }

    /**
     * Выполняет команду по имени.
     *
     * @param name имя команды
     * @param args аргументы команды
     */
    public void execute(String name, String args) {
        Command command = commands.get(name);
        if (command != null) {
            addToHistory(name);
            command.execute(args);
        } else {
            System.out.println("Команда не найдена. Введите 'help' для справки.");
        }
    }

    /**
     * Добавляет имя скрипта в стек выполняемых скриптов.
     *
     * @param fileName имя файла скрипта
     */
    public void addScriptToStack(String fileName) {
        scriptStack.add(fileName);
    }

    /**
     * Удаляет имя скрипта из стека выполняемых скриптов.
     *
     * @param fileName имя файла скрипта
     */
    public void removeScriptFromStack(String fileName) {
        scriptStack.remove(fileName);
    }

    /**
     * Проверяет, выполняется ли уже указанный скрипт.
     *
     * @param fileName имя файла скрипта
     * @return {@code true}, если скрипт уже в стеке
     */
    public boolean isScriptInStack(String fileName) {
        return scriptStack.contains(fileName);
    }

    /**
     * Добавляет команду в историю (хранится не более 8 последних).
     *
     * @param commandName имя выполненной команды
     */
    public void addToHistory(String commandName) {
        commandHistory.add(commandName);

        if (commandHistory.size() > 8) {
            commandHistory.remove(0);
        }
    }

    /**
     * Возвращает историю выполненных команд (до 8 последних).
     *
     * @return история команд
     */
    public List<String> getHistory() {
        return commandHistory;
    }
}