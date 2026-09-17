package com.lenny.smartpantrymanager;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class IngredientAdapter extends ArrayAdapter<String> {

    ArrayList<String> names;
    ArrayList<String> quantities;
    ArrayList<String> categories;
    ArrayList<String> expiryDates;
    ArrayList<Integer> ingredientIds;

    DatabaseHelper databaseHelper;

    public IngredientAdapter(
            Context context,
            ArrayList<String> names,
            ArrayList<String> quantities,
            ArrayList<String> categories,
            ArrayList<String> expiryDates,
            ArrayList<Integer> ingredientIds) {

        super(context, 0, names);

        this.names = names;
        this.quantities = quantities;
        this.categories = categories;
        this.expiryDates = expiryDates;
        this.ingredientIds = ingredientIds;

        databaseHelper = new DatabaseHelper(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.ingredient_card,
                    parent,
                    false
            );
        }

        TextView ingredientName =
                convertView.findViewById(R.id.tvIngredientName);

        TextView ingredientQuantity =
                convertView.findViewById(R.id.tvIngredientQuantity);

        TextView ingredientCategory =
                convertView.findViewById(R.id.tvIngredientCategory);

        TextView ingredientExpiry =
                convertView.findViewById(R.id.tvIngredientExpiry);

        Button editButton =
                convertView.findViewById(R.id.btnEditIngredient);

        Button deleteButton =
                convertView.findViewById(R.id.btnDeleteIngredient);

        ingredientName.setText(names.get(position));
        ingredientQuantity.setText(quantities.get(position));
        ingredientCategory.setText(categories.get(position));

        String expiry = expiryDates.get(position);

        ingredientExpiry.setText(expiry);
        ingredientExpiry.setTextColor(Color.BLACK);

        if (!expiry.equals("Expiry: Not specified")) {

            String dateText =
                    expiry.replace("Expires: ", "").trim();

            SimpleDateFormat dateFormat =
                    new SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                    );

            dateFormat.setLenient(false);

            try {

                Date expiryDate = dateFormat.parse(dateText);
                Date today = new Date();

                long difference =
                        expiryDate.getTime() - today.getTime();

                long daysRemaining =
                        difference / (1000 * 60 * 60 * 24);

                if (expiryDate.before(today)) {

                    ingredientExpiry.setText(
                            "EXPIRED: " + dateText
                    );

                    ingredientExpiry.setTextColor(Color.RED);

                } else if (daysRemaining <= 3) {

                    ingredientExpiry.setText(
                            "EXPIRING SOON: " + dateText
                    );

                    ingredientExpiry.setTextColor(
                            Color.rgb(255, 140, 0)
                    );

                } else {

                    ingredientExpiry.setText(
                            "Expires: " + dateText
                    );

                    ingredientExpiry.setTextColor(
                            Color.rgb(0, 128, 0)
                    );
                }

            } catch (ParseException e) {

                ingredientExpiry.setText(
                        "Expires: " + dateText
                );
            }
        }

        editButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    getContext(),
                    EditIngredientActivity.class
            );

            intent.putExtra(
                    "ingredient_id",
                    ingredientIds.get(position)
            );

            getContext().startActivity(intent);
        });

        deleteButton.setOnClickListener(v -> {

            SQLiteDatabase db =
                    databaseHelper.getWritableDatabase();

            db.delete(
                    "ingredients",
                    "id = ?",
                    new String[]{
                            String.valueOf(
                                    ingredientIds.get(position)
                            )
                    }
            );

            db.close();

            names.remove(position);
            quantities.remove(position);
            categories.remove(position);
            expiryDates.remove(position);
            ingredientIds.remove(position);

            notifyDataSetChanged();

            Toast.makeText(
                    getContext(),
                    "Ingredient deleted",
                    Toast.LENGTH_SHORT
            ).show();
        });

        return convertView;
    }
}