package net.huwng.vietnamdelight.procedures;

import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.minecraft.world.entity.LivingEntity;

import net.huwng.vietnamdelight.init.VietnamDelightModMobEffects;

@Mod.EventBusSubscriber
public class SaltyHealCancelProcedure {
    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.hasEffect(VietnamDelightModMobEffects.SALTY.get())) {
            event.setCanceled(true); // chặn hồi máu tự nhiên
        }
    }
}
