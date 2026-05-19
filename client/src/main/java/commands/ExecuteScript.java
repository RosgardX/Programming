package commands;

import managers.CommandManager;
import managers.InputManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * execute_script file_name : считать и исполнить скрипт из указанного файла.
 *
 * Важно для 7 лабы:
 * - Скрипт исполняет те же команды, что и интерактивный ввод.
 * - Авторизация хранится в ClientSession и автоматически подставляется сетевыми командами.
 * - Защита от рекурсии через scriptStack в CommandManager.
 */
public class ExecuteScript implements Command {
    private final CommandManager commandManager;

    public ExecuteScript(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public void execute(String args) {
        if (args == null || args.isBlank()) {
            System.out.println("Использование: execute_script <file_name>");
            return;
        }

        String fileName = args.trim();

        if (commandManager.isScriptInStack(fileName)) {
            System.out.println("Рекурсия запрещена: скрипт уже выполняется: " + fileName);
            return;
        }
        commandManager.addScriptToStack(fileName);

        Scanner oldScanner = InputManager.getScanner();

        try {
            Scanner fileScanner = new Scanner(new File(fileName));
            InputManager.setScanner(fileScanner);

            System.out.println("Выполняем скрипт: " + fileName);

            while (InputManager.getScanner().hasNextLine()) {
                String line = InputManager.getScanner().nextLine().trim();
                if (line.isEmpty()) continue;

                String name;
                String cmdArgs = "";
                int space = line.indexOf(' ');
                if (space == -1) {
                    name = line;
                } else {
                    name = line.substring(0, space);
                    cmdArgs = line.substring(space + 1).trim();
                }

                System.out.println("> " + name + (cmdArgs.isEmpty() ? "" : " " + cmdArgs));
                commandManager.execute(name, cmdArgs);
            }

        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден: " + fileName);
        } catch (Exception e) {
            System.out.println("Ошибка execute_script: " + e.getMessage());
        } finally {
            InputManager.setScanner(oldScanner);
            commandManager.removeScriptFromStack(fileName);
        }
    }

    @Override
    public String getDescription() {
        return "execute_script file_name : считать и исполнить скрипт из указанного файла";
    }

    @Override
    public String getName() {
        return "execute_script";
    }
}