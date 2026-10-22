package com.example.smartpantry;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class IngredientNames {
    private static final Map<String, String> ALIASES = new HashMap<>();
    static {
        alias("tomato", "tomatoes"); alias("potato", "potatoes");
        alias("egg", "eggs"); alias("onion", "onions");
        alias("carrot", "carrots"); alias("banana", "bananas");
        alias("apple", "apples"); alias("bean", "beans");
        alias("bell pepper", "bell peppers", "capsicum");
        alias("chickpea", "chickpeas"); alias("oats", "oat");
        alias("spring onion", "spring onions", "scallion", "scallions");
    }
    private static void alias(String name, String... alternatives) {
        for (String alternative : alternatives) ALIASES.put(alternative, name);
    }
    public static String normalise(String name) {
        String cleaned = name.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
        return ALIASES.containsKey(cleaned) ? ALIASES.get(cleaned) : cleaned;
    }
    private IngredientNames() { }
}
