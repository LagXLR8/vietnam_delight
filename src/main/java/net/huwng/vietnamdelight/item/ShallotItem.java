package net.huwng.vietnamdelight.item;

import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;
import net.huwng.vietnamdelight.init.VietnamDelightModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

public class ShallotItem extends ItemNameBlockItem {

    public ShallotItem() {
        super(
            VietnamDelightModBlocks.SHALLOT_CROP.get(),
            new Properties()
                .stacksTo(64)
                .rarity(Rarity.COMMON)
                .food(new FoodProperties.Builder()
                    .nutrition(1)
                    .saturationMod(0.1f)
                    .alwaysEat()
                    .build())
        );
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, world, entity);
        if (!world.isClientSide && entity instanceof Player player) {
            player.addEffect(new MobEffectInstance(
                VietnamDelightModMobEffects.SMELL.get(), 1200, 0, false, false));
        }
        return result;
    }
}
