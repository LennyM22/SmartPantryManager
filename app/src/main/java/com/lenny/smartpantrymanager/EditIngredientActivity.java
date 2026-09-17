package com.lenny.smartpantrymanager;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class EditIngredientActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;

    EditText etEditIngredientName;
    EditText etEditQuantity;
    Spinner spinnerEditUnit;
    Spinner spinnerEditCategory;
    EditText etEditExpiryDate;
    Button btnUpdateIngredient;

    int ingredientId;

    String[] units = {
            "Pieces",
            "Grams (g)",
            "Kilograms (kg)",
            "Millilitres (ml)",
            "Litres (L)",
            "Tablespoons (tbsp)",
            "Cups",
            "Cloves",
            "Loaves"
    };

    String[] categories = {
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
            "Oils & Sauces",
            "Sweeteners",
            "Drinks",
            "Frozen Foods",
            "Snacks",
            "Other"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_ingredient);

        databaseHelper = new DatabaseHelper(this);

        etEditIngredientName = findViewById(R.id.etEditIngredientName);
        etEditQuantity = findViewById(R.id.etEditQuantity);
        spinnerEditUnit = findViewById(R.id.spinnerEditUnit);
        spinnerEditCategory = findViewById(R.id.spinnerEditCategory);
        etEditExpiryDate = findViewById(R.id.etEditExpiryDate);
        btnUpdateIngredient = findViewById(R.id.btnUpdateIngredient);

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                units
        );

        unitAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerEditUnit.setAdapter(unitAdapter);

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );

        categoryAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerEditCategory.setAdapter(categoryAdapter);

        ingredientId = getIntent().getIntExtra("ingredient_id", -1);

        loadIngredient();

        etEditExpiryDate.setOnClickListener(v -> showDatePicker());

        btnUpdateIngredient.setOnClickListener(v -> updateIngredient());
    }

    private void loadIngredient() {

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT name, quantity, unit, category, expiry_date " +
                        "FROM ingredients WHERE id = ?",
                new String[]{String.valueOf(ingredientId)}
        );

        if (cursor.moveToFirst()) {

            String name = cursor.getString(0);
            double quantity = cursor.getDouble(1);
            String unit = cursor.getString(2);
            String category = cursor.getString(3);
            String expiryDate = cursor.getString(4);

            etEditIngredientName.setText(name);
            etEditQuantity.setText(String.valueOf(quantity));
            etEditExpiryDate.setText(expiryDate);

            spinnerEditUnit.setSelection(
                    findUnitPosition(unit)
            );

            spinnerEditCategory.setSelection(
                    findCategoryPosition(category)
            );
        }

        cursor.close();
        db.close();
    }

    private int findUnitPosition(String unit) {

        if (unit == null) {
            return 0;
        }

        if (unit.equals("piece")) {
            return 0;
        }

        if (unit.equals("g")) {
            return 1;
        }

        if (unit.equals("kg")) {
            return 2;
        }

        if (unit.equals("ml")) {
            return 3;
        }

        if (unit.equals("l")) {
            return 4;
        }

        if (unit.equals("tbsp")) {
            return 5;
        }

        if (unit.equals("cup")) {
            return 6;
        }

        if (unit.equals("clove")) {
            return 7;
        }

        if (unit.equals("loaf")) {
            return 8;
        }

        return 0;
    }

    private int findCategoryPosition(String category) {

        if (category == null) {
            return 0;
        }

        for (int i = 0; i < categories.length; i++) {

            if (categories[i].equalsIgnoreCase(category)) {
                return i;
            }
        }

        return 0;
    }

    private void showDatePicker() {

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {

                    String date = String.format(
                            "%04d-%02d-%02d",
                            selectedYear,
                            selectedMonth + 1,
                            selectedDay
                    );

                    etEditExpiryDate.setText(date);
                },
                year,
                month,
                day
        );

        datePickerDialog.show();
    }

    private void updateIngredient() {

        String name = etEditIngredientName.getText().toString().trim();
        String quantityText = etEditQuantity.getText().toString().trim();
        String unit = spinnerEditUnit.getSelectedItem().toString();
        String category = spinnerEditCategory.getSelectedItem().toString();
        String expiryDate = etEditExpiryDate.getText().toString().trim();

        if (name.isEmpty() || quantityText.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please enter the ingredient and quantity",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        double quantity;

        try {

            quantity = Double.parseDouble(quantityText);

        } catch (NumberFormatException e) {

            Toast.makeText(
                    this,
                    "Please enter a valid quantity",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (unit.equals("Pieces")) {
            unit = "piece";
        } else if (unit.equals("Grams (g)")) {
            unit = "g";
        } else if (unit.equals("Kilograms (kg)")) {
            unit = "kg";
        } else if (unit.equals("Millilitres (ml)")) {
            unit = "ml";
        } else if (unit.equals("Litres (L)")) {
            unit = "l";
        } else if (unit.equals("Tablespoons (tbsp)")) {
            unit = "tbsp";
        } else if (unit.equals("Cups")) {
            unit = "cup";
        } else if (unit.equals("Cloves")) {
            unit = "clove";
        } else if (unit.equals("Loaves")) {
            unit = "loaf";
        }

        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("name", name);
        values.put("quantity", quantity);
        values.put("unit", unit);
        values.put("category", category);
        values.put("expiry_date", expiryDate);

        int result = db.update(
                "ingredients",
                values,
                "id = ?",
                new String[]{String.valueOf(ingredientId)}
        );

        db.close();

        if (result > 0) {

            Toast.makeText(
                    this,
                    "Ingredient updated successfully",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Failed to update ingredient",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}