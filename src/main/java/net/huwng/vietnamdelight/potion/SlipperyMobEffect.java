package net.huwng.vietnamdelight.potion;

import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.GuiGraphics;

public class SlipperyMobEffect extends MobEffect {
    public SlipperyMobEffect() {
        super(MobEffectCategory.NEUTRAL, 0xD4B133);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.onGround() && !entity.isInWater()) {
            RandomSource rand = entity.getCommandSenderWorld().getRandom();
            float amount = rand.nextFloat() * (0.6F + amplifier * 0.2F);
            float x = 0.0F;
            float z = 0.0F;
            if (rand.nextBoolean())
                amount *= -1;
            if (rand.nextBoolean())
                x = amount;
            else
                z = amount;

            entity.setDeltaMovement(entity.getDeltaMovement().add(x, 0.0F, z));
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
    
    @Override
    public void initializeClient(java.util.function.Consumer<IClientMobEffectExtensions> consumer) {
        consumer.accept(new IClientMobEffectExtensions() {
            @Override
            public boolean isVisibleInInventory(MobEffectInstance effect) {
                return false;
            }

            @Override
            public boolean renderInventoryText(MobEffectInstance instance, EffectRenderingInventoryScreen<?> screen, GuiGraphics guiGraphics, int x, int y, int blitOffset) {
                return false;
            }

            @Override
            public boolean isVisibleInGui(MobEffectInstance effect) {
                return false;
            }
        });
    }
}
