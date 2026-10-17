package net.huwng.vietnamdelight.init;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraft.world.level.block.ComposterBlock;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class VietnamDelightModCompostableItems {
	@SubscribeEvent
	public static void addComposterItems(FMLCommonSetupEvent event) {
		ComposterBlock.COMPOSTABLES.put(VietnamDelightModItems.DRIED_BANANA_LEAF.get(), 0.4f);
		ComposterBlock.COMPOSTABLES.put(VietnamDelightModItems.BANANA_LEAF.get(), 0.4f);
		ComposterBlock.COMPOSTABLES.put(VietnamDelightModBlocks.BANANA_SPROUT.get().asItem(), 0.2f);
		ComposterBlock.COMPOSTABLES.put(VietnamDelightModItems.BANANA_BUNCH.get(), 0.4f);
		ComposterBlock.COMPOSTABLES.put(VietnamDelightModItems.BANANA.get(), 0.2f);
		ComposterBlock.COMPOSTABLES.put(VietnamDelightModItems.GREEN_BANANA_BUNCH.get(), 0.4f);
		ComposterBlock.COMPOSTABLES.put(VietnamDelightModBlocks.BANYAN_ROOTS.get().asItem(), 0.1f);
		ComposterBlock.COMPOSTABLES.put(VietnamDelightModBlocks.BANYAN_VINE.get().asItem(), 0.2f);
		ComposterBlock.COMPOSTABLES.put(VietnamDelightModBlocks.BANYAN_LEAVES.get().asItem(), 0.2f);
		ComposterBlock.COMPOSTABLES.put(VietnamDelightModBlocks.FLOWERING_BANYAN_LEAVES.get().asItem(), 0.2f);
		ComposterBlock.COMPOSTABLES.put(VietnamDelightModBlocks.FLAMBOYANT_LEAVES.get().asItem(), 0.2f);
		ComposterBlock.COMPOSTABLES.put(VietnamDelightModBlocks.FLOWERING_FLAMBOYANT_LEAVES.get().asItem(), 0.2f);
		ComposterBlock.COMPOSTABLES.put(VietnamDelightModBlocks.BLOOMING_FLAMBOYANT_LEAVES.get().asItem(), 0.3f);
		ComposterBlock.COMPOSTABLES.put(VietnamDelightModBlocks.FLAMBOYANT_FLOWER.get().asItem(), 0.65f);
		ComposterBlock.COMPOSTABLES.put(VietnamDelightModBlocks.FLAMBOYANT_PETALS.get().asItem(), 0.3f);
		ComposterBlock.COMPOSTABLES.put(VietnamDelightModBlocks.FLAMBOYANT_SAPLING.get().asItem(), 0.3f);
		ComposterBlock.COMPOSTABLES.put(VietnamDelightModBlocks.FLOWERING_GOLDEN_FLAMBOYANT_LEAVES.get().asItem(), 0.2f);
		ComposterBlock.COMPOSTABLES.put(VietnamDelightModBlocks.BLOOMING_GOLDEN_FLAMBOYANT_LEAVES.get().asItem(), 0.3f);
		ComposterBlock.COMPOSTABLES.put(VietnamDelightModBlocks.GOLDEN_FLAMBOYANT_FLOWER.get().asItem(), 0.65f);
		ComposterBlock.COMPOSTABLES.put(VietnamDelightModBlocks.GOLDEN_FLAMBOYANT_PETALS.get().asItem(), 0.3f);
		ComposterBlock.COMPOSTABLES.put(VietnamDelightModBlocks.GOLDEN_FLAMBOYANT_SAPLING.get().asItem(), 0.3f);
		ComposterBlock.COMPOSTABLES.put(VietnamDelightModItems.NIPA_LEAF.get(), 0.3f);
		ComposterBlock.COMPOSTABLES.put(VietnamDelightModBlocks.NIPA_ROOT.get().asItem(), 0.7f);
		ComposterBlock.COMPOSTABLES.put(VietnamDelightModBlocks.NIPA_ROOTS.get().asItem(), 0.4f);
		ComposterBlock.COMPOSTABLES.put(VietnamDelightModItems.NIPA_NUT.get(), 0.3f);
		ComposterBlock.COMPOSTABLES.put(VietnamDelightModItems.TAMARIND.get(), 0.3f);
		ComposterBlock.COMPOSTABLES.put(VietnamDelightModBlocks.CICADAS.get().asItem(), 0.6f);
	}
}
