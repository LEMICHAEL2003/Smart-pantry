package com.example.smartpantry;

import android.os.Bundle;
import android.widget.Switch;

public class SettingsActivity extends BaseActivity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        heading("Settings", "Small changes, less waste.");
        makeColumn(true);
        Switch alerts = new Switch(this);
        alerts.setText("Highlight ingredients expiring soon");
        alerts.setMinHeight((int)(56 * getResources().getDisplayMetrics().density));
        alerts.setChecked(getSharedPreferences("settings", MODE_PRIVATE).getBoolean("expiry_alerts", true));
        alerts.setOnCheckedChangeListener((button, checked) -> getSharedPreferences("settings", MODE_PRIVATE)
            .edit().putBoolean("expiry_alerts", checked).apply());
        column.addView(alerts);
        text("Shows a 'Use soon' label for items expiring today or within 3 days. These are in-app labels, not phone notifications.");
        text("Expired items are always excluded from recipes, even when highlighting is off.");
        text("Units: 1 kg = 1000 g; 1 l = 1000 ml; 1 tsp = 5 ml; 1 tbsp = 15 ml; 1 cup = 250 ml. Pieces and slices are counted separately.");
        text("Mass and volume are not interchangeable. For example, 100 g of milk cannot satisfy 100 ml. Use compatible units.");
        text("Beans and chickpeas in the recipe collection mean cooked, drained beans and chickpeas. Enter their cooked weight.");
        text("Your pantry is stored on this device using SQLite. No account or internet connection is needed.");
    }
}
