package commands;

/**
 * exit : завершить клиент.
 */
public class Exit implements Command {
    @Override
    public void execute(String args) {
        System.out.println("Выход из клиента...");
        System.exit(0);
    }

    @Override
    public String getDescription() {
        return "exit : завершить клиент";
    }

    @Override
    public String getName() {
        return "exit";
    }
}