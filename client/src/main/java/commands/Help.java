package commands;

import managers.CommandManager;

import java.util.Map;

/**
 * help : вывести справку по доступным командам.
 */
public class Help implements Command {
    private final CommandManager commandManager;

    public Help(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public void execute(String args) {
        System.out.println("Список доступных команд:");
        Map<String, Command> commands = commandManager.getCommands();
        for (Map.Entry<String, Command> e : commands.entrySet()) {
            System.out.println("- " + e.getKey() + " : " + e.getValue().getDescription());
        }
    }

    @Override
    public String getDescription() {
        return "help : вывести справку по доступным командам";
    }

    @Override
    public String getName() {
        return "help";
    }
}