package net.huwng.vietnamdelight.potion;

import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;

public class SaltyMobEffect extends MobEffect {
    public SaltyMobEffect() {
        super(MobEffectCategory.HARMFUL, 0xCFCFCF); // màu xám nhạt
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true; // luôn tick
    }

    @Override
    public void initializeClient(java.util.function.Consumer<IClientMobEffectExtensions> consumer) {
        consumer.accept(new IClientMobEffectExtensions() {
            @Override
            public boolean isVisibleInGui(MobEffectInstance effect) {
                return true; // hiện icon ở HUD
            }
        });
    }
}
