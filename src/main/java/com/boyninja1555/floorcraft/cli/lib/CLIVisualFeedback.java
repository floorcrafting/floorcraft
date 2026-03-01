package com.boyninja1555.floorcraft.cli.lib;

import javax.swing.*;
import java.awt.*;
import java.io.Console;
import java.util.Scanner;

public class CLIVisualFeedback {
    private static final Console console = System.console();
    private static final boolean headless = GraphicsEnvironment.isHeadless();

    public static String ask(String prompt) {
        if (console != null) {
            String input = console.readLine("%s ", prompt);
            return input == null ? "" : input;
        }

        if (!headless) {
            String input = JOptionPane.showInputDialog(null, prompt);
            return input == null ? "" : input;
        }

        System.out.println(prompt);
        System.out.print("> ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    public static void info(String title, Object message) {
        if (console != null) {
            System.out.println(title + "\n" + message);
            return;
        }

        if (!headless) JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
        else System.out.println(title + "\n" + message);
    }

    public static void warning(String title, Object message) {
        if (console != null) {
            System.out.println("WARNING: " + title + "\n" + message);
            return;
        }

        if (!headless) JOptionPane.showMessageDialog(null, message, title, JOptionPane.WARNING_MESSAGE);
        else System.out.println("WARNING: " + title + "\n" + message);
    }

    public static void error(String title, Object message) {
        if (console != null) {
            System.err.println("ERROR: " + title + "\n" + message);
            return;
        }

        if (!headless) JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
        else System.err.println("ERROR: " + title + "\n" + message);
    }

    public static void crash(String title, Object message) {
        error(title, message);
        System.exit(1);
    }
}
