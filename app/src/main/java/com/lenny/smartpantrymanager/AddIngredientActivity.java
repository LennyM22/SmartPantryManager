
        package com.lenny.smartpantrymanager;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AddIngredientActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;

    EditText etIngredientName;
    EditText etQuantity;
    Spinner spinnerUnit;
    Spinner spinnerCategory;
    EditText etExpiryDate;
    Button btnSaveIngredient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_ingredient);

        databaseHelper = new DatabaseHelper(this);

        etIngredientName = findViewById(R.id.etIngredientName);
        etQuantity = findViewById(R.id.etQuantity);
        spinnerUnit = findViewById(R.id.spinnerUnit);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        etExpiryDate = findViewById(R.id.etExpiryDate);
        btnSaveIngredient = findViewById(R.id.btnSaveIngredient);

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

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                units
        );

        unitAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerUnit.setAdapter(unitAdapter);

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

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );

        categoryAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerCategory.setAdapter(categoryAdapter);

        etExpiryDate.setOnClickListener(v -> {

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

                        etExpiryDate.setText(date);
                    },
                    year,
                    month,
                    day
            );

            datePickerDialog.show();
        });

        btnSaveIngredient.setOnClickListener(v -> saveIngredient());
    }

    private void saveIngredient() {

        String name = etIngredientName.getText().toString().trim();
        String quantityText = etQuantity.getText().toString().trim();
        String unit = spinnerUnit.getSelectedItem().toString();
        String category = spinnerCategory.getSelectedItem().toString();
        String expiryDate = etExpiryDate.getText().toString().trim();

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

        long result = db.insert(
                "ingredients",
                null,
                values
        );

        if (result != -1) {

            Toast.makeText(
                    this,
                    "Ingredient saved successfully",
                    Toast.LENGTH_SHORT
            ).show();

            etIngredientName.setText("");
            etQuantity.setText("");
            etExpiryDate.setText("");

            spinnerUnit.setSelection(0);
            spinnerCategory.setSelection(0);

        } else {

            Toast.makeText(
                    this,
                    "Failed to save ingredient",
                    Toast.LENGTH_SHORT
            ).show();
        }

        db.close();
    }
}

