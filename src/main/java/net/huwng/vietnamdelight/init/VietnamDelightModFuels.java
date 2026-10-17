package net.huwng.vietnamdelight.init;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;

import net.minecraft.world.item.ItemStack;

@Mod.EventBusSubscriber
public class VietnamDelightModFuels {
	@SubscribeEvent
	public static void furnaceFuelBurnTimeEvent(FurnaceFuelBurnTimeEvent event) {
		ItemStack itemstack = event.getItemStack();
		if (itemstack.getItem() == VietnamDelightModItems.DRIED_BANANA_LEAF.get())
			event.setBurnTime(800);
		else if (itemstack.getItem() == VietnamDelightModItems.NIPA_LEAF.get())
			event.setBurnTime(200);
		else if (itemstack.getItem() == VietnamDelightModBlocks.NIPA_ROOTS.get().asItem())
			event.setBurnTime(250);
		else if (itemstack.getItem() == VietnamDelightModItems.DRIED_BOUGAINVILLEA.get() || itemstack.getItem() == VietnamDelightModBlocks.DRIED_BOUGAINVILLEA.get().asItem())
			event.setBurnTime(100);
	}
}
