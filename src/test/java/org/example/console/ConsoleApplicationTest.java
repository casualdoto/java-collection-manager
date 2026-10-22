package org.example.console;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;
import org.example.io.UserFileLoader;
import org.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class ConsoleApplicationTest {
    @TempDir
    Path directory;

    private String run(String input, UserService service) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (Scanner scanner = new Scanner(input);
             PrintStream output = new PrintStream(bytes, true, StandardCharsets.UTF_8)) {
            new ConsoleApplication(scanner, output, service, new UserFileLoader()).run();
        }
        return bytes.toString(StandardCharsets.UTF_8);
    }

    @Test
    void fullMenuScenarioHandlesErrorsAndBothSorts() throws Exception {
        Path file = Files.writeString(directory.resolve("users.txt"), "2;Борис;b@x\n3;Анна;a@x\n");
        UserService service = new UserService();
        String output = run(String.join("\n", "abc", "9", "1", "bad", "-1", "Имя", "a@x",
                "1", "Тест", "bad", "1", "Вера", "v@x", "2", file.toString(), "3", "4", "АН",
                "5", "0", "1", "3", "1", "5", "2", "2", "3", "0") + "\n", service);
        assertEquals(List.of("Вера", "Борис", "Анна"), service.getAll().stream().map(u -> u.name()).toList());
        assertTrue(output.contains("необходимо ввести целое число"));
        assertTrue(output.contains("ID должен быть положительным"));
        assertTrue(output.contains("Email должен содержать"));
        assertTrue(output.contains("Добавлено: 2. Отклонено: 0."));
        assertTrue(output.contains("a@x"));
        assertTrue(output.contains("Завершение работы программы."));
        assertFalse(output.contains("Ввод завершён"));
    }

    @Test
    void eofAtAnyPromptExitsWithoutPartialUsers() {
        for (String input : List.of("", "1\n", "1\n1\n", "1\n1\nАнна\n", "2\n", "4\n",
                "5\n", "5\n1\n")) {
            UserService service = new UserService();
            assertTrue(run(input, service).contains("Ввод завершён"));
            assertTrue(service.getAll().isEmpty());
        }
    }

    @Test
    void emptyResultsAndMissingFileReturnToMenu() {
        String output = run("3\n4\n\n2\n" + directory.resolve("missing.txt") + "\n0\n", new UserService());
        assertTrue(output.contains("Список пользователей пуст"));
        assertTrue(output.contains("Совпадений не найдено"));
        assertTrue(output.contains("Не удалось прочитать файл"));
        assertTrue(output.contains("Добавлено: 0. Отклонено: 0."));
    }
}
