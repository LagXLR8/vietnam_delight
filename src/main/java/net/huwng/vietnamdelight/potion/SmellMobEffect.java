package net.huwng.vietnamdelight.potion;

import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;

import java.util.List;

public class SmellMobEffect extends MobEffect {
    public SmellMobEffect() {
        super(MobEffectCategory.NEUTRAL, -13408768);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // Tick mỗi 5 giây (100 ticks)
        return duration % 100 == 0;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.level().isClientSide && entity instanceof Player player) {
            Level level = player.level();
            List<Villager> nearbyVillagers = level.getEntitiesOfClass(Villager.class, player.getBoundingBox().inflate(3.0));

            for (Villager villager : nearbyVillagers) {
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(
                        ParticleTypes.ANGRY_VILLAGER,
                        villager.getX(), villager.getY() + 1.0, villager.getZ(),
                        1, 0.2, 0.5, 0.2, 0.0
                    );
                }
            }
        }
    }

    @Override
    public void initializeClient(java.util.function.Consumer<IClientMobEffectExtensions> consumer) {
        consumer.accept(new IClientMobEffectExtensions() {
            @Override
            public boolean isVisibleInGui(MobEffectInstance effect) {
                return false;
            }
        });
    }
}
