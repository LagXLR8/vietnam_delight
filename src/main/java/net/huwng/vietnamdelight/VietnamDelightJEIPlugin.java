package net.huwng.vietnamdelight;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;
import net.minecraft.world.item.ItemStack;

import net.huwng.vietnamdelight.CeramicClayPotRecipe;
import net.huwng.vietnamdelight.jei.CeramicClayPotCategory;
import net.huwng.vietnamdelight.jei.ModJEIRecipeTypes;

import java.util.List;
import java.util.Objects;

@JeiPlugin
public class VietnamDelightJEIPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation("vietnamdelight", "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new CeramicClayPotCategory(guiHelper));
    }
    
	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(
	        new ItemStack(VietnamDelightModBlocks.CERAMIC_CLAY_LID.get().asItem()),
	        ModJEIRecipeTypes.CERAMIC_CLAY_POT
	    );
	    registration.addRecipeCatalyst(
	        new ItemStack(VietnamDelightModBlocks.CERAMIC_CLAY_POT.get().asItem()),
	        ModJEIRecipeTypes.CERAMIC_CLAY_POT
	    );
	}
	
    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager manager = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();
        List<CeramicClayPotRecipe> recipes = manager.getAllRecipesFor(ModRecipeTypes.CERAMIC_CLAY_POT_TYPE.get());
        registration.addRecipes(ModJEIRecipeTypes.CERAMIC_CLAY_POT, recipes);
    }
}
