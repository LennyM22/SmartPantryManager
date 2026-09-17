package com.lenny.smartpantrymanager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MatchingRecipesActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    ListView listViewMatchingRecipes;

    ArrayList<Integer> recipeIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_matching_recipes);

        databaseHelper = new DatabaseHelper(this);
        listViewMatchingRecipes = findViewById(R.id.listViewMatchingRecipes);

        findMatchingRecipes();
    }

    private void findMatchingRecipes() {

        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> countries = new ArrayList<>();
        ArrayList<String> descriptions = new ArrayList<>();

        recipeIds.clear();

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor recipeCursor = db.rawQuery(
                "SELECT id, name, description, country FROM recipes",
                null
        );

        while (recipeCursor.moveToNext()) {

            int recipeId = recipeCursor.getInt(0);
            String recipeName = recipeCursor.getString(1);
            String description = recipeCursor.getString(2);
            String country = recipeCursor.getString(3);

            Cursor ingredientCursor = db.rawQuery(
                    "SELECT ingredient_name, quantity, unit " +
                            "FROM recipe_ingredients WHERE recipe_id = ?",
                    new String[]{String.valueOf(recipeId)}
            );

            boolean canMakeRecipe = true;

            while (ingredientCursor.moveToNext()) {

                String requiredIngredient =
                        ingredientCursor.getString(0).trim().toLowerCase();

                double requiredQuantity =
                        Double.parseDouble(
                                ingredientCursor.getString(1)
                        );

                String requiredUnit =
                        normaliseUnit(
                                ingredientCursor.getString(2)
                        );

                Cursor pantryCursor = db.rawQuery(
                        "SELECT quantity, unit FROM ingredients " +
                                "WHERE LOWER(TRIM(name)) = ?",
                        new String[]{requiredIngredient}
                );

                boolean ingredientFound = false;

                while (pantryCursor.moveToNext()) {

                    double pantryQuantity =
                            pantryCursor.getDouble(0);

                    String pantryUnit =
                            normaliseUnit(
                                    pantryCursor.getString(1)
                            );

                    if (unitsCanBeCompared(requiredUnit, pantryUnit)) {

                        double convertedQuantity =
                                convertQuantity(
                                        pantryQuantity,
                                        pantryUnit,
                                        requiredUnit
                                );

                        if (convertedQuantity >= requiredQuantity) {
                            ingredientFound = true;
                            break;
                        }
                    }
                }

                pantryCursor.close();

                if (!ingredientFound) {

                    canMakeRecipe = false;
                    break;
                }
            }

            ingredientCursor.close();

            if (canMakeRecipe) {

                recipeIds.add(recipeId);
                names.add(recipeName);
                countries.add(country);
                descriptions.add(description);
            }
        }

        recipeCursor.close();
        db.close();

        if (names.isEmpty()) {

            names.add("No recipes available");
            countries.add("");
            descriptions.add(
                    "You do not currently have all the required ingredients and quantities."
            );
        }

        RecipeAdapter adapter = new RecipeAdapter(
                this,
                names,
                countries,
                descriptions
        );

        listViewMatchingRecipes.setAdapter(adapter);

        listViewMatchingRecipes.setOnItemClickListener(
                (parent, view, position, id) -> {

                    if (!recipeIds.isEmpty()
                            && position < recipeIds.size()) {

                        Intent intent = new Intent(
                                MatchingRecipesActivity.this,
                                RecipeDetailsActivity.class
                        );

                        intent.putExtra(
                                "recipe_id",
                                recipeIds.get(position)
                        );

                        startActivity(intent);
                    }
                }
        );
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