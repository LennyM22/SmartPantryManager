# Smart Pantry Manager

## About the App

Smart Pantry Manager is an Android application that helps users keep track of the food ingredients they have at home. The app allows users to add ingredients to their pantry, update them, delete them and view what they currently have.

The app also has a recipe section where users can browse different recipes. The recipes include South African, Zimbabwean and International meals.

One of the main features of the app is the recipe matching system. The app checks the ingredients in the user's pantry against the ingredients needed for a recipe. A recipe is only shown as a suggested recipe if all the required ingredients are available. This means the app does not suggest a recipe if the user is missing one of the required ingredients.

## Main Features

* Add ingredients to the pantry
* View pantry ingredients
* Edit existing ingredients
* Delete ingredients
* Search for ingredients
* Filter ingredients by category
* View expired and expiring ingredients
* Browse South African recipes
* Browse Zimbabwean recipes
* Browse International recipes
* View recipe details and instructions
* Find recipes that can be made with the current pantry
* Add recipes to favourites
* View favourite recipes
* Save pantry data locally

## Technologies Used

The following technologies were used to develop the application:

* Android Studio
* Java
* Android SDK
* SQLite
* XML
* Material Design
* ListView
* Custom Adapters
* Git
* GitHub

## Database

The application uses SQLite as its database. I chose SQLite because the app mainly needs to store information locally on the user's device. The app does not need an online server for the main features.

The database contains tables for ingredients, recipes and recipe ingredients.

### Ingredients Table

This table stores the ingredients that the user adds to their pantry.

It contains:

* id
* name
* quantity
* unit
* category
* expiry_date

### Recipes Table

This table stores the recipe information.

It contains:

* id
* name
* description
* country
* instructions
* image

### Recipe Ingredients Table

This table stores the ingredients that are needed for each recipe.

It contains:

* id
* recipe_id
* ingredient_name
* quantity
* unit

## Recipe Matching

The recipe matching feature checks whether the ingredients required by a recipe are available in the pantry.

For example, if a recipe requires minced beef, onion, tomato, milk and curry powder, all of these ingredients need to be available in the pantry for the recipe to be suggested.

If one of the required ingredients is removed, the recipe will no longer appear in the recipes that the user can make.

This was done to make sure that the application follows the strict matching requirement from the assignment.

## Main Classes

Some of the main Java classes in the application are:

* `MainActivity` - Handles the main home screen.
* `PantryActivity` - Displays and manages pantry ingredients.
* `IngredientAdapter` - Displays the pantry ingredients in the list.
* `DatabaseHelper` - Creates and manages the SQLite database.
* `RecipesActivity` - Displays the recipes.
* `RecipeAdapter` - Displays the recipe information.
* `FavoritesActivity` - Displays recipes that the user has added to favourites.

## Running the Application

### Requirements

To run the application, you need:

* Android Studio
* Java 11
* Android SDK
* An Android emulator or Android phone
* Android SDK API 24 or higher

### Setup

1. Clone the repository:

```bash
git clone https://github.com/LennyM22/SmartPantryManager.git
```

2. Open the project in Android Studio.

3. Allow Android Studio to sync the Gradle files.

4. Connect an Android phone or start an Android emulator.

5. Run the application from Android Studio.

## GitHub Repository

The source code for the project is available on GitHub:

https://github.com/LennyM22/SmartPantryManager

