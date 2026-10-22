package com.example.smartpantry;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import java.text.DecimalFormat;

public abstract class BaseActivity extends Activity {
    protected PantryDatabase database;
    protected LinearLayout column;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        database = new PantryDatabase(this);
        setContentView(R.layout.activity_base);
        findViewById(R.id.nav_pantry).setOnClickListener(v -> navigate(PantryActivity.class));
        findViewById(R.id.nav_recipes).setOnClickListener(v -> navigate(SuggestedRecipesActivity.class));
        findViewById(R.id.nav_settings).setOnClickListener(v -> navigate(SettingsActivity.class));
    }

    private void navigate(Class<?> screen) {
        if (getClass() != screen) {
            startActivity(new Intent(this, screen).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
        }
    }

    protected void heading(String title, String hint) {
        ((TextView) findViewById(R.id.page_title)).setText(title);
        ((TextView) findViewById(R.id.page_hint)).setText(hint);
    }

    protected void makeColumn(boolean scroll) {
        column = new LinearLayout(this);
        column.setOrientation(LinearLayout.VERTICAL);
        FrameLayout content = findViewById(R.id.content);
        if (scroll) {
            ScrollView scroller = new ScrollView(this);
            scroller.setFillViewport(true);
            scroller.addView(column);
            content.addView(scroller);
        } else content.addView(column);
    }

    protected TextView text(String value) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(16);
        view.setPadding(0, 12, 0, 12);
        column.addView(view);
        return view;
    }

    protected Button button(String label, View.OnClickListener action) {
        Button button = new Button(this);
        button.setText(label);
        button.setAllCaps(false);
        button.setOnClickListener(action);
        column.addView(button);
        return button;
    }

    protected EditText field(String label, int id, int inputType) {
        TextView caption = text(label);
        EditText field = new EditText(this);
        field.setId(id);
        field.setSingleLine(true);
        field.setInputType(inputType);
        field.setMinHeight((int)(48 * getResources().getDisplayMetrics().density));
        caption.setLabelFor(id);
        column.addView(field);
        return field;
    }

    public static String amount(PantryItem item) {
        return new DecimalFormat("0.###").format(item.quantity) + " " + item.unit;
    }

    @Override protected void onDestroy() {
        database.close();
        super.onDestroy();
    }
}
