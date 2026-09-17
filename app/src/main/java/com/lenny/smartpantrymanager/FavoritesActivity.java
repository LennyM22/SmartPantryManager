package com.lenny.smartpantrymanager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class FavoritesActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    ListView listViewFavorites;

    ArrayList<Integer> recipeIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_favorites);

        databaseHelper = new DatabaseHelper(this);
        listViewFavorites = findViewById(R.id.listViewFavorites);

        loadFavorites();
    }

    private void loadFavorites() {

        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> countries = new ArrayList<>();
        ArrayList<String> descriptions = new ArrayList<>();

        recipeIds.clear();

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT recipes.id, recipes.name, recipes.country, recipes.description " +
                        "FROM recipes " +
                        "INNER JOIN favorites ON recipes.id = favorites.recipe_id",
                null
        );

        while (cursor.moveToNext()) {

            recipeIds.add(cursor.getInt(0));
            names.add("♥ " + cursor.getString(1));
            countries.add(cursor.getString(2));
            descriptions.add(cursor.getString(3));
        }

        cursor.close();
        db.close();

        if (names.isEmpty()) {

            names.add("No favourite recipes yet");
            countries.add("");
            descriptions.add(
                    "Open a recipe and tap ADD TO FAVOURITES."
            );
        }

        RecipeAdapter adapter = new RecipeAdapter(
                this,
                names,
                countries,
                descriptions
        );

        listViewFavorites.setAdapter(adapter);

        listViewFavorites.setOnItemClickListener((parent, view, position, id) -> {

            if (!recipeIds.isEmpty() && position < recipeIds.size()) {

                Intent intent = new Intent(
                        FavoritesActivity.this,
                        RecipeDetailsActivity.class
                );

                intent.putExtra("recipe_id", recipeIds.get(position));

                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null && listViewFavorites != null) {
            loadFavorites();
        }
    }
}