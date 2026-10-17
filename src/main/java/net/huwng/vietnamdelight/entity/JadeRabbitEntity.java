package net.huwng.vietnamdelight.entity;

import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.Level;
import net.minecraft.nbt.CompoundTag;

import net.huwng.vietnamdelight.init.VietnamDelightModEntities;
import net.huwng.vietnamdelight.init.VietnamDelightModItems;
import net.huwng.vietnamdelight.init.VietnamDelightModParticles;
import net.huwng.vietnamdelight.init.VietnamDelightModParticleTypes;

public class JadeRabbitEntity extends Rabbit {
    private static final int COOLDOWN_TICKS = 300;
    private int lastFeedTime = 0;
    private int disappearTimer = 0;
    private static final int DISAPPEAR_DURATION = 60;

    public JadeRabbitEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(VietnamDelightModEntities.JADE_RABBIT.get(), world);
    }

    public JadeRabbitEntity(EntityType<? extends Rabbit> type, Level world) {
        super(type, world);
        this.setMaxUpStep(0.6f);
        this.xpReward = 2;
        this.setNoAi(false);
        this.refreshDimensions();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.lastFeedTime > 0) {
            this.lastFeedTime--;
        }

        if (!this.level().isClientSide) {
            long dayTime = this.level().getDayTime() % 24000;
            boolean isDaytime = dayTime >= 0 && dayTime < 12000;
            
            if (isDaytime) {
                this.disappearTimer++;
                if (this.disappearTimer > 0 && this.disappearTimer <= DISAPPEAR_DURATION) {
                    if (this.disappearTimer % 4 == 0) {
                        ((ServerLevel) this.level()).sendParticles(
                            ParticleTypes.CLOUD,
                            this.getX(), this.getY() + 0.5, this.getZ(),
                            3,
                            0.3, 0.2, 0.3,
                            0.02
                        );
                    }
                    this.setInvisible(this.disappearTimer > DISAPPEAR_DURATION / 2);
                }
                if (this.disappearTimer >= DISAPPEAR_DURATION) {
                    ((ServerLevel) this.level()).sendParticles(
                        ParticleTypes.POOF,
                        this.getX(), this.getY() + 0.5, this.getZ(),
                        8,
                        0.5, 0.3, 0.5,
                        0.1
                    );
                    this.level().playSound(null, this.getX(), this.getY(), this.getZ(), 
                                         SoundEvents.GENERIC_EXTINGUISH_FIRE, this.getSoundSource(), 
                                         0.5F, 1.2F);
                    this.discard();
                }
            } else {
                this.disappearTimer = 0;
                this.setInvisible(false);
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("LastFeedTime", this.lastFeedTime);
        compound.putInt("DisappearTimer", this.disappearTimer);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.lastFeedTime = compound.getInt("LastFeedTime");
        this.disappearTimer = compound.getInt("DisappearTimer");
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (this.disappearTimer > 0) {
            return InteractionResult.PASS;
        }
        if (stack.is(VietnamDelightModItems.BAKED_MOONCAKE.get())) {
            if (this.lastFeedTime > 0) {
                return InteractionResult.PASS;
            }
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            this.lastFeedTime = COOLDOWN_TICKS;
            this.playSound(SoundEvents.FOX_EAT, 1.0F, 1.0F);
            if (!this.level().isClientSide) {
                ItemStack reward = getRandomReward();
                this.spawnAtLocation(reward.copy());
                ((ServerLevel) this.level()).sendParticles(
                    VietnamDelightModParticleTypes.JADE_DROP.get(),
                    this.getX(), this.getY() + 1.0, this.getZ(),
                    5,
                    0.3, 0.2, 0.3,
                    0.1
                );
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        return super.mobInteract(player, hand);
    }
	
	private ItemStack getRandomReward() {
	    int roll = this.random.nextInt(100); // 0–99
	    ItemStack result;
	
	    if (roll < 5) {
	        // 5% - Legendary
	        ItemStack[] legendary = {
	            new ItemStack(Items.DIAMOND),
	            new ItemStack(Items.NETHERITE_SCRAP)
	        };
	        result = legendary[this.random.nextInt(legendary.length)];
	
	    } else if (roll < 10) {
	        // 10% - Epic
	        ItemStack[] epic = {
	            new ItemStack(Items.EMERALD, this.random.nextInt(2) + 1),
	            new ItemStack(Items.GOLDEN_APPLE)
	        };
	        result = epic[this.random.nextInt(epic.length)];
	
	    } else if (roll < 40) {
	        // 20% - Rare
	        ItemStack[] rare = {
	            new ItemStack(Items.GOLD_INGOT, this.random.nextInt(2) + 1),
	            new ItemStack(Items.GLOWSTONE_DUST, this.random.nextInt(2) + 1),
	            new ItemStack(VietnamDelightModItems.ASSORTED_MOONCAKE.get()),
	            new ItemStack(VietnamDelightModItems.MUNG_BEAN_MOONCAKE.get())
	        };
	        result = rare[this.random.nextInt(rare.length)];
	
	    } else if (roll < 60) {
	        // 25% - Uncommon
	        ItemStack[] uncommon = {
	            new ItemStack(Items.IRON_INGOT, this.random.nextInt(2) + 1),
	            new ItemStack(VietnamDelightModItems.LOTUS_SEED_MOONCAKE.get()),
	            new ItemStack(VietnamDelightModItems.TARO_MOONCAKE.get()),
	            new ItemStack(VietnamDelightModItems.SALTED_EGG_MOONCAKE.get())
	        };
	        result = uncommon[this.random.nextInt(uncommon.length)];
	
	    } else {
	        // 40% - Common
			ItemStack[] common = {
			    new ItemStack(Items.STRING, this.random.nextInt(6) + 3),
			    new ItemStack(VietnamDelightModItems.LOTUS_SEEDS.get(), this.random.nextInt(2) + 1),
			    new ItemStack(VietnamDelightModItems.BANYAN_SAPLING.get()),
			    new ItemStack(VietnamDelightModItems.BANYAN_FLOWER.get(), this.random.nextInt(2) + 1)
			};
	        result = common[this.random.nextInt(common.length)];
	    }
	
	    return result;
	}

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return EntityDimensions.scalable(0.5f, 0.5f); 
    }

    public static void init() {}

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Rabbit.createAttributes();
        builder = builder.add(Attributes.MAX_HEALTH, 5);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 3); 
        return builder;
    }
}
