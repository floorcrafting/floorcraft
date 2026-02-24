package com.boyninja1555.floorcraft.lib;

public class PrimitiveShit {

    public static int[] integerArrayToIntArray(Integer[] array) {
        int[] result = new int[array.length];
        for (int i = 0; i < array.length; i++) result[i] = array[i];
        return result;
    }

    public static Integer[] intArrayToIntegerArray(int[] array) {
        Integer[] result = new Integer[array.length];

        for (int i = 0; i < array.length; i++) result[i] = array[i];
        return result;
    }
}
