package com.example.smartpantry;

import org.junit.Test;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import static org.junit.Assert.*;

public class RecipeMatcherTest {
    private final LocalDate today = LocalDate.of(2026, 10, 22);

    private PantryItem item(String name, double quantity, String unit) {
        return new PantryItem(-1, name, quantity, unit, "");
    }
    private Recipe recipe(PantryItem... ingredients) {
        Recipe recipe = new Recipe(1, "Test recipe", "Test method");
        recipe.ingredients.addAll(Arrays.asList(ingredients));
        return recipe;
    }
    private boolean matches(Recipe recipe, PantryItem... pantry) {
        return RecipeMatcher.canMake(recipe, Arrays.asList(pantry), today);
    }

    @Test public void everyIngredientMustBePresent() {
        assertFalse(matches(recipe(item("egg", 2, "pcs"), item("butter", 10, "g")), item("egg", 2, "pcs")));
    }
    @Test public void notEnoughQuantityIsExcluded() {
        assertFalse(matches(recipe(item("butter", 10, "g")), item("butter", 9.99, "g")));
    }
    @Test public void exactQuantityIsIncluded() {
        assertTrue(matches(recipe(item("egg", 2, "pcs"), item("butter", 10, "g")), item("egg", 2, "pcs"), item("butter", 10, "g")));
    }
    @Test public void extraIngredientsDoNotPreventAMatch() {
        assertTrue(matches(recipe(item("egg", 2, "pcs")), item("egg", 3, "pcs"), item("milk", 20, "ml")));
    }
    @Test public void convertsKilogramsToGrams() {
        assertTrue(matches(recipe(item("potato", 300, "g")), item("potatoes", 0.3, "kg")));
    }
    @Test public void convertsGramsToKilograms() {
        assertTrue(matches(recipe(item("potato", 0.3, "kg")), item("potato", 300, "g")));
    }
    @Test public void convertsLitresAndCups() {
        assertTrue(matches(recipe(item("milk", 1, "cup")), item("milk", 0.25, "l")));
    }
    @Test public void convertsSpoons() {
        assertTrue(matches(recipe(item("olive oil", 1, "tbsp")), item("olive oil", 3, "tsp")));
    }
    @Test public void cleansSpacesCaseAndPlurals() {
        assertTrue(matches(recipe(item("tomato", 2, "pcs")), item("  TOMATOES  ", 2, "pcs")));
        assertEquals("olive oil", IngredientNames.normalise(" OLIVE   OIL "));
    }
    @Test public void combinesPantryBatchesInDifferentUnits() {
        assertTrue(matches(recipe(item("potato", 300, "g")), item("potatoes", 0.1, "kg"), item("potato", 200, "g")));
    }
    @Test public void combinesRepeatedRecipeRequirements() {
        assertFalse(matches(recipe(item("butter", 10, "g"), item("butter", 10, "g")), item("butter", 15, "g")));
    }
    @Test public void doesNotConvertWeightToVolume() {
        assertFalse(matches(recipe(item("milk", 100, "ml")), item("milk", 100, "g")));
    }
    @Test public void doesNotConvertSlicesToPieces() {
        assertFalse(matches(recipe(item("bread", 2, "slice")), item("bread", 2, "pcs")));
    }
    @Test public void excludesExpiredBatches() {
        PantryItem expired = new PantryItem(-1, "egg", 2, "pcs", "2026-10-21");
        assertFalse(matches(recipe(item("egg", 2, "pcs")), expired));
    }
    @Test public void expiryTodayStillCounts() {
        PantryItem fresh = new PantryItem(-1, "egg", 2, "pcs", "2026-10-22");
        assertTrue(matches(recipe(item("egg", 2, "pcs")), fresh));
    }
    @Test public void expiredBatchCannotTopUpFreshBatch() {
        PantryItem expired = new PantryItem(-1, "egg", 1, "pcs", "2026-10-21");
        assertFalse(matches(recipe(item("egg", 2, "pcs")), expired, item("egg", 1, "pcs")));
    }
    @Test public void emptyPantryCannotMakeRecipe() {
        assertFalse(RecipeMatcher.canMake(recipe(item("egg", 1, "pcs")), Collections.emptyList(), today));
    }
    @Test public void emptyRecipeIsExcluded() {
        assertFalse(matches(recipe(), item("egg", 1, "pcs")));
    }
}
