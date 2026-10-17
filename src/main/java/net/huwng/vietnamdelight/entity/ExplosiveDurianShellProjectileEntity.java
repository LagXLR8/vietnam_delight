package net.huwng.vietnamdelight.entity;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PlayMessages;
import net.huwng.vietnamdelight.init.VietnamDelightModEntities;
import net.huwng.vietnamdelight.init.VietnamDelightModItems;

import java.util.List;

public class ExplosiveDurianShellProjectileEntity extends DurianShellProjectileEntity {

    private int explosiveGroundTicks = 0;

    public ExplosiveDurianShellProjectileEntity(EntityType<? extends ExplosiveDurianShellProjectileEntity> type, Level world) {
        super(type, world);
    }

    public ExplosiveDurianShellProjectileEntity(Level world, LivingEntity shooter) {
        super(VietnamDelightModEntities.EXPLOSIVE_DURIAN_SHELL_PROJECTILE.get(), shooter, world);
    }

    public ExplosiveDurianShellProjectileEntity(PlayMessages.SpawnEntity packet, Level world) {
        super(VietnamDelightModEntities.EXPLOSIVE_DURIAN_SHELL_PROJECTILE.get(), world);
    }

    @Override
    protected Item getDefaultItem() {
        return VietnamDelightModItems.EXPLOSIVE_DURIAN_SHELL.get();
    }

    @Override
    public void tick() {
        if (this.isGrounded()) {
            this.setDeltaMovement(Vec3.ZERO);
            this.explosiveGroundTicks++;

            if (!this.level().isClientSide) {
                // Kiểm tra sinh vật giẫm vào vỏ sầu riêng nổ trên đất
                List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class,
                        this.getBoundingBox().inflate(0.25D, 0.15D, 0.25D),
                        entity -> entity.isAlive() && !entity.isSpectator());

                for (LivingEntity target : targets) {
                    if (target == this.getOwner() && this.explosiveGroundTicks < 20) {
                        continue; // Người ném có 1 giây an toàn
                    }

                    // Giẫm phải: gây đúng chuẩn 5.0 damage (2.5 tim)
                    target.hurt(this.damageSources().thrown(this, this.getOwner()), 5.0F);

                    triggerExplosionEffect();
                    this.discard();
                    return;
                }

                // Sau 10 giây (200 ticks = 10s) biến mất
                if (this.explosiveGroundTicks >= 200) {
                    this.discard();
                    return;
                }
            }
            return;
        }

        super.tick();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (this.isRemoved()) {
            return;
        }

        if (result.getEntity() == this.getOwner() && this.tickCount < 15) {
            return;
        }

        // Ném trúng entity: gây đúng chuẩn 4.0 damage (2 tim)
        result.getEntity().hurt(this.damageSources().thrown(this, this.getOwner()), 4.0F);

        triggerExplosionEffect();
        this.discard();
    }

    private void triggerExplosionEffect() {
        if (this.level() instanceof ServerLevel serverLevel) {
            // Hiệu ứng khói nổ và chùm mảnh vỡ vỏ sầu riêng
            serverLevel.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY() + 0.1, this.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
            serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, this.getItem()),
                    this.getX(), this.getY() + 0.2, this.getZ(),
                    12, 0.2, 0.2, 0.2, 0.1);
        }
        // Âm thanh nổ vang
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 1.0F, 1.2F);
    }
}
