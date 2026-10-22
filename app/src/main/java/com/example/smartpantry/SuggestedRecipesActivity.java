package com.example.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SuggestedRecipesActivity extends BaseActivity {
    private ListView list;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        heading("Cook with what you have", "Every ingredient, in the right amount. Expired items are excluded.");
        makeColumn(false);
        TextView empty = text("No recipes match your pantry yet. Add more ingredients or check your quantities and units.");
        list = new ListView(this);
        column.addView(list, new LinearLayout.LayoutParams(-1, 0, 1));
        list.setEmptyView(empty);
        list.setOnItemClickListener((parent, view, position, id) -> {
            Recipe recipe = (Recipe) parent.getItemAtPosition(position);
            startActivity(new Intent(this, RecipeDetailActivity.class).putExtra("recipe_id", recipe.id));
        });
    }

    @Override protected void onResume() {
        super.onResume();
        List<PantryItem> pantry = database.getPantry();
        List<Recipe> matches = new ArrayList<>();
        for (Recipe recipe : database.getRecipes()) {
            if (RecipeMatcher.canMake(recipe, pantry, LocalDate.now())) matches.add(recipe);
        }
        list.setAdapter(new ArrayAdapter<Recipe>(this, R.layout.list_row, matches) {
            @Override public View getView(int position, View row, ViewGroup parent) {
                if (row == null) row = LayoutInflater.from(getContext()).inflate(R.layout.list_row, parent, false);
                Recipe recipe = getItem(position);
                ((TextView) row.findViewById(R.id.row_title)).setText(recipe.name);
                ((TextView) row.findViewById(R.id.row_description)).setText(recipe.ingredients.size() + " ingredients · Ready to make");
                return row;
            }
        });
    }
}
