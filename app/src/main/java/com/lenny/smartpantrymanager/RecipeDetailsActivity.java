package com.lenny.smartpantrymanager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailsActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;

    TextView tvRecipeName;
    TextView tvRecipeCountry;
    TextView tvRecipeDescription;
    TextView tvRecipeIngredients;
    TextView tvRecipeInstructions;

    Button btnFavourite;

    int recipeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recipe_details);

        databaseHelper = new DatabaseHelper(this);

        tvRecipeName = findViewById(R.id.tvRecipeName);
        tvRecipeCountry = findViewById(R.id.tvRecipeCountry);
        tvRecipeDescription = findViewById(R.id.tvRecipeDescription);
        tvRecipeIngredients = findViewById(R.id.tvRecipeIngredients);
        tvRecipeInstructions = findViewById(R.id.tvRecipeInstructions);
        btnFavourite = findViewById(R.id.btnFavourite);

        recipeId = getIntent().getIntExtra("recipe_id", -1);

        if (recipeId != -1) {
            loadRecipe();
            checkFavourite();
        } else {
            Toast.makeText(this, "Recipe not found", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnFavourite.setOnClickListener(v -> toggleFavourite());
    }

    private void loadRecipe() {

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor recipeCursor = db.rawQuery(
                "SELECT name, description, country, instructions " +
                        "FROM recipes WHERE id = ?",
                new String[]{String.valueOf(recipeId)}
        );

        if (recipeCursor.moveToFirst()) {

            String name = recipeCursor.getString(0);
            String description = recipeCursor.getString(1);
            String country = recipeCursor.getString(2);
            String instructions = recipeCursor.getString(3);

            tvRecipeName.setText(name);
            tvRecipeCountry.setText(country);
            tvRecipeDescription.setText(description);
            tvRecipeInstructions.setText(instructions);
        }

        recipeCursor.close();

        Cursor ingredientCursor = db.rawQuery(
                "SELECT ingredient_name, quantity, unit " +
                        "FROM recipe_ingredients WHERE recipe_id = ?",
                new String[]{String.valueOf(recipeId)}
        );

        StringBuilder ingredients = new StringBuilder();

        while (ingredientCursor.moveToNext()) {

            String name = ingredientCursor.getString(0);
            String quantity = ingredientCursor.getString(1);
            String unit = ingredientCursor.getString(2);

            ingredients.append("• ")
                    .append(name)
                    .append(" - ")
                    .append(quantity)
                    .append(" ")
                    .append(unit)
                    .append("\n");
        }

        tvRecipeIngredients.setText(ingredients.toString());

        ingredientCursor.close();
        db.close();
    }

    private void checkFavourite() {

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id FROM favorites WHERE recipe_id = ?",
                new String[]{String.valueOf(recipeId)}
        );

        if (cursor.moveToFirst()) {
            btnFavourite.setText("REMOVE FROM FAVOURITES");
        } else {
            btnFavourite.setText("ADD TO FAVOURITES");
        }

        cursor.close();
        db.close();
    }

    private void toggleFavourite() {

        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id FROM favorites WHERE recipe_id = ?",
                new String[]{String.valueOf(recipeId)}
        );

        if (cursor.moveToFirst()) {

            db.delete(
                    "favorites",
                    "recipe_id = ?",
                    new String[]{String.valueOf(recipeId)}
            );

            btnFavourite.setText("ADD TO FAVOURITES");

            Toast.makeText(
                    this,
                    "Removed from favourites",
                    Toast.LENGTH_SHORT
            ).show();

        } else {

            ContentValues values = new ContentValues();
            values.put("recipe_id", recipeId);

            db.insert("favorites", null, values);

            btnFavourite.setText("REMOVE FROM FAVOURITES");

            Toast.makeText(
                    this,
                    "Added to favourites",
                    Toast.LENGTH_SHORT
            ).show();
        }

        cursor.close();
        db.close();
    }
}