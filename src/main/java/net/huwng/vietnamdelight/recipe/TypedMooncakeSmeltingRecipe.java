package net.huwng.vietnamdelight.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.Container;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import com.google.gson.JsonObject;
import net.huwng.vietnamdelight.item.UnbakedMooncakeItem;
import net.huwng.vietnamdelight.ModRecipeSerializers;

public class TypedMooncakeSmeltingRecipe extends SmeltingRecipe {
    private final String requiredType;
    
    public TypedMooncakeSmeltingRecipe(ResourceLocation id, String group, CookingBookCategory category,
                                       Ingredient ingredient, ItemStack result, float experience, 
                                       int cookingTime, String requiredType) {
        super(id, group, category, ingredient, result, experience, cookingTime);
        this.requiredType = requiredType;
    }

    @Override
    public boolean matches(Container container, net.minecraft.world.level.Level level) {
        if (!super.matches(container, level)) {
            return false;
        }
        
        ItemStack input = container.getItem(0);
        String inputType = UnbakedMooncakeItem.getMooncakeType(input);
        
        return requiredType.equals(inputType);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.TYPED_MOONCAKE_SMELTING.get();
    }
    
    public static class Serializer implements RecipeSerializer<TypedMooncakeSmeltingRecipe> {
        
        @Override
        public TypedMooncakeSmeltingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            SmeltingRecipe baseRecipe = RecipeSerializer.SMELTING_RECIPE.fromJson(recipeId, json);
            
            String requiredType = json.has("required_type") ? 
                json.get("required_type").getAsString() : "plain";
            
            return new TypedMooncakeSmeltingRecipe(
                recipeId,
                baseRecipe.getGroup(),
                baseRecipe.category(),
                baseRecipe.getIngredients().get(0),
                baseRecipe.getResultItem(RegistryAccess.EMPTY),
                baseRecipe.getExperience(),
                baseRecipe.getCookingTime(),
                requiredType
            );
        }

        @Override
        public TypedMooncakeSmeltingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            SmeltingRecipe baseRecipe = RecipeSerializer.SMELTING_RECIPE.fromNetwork(recipeId, buffer);
            String requiredType = buffer.readUtf();
            
            return new TypedMooncakeSmeltingRecipe(
                recipeId,
                baseRecipe.getGroup(),
                baseRecipe.category(),
                baseRecipe.getIngredients().get(0),
                baseRecipe.getResultItem(RegistryAccess.EMPTY),
                baseRecipe.getExperience(),
                baseRecipe.getCookingTime(),
                requiredType
            );
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, TypedMooncakeSmeltingRecipe recipe) {
            RecipeSerializer.SMELTING_RECIPE.toNetwork(buffer, recipe);
            buffer.writeUtf(recipe.requiredType);
        }
    }
}