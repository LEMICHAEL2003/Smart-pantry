package com.example.smartpantry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PantryDatabase extends SQLiteOpenHelper {
    private final Context context;

    public PantryDatabase(Context context) {
        super(context.getApplicationContext(), "pantry.db", null, 1);
        this.context = context.getApplicationContext();
    }

    @Override public void onConfigure(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE pantry (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, quantity REAL NOT NULL CHECK(quantity > 0), unit TEXT NOT NULL, expiry TEXT NOT NULL DEFAULT '')");
        db.execSQL("CREATE TABLE recipes (id INTEGER PRIMARY KEY, name TEXT NOT NULL, steps TEXT NOT NULL)");
        db.execSQL("CREATE TABLE recipe_ingredients (id INTEGER PRIMARY KEY AUTOINCREMENT, recipe_id INTEGER NOT NULL REFERENCES recipes(id) ON DELETE CASCADE, name TEXT NOT NULL, quantity REAL NOT NULL CHECK(quantity > 0), unit TEXT NOT NULL)");
        seedRecipes(db);
    }

    private void seedRecipes(SQLiteDatabase db) {
        // SQLiteOpenHelper already wraps onCreate in a transaction.
        try (InputStream input = context.getAssets().open("recipes.json")) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int count;
            while ((count = input.read(buffer)) != -1) bytes.write(buffer, 0, count);
            JSONArray recipes = new JSONArray(new String(bytes.toByteArray(), StandardCharsets.UTF_8));
            for (int i = 0; i < recipes.length(); i++) {
                JSONObject recipe = recipes.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put("id", i + 1);
                values.put("name", recipe.getString("name"));
                values.put("steps", recipe.getString("steps"));
                db.insertOrThrow("recipes", null, values);
                JSONArray ingredients = recipe.getJSONArray("ingredients");
                for (int j = 0; j < ingredients.length(); j++) {
                    JSONObject ingredient = ingredients.getJSONObject(j);
                    values = new ContentValues();
                    values.put("recipe_id", i + 1);
                    values.put("name", ingredient.getString("name"));
                    values.put("quantity", ingredient.getDouble("quantity"));
                    values.put("unit", ingredient.getString("unit"));
                    db.insertOrThrow("recipe_ingredients", null, values);
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("Could not load the recipe collection", e);
        }
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Add a data-preserving migration here when the schema version changes.
        throw new IllegalStateException("No migration from " + oldVersion + " to " + newVersion);
    }

    public List<PantryItem> getPantry() {
        List<PantryItem> items = new ArrayList<>();
        try (Cursor c = getReadableDatabase().query("pantry", null, null, null, null, null, "name COLLATE NOCASE, id")) {
            while (c.moveToNext()) items.add(readItem(c));
        }
        return items;
    }

    private PantryItem readItem(Cursor c) {
        return new PantryItem(c.getLong(c.getColumnIndexOrThrow("id")),
            c.getString(c.getColumnIndexOrThrow("name")), c.getDouble(c.getColumnIndexOrThrow("quantity")),
            c.getString(c.getColumnIndexOrThrow("unit")), c.getString(c.getColumnIndexOrThrow("expiry")));
    }

    public PantryItem getItem(long id) {
        try (Cursor c = getReadableDatabase().query("pantry", null, "id=?", new String[]{String.valueOf(id)}, null, null, null)) {
            return c.moveToFirst() ? readItem(c) : null;
        }
    }

    public void save(PantryItem item) {
        ContentValues values = new ContentValues();
        values.put("name", item.name);
        values.put("quantity", item.quantity);
        values.put("unit", item.unit);
        values.put("expiry", item.expiry);
        if (item.id == -1) {
            getWritableDatabase().insertOrThrow("pantry", null, values);
        } else if (getWritableDatabase().update("pantry", values, "id=?", new String[]{String.valueOf(item.id)}) != 1) {
            throw new IllegalStateException("Ingredient no longer exists");
        }
    }

    public void delete(long id) {
        getWritableDatabase().delete("pantry", "id=?", new String[]{String.valueOf(id)});
    }

    public List<Recipe> getRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        try (Cursor c = getReadableDatabase().rawQuery("SELECT id, name, steps FROM recipes ORDER BY name", null)) {
            while (c.moveToNext()) recipes.add(new Recipe(c.getLong(0), c.getString(1), c.getString(2)));
        }
        for (Recipe recipe : recipes) {
            try (Cursor c = getReadableDatabase().rawQuery("SELECT name, quantity, unit FROM recipe_ingredients WHERE recipe_id=? ORDER BY id", new String[]{String.valueOf(recipe.id)})) {
                while (c.moveToNext()) recipe.ingredients.add(new PantryItem(-1, c.getString(0), c.getDouble(1), c.getString(2), ""));
            }
        }
        return recipes;
    }

    public Recipe getRecipe(long id) {
        for (Recipe recipe : getRecipes()) if (recipe.id == id) return recipe;
        return null;
    }
}
