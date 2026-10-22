package com.example.smartpantry;

import android.os.Bundle;
import java.time.LocalDate;

public class RecipeDetailActivity extends BaseActivity {
    private Recipe recipe;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        recipe = database.getRecipe(getIntent().getLongExtra("recipe_id", -1));
        if (recipe == null) { finish(); return; }
        heading(recipe.name, "One small batch. Water, seasoning and oil are only needed when listed.");
        makeColumn(true);
        text("INGREDIENTS").setTypeface(null, android.graphics.Typeface.BOLD);
        for (PantryItem ingredient : recipe.ingredients) text("• " + ingredient.name + " — " + amount(ingredient));
        text("METHOD").setTypeface(null, android.graphics.Typeface.BOLD);
        text(recipe.steps);
        text("Each suggestion is checked separately. Cooking does not automatically deduct pantry quantities.");
        button("Back", v -> finish());
    }

    @Override protected void onResume() {
        super.onResume();
        if (recipe != null && !RecipeMatcher.canMake(recipe, database.getPantry(), LocalDate.now())) {
            android.widget.Toast.makeText(this, "Your pantry changed. This recipe is no longer available.", android.widget.Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
