package com.example.smartpantry;

public final class Units {
    public static final String[] OPTIONS = {"g", "kg", "ml", "l", "tsp", "tbsp", "cup", "pcs", "slice"};

    public static String dimension(String unit) {
        switch (unit) {
            case "g": case "kg": return "mass";
            case "ml": case "l": case "tsp": case "tbsp": case "cup": return "volume";
            case "pcs": return "count";
            case "slice": return "slice";
            default: throw new IllegalArgumentException("Unsupported unit: " + unit);
        }
    }
    public static double baseAmount(double amount, String unit) {
        switch (unit) {
            case "kg": case "l": return amount * 1000;
            case "tsp": return amount * 5;
            case "tbsp": return amount * 15;
            case "cup": return amount * 250;
            default: dimension(unit); return amount;
        }
    }
    private Units() { }
}
