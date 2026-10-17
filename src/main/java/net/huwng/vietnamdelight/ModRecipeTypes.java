package net.huwng.vietnamdelight;

import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, "vietnam_delight");

    public static final RegistryObject<RecipeType<CeramicClayPotRecipe>> CERAMIC_CLAY_POT_TYPE =
            RECIPE_TYPES.register(CeramicClayPotRecipe.Type.ID,
                    () -> CeramicClayPotRecipe.Type.INSTANCE);
}
