# Latest verification on 22 October 2026

- All 18 matching tests passed again using javac and JUnit 4.13.2.
- Android resources were compiled and linked with aapt2 against API 35.
- Production Java compiled, D8 generated classes.dex, zipalign aligned the package, and apksigner signed and verified SmartPantry-debug.apk.
- APK inspection confirms package com.example.smartpantry, minimum API 26, target API 34 and PantryActivity as launcher.
- Runtime testing remains blocked by the emulator acceleration setup. No app screenshots or video have been captured.
- Android Gradle Plugin assembly and lint remain unverified.

The historical source checks and outstanding device checklist follow.

# Checks and device test guide

## Completed locally

- The five plain Java model/matching classes and `RecipeMatcherTest` compiled using Eclipse ECJ 3.26.0 with Java 8 compatibility.
- All 13 production Java files also compiled against the official Android API 35 android.jar using ECJ, with a temporary R class generated from the actual XML resource names. This checks Java syntax and Android API references; it does not assemble resources, run lint or build an APK.
- Every project XML file parsed successfully. All 20 recipe records have nonempty names, methods and ingredients, positive quantities and supported units.
- The exact CREATE TABLE statements were executed in Python SQLite. All recipe/ingredient seed inserts and a SQL create/read/update/delete cycle passed. This does not test Android SQLiteOpenHelper itself.
- JUnit 4.13.2 reported `OK (18 tests)`.
- Covered missing ingredients, insufficient amounts, exact amounts, surplus ingredients, kg/g conversion in both directions, litre/cup conversion, spoon conversion, case/spacing/plurals, duplicate pantry batches, repeated recipe requirements, incompatible dimensions, slices/pieces, expiry, empty pantry and empty recipes.

## Run in Android Studio

Use JDK 17, SDK Platform 35 and the supplied Gradle wrapper. Run:

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug lintDebug
```

The following checks require the running Android app and have not been completed here:

| Check | Expected result |
| --- | --- |
| Fresh install | Empty pantry; recipe suggestions show a useful message |
| Add valid item | Item appears in pantry with saved quantity/unit |
| Blank name or zero/negative quantity | Validation prevents saving |
| Bad date, including 2026-02-30 | Validation prevents saving |
| Edit item | One existing row changes; no duplicate created |
| Delete / cancel deletion | Confirmation deletes; Keep leaves data intact |
| Force-stop and reopen | Pantry items and settings remain |
| Eggs 2 pcs without butter | Scrambled eggs absent |
| Add butter 0.01 kg | Scrambled eggs present |
| Reduce butter to 0.009 kg | Scrambled eggs absent |
| Expired butter | Scrambled eggs absent, even with alerts off |
| Tap suggested recipe | Correct ingredients and method appear |
| Change stock via bottom navigation, then return | Suggestions refresh; stale recipe details close |
| Toggle highlighting and reopen app | Setting persists; pantry labels reflect it |
| Rotate add/edit form after typing | Text and unit selection survive recreation |
| Small phone and larger font | Form scrolls; buttons and list remain usable |
| Back / bottom navigation | All five screens remain reachable without a crash |

Capture real screenshots while doing these checks. Keep the output APK and build caches out of the final submission ZIP unless specifically requested.
