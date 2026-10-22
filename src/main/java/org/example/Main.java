package org.example;

import java.util.Scanner;
import org.example.console.ConsoleApplication;
import org.example.io.UserFileLoader;
import org.example.service.UserService;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            new ConsoleApplication(scanner, System.out, new UserService(), new UserFileLoader()).run();
        }
    }
}
