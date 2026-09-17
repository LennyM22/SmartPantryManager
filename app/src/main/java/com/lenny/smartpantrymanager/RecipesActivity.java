package com.lenny.smartpantrymanager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class RecipesActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    ListView listViewRecipes;
    EditText etSearchRecipe;
    Spinner spinnerCategory;
    ImageButton btnSearchRecipe;

    Button btnAllRecipes;
    Button btnSouthAfrica;
    Button btnZimbabwe;
    Button btnInternational;

    String selectedCountry = "";
    String selectedCategory = "All Categories";

    ArrayList<Integer> recipeIds = new ArrayList<>();
    ArrayList<String> names = new ArrayList<>();
    ArrayList<String> countries = new ArrayList<>();
    ArrayList<String> categories = new ArrayList<>();
    ArrayList<String> descriptions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recipes);

        databaseHelper = new DatabaseHelper(this);

        listViewRecipes = findViewById(R.id.listViewRecipes);
        etSearchRecipe = findViewById(R.id.etSearchRecipe);
        spinnerCategory = findViewById(R.id.spinnerRecipeCategory);
        btnSearchRecipe = findViewById(R.id.btnSearchRecipe);

        btnAllRecipes = findViewById(R.id.btnAllRecipes);
        btnSouthAfrica = findViewById(R.id.btnSouthAfrica);
        btnZimbabwe = findViewById(R.id.btnZimbabwe);
        btnInternational = findViewById(R.id.btnInternational);

        String[] categoryList = {
                "All Categories",
                "Breakfast",
                "Quick Meal",
                "Pasta",
                "Curry",
                "Rice",
                "Mexican",
                "Chicken",
                "Seafood",
                "Salad",
                "Soup",
                "Stew",
                "Traditional"
        };

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categoryList
        );

        categoryAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerCategory.setAdapter(categoryAdapter);

        loadRecipes();

        btnSearchRecipe.setOnClickListener(v -> {

            if (etSearchRecipe.getVisibility() == View.GONE) {

                etSearchRecipe.setVisibility(View.VISIBLE);
                etSearchRecipe.requestFocus();

                InputMethodManager keyboard =
                        (InputMethodManager) getSystemService(
                                Context.INPUT_METHOD_SERVICE
                        );

                if (keyboard != null) {
                    keyboard.showSoftInput(
                            etSearchRecipe,
                            InputMethodManager.SHOW_IMPLICIT
                    );
                }

            } else {

                etSearchRecipe.setText("");
                etSearchRecipe.setVisibility(View.GONE);

                InputMethodManager keyboard =
                        (InputMethodManager) getSystemService(
                                Context.INPUT_METHOD_SERVICE
                        );

                if (keyboard != null) {
                    keyboard.hideSoftInputFromWindow(
                            etSearchRecipe.getWindowToken(),
                            0
                    );
                }

                loadRecipes();
            }
        });

        etSearchRecipe.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count) {

                loadRecipes();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        spinnerCategory.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        selectedCategory = categoryList[position];

                        loadRecipes();
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent) {
                    }
                }
        );

        btnAllRecipes.setOnClickListener(v -> {

            selectedCountry = "";

            loadRecipes();
        });

        btnSouthAfrica.setOnClickListener(v -> {

            selectedCountry = "South Africa";

            loadRecipes();
        });

        btnZimbabwe.setOnClickListener(v -> {

            selectedCountry = "Zimbabwe";

            loadRecipes();
        });

        btnInternational.setOnClickListener(v -> {

            selectedCountry = "International";

            loadRecipes();
        });

        listViewRecipes.setOnItemClickListener(
                (parent, view, position, id) -> {

                    if (position < recipeIds.size()) {

                        Intent intent = new Intent(
                                RecipesActivity.this,
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

    private void loadRecipes() {

        names.clear();
        countries.clear();
        categories.clear();
        descriptions.clear();
        recipeIds.clear();

        String searchText =
                etSearchRecipe.getText().toString().trim().toLowerCase();

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append(
                "SELECT id, name, description, country, category " +
                        "FROM recipes WHERE 1=1"
        );

        ArrayList<String> argumentList = new ArrayList<>();

        if (!searchText.isEmpty()) {

            queryBuilder.append(
                    " AND (" +
                            "LOWER(name) LIKE ? " +
                            "OR LOWER(country) LIKE ? " +
                            "OR LOWER(category) LIKE ?" +
                            ")"
            );

            argumentList.add("%" + searchText + "%");
            argumentList.add("%" + searchText + "%");
            argumentList.add("%" + searchText + "%");
        }

        if (!selectedCountry.isEmpty()) {

            if (selectedCountry.equals("International")) {

                queryBuilder.append(
                        " AND country NOT IN (?, ?)"
                );

                argumentList.add("South Africa");
                argumentList.add("Zimbabwe");

            } else {

                queryBuilder.append(
                        " AND country = ?"
                );

                argumentList.add(selectedCountry);
            }
        }

        if (!selectedCategory.equals("All Categories")) {

            queryBuilder.append(
                    " AND category = ?"
            );

            argumentList.add(selectedCategory);
        }

        queryBuilder.append(" ORDER BY name ASC");

        String[] arguments =
                argumentList.toArray(new String[0]);

        Cursor cursor = db.rawQuery(
                queryBuilder.toString(),
                arguments
        );

        while (cursor.moveToNext()) {

            recipeIds.add(cursor.getInt(0));
            names.add(cursor.getString(1));
            descriptions.add(cursor.getString(2));
            countries.add(cursor.getString(3));

            String category = cursor.getString(4);

            if (category == null || category.isEmpty()) {
                category = "Recipe";
            }

            categories.add(category);
        }

        cursor.close();
        db.close();

        if (names.isEmpty()) {

            names.add("No recipes found");
            countries.add("");
            categories.add("");
            descriptions.add(
                    "Try another search, country or category."
            );
        }

        RecipeAdapter adapter = new RecipeAdapter(
                this,
                names,
                countries,
                categories,
                descriptions
        );

        listViewRecipes.setAdapter(adapter);
    }
}
