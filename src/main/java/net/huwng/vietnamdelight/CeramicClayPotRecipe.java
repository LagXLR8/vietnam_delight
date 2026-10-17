package net.huwng.vietnamdelight;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.core.RegistryAccess;

import net.huwng.vietnamdelight.ModRecipeSerializers;
import net.huwng.vietnamdelight.ModRecipeTypes;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class CeramicClayPotRecipe implements Recipe<SimpleContainer> {
    private final ResourceLocation id;
    private final NonNullList<Ingredient> ingredients;
    private final ItemStack result;
    private final int cookingTime;
	private final float experience;
	
	public CeramicClayPotRecipe(ResourceLocation id, NonNullList<Ingredient> ingredients, ItemStack result, int cookingTime, float experience) {
	    this.id = id;
	    this.ingredients = ingredients;
	    this.result = result;
	    this.cookingTime = cookingTime;
	    this.experience = experience;
	}
	
	public float getExperience() {
	    return experience;
	}

	@Override
	public boolean matches(SimpleContainer container, Level level) {
	    if (level.isClientSide) return false;
	    List<Ingredient> remaining = new ArrayList<>(ingredients);
	    for (int slot = 0; slot < 6; slot++) {
	        ItemStack stack = container.getItem(slot);
	        if (stack.isEmpty()) continue;
	        boolean matched = false;
	        Iterator<Ingredient> iter = remaining.iterator();
	        while (iter.hasNext()) {
	            Ingredient ing = iter.next();
	            if (ing.test(stack)) {
	                iter.remove();
	                matched = true;
	                break;
	            }
	        }
	        if (!matched) return false;
	    }
	
	    return remaining.isEmpty();
	}

    @Override
    public ItemStack assemble(SimpleContainer container, RegistryAccess registryAccess) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return result.copy();
    }

    public int getCookingTime() {
        return cookingTime;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }
    
	@Override
	public NonNullList<Ingredient> getIngredients() {
	    return ingredients;
	}

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.CERAMIC_CLAY_POT_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.CERAMIC_CLAY_POT_TYPE.get();
    }

    public static class Type implements RecipeType<CeramicClayPotRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "ceramic_clay_pot";
    }

    public static class Serializer implements RecipeSerializer<CeramicClayPotRecipe> {
        public static final Serializer INSTANCE = new Serializer();

		@Override
		public CeramicClayPotRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
		    JsonArray ingredientsArray = GsonHelper.getAsJsonArray(json, "ingredients");
		    NonNullList<Ingredient> ingredients = NonNullList.withSize(ingredientsArray.size(), Ingredient.EMPTY);
		    for (int i = 0; i < ingredientsArray.size(); i++) {
		        ingredients.set(i, Ingredient.fromJson(ingredientsArray.get(i)));
		    }
		    ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
		    int time = GsonHelper.getAsInt(json, "cookingtime", 200);
		    float xp = GsonHelper.getAsFloat(json, "experience", 0.0F);
		    return new CeramicClayPotRecipe(recipeId, ingredients, result, time, xp);
		}
		
		@Override
		public CeramicClayPotRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buf) {
		    int size = buf.readInt();
		    NonNullList<Ingredient> ingredients = NonNullList.withSize(size, Ingredient.EMPTY);
		    for (int i = 0; i < size; i++) {
		        ingredients.set(i, Ingredient.fromNetwork(buf));
		    }
		    ItemStack result = buf.readItem();
		    int time = buf.readInt();
		    float xp = buf.readFloat();
		    return new CeramicClayPotRecipe(recipeId, ingredients, result, time, xp);
		}
		
		@Override
		public void toNetwork(FriendlyByteBuf buf, CeramicClayPotRecipe recipe) {
		    buf.writeInt(recipe.ingredients.size());
		    for (Ingredient ing : recipe.ingredients) {
		        ing.toNetwork(buf);
		    }
		    buf.writeItem(recipe.result);
		    buf.writeInt(recipe.cookingTime);
		    buf.writeFloat(recipe.experience);
		}
    }
}
