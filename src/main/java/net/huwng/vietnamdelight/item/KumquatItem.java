package net.huwng.vietnamdelight.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import net.huwng.vietnamdelight.init.VietnamDelightModItems;

import java.util.Random;

public class KumquatItem extends Item {
    public KumquatItem() {
        super(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.COMMON)
            .food(new FoodProperties.Builder()
                .nutrition(1)
                .saturationMod(0.1f)
                .alwaysEat()
                .build()));
    }

    @Override
    public int getUseDuration(ItemStack itemstack) {
        return 16;
    }

@Override
public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entity) {
    ItemStack result = super.finishUsingItem(stack, world, entity);

    if (!world.isClientSide && entity instanceof Player player) {
        // 30% tỉ lệ rơi ra Kumquat Seeds
        if (new Random().nextFloat() < 0.3f) {
            player.drop(new ItemStack(VietnamDelightModItems.KUMQUAT_SEEDS.get()), false);

            // Phát âm thanh llama spit cho người chơi gần đó
            world.playSound(
                null,
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.LLAMA_SPIT,
                SoundSource.PLAYERS,
                0.5F,
                1.0F
            );
        }
    }

    return result;
}
}
