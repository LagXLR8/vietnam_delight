package net.huwng.vietnamdelight.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.huwng.vietnamdelight.VietnamDelightMod;
import net.huwng.vietnamdelight.CeramicClayPotRecipe;
import net.huwng.vietnamdelight.init.VietnamDelightModItems;
import net.huwng.vietnamdelight.jei.ModJEIRecipeTypes;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
public class CeramicClayPotCategory implements IRecipeCategory<CeramicClayPotRecipe> {

    public static final ResourceLocation UID = new ResourceLocation(VietnamDelightMod.MODID, "ceramic_clay_pot");
    public static final ResourceLocation TEXTURE = new ResourceLocation(VietnamDelightMod.MODID, "textures/gui/jei_ceramic_pot.png");

    private final IDrawable background;
    private final IDrawable icon;

    public CeramicClayPotCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(TEXTURE, 0, 0, 118, 58);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(VietnamDelightModItems.CERAMIC_CLAY_POT.get()));
    }

    @Override
    public RecipeType<CeramicClayPotRecipe> getRecipeType() {
        return ModJEIRecipeTypes.CERAMIC_CLAY_POT;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.vietnamdelight.ceramic_clay_pot");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, CeramicClayPotRecipe recipe, IFocusGroup focuses) {
	    List<Ingredient> inputs = recipe.getIngredients();
	    int index = 0;
	    for (int row = 0; row < 2; row++) {
	        for (int col = 0; col < 3; col++) {
	            if (index < inputs.size()) {
	                builder.addSlot(RecipeIngredientRole.INPUT, 5 + col * 18, 3 + row * 18)
	                       .addIngredients(inputs.get(index));
	                index++;
	            }
	        }
	    }
	    builder.addSlot(RecipeIngredientRole.OUTPUT, 91, 14)
	           .addItemStack(recipe.getResultItem(null));
	}
}
