package net.huwng.vietnamdelight.procedures;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;

import net.huwng.vietnamdelight.init.VietnamDelightModMobEffects;

public class FishSaucePlayerFinishesUsingItemProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
			_entity.addEffect(new MobEffectInstance(VietnamDelightModMobEffects.SALTY.get(), 60, 1, false, false));
	}
}
