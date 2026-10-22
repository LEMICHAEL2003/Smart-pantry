# Smart Pantry Manager

A small Java Android app for keeping track of leftover ingredients and finding recipes you can make with them. The interface uses five Activities, ordinary Android layouts and a ListView with a custom adapter. No location or internet permissions are requested.

Repository: https://github.com/LEMICHAEL2003/Smart-pantry

## Open and run

1. Extract the ZIP and open the **SmartPantryManager** folder in Android Studio. If you cloned GitHub instead, first run `powershell -ExecutionPolicy Bypass -File Restore-Wrapper.ps1` in the project folder. The connector publishes text files, so the official wrapper JAR is stored losslessly as Base64 there; the ZIP already contains the binary JAR.
2. Choose **JDK 17** as the Gradle JDK under Settings > Build, Execution, Deployment > Build Tools > Gradle. The Java 8 runtime on the development computer is not enough to run this Android Gradle build.
3. Install **Android SDK Platform 35** and accept any build-tool installation prompts.
4. Let Gradle sync. The included wrapper downloads Gradle 8.9; the first build needs internet access to fetch build dependencies.
5. Select an emulator or phone running Android 8.0 / API 26 or later, then press Run.

Command-line alternative after configuring JDK 17 and the SDK:

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug
```

On macOS/Linux, run `chmod +x gradlew` once, then `./gradlew testDebugUnitTest assembleDebug`.

If Android Studio asks for an SDK location, select your installed SDK. It normally creates `local.properties` automatically. Do not commit this machine-specific file.

The project uses Android Gradle Plugin 8.7.3, Gradle 8.9, compile SDK 35 and target SDK 34. This is a coursework project, not a Play Store release configuration. AGP compatibility is documented at https://developer.android.com/build/releases/agp-8-7-0-release-notes.

## What is included

- Pantry list: view saved ingredients, quantities, units and optional expiry dates.
- Add/edit form: validation, save, cancel and confirmed deletion.
- Suggested recipes: only complete matches, with a helpful empty state.
- Recipe details: every required ingredient and preparation steps.
- Settings: persist the option to highlight items expiring within three days.
- Twenty recipes, seeded into SQLite on database creation.
- Eighteen JUnit tests for the strict matching rule.

## Why SQLite

The pantry belongs to one device and must work offline. SQLiteOpenHelper gives this project persistent storage without accounts, a server or cloud configuration. `PantryDatabase` handles database creation, recipe seeding and pantry CRUD. Queries with user-controlled identifiers use selection arguments. Cursors and asset streams are closed with try-with-resources.

`SharedPreferences` stores the expiry highlighting preference. Pantry and recipe records live in SQLite, not in preferences or temporary lists.

## Matching rules

`RecipeMatcher` first totals the available pantry ingredients, then totals the recipe requirements. A recipe qualifies only when every required total is available.

- Ingredient names ignore case and repeated whitespace. A small explicit alias table handles common plurals such as tomatoes and eggs. It does not blindly remove the last letter from words.
- Separate batches with the same ingredient name and compatible units are added together.
- Grams and kilograms share a mass total. Millilitres, litres, teaspoons, tablespoons and cups share a volume total.
- A teaspoon is 5 ml, a tablespoon is 15 ml and a cup is 250 ml in this app.
- Pieces and slices have separate totals. Weight is never converted to volume or pieces without a known ingredient-specific conversion.
- Items with an expiry before today are excluded. Items dated today count. A blank expiry means no recorded expiry.
- Water, oil and other ingredients are required when listed; there are no free or assumed pantry ingredients.
- Each recipe is checked independently. Showing two recipes does not mean both can be cooked from the same stock. Cooking does not deduct quantities automatically.

For beans and chickpeas, recipes use cooked, drained weight. Enter these ingredients in that form. Other preparation states and ingredient synonyms beyond the explicit alias table are not inferred.

## A quick demonstration

1. Begin with an empty pantry. Recipes should show the empty-state message.
2. Add `Eggs`, quantity `2`, unit `pcs`. Scrambled eggs should still be absent.
3. Add `Butter`, quantity `0.01`, unit `kg`. Scrambled eggs should appear because 0.01 kg equals 10 g.
4. Open the recipe and read its ingredients and steps.
5. Edit butter to `0.009` kg. Scrambled eggs should disappear.
6. Restore butter to `0.01` kg, close the app and reopen it. Both pantry entries should remain.
7. Delete butter using the confirmation dialog. Scrambled eggs should disappear again.
8. Try saving a blank name, zero quantity and an impossible date. Each should show an error without adding a record.

## Source guide

Java files are under `app/src/main/java/com/example/smartpantry/`.

| File | Responsibility |
| --- | --- |
| `PantryActivity` | Reads the saved pantry in onResume and opens the edit form using an Intent |
| `EditIngredientActivity` | Validates entries, saves changes and confirms deletion |
| `SuggestedRecipesActivity` | Reloads pantry data and applies strict matching in onResume |
| `RecipeDetailActivity` | Reads the selected recipe ID from an Intent and displays its method |
| `SettingsActivity` | Saves the expiry label preference |
| `BaseActivity` | Shares the navigation and small UI helpers |
| `PantryAdapter` | Binds each PantryItem to a reusable ListView row |
| `PantryDatabase` | Creates tables, seeds recipes and performs persistent CRUD |
| `RecipeMatcher`, `IngredientNames`, `Units` | Plain Java matching logic with no Android dependency |

The small screen contents are built with LinearLayout inside a shared XML screen shell; row layouts are XML. This keeps the example compact without adding a UI framework.

## Validation status

The actual plain Java matching classes and JUnit suite were compiled locally with Eclipse ECJ and run under Java 8: **18 tests passed**. All production Java files also passed a source compilation check against Android API 35 with a temporary resource-ID class. XML, recipe seed data and the SQL schema were checked separately. See `CHECKS.md` for details and the device checklist.

On 22 October 2026 the sources and resources were also compiled with Android SDK tools to produce a signed debug APK. The APK signature was verified and the 18 JUnit tests passed again under the JDK bundled with Android Studio. A Gradle build and device execution are still unverified. The installed emulator reported a missing acceleration driver; software-mode attempts also exited. See ASSISTANCE.md for provenance and limitations.

## Coursework evidence still needed

This is the source-code deliverable, not the complete assignment submission. The repository above contains the project import. Actual running-app screenshots and a narrated 5–7 minute video are still needed; a report draft is supplied separately. The brief requests at least ten genuine incremental development commits. File-by-file import commits are explicitly labelled as imports and are not evidence of incremental development. No history has been fabricated here.

Read and adapt the code, verify it on a device and follow your institution's rules on acknowledging assistance. Record your own explanations and observations. Do not claim invented development challenges or sign an inaccurate originality declaration.

## References

Android Developers (n.d.) *SQLiteOpenHelper*. Available at: https://developer.android.com/reference/android/database/sqlite/SQLiteOpenHelper (Accessed: 22 October 2026).

Android Developers (n.d.) *Activity*. Available at: https://developer.android.com/reference/android/app/Activity (Accessed: 22 October 2026).

Android Developers (n.d.) *ArrayAdapter*. Available at: https://developer.android.com/reference/android/widget/ArrayAdapter (Accessed: 22 October 2026).

Android Developers (2024) *Android Gradle plugin 8.7*. Available at: https://developer.android.com/build/releases/agp-8-7-0-release-notes (Accessed: 22 October 2026).

Gradle (2024) *Gradle Wrapper*. Launcher scripts and wrapper JAR included from Gradle v8.9.0: https://github.com/gradle/gradle/tree/v8.9.0/gradle/wrapper. These files retain their upstream licensing notices.
