package net.huwng.vietnamdelight.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;

import net.huwng.vietnamdelight.VietnamDelightMod;

public class VietnamDelightModSounds {
	public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, VietnamDelightMod.MODID);
	public static final RegistryObject<SoundEvent> PICK_FRUIT_1 = REGISTRY.register("pick_fruit_1", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("vietnam_delight", "pick_fruit_1")));
	public static final RegistryObject<SoundEvent> PICK_FRUIT_2 = REGISTRY.register("pick_fruit_2", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("vietnam_delight", "pick_fruit_2")));
	public static final RegistryObject<SoundEvent> CRACK_1 = REGISTRY.register("crack_1", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("vietnam_delight", "crack_1")));
	public static final RegistryObject<SoundEvent> CRACK_2 = REGISTRY.register("crack_2", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("vietnam_delight", "crack_2")));
}
