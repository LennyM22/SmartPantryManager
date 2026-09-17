package com.lenny.smartpantrymanager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button btnPantry;
    Button btnRecipes;
    Button btnAddIngredient;
    Button btnFavorites;
    Button btnMatchingRecipes;

    TextView tvPantryCount;
    TextView tvExpiringCount;
    TextView tvExpiredCount;
    TextView tvMatchingCount;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        btnPantry = findViewById(R.id.btnPantry);
        btnRecipes = findViewById(R.id.btnRecipes);
        btnAddIngredient = findViewById(R.id.btnAddIngredient);
        btnFavorites = findViewById(R.id.btnFavorites);
        btnMatchingRecipes = findViewById(R.id.btnMatchingRecipes);

        tvPantryCount = findViewById(R.id.tvPantryCount);
        tvExpiringCount = findViewById(R.id.tvExpiringCount);
        tvExpiredCount = findViewById(R.id.tvExpiredCount);
        tvMatchingCount = findViewById(R.id.tvMatchingCount);

        btnPantry.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    PantryActivity.class
            );
            startActivity(intent);
        });

        btnRecipes.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    RecipesActivity.class
            );
            startActivity(intent);
        });

        btnAddIngredient.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    AddIngredientActivity.class
            );
            startActivity(intent);
        });

        btnFavorites.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    FavoritesActivity.class
            );
            startActivity(intent);
        });

        btnMatchingRecipes.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    MatchingRecipesActivity.class
            );
            startActivity(intent);
        });

        updateSummary();
    }

    private void updateSummary() {

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor ingredientCursor = db.rawQuery(
                "SELECT expiry_date FROM ingredients",
                null
        );

        int ingredientCount = 0;
        int expiringCount = 0;
        int expiredCount = 0;

        SimpleDateFormat dateFormat =
                new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                );

        dateFormat.setLenient(false);

        Date today = new Date();

        while (ingredientCursor.moveToNext()) {

            ingredientCount++;

            String expiryDateText =
                    ingredientCursor.getString(0);

            if (expiryDateText != null
                    && !expiryDateText.isEmpty()) {

                try {

                    Date expiryDate =
                            dateFormat.parse(expiryDateText);

                    long difference =
                            expiryDate.getTime()
                                    - today.getTime();

                    long daysRemaining =
                            difference
                                    / (1000 * 60 * 60 * 24);

                    if (expiryDate.before(today)) {

                        expiredCount++;

                    } else if (daysRemaining <= 3) {

                        expiringCount++;
                    }

                } catch (ParseException e) {
                }
            }
        }

        ingredientCursor.close();

        Cursor recipeCursor = db.rawQuery(
                "SELECT id FROM recipes",
                null
        );

        int matchingCount = 0;

        while (recipeCursor.moveToNext()) {

            int recipeId =
                    recipeCursor.getInt(0);

            Cursor requiredCursor = db.rawQuery(
                    "SELECT ingredient_name, quantity, unit " +
                            "FROM recipe_ingredients " +
                            "WHERE recipe_id = ?",
                    new String[]{
                            String.valueOf(recipeId)
                    }
            );

            boolean canMake = true;

            while (requiredCursor.moveToNext()) {

                String requiredIngredient =
                        requiredCursor
                                .getString(0)
                                .trim()
                                .toLowerCase();

                double requiredQuantity =
                        Double.parseDouble(
                                requiredCursor.getString(1)
                        );

                String requiredUnit =
                        normaliseUnit(
                                requiredCursor.getString(2)
                        );

                Cursor pantryCursor = db.rawQuery(
                        "SELECT quantity, unit " +
                                "FROM ingredients " +
                                "WHERE LOWER(TRIM(name)) = ?",
                        new String[]{
                                requiredIngredient
                        }
                );

                boolean ingredientFound = false;

                while (pantryCursor.moveToNext()) {

                    double pantryQuantity =
                            pantryCursor.getDouble(0);

                    String pantryUnit =
                            normaliseUnit(
                                    pantryCursor.getString(1)
                            );

                    if (unitsCanBeCompared(
                            requiredUnit,
                            pantryUnit)) {

                        double convertedQuantity =
                                convertQuantity(
                                        pantryQuantity,
                                        pantryUnit,
                                        requiredUnit
                                );

                        if (convertedQuantity
                                >= requiredQuantity) {

                            ingredientFound = true;
                            break;
                        }
                    }
                }

                pantryCursor.close();

                if (!ingredientFound) {
                    canMake = false;
                    break;
                }
            }

            requiredCursor.close();

            if (canMake) {
                matchingCount++;
            }
        }

        recipeCursor.close();
        db.close();

        tvPantryCount.setText(
                String.valueOf(ingredientCount)
        );

        tvExpiringCount.setText(
                String.valueOf(expiringCount)
        );

        tvExpiredCount.setText(
                String.valueOf(expiredCount)
        );

        if (matchingCount == 1) {

            tvMatchingCount.setText(
                    "1 recipe available"
            );

        } else {

            tvMatchingCount.setText(
                    matchingCount + " recipes available"
            );
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null) {
            updateSummary();
        }
    }

    private String normaliseUnit(String unit) {

        if (unit == null) {
            return "";
        }

        unit = unit.trim().toLowerCase();

        if (unit.equals("gram")
                || unit.equals("grams")
                || unit.equals("g")) {
            return "g";
        }

        if (unit.equals("kilogram")
                || unit.equals("kilograms")
                || unit.equals("kg")) {
            return "kg";
        }

        if (unit.equals("millilitre")
                || unit.equals("millilitres")
                || unit.equals("milliliter")
                || unit.equals("milliliters")
                || unit.equals("ml")) {
            return "ml";
        }

        if (unit.equals("litre")
                || unit.equals("litres")
                || unit.equals("liter")
                || unit.equals("liters")
                || unit.equals("l")) {
            return "l";
        }

        if (unit.equals("piece")
                || unit.equals("pieces")
                || unit.equals("pc")
                || unit.equals("pcs")) {
            return "piece";
        }

        if (unit.equals("clove")
                || unit.equals("cloves")) {
            return "clove";
        }

        if (unit.equals("tablespoon")
                || unit.equals("tablespoons")
                || unit.equals("tbsp")) {
            return "tbsp";
        }

        if (unit.equals("cup")
                || unit.equals("cups")) {
            return "cup";
        }

        if (unit.equals("loaf")
                || unit.equals("loaves")) {
            return "loaf";
        }

        return unit;
    }

    private boolean unitsCanBeCompared(
            String requiredUnit,
            String pantryUnit) {

        if (requiredUnit.equals(pantryUnit)) {
            return true;
        }

        if (requiredUnit.equals("g")
                && pantryUnit.equals("kg")) {
            return true;
        }

        if (requiredUnit.equals("kg")
                && pantryUnit.equals("g")) {
            return true;
        }

        if (requiredUnit.equals("ml")
                && pantryUnit.equals("l")) {
            return true;
        }

        if (requiredUnit.equals("l")
                && pantryUnit.equals("ml")) {
            return true;
        }

        return false;
    }

    private double convertQuantity(
            double quantity,
            String fromUnit,
            String toUnit) {

        if (fromUnit.equals(toUnit)) {
            return quantity;
        }

        if (fromUnit.equals("kg")
                && toUnit.equals("g")) {
            return quantity * 1000;
        }

        if (fromUnit.equals("g")
                && toUnit.equals("kg")) {
            return quantity / 1000;
        }

        if (fromUnit.equals("l")
                && toUnit.equals("ml")) {
            return quantity * 1000;
        }

        if (fromUnit.equals("ml")
                && toUnit.equals("l")) {
            return quantity / 1000;
        }

        return quantity;
    }
}
