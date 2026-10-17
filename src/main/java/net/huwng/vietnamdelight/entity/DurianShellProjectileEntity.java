package net.huwng.vietnamdelight.entity;

import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import net.huwng.vietnamdelight.init.VietnamDelightModEntities;
import net.huwng.vietnamdelight.init.VietnamDelightModItems;

import java.util.List;

public class DurianShellProjectileEntity extends ThrowableItemProjectile {

    private static final EntityDataAccessor<Boolean> DATA_GROUNDED = SynchedEntityData.defineId(DurianShellProjectileEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> DATA_GROUND_Y_ROT = SynchedEntityData.defineId(DurianShellProjectileEntity.class, EntityDataSerializers.FLOAT);

    private int groundTicks = 0;

    public DurianShellProjectileEntity(EntityType<? extends DurianShellProjectileEntity> type, Level world) {
        super(type, world);
    }

    public DurianShellProjectileEntity(EntityType<? extends DurianShellProjectileEntity> type, LivingEntity shooter, Level world) {
        super(type, shooter, world);
    }

    public DurianShellProjectileEntity(Level world, LivingEntity shooter) {
        this(VietnamDelightModEntities.DURIAN_SHELL_PROJECTILE.get(), shooter, world);
    }

    public DurianShellProjectileEntity(PlayMessages.SpawnEntity packet, Level world) {
        super(VietnamDelightModEntities.DURIAN_SHELL_PROJECTILE.get(), world);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_GROUNDED, false);
        this.entityData.define(DATA_GROUND_Y_ROT, 0.0F);
    }

    public boolean isGrounded() {
        return this.entityData.get(DATA_GROUNDED);
    }

    public void setGrounded(boolean grounded) {
        this.entityData.set(DATA_GROUNDED, grounded);
    }

    public float getGroundYRot() {
        return this.entityData.get(DATA_GROUND_Y_ROT);
    }

    public void setGroundYRot(float yRot) {
        this.entityData.set(DATA_GROUND_Y_ROT, yRot);
    }

    @Override
    protected Item getDefaultItem() {
        return VietnamDelightModItems.DURIAN_SHELL.get();
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        if (entity == this.getOwner() && this.tickCount < 15) {
            return false;
        }
        return super.canHitEntity(entity);
    }

    @Override
    public void tick() {
        if (this.isGrounded()) {
            // Khi đã nằm trên mặt đất
            this.setDeltaMovement(Vec3.ZERO);
            this.groundTicks++;

            if (!this.level().isClientSide) {
                // Kiểm tra sinh vật giẫm / chạm vào vỏ sầu riêng trên mặt đất
                List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class,
                        this.getBoundingBox().inflate(0.2D, 0.15D, 0.2D),
                        entity -> entity.isAlive() && !entity.isSpectator());

                for (LivingEntity target : targets) {
                    if (target == this.getOwner() && this.groundTicks < 20) {
                        continue; // Người ném có 1 giây an toàn
                    }

                    // Gây 2 sát thương khi giẫm vào vỏ trên đất
                    target.hurt(this.damageSources().thrown(this, this.getOwner()), 2.0F);

                    spawnHitParticles();
                    this.discard();
                    return;
                }

                // Sau 10 giây (200 ticks = 10s), biến mất không tạo hạt poof
                if (this.groundTicks >= 200) {
                    this.discard();
                    return;
                }
            }
            return;
        }

        super.tick();
    }

    @Override
    protected void onHit(HitResult result) {
        if (this.isRemoved()) {
            return;
        }

        HitResult.Type type = result.getType();
        if (type == HitResult.Type.ENTITY) {
            this.onHitEntity((EntityHitResult) result);
        } else if (type == HitResult.Type.BLOCK) {
            this.onHitBlock((BlockHitResult) result);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (this.isRemoved()) {
            return;
        }

        if (result.getEntity() == this.getOwner() && this.tickCount < 15) {
            return;
        }

        // Gây 2 sát thương khi ném trúng mục tiêu (chỉ 1 lần)
        result.getEntity().hurt(this.damageSources().thrown(this, this.getOwner()), 2.0F);

        spawnHitParticles();
        // Hủy ngay lập tức để không bị rớt xuống đất gây damage lần 2
        this.discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (this.isRemoved() || this.isGrounded()) {
            return;
        }

        // Nếu chạm mặt trên của block rắn (mặt đất)
        if (result.getDirection() == Direction.UP || this.onGround()) {
            this.setGrounded(true);
            // Xoay chiều ngẫu nhiên khi nằm dưới đất
            this.setGroundYRot(this.random.nextFloat() * 360.0F);
            this.setPos(this.getX(), result.getLocation().y + 0.02D, this.getZ());
            this.setDeltaMovement(Vec3.ZERO);

            spawnLandParticles();
        } else {
            // Đập vào tường / trần nhà
            spawnHitParticles();
            this.discard();
        }
    }

    private void spawnHitParticles() {
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, this.getItem()),
                    this.getX(), this.getY() + 0.2, this.getZ(),
                    10, 0.2, 0.2, 0.2, 0.08);
        }
    }

    private void spawnLandParticles() {
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, this.getItem()),
                    this.getX(), this.getY() + 0.05, this.getZ(),
                    6, 0.15, 0.05, 0.15, 0.02);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Grounded", this.isGrounded());
        tag.putInt("GroundTicks", this.groundTicks);
        tag.putFloat("GroundYRot", this.getGroundYRot());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setGrounded(tag.getBoolean("Grounded"));
        this.groundTicks = tag.getInt("GroundTicks");
        this.setGroundYRot(tag.getFloat("GroundYRot"));
    }
}
