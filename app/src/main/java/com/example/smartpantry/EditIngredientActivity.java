package com.example.smartpantry;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.widget.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class EditIngredientActivity extends BaseActivity {
    private long itemId;
    private EditText name, quantity, expiry;
    private Spinner unit;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        itemId = getIntent().getLongExtra("item_id", -1);
        heading(itemId == -1 ? "Add ingredient" : "Edit ingredient", "Expiry is optional. Use YYYY-MM-DD.");
        makeColumn(true);
        name = field("Ingredient name", 1001, InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        quantity = field("Quantity", 1002, InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        text("Unit");
        unit = new Spinner(this);
        unit.setId(1003);
        unit.setContentDescription("Ingredient unit");
        unit.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Units.OPTIONS));
        column.addView(unit);
        expiry = field("Expiry date (optional)", 1004, InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_DATE);
        if (itemId != -1) {
            PantryItem item = database.getItem(itemId);
            if (item == null) { finish(); return; }
            name.setText(item.name);
            quantity.setText(Double.toString(item.quantity));
            expiry.setText(item.expiry);
            for (int i = 0; i < Units.OPTIONS.length; i++) if (Units.OPTIONS[i].equals(item.unit)) unit.setSelection(i);
        }
        button("Save ingredient", v -> save());
        button("Cancel", v -> finish());
        if (itemId != -1) button("Delete ingredient", v -> new AlertDialog.Builder(this)
            .setTitle("Delete ingredient?")
            .setMessage("This will remove the ingredient from your pantry.")
            .setNegativeButton("Keep", null)
            .setPositiveButton("Delete", (dialog, which) -> {
                database.delete(itemId);
                Toast.makeText(this, "Ingredient deleted", Toast.LENGTH_SHORT).show();
                finish();
            }).show());
    }

    private void save() {
        String itemName = name.getText().toString().trim().replaceAll("\\s+", " ");
        String date = expiry.getText().toString().trim();
        if (itemName.isEmpty() || itemName.length() > 60) {
            name.setError("Enter a name between 1 and 60 characters");
            name.requestFocus();
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(quantity.getText().toString().trim());
        } catch (NumberFormatException e) {
            quantity.setError("Enter a quantity, for example 250 or 0.5");
            quantity.requestFocus();
            return;
        }
        if (!Double.isFinite(amount) || amount <= 0 || amount > 1000000) {
            quantity.setError("Enter a quantity greater than 0 and at most 1000000");
            quantity.requestFocus();
            return;
        }
        if (!date.isEmpty()) {
            try {
                if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) throw new DateTimeParseException("Date format", date, 0);
                LocalDate.parse(date);
            } catch (DateTimeParseException e) {
                expiry.setError("Use a real date in YYYY-MM-DD format");
                expiry.requestFocus();
                return;
            }
        }
        try {
            database.save(new PantryItem(itemId, itemName, amount, unit.getSelectedItem().toString(), date));
            Toast.makeText(this, "Ingredient saved", Toast.LENGTH_SHORT).show();
            finish();
        } catch (android.database.SQLException | IllegalStateException e) {
            Toast.makeText(this, "Could not save. Please reopen this form and try again.", Toast.LENGTH_LONG).show();
        }
    }
}
