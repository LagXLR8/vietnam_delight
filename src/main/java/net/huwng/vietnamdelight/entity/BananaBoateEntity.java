package net.huwng.vietnamdelight.entity;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.network.NetworkHooks;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;

import net.huwng.vietnamdelight.init.VietnamDelightModEntities;
import net.huwng.vietnamdelight.init.VietnamDelightModItems;

public class BananaBoateEntity extends PathfinderMob {
	private int waterTicks = 0;

	public BananaBoateEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(VietnamDelightModEntities.BANANA_BOATE.get(), world);
	}

	public BananaBoateEntity(EntityType<BananaBoateEntity> type, Level world) {
		super(type, world);
		setMaxUpStep(0.1f);
		xpReward = 0;
		setNoAi(true);
		setHealth(1.0F);
		this.noPhysics = true;
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override public MobType getMobType() { return MobType.UNDEFINED; }

	@Override
	public SoundEvent getDeathSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("ambient.underwater.exit"));
	}

	@Override public boolean shouldShowName() { return false; }
	@Override public boolean isCustomNameVisible() { return false; }
	@Override public boolean displayFireAnimation() { return false; }
	@Override protected boolean shouldDespawnInPeaceful() { return false; }
	@Override public boolean isNoGravity() { return true; } // không bị chìm tự do

	@Override
	public void tick() {
	    super.tick();
	
			if (!this.level().isClientSide) {
			// Nếu block bên dưới KHÔNG phải nước -> coi như lên bờ/đâm bờ -> rơi item
			BlockPos below = this.blockPosition().below();
			if (!this.level().getFluidState(below).is(FluidTags.WATER)) {
				dropAndRemove();
				return;
			}

			// Đang ở trên mặt nước
			if (this.isInWater() || this.level().getBlockState(below).is(Blocks.WATER)) {
				waterTicks++;

				// Neo Y gần mặt nước, không neo X/Z để không "đứng im"
				double waterY = this.level().getHeightmapPos(
						net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING,
						this.blockPosition()
				).getY();
				// Chỉ hiệu chỉnh khi tụt xuống
				if (this.getY() < waterY + 0.01) {
					this.setPos(this.getX(), waterY + 0.01, this.getZ());
				}

				// Đi thẳng theo hướng nhìn
				float yawRad = (float) Math.toRadians(this.getYRot());
				double speed = 0.10D; // tốc độ trôi
				double dx = -Math.sin(yawRad) * speed;
				double dz =  Math.cos(yawRad) * speed;

				this.setDeltaMovement(dx, 0.0D, dz);
				this.move(MoverType.SELF, this.getDeltaMovement()); // ★ QUAN TRỌNG: thực sự di chuyển khi noPhysics=true

				// Thỉnh thoảng quẹo nhẹ
				if (this.tickCount % 20 == 0) {
					float deltaYaw = (this.random.nextFloat() - 0.5f) * 10f; // ±5°
					this.setYRot(this.getYRot() + deltaYaw);
					this.yHeadRot = this.getYRot();
				}
				// Particle "wake"
				if (this.tickCount % 5 == 0) {
					((ServerLevel) this.level()).sendParticles(
							ParticleTypes.FISHING,
							this.getX(), this.getY(), this.getZ(),
							2,
							0.1, 0.0, 0.1,
							0.01
					);
				}
	
	            // Âm thanh bơi
	            if (this.tickCount % 20 == 0) {
	                this.playSound(SoundEvents.PLAYER_SWIM, 0.2F, 1.0F);
	            }
	            
	            // Kiểm tra va chạm phía trước
	            int aheadX = Mth.floor(this.getX() - Math.sin(yawRad));
	            int aheadY = this.blockPosition().getY();
	            int aheadZ = Mth.floor(this.getZ() + Math.cos(yawRad));
	            BlockPos ahead = new BlockPos(aheadX, aheadY, aheadZ);
	
	            if (!this.level().getBlockState(ahead).isAir() &&
	                !this.level().getBlockState(ahead).is(Blocks.WATER)) {
	                dropAndRemove();
	                return;
	            }
	
	            // Boat hỏng sau ~20s
	            if (waterTicks > 400) {
	                dropAndRemove();
	            }
	
	        } else {
	            waterTicks = 0;
	        }
	    }
	}

	private void dropAndRemove() {
		if (!this.level().isClientSide && this.isAlive()) {
			this.spawnAtLocation(new ItemStack(VietnamDelightModItems.BANANA_BOAT.get()));
			this.discard();
		}
	}

	@Override
	protected boolean canRide(Entity entity) { return false; }

	@Override
	public boolean isPushable() { return false; }

	@Override
	protected void pushEntities() { /* no-op */ }

	@Override
	protected void tickDeath() {
		this.remove(RemovalReason.KILLED); // không nghiêng khi chết
	}

	// Bị người chơi đập -> rơi item
	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (!this.level().isClientSide && source.getEntity() instanceof net.minecraft.world.entity.player.Player) {
			dropAndRemove();
		}
		return false; // không nhận damage -> không overlay đỏ
	}

	public static void init() {}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.2);
		builder = builder.add(Attributes.MAX_HEALTH, 1);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 0);
		builder = builder.add(Attributes.FOLLOW_RANGE, 16);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 3);
		return builder;
	}
}
