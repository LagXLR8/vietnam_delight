package net.huwng.vietnamdelight.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.effect.MobEffect;

import net.huwng.vietnamdelight.potion.SourMobEffect;
import net.huwng.vietnamdelight.potion.SmellMobEffect;
import net.huwng.vietnamdelight.potion.SlipperyMobEffect;
import net.huwng.vietnamdelight.potion.SaltyMobEffect;
import net.huwng.vietnamdelight.VietnamDelightMod;

public class VietnamDelightModMobEffects {
	public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, VietnamDelightMod.MODID);
	public static final RegistryObject<MobEffect> SOUR = REGISTRY.register("sour", () -> new SourMobEffect());
	public static final RegistryObject<MobEffect> SMELL = REGISTRY.register("smell", () -> new SmellMobEffect());
	public static final RegistryObject<MobEffect> SLIPPERY = REGISTRY.register("slippery", () -> new SlipperyMobEffect());
	public static final RegistryObject<MobEffect> SALTY = REGISTRY.register("salty", () -> new SaltyMobEffect());
	public static final RegistryObject<MobEffect> PHANTASM = REGISTRY.register("phantasm", () -> new net.huwng.vietnamdelight.potion.PhantasmMobEffect());
}
