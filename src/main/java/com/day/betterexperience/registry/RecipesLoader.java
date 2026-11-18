package com.day.betterexperience.registry;

import com.day.betterexperience.BetterGTExperience;
import com.day.betterexperience.api.ModRecipeLoader;
import com.day.betterexperience.common.recipe.CraftingRecipe;

public final class RecipesLoader {

    public static void RegisterRecipes() {
        ModRecipeLoader[] Recipes = new ModRecipeLoader[] { new CraftingRecipe() };

        for (ModRecipeLoader Recipe : Recipes) {
            BetterGTExperience.LOG.info("Loading BGTE Recipes :{}", Recipe.name());
            Recipe.loadRecipes();
        }
    }
}
