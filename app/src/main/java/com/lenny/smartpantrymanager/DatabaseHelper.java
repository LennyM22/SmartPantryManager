package com.lenny.smartpantrymanager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 6;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE ingredients (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name TEXT NOT NULL," +
                        "quantity REAL NOT NULL," +
                        "unit TEXT NOT NULL," +
                        "category TEXT," +
                        "expiry_date TEXT" +
                        ")"
        );

        db.execSQL(
                "CREATE TABLE recipes (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name TEXT NOT NULL," +
                        "description TEXT," +
                        "country TEXT," +
                        "category TEXT," +
                        "instructions TEXT," +
                        "image TEXT" +
                        ")"
        );

        db.execSQL(
                "CREATE TABLE recipe_ingredients (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "recipe_id INTEGER NOT NULL," +
                        "ingredient_name TEXT NOT NULL," +
                        "quantity TEXT," +
                        "unit TEXT," +
                        "FOREIGN KEY(recipe_id) REFERENCES recipes(id)" +
                        ")"
        );

        db.execSQL(
                "CREATE TABLE favorites (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "recipe_id INTEGER NOT NULL UNIQUE," +
                        "FOREIGN KEY(recipe_id) REFERENCES recipes(id)" +
                        ")"
        );

        addRecipes(db);
        addMoreRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion < 2) {

            db.execSQL(
                    "CREATE TABLE recipes (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "name TEXT NOT NULL," +
                            "description TEXT," +
                            "country TEXT," +
                            "instructions TEXT," +
                            "image TEXT" +
                            ")"
            );

            db.execSQL(
                    "CREATE TABLE recipe_ingredients (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "recipe_id INTEGER NOT NULL," +
                            "ingredient_name TEXT NOT NULL," +
                            "quantity TEXT," +
                            "unit TEXT," +
                            "FOREIGN KEY(recipe_id) REFERENCES recipes(id)" +
                            ")"
            );
        }

        if (oldVersion < 3) {
            addRecipes(db);
        }

        if (oldVersion < 4) {

            db.execSQL(
                    "CREATE TABLE favorites (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "recipe_id INTEGER NOT NULL UNIQUE," +
                            "FOREIGN KEY(recipe_id) REFERENCES recipes(id)" +
                            ")"
            );
        }

        if (oldVersion < 5) {
            fixRecipeIngredients(db);
        }

        if (oldVersion < 6) {
            db.execSQL("ALTER TABLE recipes ADD COLUMN category TEXT");
            addMoreRecipes(db);
        }
    }

    private void addRecipes(SQLiteDatabase db) {

        insertRecipe(
                db,
                "Bobotie",
                "A traditional South African baked dish made with spiced minced meat and an egg topping.",
                "South Africa",
                "Traditional",
                "Cook the mince with onion and spices. Add the other ingredients and place the mixture in a baking dish. Add the egg topping and bake until golden."
        );

        insertRecipe(
                db,
                "Bunny Chow",
                "A popular South African curry served inside a hollowed-out loaf of bread.",
                "South Africa",
                "Curry",
                "Cook the curry with the meat, potatoes, onion and tomatoes. Hollow out the bread and fill it with the prepared curry."
        );

        insertRecipe(
                db,
                "Pap and Chakalaka",
                "A South African combination of maize meal porridge and spicy vegetable relish.",
                "South Africa",
                "Traditional",
                "Prepare the pap using maize meal and water. Cook the vegetables with spices and tomatoes to make the chakalaka. Serve together."
        );

        insertRecipe(
                db,
                "Dovi",
                "A Zimbabwean peanut butter chicken stew with a rich and creamy sauce.",
                "Zimbabwe",
                "Traditional",
                "Cook the chicken with onion, garlic and tomatoes. Add peanut butter and simmer until the chicken is cooked and the sauce is thick."
        );

        insertRecipe(
                db,
                "Sadza and Beef Stew",
                "A Zimbabwean staple consisting of thick maize meal served with beef stew.",
                "Zimbabwe",
                "Traditional",
                "Cook the beef with onion, tomatoes and vegetables until tender. Prepare thick sadza using maize meal and water. Serve together."
        );

        insertRecipe(
                db,
                "Muriwo Unedovi",
                "Zimbabwean leafy vegetables cooked with peanut butter.",
                "Zimbabwe",
                "Traditional",
                "Cook the leafy vegetables with onion and tomatoes. Add peanut butter and simmer until the vegetables are tender and the sauce is creamy."
        );

        addRecipeIngredient(db, 1, "Minced Beef", "500", "g");
        addRecipeIngredient(db, 1, "Onion", "1", "piece");
        addRecipeIngredient(db, 1, "Eggs", "2", "piece");
        addRecipeIngredient(db, 1, "Curry Powder", "2", "tbsp");
        addRecipeIngredient(db, 1, "Milk", "250", "ml");

        addRecipeIngredient(db, 2, "Bread", "1", "loaf");
        addRecipeIngredient(db, 2, "Chicken", "500", "g");
        addRecipeIngredient(db, 2, "Potatoes", "3", "piece");
        addRecipeIngredient(db, 2, "Onion", "1", "piece");
        addRecipeIngredient(db, 2, "Tomatoes", "2", "piece");

        addRecipeIngredient(db, 3, "Maize Meal", "2", "cup");
        addRecipeIngredient(db, 3, "Tomatoes", "3", "piece");
        addRecipeIngredient(db, 3, "Onion", "1", "piece");
        addRecipeIngredient(db, 3, "Cabbage", "1", "cup");
        addRecipeIngredient(db, 3, "Carrots", "2", "piece");

        addRecipeIngredient(db, 4, "Chicken", "500", "g");
        addRecipeIngredient(db, 4, "Peanut Butter", "4", "tbsp");
        addRecipeIngredient(db, 4, "Onion", "1", "piece");
        addRecipeIngredient(db, 4, "Tomatoes", "2", "piece");
        addRecipeIngredient(db, 4, "Garlic", "2", "clove");

        addRecipeIngredient(db, 5, "Beef", "500", "g");
        addRecipeIngredient(db, 5, "Maize Meal", "2", "cup");
        addRecipeIngredient(db, 5, "Onion", "1", "piece");
        addRecipeIngredient(db, 5, "Tomatoes", "2", "piece");
        addRecipeIngredient(db, 5, "Carrots", "2", "piece");

        addRecipeIngredient(db, 6, "Leafy Greens", "2", "cup");
        addRecipeIngredient(db, 6, "Peanut Butter", "3", "tbsp");
        addRecipeIngredient(db, 6, "Onion", "1", "piece");
        addRecipeIngredient(db, 6, "Tomatoes", "2", "piece");
    }

    private void addMoreRecipes(SQLiteDatabase db) {

        int id;

        id = insertRecipe(
                db,
                "Toast with Eggs",
                "A simple everyday breakfast made with toasted bread and eggs.",
                "United Kingdom",
                "Breakfast",
                "Toast the bread. Cook the eggs to your preference and serve with the toast."
        );
        addRecipeIngredient(db, id, "Bread", "2", "piece");
        addRecipeIngredient(db, id, "Eggs", "2", "piece");

        id = insertRecipe(
                db,
                "French Toast",
                "Bread dipped in an egg and milk mixture and fried until golden.",
                "France",
                "Breakfast",
                "Mix eggs and milk. Dip the bread into the mixture and fry both sides until golden."
        );
        addRecipeIngredient(db, id, "Bread", "2", "piece");
        addRecipeIngredient(db, id, "Eggs", "2", "piece");
        addRecipeIngredient(db, id, "Milk", "100", "ml");
        addRecipeIngredient(db, id, "Sugar", "1", "tbsp");

        id = insertRecipe(
                db,
                "Omelette",
                "A quick egg breakfast that can be made with simple pantry ingredients.",
                "France",
                "Breakfast",
                "Beat the eggs and cook them in a pan. Add onion, tomatoes and cheese, then fold the omelette."
        );
        addRecipeIngredient(db, id, "Eggs", "3", "piece");
        addRecipeIngredient(db, id, "Onion", "1", "piece");
        addRecipeIngredient(db, id, "Tomatoes", "1", "piece");
        addRecipeIngredient(db, id, "Cheddar Cheese", "50", "g");

        id = insertRecipe(
                db,
                "Grilled Cheese Sandwich",
                "A quick toasted sandwich filled with melted cheese.",
                "United States",
                "Quick Meal",
                "Place cheese between two slices of bread. Toast in a pan until the bread is golden and the cheese has melted."
        );
        addRecipeIngredient(db, id, "Bread", "2", "piece");
        addRecipeIngredient(db, id, "Cheddar Cheese", "100", "g");

        id = insertRecipe(
                db,
                "Spaghetti Bolognese",
                "Spaghetti served with a rich minced beef and tomato sauce.",
                "Italy",
                "Pasta",
                "Cook the spaghetti. Fry the mince with onion and tomatoes. Simmer the sauce and serve over the pasta."
        );
        addRecipeIngredient(db, id, "Spaghetti", "250", "g");
        addRecipeIngredient(db, id, "Beef Mince", "500", "g");
        addRecipeIngredient(db, id, "Onion", "1", "piece");
        addRecipeIngredient(db, id, "Tomatoes", "3", "piece");
        addRecipeIngredient(db, id, "Cooking Oil", "2", "tbsp");

        id = insertRecipe(
                db,
                "Creamy Chicken Pasta",
                "A creamy pasta dish made with chicken, milk and cheese.",
                "Italy",
                "Pasta",
                "Cook the pasta. Fry the chicken and onion. Add milk and cheese and simmer until creamy. Mix with the pasta."
        );
        addRecipeIngredient(db, id, "Pasta", "250", "g");
        addRecipeIngredient(db, id, "Chicken Breasts", "2", "piece");
        addRecipeIngredient(db, id, "Milk", "250", "ml");
        addRecipeIngredient(db, id, "Cheddar Cheese", "100", "g");
        addRecipeIngredient(db, id, "Onion", "1", "piece");

        id = insertRecipe(
                db,
                "Macaroni and Cheese",
                "A simple creamy macaroni dish with melted cheese.",
                "United States",
                "Pasta",
                "Cook the macaroni. Heat the milk and cheese together until creamy. Mix with the cooked macaroni."
        );
        addRecipeIngredient(db, id, "Macaroni", "250", "g");
        addRecipeIngredient(db, id, "Milk", "250", "ml");
        addRecipeIngredient(db, id, "Cheddar Cheese", "150", "g");

        id = insertRecipe(
                db,
                "Carbonara",
                "A classic Italian pasta dish made with eggs and cheese.",
                "Italy",
                "Pasta",
                "Cook the spaghetti. Mix eggs and cheese. Add the hot pasta and stir until creamy."
        );
        addRecipeIngredient(db, id, "Spaghetti", "250", "g");
        addRecipeIngredient(db, id, "Eggs", "2", "piece");
        addRecipeIngredient(db, id, "Cheddar Cheese", "100", "g");

        id = insertRecipe(
                db,
                "Chicken Alfredo",
                "Creamy pasta with chicken and cheese.",
                "United States",
                "Pasta",
                "Cook the pasta. Fry the chicken. Add milk and cheese and cook until creamy. Combine with the pasta."
        );
        addRecipeIngredient(db, id, "Pasta", "250", "g");
        addRecipeIngredient(db, id, "Chicken Breasts", "2", "piece");
        addRecipeIngredient(db, id, "Milk", "250", "ml");
        addRecipeIngredient(db, id, "Cheddar Cheese", "100", "g");

        id = insertRecipe(
                db,
                "Chicken Curry",
                "A simple everyday chicken curry with onion, tomatoes and spices.",
                "India",
                "Curry",
                "Fry the chicken with onion and curry powder. Add tomatoes and simmer until the chicken is cooked."
        );
        addRecipeIngredient(db, id, "Chicken Breasts", "2", "piece");
        addRecipeIngredient(db, id, "Onion", "1", "piece");
        addRecipeIngredient(db, id, "Tomatoes", "2", "piece");
        addRecipeIngredient(db, id, "Curry Powder", "2", "tbsp");
        addRecipeIngredient(db, id, "Cooking Oil", "2", "tbsp");

        id = insertRecipe(
                db,
                "Butter Chicken",
                "A creamy Indian chicken curry with a rich tomato sauce.",
                "India",
                "Curry",
                "Cook the chicken with onion and spices. Add tomatoes, milk and cheese and simmer until creamy."
        );
        addRecipeIngredient(db, id, "Chicken Breasts", "2", "piece");
        addRecipeIngredient(db, id, "Onion", "1", "piece");
        addRecipeIngredient(db, id, "Tomatoes", "3", "piece");
        addRecipeIngredient(db, id, "Milk", "250", "ml");
        addRecipeIngredient(db, id, "Curry Powder", "2", "tbsp");

        id = insertRecipe(
                db,
                "Beef Curry",
                "Tender beef cooked slowly with tomatoes, onions and curry spices.",
                "India",
                "Curry",
                "Brown the beef. Add onion, curry powder and tomatoes. Add water and simmer until the beef is tender."
        );
        addRecipeIngredient(db, id, "Beef Mince", "500", "g");
        addRecipeIngredient(db, id, "Onion", "1", "piece");
        addRecipeIngredient(db, id, "Tomatoes", "3", "piece");
        addRecipeIngredient(db, id, "Curry Powder", "2", "tbsp");
        addRecipeIngredient(db, id, "Cooking Oil", "2", "tbsp");

        id = insertRecipe(
                db,
                "Chicken and Rice",
                "A simple everyday meal combining seasoned chicken and rice.",
                "United States",
                "Rice",
                "Cook the chicken with onion and spices. Cook the rice separately and serve together."
        );
        addRecipeIngredient(db, id, "Chicken Breasts", "2", "piece");
        addRecipeIngredient(db, id, "Rice", "2", "cup");
        addRecipeIngredient(db, id, "Onion", "1", "piece");
        addRecipeIngredient(db, id, "Cooking Oil", "2", "tbsp");

        id = insertRecipe(
                db,
                "Chicken Fried Rice",
                "A quick fried rice dish with chicken, eggs and vegetables.",
                "China",
                "Rice",
                "Cook the rice. Fry the chicken, onion and eggs. Add the rice and stir-fry everything together."
        );
        addRecipeIngredient(db, id, "Rice", "2", "cup");
        addRecipeIngredient(db, id, "Chicken Breasts", "2", "piece");
        addRecipeIngredient(db, id, "Eggs", "2", "piece");
        addRecipeIngredient(db, id, "Onion", "1", "piece");
        addRecipeIngredient(db, id, "Carrots", "2", "piece");

        id = insertRecipe(
                db,
                "Egg Fried Rice",
                "A simple fried rice meal made with eggs and vegetables.",
                "China",
                "Rice",
                "Cook the rice. Fry the eggs and vegetables. Add the rice and stir-fry until hot."
        );
        addRecipeIngredient(db, id, "Rice", "2", "cup");
        addRecipeIngredient(db, id, "Eggs", "2", "piece");
        addRecipeIngredient(db, id, "Carrots", "2", "piece");
        addRecipeIngredient(db, id, "Onion", "1", "piece");

        id = insertRecipe(
                db,
                "Beef Tacos",
                "Mexican tacos filled with seasoned beef and fresh vegetables.",
                "Mexico",
                "Mexican",
                "Cook the beef with onion and spices. Fill the taco shells with the beef and tomatoes."
        );
        addRecipeIngredient(db, id, "Beef Mince", "500", "g");
        addRecipeIngredient(db, id, "Onion", "1", "piece");
        addRecipeIngredient(db, id, "Tomatoes", "2", "piece");
        addRecipeIngredient(db, id, "Cheddar Cheese", "100", "g");

        id = insertRecipe(
                db,
                "Chicken Tacos",
                "Soft or crispy tacos filled with seasoned chicken and vegetables.",
                "Mexico",
                "Mexican",
                "Cook the chicken with onion and spices. Fill the tacos and add tomatoes and cheese."
        );
        addRecipeIngredient(db, id, "Chicken Breasts", "2", "piece");
        addRecipeIngredient(db, id, "Onion", "1", "piece");
        addRecipeIngredient(db, id, "Tomatoes", "2", "piece");
        addRecipeIngredient(db, id, "Cheddar Cheese", "100", "g");

        id = insertRecipe(
                db,
                "Quesadilla",
                "A toasted Mexican tortilla filled with melted cheese and chicken.",
                "Mexico",
                "Mexican",
                "Fill a tortilla with cheese and chicken. Fold it and cook in a pan until golden and the cheese melts."
        );
        addRecipeIngredient(db, id, "Chicken Breasts", "1", "piece");
        addRecipeIngredient(db, id, "Cheddar Cheese", "100", "g");

        id = insertRecipe(
                db,
                "Fish and Chips",
                "Crispy fish served with golden chips.",
                "United Kingdom",
                "Seafood",
                "Coat the fish and fry until golden. Cut and cook the potatoes until crispy. Serve together."
        );
        addRecipeIngredient(db, id, "Potatoes", "4", "piece");
        addRecipeIngredient(db, id, "Fish", "2", "piece");
        addRecipeIngredient(db, id, "Cooking Oil", "500", "ml");

        id = insertRecipe(
                db,
                "Chicken Schnitzel",
                "Breaded chicken fried until golden and crispy.",
                "Germany",
                "Chicken",
                "Coat the chicken in egg and breadcrumbs. Fry until golden and fully cooked."
        );
        addRecipeIngredient(db, id, "Chicken Breasts", "2", "piece");
        addRecipeIngredient(db, id, "Eggs", "2", "piece");
        addRecipeIngredient(db, id, "Bread", "2", "piece");
        addRecipeIngredient(db, id, "Cooking Oil", "3", "tbsp");

        id = insertRecipe(
                db,
                "Greek Salad",
                "A fresh salad made with vegetables and cheese.",
                "Greece",
                "Salad",
                "Chop the vegetables and cheese. Mix together and serve fresh."
        );
        addRecipeIngredient(db, id, "Tomatoes", "3", "piece");
        addRecipeIngredient(db, id, "Onion", "1", "piece");
        addRecipeIngredient(db, id, "Cucumber", "1", "piece");
        addRecipeIngredient(db, id, "Cheddar Cheese", "100", "g");

        id = insertRecipe(
                db,
                "Fried Chicken",
                "Crispy seasoned chicken that is popular around the world.",
                "United States",
                "Chicken",
                "Season the chicken and fry until golden and fully cooked."
        );
        addRecipeIngredient(db, id, "Chicken Breasts", "2", "piece");
        addRecipeIngredient(db, id, "Cooking Oil", "500", "ml");
        addRecipeIngredient(db, id, "Salt", "1", "tsp");
        addRecipeIngredient(db, id, "Black Pepper", "1", "tsp");

        id = insertRecipe(
                db,
                "Beef Stew",
                "A warm and filling beef stew with vegetables.",
                "Ireland",
                "Stew",
                "Cook the beef with onions and vegetables. Add water and simmer until the beef is tender."
        );
        addRecipeIngredient(db, id, "Beef Mince", "500", "g");
        addRecipeIngredient(db, id, "Potatoes", "3", "piece");
        addRecipeIngredient(db, id, "Carrots", "2", "piece");
        addRecipeIngredient(db, id, "Onion", "1", "piece");
        addRecipeIngredient(db, id, "Tomatoes", "2", "piece");

        id = insertRecipe(
                db,
                "Chicken Soup",
                "A simple homemade soup with chicken and vegetables.",
                "United States",
                "Soup",
                "Cook the chicken with onions and vegetables in water until tender. Season and serve hot."
        );
        addRecipeIngredient(db, id, "Chicken Breasts", "2", "piece");
        addRecipeIngredient(db, id, "Carrots", "2", "piece");
        addRecipeIngredient(db, id, "Onion", "1", "piece");
        addRecipeIngredient(db, id, "Potatoes", "2", "piece");

        id = insertRecipe(
                db,
                "Vegetable Soup",
                "A simple vegetable soup made with common pantry ingredients.",
                "International",
                "Soup",
                "Chop the vegetables and cook them in water until tender. Season and serve hot."
        );
        addRecipeIngredient(db, id, "Potatoes", "2", "piece");
        addRecipeIngredient(db, id, "Carrots", "2", "piece");
        addRecipeIngredient(db, id, "Onion", "1", "piece");
        addRecipeIngredient(db, id, "Cabbage", "1", "cup");

        id = insertRecipe(
                db,
                "Peanut Butter Sandwich",
                "A quick sandwich made with bread and peanut butter.",
                "United States",
                "Quick Meal",
                "Spread peanut butter on two slices of bread and put the slices together."
        );
        addRecipeIngredient(db, id, "Bread", "2", "piece");
        addRecipeIngredient(db, id, "Peanut Butter", "2", "tbsp");

        id = insertRecipe(
                db,
                "Chicken Breyani",
                "A South African rice dish made with spiced chicken.",
                "South Africa",
                "Rice",
                "Cook the chicken with spices and onion. Cook the rice and layer it with the chicken. Simmer until ready."
        );
        addRecipeIngredient(db, id, "Chicken Breasts", "2", "piece");
        addRecipeIngredient(db, id, "Rice", "2", "cup");
        addRecipeIngredient(db, id, "Onion", "1", "piece");
        addRecipeIngredient(db, id, "Curry Powder", "2", "tbsp");

        id = insertRecipe(
                db,
                "Beef Potjie",
                "A South African slow-cooked beef dish with vegetables.",
                "South Africa",
                "Traditional",
                "Brown the beef and add onions, tomatoes, potatoes and carrots. Cover and cook slowly until tender."
        );
        addRecipeIngredient(db, id, "Beef Mince", "500", "g");
        addRecipeIngredient(db, id, "Potatoes", "3", "piece");
        addRecipeIngredient(db, id, "Carrots", "2", "piece");
        addRecipeIngredient(db, id, "Onion", "1", "piece");
        addRecipeIngredient(db, id, "Tomatoes", "2", "piece");

        id = insertRecipe(
                db,
                "Chakalaka with Pap",
                "A South African meal combining spicy vegetables with maize meal.",
                "South Africa",
                "Traditional",
                "Prepare the pap using maize meal. Cook tomatoes, onions, carrots and cabbage with spices and serve together."
        );
        addRecipeIngredient(db, id, "Maize Meal", "2", "cup");
        addRecipeIngredient(db, id, "Tomatoes", "3", "piece");
        addRecipeIngredient(db, id, "Onion", "1", "piece");
        addRecipeIngredient(db, id, "Carrots", "2", "piece");
        addRecipeIngredient(db, id, "Cabbage", "1", "cup");

        id = insertRecipe(
                db,
                "Sadza with Chicken",
                "A Zimbabwean staple served with chicken and vegetables.",
                "Zimbabwe",
                "Traditional",
                "Prepare thick sadza using maize meal. Cook the chicken with onion and tomatoes and serve together."
        );
        addRecipeIngredient(db, id, "Maize Meal", "2", "cup");
        addRecipeIngredient(db, id, "Chicken Breasts", "2", "piece");
        addRecipeIngredient(db, id, "Onion", "1", "piece");
        addRecipeIngredient(db, id, "Tomatoes", "2", "piece");
    }

    private int insertRecipe(
            SQLiteDatabase db,
            String name,
            String description,
            String country,
            String category,
            String instructions) {

        db.execSQL(
                "INSERT INTO recipes " +
                        "(name, description, country, category, instructions, image) " +
                        "VALUES (?, ?, ?, ?, ?, ?)",
                new Object[]{
                        name,
                        description,
                        country,
                        category,
                        instructions,
                        ""
                }
        );

        Cursor cursor = db.rawQuery(
                "SELECT last_insert_rowid()",
                null
        );

        int id = 0;

        if (cursor.moveToFirst()) {
            id = cursor.getInt(0);
        }

        cursor.close();

        return id;
    }

    private void fixRecipeIngredients(SQLiteDatabase db) {

        db.delete("recipe_ingredients", null, null);

        addRecipeIngredient(db, 1, "Minced Beef", "500", "g");
        addRecipeIngredient(db, 1, "Onion", "1", "piece");
        addRecipeIngredient(db, 1, "Eggs", "2", "piece");
        addRecipeIngredient(db, 1, "Curry Powder", "2", "tbsp");
        addRecipeIngredient(db, 1, "Milk", "250", "ml");

        addRecipeIngredient(db, 2, "Bread", "1", "loaf");
        addRecipeIngredient(db, 2, "Chicken", "500", "g");
        addRecipeIngredient(db, 2, "Potatoes", "3", "piece");
        addRecipeIngredient(db, 2, "Onion", "1", "piece");
        addRecipeIngredient(db, 2, "Tomatoes", "2", "piece");

        addRecipeIngredient(db, 3, "Maize Meal", "2", "cup");
        addRecipeIngredient(db, 3, "Tomatoes", "3", "piece");
        addRecipeIngredient(db, 3, "Onion", "1", "piece");
        addRecipeIngredient(db, 3, "Cabbage", "1", "cup");
        addRecipeIngredient(db, 3, "Carrots", "2", "piece");

        addRecipeIngredient(db, 4, "Chicken", "500", "g");
        addRecipeIngredient(db, 4, "Peanut Butter", "4", "tbsp");
        addRecipeIngredient(db, 4, "Onion", "1", "piece");
        addRecipeIngredient(db, 4, "Tomatoes", "2", "piece");
        addRecipeIngredient(db, 4, "Garlic", "2", "clove");

        addRecipeIngredient(db, 5, "Beef", "500", "g");
        addRecipeIngredient(db, 5, "Maize Meal", "2", "cup");
        addRecipeIngredient(db, 5, "Onion", "1", "piece");
        addRecipeIngredient(db, 5, "Tomatoes", "2", "piece");
        addRecipeIngredient(db, 5, "Carrots", "2", "piece");

        addRecipeIngredient(db, 6, "Leafy Greens", "2", "cup");
        addRecipeIngredient(db, 6, "Peanut Butter", "3", "tbsp");
        addRecipeIngredient(db, 6, "Onion", "1", "piece");
        addRecipeIngredient(db, 6, "Tomatoes", "2", "piece");
    }

    private void addRecipeIngredient(
            SQLiteDatabase db,
            int recipeId,
            String ingredientName,
            String quantity,
            String unit) {

        db.execSQL(
                "INSERT INTO recipe_ingredients " +
                        "(recipe_id, ingredient_name, quantity, unit) VALUES (?, ?, ?, ?)",
                new Object[]{
                        recipeId,
                        ingredientName,
                        quantity,
                        unit
                }
        );
    }
}
