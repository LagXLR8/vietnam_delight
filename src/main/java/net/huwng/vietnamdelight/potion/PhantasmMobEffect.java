package net.huwng.vietnamdelight.potion;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class PhantasmMobEffect extends MobEffect {
    public PhantasmMobEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x8A2BE2);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
    }
}
