package com.boyninja1555.floorcraft.cli.lib;

import javax.swing.*;

public class CLIVisualFeedback {

    public static String ask(String prompt) {
        return JOptionPane.showInputDialog(prompt);
    }

    public static void info(String title, Object message) {
        System.out.println(title + "\n" + message);
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void warning(String title, Object message) {
        System.out.println(title + "\n" + message);
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.WARNING_MESSAGE);
    }

    public static void error(String title, Object message) {
        System.out.println(title + "\n" + message);
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public static void crash(String title, Object message) {
        error(title, message);
        System.exit(1);
    }
}
