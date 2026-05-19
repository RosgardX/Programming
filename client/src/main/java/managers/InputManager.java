package managers;

import java.util.Scanner;

/**
 * Утилитный класс для хранения текущего {@link Scanner}, используемого для ввода.
 * <p>
 * Нужен, чтобы временно переключать источник ввода (например, при выполнении скрипта).
 * </p>
 */
public class InputManager {
    private static Scanner scanner = new Scanner(System.in);

    /**
     * Возвращает текущий scanner для ввода.
     *
     * @return текущий {@link Scanner}
     */
    public static Scanner getScanner() {
        return scanner;
    }

    /**
     * Устанавливает scanner для ввода.
     *
     * @param sc новый {@link Scanner}
     */
    public static void setScanner(Scanner sc) {
        scanner = sc;
    }
}