package com.javarush.task.task30.task3008;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleHelper {
    private static BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

    public static void writeMessage(String message) {
        System.out.println(message);
    }

    public static String readString() {
        try {
            return bufferedReader.readLine();
        } catch (IOException e) {
            System.out.println("An error occurred while trying to enter text. Try again.\n" + e.getMessage());
            return readString();
        }
    }

    public static int readInt() {
        try {
            return Integer.parseInt(readString());
        } catch (NumberFormatException e) {
            System.out.println("An error occurred while trying to enter a number. Try again.\n" + e.getMessage());
            return readInt();
        }
    }
}
