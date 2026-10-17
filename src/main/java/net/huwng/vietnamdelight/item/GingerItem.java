package net.huwng.vietnamdelight.item;

import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.LivingEntity;

public class GingerItem extends ItemNameBlockItem {

    public GingerItem() {
        super(
            VietnamDelightModBlocks.GINGERS_CROP.get(),
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
