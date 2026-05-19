package commands;

import managers.CommandManager;

import java.util.List;

/**
 * history : вывести последние 8 команд.
 */
public class History implements Command {
    private final CommandManager commandManager;

    public History(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public void execute(String args) {
        List<String> history = commandManager.getHistory();
        if (history.isEmpty()) {
            System.out.println("История команд пуста.");
            return;
        }
        System.out.println("Последние выполненные команды:");
        for (int i = 0; i < history.size(); i++) {
            System.out.println((i + 1) + ". " + history.get(i));
        }
    }

    @Override
    public String getDescription() {
        return "history : вывести последние 8 команд (без аргументов)";
    }

    @Override
    public String getName() {
        return "history";
    }
}