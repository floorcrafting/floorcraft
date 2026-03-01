package com.boyninja1555.floorcraft.cli.lib;

public class CLIErrorHandler {

    public static void error(String message) {
        System.err.println(message);
    }

    public static void crash(String message) {
        error(message);
        System.exit(1);
    }
}
