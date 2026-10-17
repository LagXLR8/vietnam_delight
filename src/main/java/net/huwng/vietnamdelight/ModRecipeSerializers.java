package net.huwng.vietnamdelight;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.huwng.vietnamdelight.recipe.TypedMooncakeCraftingRecipe;
import net.huwng.vietnamdelight.recipe.TypedMooncakeSmeltingRecipe;

public class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, "vietnam_delight");

    public static final RegistryObject<RecipeSerializer<CeramicClayPotRecipe>> CERAMIC_CLAY_POT_SERIALIZER =
            SERIALIZERS.register(CeramicClayPotRecipe.Type.ID,
                    () -> new CeramicClayPotRecipe.Serializer());
                    
    public static final RegistryObject<RecipeSerializer<TypedMooncakeCraftingRecipe>> 
        TYPED_MOONCAKE_CRAFTING = SERIALIZERS.register("typed_mooncake_crafting",
            () -> new TypedMooncakeCraftingRecipe.Serializer());
    
    public static final RegistryObject<RecipeSerializer<TypedMooncakeSmeltingRecipe>> 
        TYPED_MOONCAKE_SMELTING = SERIALIZERS.register("typed_mooncake_smelting",
            () -> new TypedMooncakeSmeltingRecipe.Serializer());
}
