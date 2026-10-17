package net.huwng.vietnamdelight.entity;

import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.network.NetworkHooks;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import net.huwng.vietnamdelight.init.VietnamDelightModItems;
import net.huwng.vietnamdelight.init.VietnamDelightModEntities;
import net.huwng.vietnamdelight.init.VietnamDelightModMobEffects;

public class SaltEntityEntity extends ThrowableItemProjectile {
    public SaltEntityEntity(EntityType<? extends SaltEntityEntity> type, Level world) {
        super(type, world);
    }

    public SaltEntityEntity(Level world, LivingEntity shooter) {
        super(VietnamDelightModEntities.SALT_ENTITY.get(), shooter, world);
    }

    public SaltEntityEntity(PlayMessages.SpawnEntity packet, Level world) {
        super(VietnamDelightModEntities.SALT_ENTITY.get(), world);
    }

    @Override
    protected Item getDefaultItem() {
        return VietnamDelightModItems.SALT.get();
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void onHitEntity(net.minecraft.world.phys.EntityHitResult result) {
        super.onHitEntity(result);
        if (result.getEntity() instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(VietnamDelightModMobEffects.SALTY.get(), 200, 0));
        }
        makePoof();
        this.discard();
    }

    @Override
    protected void onHitBlock(net.minecraft.world.phys.BlockHitResult result) {
        super.onHitBlock(result);
        makePoof();
        this.discard();
    }

    private void makePoof() {
        if (this.level().isClientSide) {
            for (int i = 0; i < 8; i++) {
                this.level().addParticle(
                        ParticleTypes.POOF,
                        this.getX(), this.getY(), this.getZ(),
                        (this.random.nextDouble() - 0.5D) * 0.1D,
                        (this.random.nextDouble() - 0.5D) * 0.1D,
                        (this.random.nextDouble() - 0.5D) * 0.1D
                );
            }
        }
    }
}
