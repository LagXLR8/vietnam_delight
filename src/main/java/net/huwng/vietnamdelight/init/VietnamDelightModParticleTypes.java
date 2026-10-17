package net.huwng.vietnamdelight.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleType;

import net.huwng.vietnamdelight.VietnamDelightMod;

public class VietnamDelightModParticleTypes {
	public static final DeferredRegister<ParticleType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, VietnamDelightMod.MODID);
	public static final RegistryObject<SimpleParticleType> SKY_LANTERN_FLASH = REGISTRY.register("sky_lantern_flash", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> LANTERN_CLOUD = REGISTRY.register("lantern_cloud", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> BANYAN_LEAF = REGISTRY.register("banyan_leaf", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> FLAMBOYANT_PETAL = REGISTRY.register("flamboyant_petal", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> GOLDEN_FLAMBOYANT_PETAL = REGISTRY.register("golden_flamboyant_petal", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> JADE_DROP = REGISTRY.register("jade_drop", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> CHUCMUNGPARTICLE = REGISTRY.register("chucmungparticle", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> STEAM = REGISTRY.register("steam", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> DURIAN_LEAF = REGISTRY.register("durian_leaf", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> ORANGE_BOUGAINVILLEA_PARTICLE = REGISTRY.register("orange_bougainvillea_particle", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> PINK_BOUGAINVILLEA_PARTICLE = REGISTRY.register("pink_bougainvillea_particle", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> WHITE_BOUGAINVILLEA_PARTICLE = REGISTRY.register("white_bougainvillea_particle", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> LEAF_BOUGAINVILLEA_PARTICLE = REGISTRY.register("leaf_bougainvillea_particle", () -> new SimpleParticleType(false));
}
