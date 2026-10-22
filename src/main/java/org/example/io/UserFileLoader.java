package org.example.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import org.example.model.User;
import org.example.service.UserService;

public class UserFileLoader {
    public record LoadResult(int added, int rejected, String readError) { }

    public LoadResult load(Path path, UserService service, Consumer<String> diagnostics) {
        int added = 0;
        int rejected = 0;
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }
                try {
                    service.add(parse(line));
                    added++;
                } catch (IllegalArgumentException e) {
                    rejected++;
                    diagnostics.accept("Строка " + lineNumber + ": " + e.getMessage());
                }
            }
            return new LoadResult(added, rejected, null);
        } catch (IOException | SecurityException e) {
            return new LoadResult(added, rejected, "Не удалось прочитать файл: " + e.getMessage());
        }
    }

    private User parse(String line) {
        String[] fields = line.split(";", -1);
        if (fields.length != 3) {
            throw new IllegalArgumentException("Ожидается формат id;name;email (ровно три поля).");
        }
        long id;
        try {
            id = Long.parseLong(fields[0].strip());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ID должен быть целым числом в диапазоне long.");
        }
        return new User(id, fields[1], fields[2]);
    }
}
