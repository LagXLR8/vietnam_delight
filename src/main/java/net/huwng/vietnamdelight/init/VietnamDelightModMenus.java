package net.huwng.vietnamdelight.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.common.extensions.IForgeMenuType;

import net.minecraft.world.inventory.MenuType;

import net.huwng.vietnamdelight.world.inventory.FishTrapGuiMenu;
import net.huwng.vietnamdelight.world.inventory.CeramicClayPotGuiMenu;
import net.huwng.vietnamdelight.VietnamDelightMod;

public class VietnamDelightModMenus {
	public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, VietnamDelightMod.MODID);
	public static final RegistryObject<MenuType<CeramicClayPotGuiMenu>> CERAMIC_CLAY_POT_GUI = REGISTRY.register("ceramic_clay_pot_gui", () -> IForgeMenuType.create(CeramicClayPotGuiMenu::new));
	public static final RegistryObject<MenuType<FishTrapGuiMenu>> FISH_TRAP_GUI = REGISTRY.register("fish_trap_gui", () -> IForgeMenuType.create(FishTrapGuiMenu::new));
}
