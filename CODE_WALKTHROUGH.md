# Understanding the project

## Screen flow

```text
Pantry -- Add / tap item --> Add or Edit Ingredient
  |                              |
  | <---- Save / Cancel / Delete --+
  |
  +-- Recipes --> Suggested Recipes -- tap --> Recipe Detail
  |
  +-- Settings --> Expiry highlighting
```

The bottom buttons appear on every screen. Android's Back action returns to the previous Activity. An explicit Intent starts each destination. `item_id` and `recipe_id` identify saved database records; the app does not copy whole database objects into Intent extras.

## Data model

```text
pantry                         recipes
  id (primary key)               id (primary key)
  name                           name
  quantity                       steps
  unit                             |
  expiry                           | one-to-many
                                   v
                               recipe_ingredients
                                 id (primary key)
                                 recipe_id (foreign key)
                                 name
                                 quantity
                                 unit
```

The pantry and recipe ingredients are deliberately independent. A recipe must continue to exist when a pantry ingredient is deleted. Ingredient names are normalised only for matching; the pantry retains the user's wording for display.

`SQLiteOpenHelper` creates `pantry.db` on first use. Its `onCreate` method creates the three tables and imports `recipes.json` within the helper's creation transaction. Subsequent launches reopen the same database instead of reseeding it. Increasing the database version later requires an explicit migration; the current project does not silently destroy existing data.

## Lifecycle and lists

Each screen builds its views in `onCreate`. The pantry and suggestions reload data in `onResume`, because that method runs again after returning from the edit screen. Without this refresh, a list could display old stock even though the database has changed.

`PantryAdapter.getView` fills two TextViews for each row. It reuses the row passed by the ListView when possible. The displayed objects come from a fresh database query. Editing and deletion use database IDs rather than row positions, so sorting the list does not change which record is modified.

The database helper is closed in `onDestroy`. Android may kill the process without calling onDestroy; saved data remains because each CRUD operation already writes to SQLite.

## Strict matching example

Suppose scrambled eggs require 2 eggs and 10 g butter. The pantry contains 2 eggs and 0.01 kg butter. The matcher normalises `eggs` to `egg` and converts butter to 10 g. Both totals satisfy the recipe, so it is suggested.

With 0.009 kg butter, the converted total is 9 g. The recipe is excluded. Having extra eggs cannot compensate for missing butter. An expired batch cannot fill the gap either.

Two pantry batches can contribute to the same total. The matcher also sums repeated ingredient entries in a recipe, so one 10 g batch cannot incorrectly satisfy two separate 10 g requirements.

## Design tradeoffs

- Native Activities and widgets keep the app small and the Java easy to follow.
- The collection is only 20 recipes, so straightforward queries and in-memory comparisons are adequate for this example. Large collections should move database work off the main thread and avoid querying ingredients once per recipe.
- The alias list is deliberately explicit. It handles common names without assuming that different ingredients are interchangeable.
- Expiry highlighting is an on-screen preference, not a background notification service.
- No stock deduction is performed when opening a recipe. Pantry quantities are changed through the edit form.

Use these notes to understand the implementation, then explain it in your own words while pointing to the actual code in your video.
