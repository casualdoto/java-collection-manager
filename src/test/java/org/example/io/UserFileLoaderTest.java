package org.example.io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.example.model.User;
import org.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class UserFileLoaderTest {
    @TempDir
    Path directory;

    @Test
    void loadsUtf8TrimsFieldsAndReportsBadRows() throws IOException {
        Path file = directory.resolve("пользователи.txt");
        Files.writeString(file, """
                1;Дубликат;d@x
                2; Анна ; a@x

                2;Повтор;d@x
                abc;Имя;a@x
                3;;a@x
                4;Имя;bad
                5;Имя;a@x;лишнее
                6;Имя;
                -1;Имя;a@x
                9223372036854775808;Имя;a@x
                7;Борис;a@x
                """, StandardCharsets.UTF_8);
        UserService service = new UserService();
        service.add(new User(1, "Существующий", "1@x"));
        List<String> messages = new ArrayList<>();
        var result = new UserFileLoader().load(file, service, messages::add);
        assertEquals(2, result.added());
        assertEquals(9, result.rejected());
        assertNull(result.readError());
        assertEquals(List.of(1L, 2L, 7L), service.getAll().stream().map(User::id).toList());
        assertEquals("Анна", service.getAll().get(1).name());
        assertEquals(9, messages.size());
        assertTrue(messages.getFirst().startsWith("Строка 1:"));
        assertTrue(messages.get(1).startsWith("Строка 4:"));
    }

    @Test
    void emptyFileAddsNothing() throws IOException {
        Path file = Files.writeString(directory.resolve("empty.txt"), "\n  \n");
        var result = new UserFileLoader().load(file, new UserService(), message -> fail(message));
        assertEquals(new UserFileLoader.LoadResult(0, 0, null), result);
    }

    @Test
    void missingFilePreservesExistingData() {
        UserService service = new UserService();
        service.add(new User(1, "Анна", "a@x"));
        var result = new UserFileLoader().load(directory.resolve("missing.txt"), service, message -> fail(message));
        assertEquals(0, result.added());
        assertNotNull(result.readError());
        assertEquals(1, service.getAll().size());
    }

    @Test
    void readFailurePreservesAlreadyImportedRows() throws IOException {
        StringBuilder content = new StringBuilder();
        for (int id = 1; id <= 600; id++) {
            content.append(id).append(';').append("A".repeat(100)).append(";a@x\n");
        }
        byte[] valid = content.toString().getBytes(StandardCharsets.UTF_8);
        byte[] broken = java.util.Arrays.copyOf(valid, valid.length + 1);
        broken[valid.length] = (byte) 0xff;
        Path file = Files.write(directory.resolve("broken.txt"), broken);
        UserService service = new UserService();
        var result = new UserFileLoader().load(file, service, message -> fail(message));
        assertNotNull(result.readError());
        assertTrue(result.added() > 0);
        assertEquals(result.added(), service.getAll().size());
    }
}
