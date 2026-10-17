package net.huwng.vietnamdelight.entity;

import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.network.NetworkHooks;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.item.Items;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.huwng.vietnamdelight.init.VietnamDelightModParticleTypes;
import net.huwng.vietnamdelight.init.VietnamDelightModEntities;
public class SkyLanternsEntity extends PathfinderMob {

    public SkyLanternsEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(VietnamDelightModEntities.SKY_LANTERNS.get(), world);
    }

    public SkyLanternsEntity(EntityType<? extends SkyLanternsEntity> type, Level world) {
        super(type, world);
        setNoAi(true); // không dùng AI
        this.noPhysics = true; // không va chạm
        this.setHealth(1.0F);
    }
    
	private double driftX = 0;
	private double driftZ = 0;
	
	@Override
	public void tick() {
	    super.tick();
	
	    if (!this.level().isClientSide) {
	        // 40 tick (2 giây) thì đổi hướng 1 lần
	        if (this.tickCount % 40 == 0) {
	            driftX = (random.nextDouble() - 0.5) * 0.01;
	            driftZ = (random.nextDouble() - 0.5) * 0.01;
	        }
	
	        double dy = 0.03;
	        this.setDeltaMovement(driftX, dy, driftZ);
	        this.move(MoverType.SELF, this.getDeltaMovement());
	    }
	
	    // 🔥 particle lửa nhỏ bên dưới
	    if (this.level().isClientSide) {
	        this.level().addParticle(ParticleTypes.FLAME,
	                this.getX(), this.getY() + 0.5, this.getZ(),
	                0, 0.005, 0);
	
	        // ✨ sau 3 giây (60 tick) thì phát particle flash
	        if (this.tickCount > 90) {
	            this.level().addParticle(VietnamDelightModParticleTypes.SKY_LANTERN_FLASH.get(),
	                    this.getX(), this.getY() + 0.7, this.getZ(),
	                    0, 0, 0);
	        }
	    }

		// ⏳ sau 20s thì tan biến
		if (this.tickCount > 1000) {
		    if (!this.level().isClientSide && this.level() instanceof ServerLevel serverLevel) {
		        serverLevel.sendParticles(
		            VietnamDelightModParticleTypes.LANTERN_CLOUD.get(), // 👈 dùng particle custom
		            this.getX(), this.getY() + 0.5, this.getZ(),
		            10,        // số lượng particle
		            0.5, 0.5, 0.5, // bán kính tỏa ra (spread)
		            0.1        // tốc độ
		        );
		
		        // 🔊 phát âm thanh nổ nhỏ
				serverLevel.playSound(
				    null,
				    this.blockPosition(),
				    SoundEvents.FIREWORK_ROCKET_BLAST_FAR,
				    SoundSource.NEUTRAL,
				    2F,
				    0.9F + this.random.nextFloat() * 0.2F // pitch ngẫu nhiên
				);
		    }
		    this.discard();
		}
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
	    if (!this.level().isClientSide && source.getEntity() instanceof Player) {
	        // Drop 1–3 giấy
	        int count = 1 + this.random.nextInt(3);
	        this.spawnAtLocation(new ItemStack(Items.PAPER, count));
	
	        // Particle trắng nổ tỏa ra
	        if (this.level() instanceof ServerLevel serverLevel) {
	            serverLevel.sendParticles(
	                ParticleTypes.CLOUD,  // particle trắng tỏa
	                this.getX(), this.getY() + 0.5D, this.getZ(),
	                30,  // số lượng
	                0.5D, 0.5D, 0.5D, // bán kính tỏa
	                0.1D // tốc độ lan
	            );
	        }
	
	        // Xóa entity ngay, không có animation chết
	        this.discard();
	        return true;
	    }
	    return super.hurt(source, amount);
	}

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEFINED;
    }
    
	@Override
	public boolean shouldRenderAtSqrDistance(double distance) {
	    // mặc định dùng hitbox, nhưng ta ép nó render xa hơn
	    double maxDistance = 128.0D; // 64 block
	    return distance < maxDistance * maxDistance;
	}

    // các method save/load bắt buộc phải là public
    @Override
    public void readAdditionalSaveData(CompoundTag tag) {}

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {}

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public static void init() {}

    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH, 1.0D)
                .add(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED, 0.0D);
    }
}
