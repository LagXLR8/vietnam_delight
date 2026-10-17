package net.huwng.vietnamdelight.init;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.client.gui.screens.MenuScreens;

import net.huwng.vietnamdelight.client.gui.FishTrapGuiScreen;
import net.huwng.vietnamdelight.client.gui.CeramicClayPotGuiScreen;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class VietnamDelightModScreens {
	@SubscribeEvent
	public static void clientLoad(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			MenuScreens.register(VietnamDelightModMenus.CERAMIC_CLAY_POT_GUI.get(), CeramicClayPotGuiScreen::new);
			MenuScreens.register(VietnamDelightModMenus.FISH_TRAP_GUI.get(), FishTrapGuiScreen::new);
		});
	}
}
