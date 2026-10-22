package com.example.smartpantry;

import java.util.ArrayList;
import java.util.List;

public class Recipe {
    public final long id;
    public final String name;
    public final String steps;
    public final List<PantryItem> ingredients = new ArrayList<>();

    public Recipe(long id, String name, String steps) {
        this.id = id;
        this.name = name;
        this.steps = steps;
    }
}
