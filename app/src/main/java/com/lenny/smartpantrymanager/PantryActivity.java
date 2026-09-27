package com.lenny.smartpantrymanager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PantryActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;

    ListView listViewIngredients;

    EditText etPantrySearch;

    ImageButton btnPantrySearch;

    Spinner spinnerPantryCategory;

    TextView tvPantryCount;
    TextView tvTotalIngredients;
    TextView tvExpiringSoon;
    TextView tvExpiredIngredients;

    String selectedCategory = "All Categories";
    String selectedExpiryFilter = "ALL";

    ArrayList<Integer> ingredientIds = new ArrayList<>();
    ArrayList<String> names = new ArrayList<>();
    ArrayList<String> quantities = new ArrayList<>();
    ArrayList<String> categories = new ArrayList<>();
    ArrayList<String> expiryDates = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pantry);

        databaseHelper = new DatabaseHelper(this);

        listViewIngredients =
                findViewById(R.id.listViewIngredients);

        etPantrySearch =
                findViewById(R.id.etPantrySearch);

        btnPantrySearch =
                findViewById(R.id.btnPantrySearch);

        spinnerPantryCategory =
                findViewById(R.id.spinnerPantryCategory);

        tvPantryCount =
                findViewById(R.id.tvPantryCount);

        tvTotalIngredients =
                findViewById(R.id.tvTotalIngredients);

        tvExpiringSoon =
                findViewById(R.id.tvExpiringSoon);

        tvExpiredIngredients =
                findViewById(R.id.tvExpiredIngredients);

        setupCategorySpinner();

        tvTotalIngredients.setOnClickListener(v -> {

            if (selectedExpiryFilter.equals("FRESH")) {

                selectedExpiryFilter = "ALL";

            } else {

                selectedExpiryFilter = "FRESH";
            }

            loadIngredients();
        });

        tvExpiringSoon.setOnClickListener(v -> {

            if (selectedExpiryFilter.equals("EXPIRING")) {

                selectedExpiryFilter = "ALL";

            } else {

                selectedExpiryFilter = "EXPIRING";
            }

            loadIngredients();
        });

        tvExpiredIngredients.setOnClickListener(v -> {

            if (selectedExpiryFilter.equals("EXPIRED")) {

                selectedExpiryFilter = "ALL";

            } else {

                selectedExpiryFilter = "EXPIRED";
            }

            loadIngredients();
        });

        btnPantrySearch.setOnClickListener(v -> {

            if (etPantrySearch.getVisibility()
                    == View.GONE) {

                etPantrySearch.setVisibility(
                        View.VISIBLE
                );

                etPantrySearch.requestFocus();

                InputMethodManager keyboard =
                        (InputMethodManager)
                                getSystemService(
                                        Context.INPUT_METHOD_SERVICE
                                );

                if (keyboard != null) {

                    keyboard.showSoftInput(
                            etPantrySearch,
                            InputMethodManager.SHOW_IMPLICIT
                    );
                }

            } else {

                etPantrySearch.setText("");

                etPantrySearch.setVisibility(
                        View.GONE
                );

                InputMethodManager keyboard =
                        (InputMethodManager)
                                getSystemService(
                                        Context.INPUT_METHOD_SERVICE
                                );

                if (keyboard != null) {

                    keyboard.hideSoftInputFromWindow(
                            etPantrySearch.getWindowToken(),
                            0
                    );
                }
            }
        });

        etPantrySearch.addTextChangedListener(
                new TextWatcher() {

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

                        loadIngredients();
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }
                }
        );

        spinnerPantryCategory.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        selectedCategory =
                                parent.getItemAtPosition(
                                        position
                                ).toString();

                        loadIngredients();
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent) {
                    }
                }
        );

        loadIngredients();
    }

    private void setupCategorySpinner() {

        String[] categoriesList = {

                "All Categories",
                "Meat",
                "Poultry",
                "Fish & Seafood",
                "Eggs",
                "Dairy",
                "Vegetables",
                "Fruits",
                "Grains & Cereals",
                "Bread & Bakery",
                "Beans & Legumes",
                "Nuts & Spreads",
                "Spices & Seasonings",
                "Canned & Packaged Foods",
                "Pasta & Noodles",
                "Oils"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        categoriesList
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerPantryCategory.setAdapter(adapter);
    }

    private void loadIngredients() {

        names.clear();
        quantities.clear();
        categories.clear();
        expiryDates.clear();
        ingredientIds.clear();

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        String searchText =
                etPantrySearch.getText()
                        .toString()
                        .trim()
                        .toLowerCase();

        StringBuilder query =
                new StringBuilder(
                        "SELECT id, name, quantity, unit, " +
                                "category, expiry_date " +
                                "FROM ingredients WHERE "
                );

        ArrayList<String> argumentsList =
                new ArrayList<>();

        if (selectedCategory.equals(
                "All Categories")) {

            query.append(
                    "1 = 1 "
            );

        } else {

            query.append(
                    "category = ? "
            );

            argumentsList.add(
                    selectedCategory
            );
        }

        query.append(
                "AND LOWER(name) LIKE ? "
        );

        argumentsList.add(
                "%" + searchText + "%"
        );

        String[] arguments =
                argumentsList.toArray(
                        new String[0]
                );

        Cursor cursor =
                db.rawQuery(
                        query.toString(),
                        arguments
                );

        SimpleDateFormat dateFormat =
                new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                );

        dateFormat.setLenient(false);

        Date today = new Date();

        while (cursor.moveToNext()) {

            int id =
                    cursor.getInt(0);

            String name =
                    cursor.getString(1);

            double quantity =
                    cursor.getDouble(2);

            String unit =
                    cursor.getString(3);

            String category =
                    cursor.getString(4);

            String expiry =
                    cursor.getString(5);

            boolean includeIngredient =
                    matchesExpiryFilter(
                            expiry,
                            dateFormat,
                            today
                    );

            if (!includeIngredient) {

                continue;
            }

            ingredientIds.add(id);

            names.add(name);

            quantities.add(
                    "Quantity: "
                            + quantity
                            + " "
                            + unit
            );

            categories.add(
                    "Category: "
                            + category
            );

            if (expiry == null
                    || expiry.isEmpty()) {

                expiryDates.add(
                        "Expiry: Not specified"
                );

            } else {

                expiryDates.add(
                        "Expires: "
                                + expiry
                );
            }
        }

        cursor.close();

        updateSummary(db);

        db.close();

        if (names.isEmpty()) {

            names.add("No ingredients found");

            quantities.add("");

            categories.add("");

            if (selectedExpiryFilter.equals("FRESH")) {

                expiryDates.add(
                        "No fresh ingredients found."
                );

            } else if (selectedExpiryFilter.equals("EXPIRING")) {

                expiryDates.add(
                        "No ingredients are expiring soon."
                );

            } else if (selectedExpiryFilter.equals("EXPIRED")) {

                expiryDates.add(
                        "No expired ingredients found."
                );

            } else {

                expiryDates.add(
                        "Try another search or category."
                );
            }
        }

        IngredientAdapter adapter =
                new IngredientAdapter(
                        this,
                        names,
                        quantities,
                        categories,
                        expiryDates,
                        ingredientIds
                );

        listViewIngredients.setAdapter(adapter);
    }

    private boolean matchesExpiryFilter(
            String expiryDateText,
            SimpleDateFormat dateFormat,
            Date today) {

        if (selectedExpiryFilter.equals("ALL")) {

            return true;
        }

        if (expiryDateText == null
                || expiryDateText.isEmpty()) {

            return selectedExpiryFilter.equals("FRESH");
        }

        try {

            Date expiryDate =
                    dateFormat.parse(
                            expiryDateText
                    );

            if (expiryDate.before(today)) {

                return selectedExpiryFilter.equals(
                        "EXPIRED"
                );
            }

            long difference =
                    expiryDate.getTime()
                            - today.getTime();

            long daysRemaining =
                    difference
                            / (1000 * 60 * 60 * 24);

            if (daysRemaining <= 3) {

                return selectedExpiryFilter.equals(
                        "EXPIRING"
                );
            }

            return selectedExpiryFilter.equals(
                    "FRESH"
            );

        } catch (ParseException e) {

            return false;
        }
    }

    private void updateSummary(
            SQLiteDatabase db) {

        Cursor cursor =
                db.rawQuery(
                        "SELECT expiry_date FROM ingredients",
                        null
                );

        int total = 0;
        int fresh = 0;
        int expiring = 0;
        int expired = 0;

        SimpleDateFormat dateFormat =
                new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                );

        dateFormat.setLenient(false);

        Date today = new Date();

        while (cursor.moveToNext()) {

            total++;

            String expiryDateText =
                    cursor.getString(0);

            if (expiryDateText == null
                    || expiryDateText.isEmpty()) {

                fresh++;

                continue;
            }

            try {

                Date expiryDate =
                        dateFormat.parse(
                                expiryDateText
                        );

                if (expiryDate.before(today)) {

                    expired++;

                } else {

                    long difference =
                            expiryDate.getTime()
                                    - today.getTime();

                    long daysRemaining =
                            difference
                                    / (1000 * 60 * 60 * 24);

                    if (daysRemaining <= 3) {

                        expiring++;

                    } else {

                        fresh++;
                    }
                }

            } catch (ParseException e) {
            }
        }

        cursor.close();

        tvPantryCount.setText(
                total + " ingredients stored"
        );

        tvTotalIngredients.setText(
                String.valueOf(fresh)
        );

        tvExpiringSoon.setText(
                String.valueOf(expiring)
        );

        tvExpiredIngredients.setText(
                String.valueOf(expired)
        );
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (databaseHelper != null) {

            loadIngredients();
        }
    }
}
