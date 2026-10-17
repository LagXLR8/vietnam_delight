package net.huwng.vietnamdelight.item;

import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.Rarity;

public class TurmericItem extends ItemNameBlockItem {

    public TurmericItem() {
        super(
            VietnamDelightModBlocks.TURMERIC_CROP.get(),
            new Properties()
                .stacksTo(64)
                .rarity(Rarity.COMMON)
                .food(new FoodProperties.Builder()
                    .nutrition(1)
                    .saturationMod(0f)
                    .alwaysEat()
                    .effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 200), 1.0f)
                    .build())
        );
    }
}
