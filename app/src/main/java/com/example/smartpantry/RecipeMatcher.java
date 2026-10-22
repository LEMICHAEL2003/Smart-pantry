package com.example.smartpantry;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RecipeMatcher {
    private static void add(Map<String, Double> totals, PantryItem item) {
        String key = IngredientNames.normalise(item.name) + ":" + Units.dimension(item.unit);
        double amount = Units.baseAmount(item.quantity, item.unit);
        totals.put(key, totals.getOrDefault(key, 0.0) + amount);
    }
    public static boolean canMake(Recipe recipe, List<PantryItem> pantry, LocalDate today) {
        if (recipe.ingredients.isEmpty()) return false;
        Map<String, Double> available = new HashMap<>();
        for (PantryItem item : pantry) {
            // An item dated today is still available. Older batches do not count.
            if (!item.expiry.isEmpty() && LocalDate.parse(item.expiry).isBefore(today)) continue;
            add(available, item);
        }
        Map<String, Double> required = new HashMap<>();
        for (PantryItem ingredient : recipe.ingredients) add(required, ingredient);
        for (Map.Entry<String, Double> need : required.entrySet()) {
            // This tolerance only absorbs floating-point conversion noise.
            if (available.getOrDefault(need.getKey(), 0.0) + 1e-9 < need.getValue()) return false;
        }
        return true;
    }
    private RecipeMatcher() { }
}
