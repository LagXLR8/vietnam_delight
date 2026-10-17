package net.huwng.vietnamdelight.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;

import net.huwng.vietnamdelight.entity.SkyLanternsEntity;
import net.huwng.vietnamdelight.entity.SaltEntityEntity;
import net.huwng.vietnamdelight.entity.JadeRabbitEntity;
import net.huwng.vietnamdelight.entity.BananaBoateEntity;
import net.huwng.vietnamdelight.VietnamDelightMod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class VietnamDelightModEntities {
	public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, VietnamDelightMod.MODID);
	public static final RegistryObject<EntityType<BananaBoateEntity>> BANANA_BOATE = register("banana_boate",
			EntityType.Builder.<BananaBoateEntity>of(BananaBoateEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(BananaBoateEntity::new)

					.sized(0.6f, 0.3f));
	public static final RegistryObject<EntityType<SaltEntityEntity>> SALT_ENTITY = register("salt_entity",
			EntityType.Builder.<SaltEntityEntity>of(SaltEntityEntity::new, MobCategory.MISC).setCustomClientFactory(SaltEntityEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<SkyLanternsEntity>> SKY_LANTERNS = register("sky_lanterns",
			EntityType.Builder.<SkyLanternsEntity>of(SkyLanternsEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(SkyLanternsEntity::new)

					.sized(0.25f, 0.25f));
	public static final RegistryObject<EntityType<JadeRabbitEntity>> JADE_RABBIT = register("jade_rabbit",
			EntityType.Builder.<JadeRabbitEntity>of(JadeRabbitEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(JadeRabbitEntity::new)

					.sized(0.6f, 0.6f));

	public static final RegistryObject<EntityType<net.huwng.vietnamdelight.entity.DurianShellProjectileEntity>> DURIAN_SHELL_PROJECTILE = register("durian_shell_projectile",
			EntityType.Builder.<net.huwng.vietnamdelight.entity.DurianShellProjectileEntity>of(net.huwng.vietnamdelight.entity.DurianShellProjectileEntity::new, MobCategory.MISC).setCustomClientFactory(net.huwng.vietnamdelight.entity.DurianShellProjectileEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.35f, 0.35f));
	public static final RegistryObject<EntityType<net.huwng.vietnamdelight.entity.ExplosiveDurianShellProjectileEntity>> EXPLOSIVE_DURIAN_SHELL_PROJECTILE = register("explosive_durian_shell_projectile",
			EntityType.Builder.<net.huwng.vietnamdelight.entity.ExplosiveDurianShellProjectileEntity>of(net.huwng.vietnamdelight.entity.ExplosiveDurianShellProjectileEntity::new, MobCategory.MISC).setCustomClientFactory(net.huwng.vietnamdelight.entity.ExplosiveDurianShellProjectileEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.35f, 0.35f));

	// Start of user code block custom entities
	// End of user code block custom entities
	private static <T extends Entity> RegistryObject<EntityType<T>> register(String registryname, EntityType.Builder<T> entityTypeBuilder) {
		return REGISTRY.register(registryname, () -> (EntityType<T>) entityTypeBuilder.build(registryname));
	}

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			BananaBoateEntity.init();
			SkyLanternsEntity.init();
			JadeRabbitEntity.init();
		});
	}

	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(BANANA_BOATE.get(), BananaBoateEntity.createAttributes().build());
		event.put(SKY_LANTERNS.get(), SkyLanternsEntity.createAttributes().build());
		event.put(JADE_RABBIT.get(), JadeRabbitEntity.createAttributes().build());
	}
}
