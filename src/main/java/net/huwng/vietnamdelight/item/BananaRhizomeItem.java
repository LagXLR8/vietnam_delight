package net.huwng.vietnamdelight.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.ChatFormatting;
import javax.annotation.Nullable;
import java.util.List;

public class BananaRhizomeItem extends Item {
    public BananaRhizomeItem() {
        super(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.COMMON)
            .food(new FoodProperties.Builder()
                .nutrition(2)
                .saturationMod(0.2f)
                .alwaysEat()
                .build()));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);

        if (!level.isClientSide && entity instanceof Player player) {
            player.setAirSupply(player.getMaxAirSupply());
            for (int i = 0; i < 10; i++) {
                double dx = level.random.nextGaussian() * 0.02;
                double dy = level.random.nextDouble() * 0.1;
                double dz = level.random.nextGaussian() * 0.02;
                ((ServerLevel) level).sendParticles(ParticleTypes.BUBBLE, player.getX(), player.getY() + 0.5, player.getZ(), 1, dx, dy, dz, 0.1);
            }
            level.playSound(null, player.blockPosition(), SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundSource.PLAYERS, 1.0F, 1.0F);
     	}
        return result;
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Oxygen supply").withStyle(ChatFormatting.BLUE));
    }
}
