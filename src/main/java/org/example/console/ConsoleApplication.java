package org.example.console;

import java.io.PrintStream;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;
import org.example.io.UserFileLoader;
import org.example.model.User;
import org.example.service.UserService;
import org.example.sort.InsertionSort;
import org.example.sort.MergeSort;
import org.example.sort.SortAlgorithm;

public class ConsoleApplication {
    private final Scanner scanner;
    private final PrintStream output;
    private final UserService service;
    private final UserFileLoader loader;

    public ConsoleApplication(Scanner scanner, PrintStream output, UserService service, UserFileLoader loader) {
        this.scanner = scanner;
        this.output = output;
        this.service = service;
        this.loader = loader;
    }

    public void run() {
        try {
            while (true) {
                output.println("""

                        ===== МЕНЮ =====
                        1. Добавить пользователя вручную
                        2. Загрузить пользователей из файла
                        3. Показать всех пользователей
                        4. Обработать пользователей через Stream API
                        5. Выполнить собственную сортировку
                        0. Выход""");
                switch (readChoice("Выберите пункт меню: ", 0, 5)) {
                    case 1 -> addUser();
                    case 2 -> loadUsers();
                    case 3 -> printUsers(service.getAll());
                    case 4 -> processUsers();
                    case 5 -> sortUsers();
                    case 0 -> {
                        output.println("Завершение работы программы.");
                        return;
                    }
                    default -> throw new IllegalStateException("Неизвестная команда.");
                }
            }
        } catch (EndOfInputException e) {
            output.println("\nВвод завершён. Завершение работы программы.");
        }
    }

    private void addUser() {
        while (true) {
            try {
                long id;
                try {
                    id = Long.parseLong(readLine("ID: ").strip());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("ID должен быть целым числом в диапазоне long.");
                }
                String name = readLine("Имя: ");
                String email = readLine("Email: ");
                service.add(new User(id, name, email));
                output.println("Пользователь добавлен.");
                return;
            } catch (IllegalArgumentException e) {
                output.println("Ошибка: " + e.getMessage() + " Повторите ввод пользователя.");
            }
        }
    }

    private void loadUsers() {
        while (true) {
            try {
                String input = readLine("Путь к TXT-файлу: ").strip();
                if (input.isEmpty()) {
                    output.println("Ошибка: путь не должен быть пустым.");
                    continue;
                }
                UserFileLoader.LoadResult result = loader.load(Path.of(input), service, output::println);
                if (result.readError() != null) {
                    output.println(result.readError());
                }
                output.printf("Добавлено: %d. Отклонено: %d.%n", result.added(), result.rejected());
                return;
            } catch (InvalidPathException e) {
                output.println("Ошибка: некорректный путь. Повторите ввод.");
            }
        }
    }

    private void processUsers() {
        List<String> emails = service.findEmails(readLine("Подстрока имени (пустая - все): "));
        if (emails.isEmpty()) {
            output.println("Совпадений не найдено.");
        } else {
            output.println("Email найденных пользователей в порядке имён:");
            emails.forEach(output::println);
        }
    }

    private void sortUsers() {
        int algorithmChoice = readChoice("Алгоритм (1 - вставки, 2 - слияние): ", 1, 2);
        SortAlgorithm<User> algorithm = algorithmChoice == 1 ? new InsertionSort<>() : new MergeSort<>();
        boolean descending = readChoice("Направление (1 - возрастание, 2 - убывание): ", 1, 2) == 2;
        printUsers(service.sorted(algorithm, descending));
    }

    private void printUsers(List<User> users) {
        if (users.isEmpty()) {
            output.println("Список пользователей пуст.");
        } else {
            users.forEach(user -> output.printf("ID: %d | Имя: %s | Email: %s%n",
                    user.id(), user.name(), user.email()));
        }
    }

    private int readChoice(String prompt, int min, int max) {
        while (true) {
            try {
                int choice = Integer.parseInt(readLine(prompt).strip());
                if (choice >= min && choice <= max) {
                    return choice;
                }
                output.printf("Ошибка: выберите число от %d до %d.%n", min, max);
            } catch (NumberFormatException e) {
                output.println("Ошибка: необходимо ввести целое число.");
            }
        }
    }

    private String readLine(String prompt) {
        output.print(prompt);
        if (!scanner.hasNextLine()) {
            throw new EndOfInputException();
        }
        return scanner.nextLine();
    }

    private static class EndOfInputException extends RuntimeException { }
}
