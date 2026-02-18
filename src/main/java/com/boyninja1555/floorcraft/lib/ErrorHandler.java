package com.boyninja1555.floorcraft.lib;

import javax.swing.*;

public class ErrorHandler {

    public static void error(String message) {
        JOptionPane.showMessageDialog(null, message, "Floorcraft Errored (Not a Crash)", JOptionPane.ERROR_MESSAGE);
    }

    public static void crash(String message) {
        JOptionPane.showMessageDialog(null, message, "Floorcraft Crash Report", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }
}
