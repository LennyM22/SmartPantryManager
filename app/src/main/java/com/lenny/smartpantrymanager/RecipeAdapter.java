package com.lenny.smartpantrymanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class RecipeAdapter extends ArrayAdapter<String> {

    ArrayList<String> names;
    ArrayList<String> countries;
    ArrayList<String> categories;
    ArrayList<String> descriptions;

    public RecipeAdapter(
            Context context,
            ArrayList<String> names,
            ArrayList<String> countries,
            ArrayList<String> descriptions) {

        super(context, 0, names);

        this.names = names;
        this.countries = countries;
        this.descriptions = descriptions;

        this.categories = new ArrayList<>();

        for (int i = 0; i < names.size(); i++) {
            this.categories.add("");
        }
    }

    public RecipeAdapter(
            Context context,
            ArrayList<String> names,
            ArrayList<String> countries,
            ArrayList<String> categories,
            ArrayList<String> descriptions) {

        super(context, 0, names);

        this.names = names;
        this.countries = countries;
        this.categories = categories;
        this.descriptions = descriptions;
    }

    @Override
    public View getView(
            int position,
            View convertView,
            ViewGroup parent) {

        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.recipe_card,
                    parent,
                    false
            );
        }

        TextView recipeName =
                convertView.findViewById(R.id.tvCardRecipeName);

        TextView country =
                convertView.findViewById(R.id.tvCardCountry);

        TextView description =
                convertView.findViewById(R.id.tvCardDescription);

        TextView category =
                convertView.findViewById(R.id.tvCardCategory);

        recipeName.setText(names.get(position));
        country.setText(countries.get(position));
        description.setText(descriptions.get(position));

        if (category != null) {

            if (categories.size() > position &&
                    !categories.get(position).isEmpty()) {

                category.setText(categories.get(position));
                category.setVisibility(View.VISIBLE);

            } else {

                category.setText("");
                category.setVisibility(View.GONE);
            }
        }

        return convertView;
    }
}
