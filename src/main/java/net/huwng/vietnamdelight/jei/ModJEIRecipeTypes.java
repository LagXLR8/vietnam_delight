package net.huwng.vietnamdelight.jei;

import mezz.jei.api.recipe.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.huwng.vietnamdelight.CeramicClayPotRecipe;

public class ModJEIRecipeTypes {
    public static final RecipeType<CeramicClayPotRecipe> CERAMIC_CLAY_POT =
            RecipeType.create("vietnamdelight", "ceramic_clay_pot", CeramicClayPotRecipe.class);
}
