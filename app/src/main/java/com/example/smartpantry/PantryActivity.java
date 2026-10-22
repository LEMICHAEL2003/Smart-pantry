package com.example.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

public class PantryActivity extends BaseActivity {
    private ListView list;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        heading("My pantry", "Tap an ingredient to edit or delete it.");
        makeColumn(false);
        button("+ Add ingredient", v -> startActivity(new Intent(this, EditIngredientActivity.class)));
        TextView empty = text("Your pantry is empty. Add your first ingredient to get started.");
        list = new ListView(this);
        column.addView(list, new LinearLayout.LayoutParams(-1, 0, 1));
        list.setEmptyView(empty);
        list.setOnItemClickListener((parent, view, position, id) -> {
            PantryItem item = (PantryItem) parent.getItemAtPosition(position);
            startActivity(new Intent(this, EditIngredientActivity.class).putExtra("item_id", item.id));
        });
    }

    @Override protected void onResume() {
        super.onResume();
        boolean alerts = getSharedPreferences("settings", MODE_PRIVATE).getBoolean("expiry_alerts", true);
        list.setAdapter(new PantryAdapter(this, database.getPantry(), alerts));
    }
}
