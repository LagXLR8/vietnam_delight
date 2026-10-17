package net.huwng.vietnamdelight.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import com.google.gson.JsonObject;
import net.huwng.vietnamdelight.item.UnbakedMooncakeItem;
import net.huwng.vietnamdelight.ModRecipeSerializers;

public class TypedMooncakeCraftingRecipe extends ShapelessRecipe {
    private final String mooncakeType;
    
    public TypedMooncakeCraftingRecipe(ResourceLocation id, String group, CraftingBookCategory category,
                                       ItemStack result, NonNullList<Ingredient> ingredients, 
                                       String mooncakeType) {
        super(id, group, category, result, ingredients);
        this.mooncakeType = mooncakeType;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        ItemStack result = super.assemble(container, registryAccess);
        return UnbakedMooncakeItem.setMooncakeType(result, mooncakeType);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.TYPED_MOONCAKE_CRAFTING.get();
    }
    
    public static class Serializer implements RecipeSerializer<TypedMooncakeCraftingRecipe> {
        
        @Override
        public TypedMooncakeCraftingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            ShapelessRecipe baseRecipe = RecipeSerializer.SHAPELESS_RECIPE.fromJson(recipeId, json);
            
            String mooncakeType = json.has("mooncake_type") ? 
                json.get("mooncake_type").getAsString() : "plain";
            
            return new TypedMooncakeCraftingRecipe(
                recipeId,
                baseRecipe.getGroup(),
                baseRecipe.category(),
                baseRecipe.getResultItem(null),
                baseRecipe.getIngredients(),
                mooncakeType
            );
        }

        @Override
        public TypedMooncakeCraftingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            ShapelessRecipe baseRecipe = RecipeSerializer.SHAPELESS_RECIPE.fromNetwork(recipeId, buffer);
            String mooncakeType = buffer.readUtf();
            
            return new TypedMooncakeCraftingRecipe(
                recipeId,
                baseRecipe.getGroup(),
                baseRecipe.category(),
                baseRecipe.getResultItem(null),
                baseRecipe.getIngredients(),
                mooncakeType
            );
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, TypedMooncakeCraftingRecipe recipe) {
            RecipeSerializer.SHAPELESS_RECIPE.toNetwork(buffer, recipe);
            buffer.writeUtf(recipe.mooncakeType);
        }
    }
}