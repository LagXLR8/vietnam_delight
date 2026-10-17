package net.huwng.vietnamdelight;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.common.ToolActions;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;

import java.lang.reflect.Method;

public class CommonSetupHandler {
    public static void init(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(VietnamDelightModBlocks.DURIAN_SAPLING.getId(), VietnamDelightModBlocks.POTTED_DURIAN_SAPLING);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(VietnamDelightModBlocks.MARIGOLD.getId(), VietnamDelightModBlocks.POTTED_MARIGOLD);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(VietnamDelightModBlocks.BOUGAINVILLEA_SAPLING.getId(), VietnamDelightModBlocks.POTTED_BOUGAINVILLEA_SAPLING);

            ComposterBlock.COMPOSTABLES.put(VietnamDelightModBlocks.DURIAN_LEAVES.get().asItem(), 0.3F);
            ComposterBlock.COMPOSTABLES.put(VietnamDelightModBlocks.DURIAN_SAPLING.get().asItem(), 0.3F);
            ComposterBlock.COMPOSTABLES.put(VietnamDelightModBlocks.MARIGOLD.get().asItem(), 0.65F);
            ComposterBlock.COMPOSTABLES.put(VietnamDelightModBlocks.MARIGOLD_BUSH.get().asItem(), 0.65F);
            ComposterBlock.COMPOSTABLES.put(VietnamDelightModBlocks.HIBISCUS_FLOWER.get().asItem(), 0.65F);
            ComposterBlock.COMPOSTABLES.put(VietnamDelightModBlocks.HIBISCUS_LEAVES.get().asItem(), 0.3F);
            ComposterBlock.COMPOSTABLES.put(VietnamDelightModBlocks.FLOWERING_HIBISCUS_LEAVES.get().asItem(), 0.3F);
            ComposterBlock.COMPOSTABLES.put(VietnamDelightModBlocks.BOUGAINVILLEA_SAPLING.get().asItem(), 0.3F);
            ComposterBlock.COMPOSTABLES.put(VietnamDelightModBlocks.BOUGAINVILLEA.get().asItem(), 0.3F);
            ComposterBlock.COMPOSTABLES.put(VietnamDelightModBlocks.DRIED_BOUGAINVILLEA.get().asItem(), 0.3F);
            ComposterBlock.COMPOSTABLES.put(VietnamDelightModBlocks.BOUGAINVILLEA_LEAVES.get().asItem(), 0.3F);
            ComposterBlock.COMPOSTABLES.put(VietnamDelightModBlocks.FLOWERING_ORANGE_BOUGAINVILLEA_LEAVES.get().asItem(), 0.3F);
            ComposterBlock.COMPOSTABLES.put(VietnamDelightModBlocks.FLOWERING_PINK_BOUGAINVILLEA_LEAVES.get().asItem(), 0.3F);
            ComposterBlock.COMPOSTABLES.put(VietnamDelightModBlocks.WHITE_BOUGAINVILLEA_LEAVES.get().asItem(), 0.3F);

            registerFlammables();
        });
    }

    private static void registerFlammables() {
        FireBlock fireBlock = (FireBlock) Blocks.FIRE;

        // Durian Wood Set
        registerFlammable(fireBlock, VietnamDelightModBlocks.DURIAN_LOG.get(), 5, 5);
        registerFlammable(fireBlock, VietnamDelightModBlocks.STRIPPED_DURIAN_LOG.get(), 5, 5);
        registerFlammable(fireBlock, VietnamDelightModBlocks.DURIAN_WOOD.get(), 5, 5);
        registerFlammable(fireBlock, VietnamDelightModBlocks.STRIPPED_DURIAN_WOOD.get(), 5, 5);
        registerFlammable(fireBlock, VietnamDelightModBlocks.DURIAN_PLANKS.get(), 5, 20);
        registerFlammable(fireBlock, VietnamDelightModBlocks.DURIAN_STAIRS.get(), 5, 20);
        registerFlammable(fireBlock, VietnamDelightModBlocks.DURIAN_SLAB.get(), 5, 20);
        registerFlammable(fireBlock, VietnamDelightModBlocks.DURIAN_FENCE.get(), 5, 20);
        registerFlammable(fireBlock, VietnamDelightModBlocks.DURIAN_FENCE_GATE.get(), 5, 20);
        registerFlammable(fireBlock, VietnamDelightModBlocks.DURIAN_DOOR.get(), 5, 20);
        registerFlammable(fireBlock, VietnamDelightModBlocks.DURIAN_TRAPDOOR.get(), 5, 20);
        registerFlammable(fireBlock, VietnamDelightModBlocks.DURIAN_PRESSURE_PLATE.get(), 5, 20);
        registerFlammable(fireBlock, VietnamDelightModBlocks.DURIAN_BUTTON.get(), 5, 20);
        registerFlammable(fireBlock, VietnamDelightModBlocks.DURIAN_SIGN.get(), 5, 20);
        registerFlammable(fireBlock, VietnamDelightModBlocks.DURIAN_WALL_SIGN.get(), 5, 20);
        registerFlammable(fireBlock, VietnamDelightModBlocks.DURIAN_HANGING_SIGN.get(), 5, 20);
        registerFlammable(fireBlock, VietnamDelightModBlocks.DURIAN_WALL_HANGING_SIGN.get(), 5, 20);
        registerFlammable(fireBlock, VietnamDelightModBlocks.DURIAN_LEAVES.get(), 30, 60);

        // Flamboyant Wood Set
        registerFlammable(fireBlock, VietnamDelightModBlocks.FLAMBOYANT_LOG.get(), 5, 5);
        registerFlammable(fireBlock, VietnamDelightModBlocks.STRIPPED_FLAMBOYANT_LOG.get(), 5, 5);
        registerFlammable(fireBlock, VietnamDelightModBlocks.FLAMBOYANT_WOOD.get(), 5, 5);
        registerFlammable(fireBlock, VietnamDelightModBlocks.STRIPPED_FLAMBOYANT_WOOD.get(), 5, 5);
        registerFlammable(fireBlock, VietnamDelightModBlocks.FLAMBOYANT_PLANKS.get(), 5, 20);
        registerFlammable(fireBlock, VietnamDelightModBlocks.FLAMBOYANT_STAIRS.get(), 5, 20);
        registerFlammable(fireBlock, VietnamDelightModBlocks.FLAMBOYANT_SLAB.get(), 5, 20);
        registerFlammable(fireBlock, VietnamDelightModBlocks.FLAMBOYANT_FENCE.get(), 5, 20);
        registerFlammable(fireBlock, VietnamDelightModBlocks.FLAMBOYANT_FENCE_GATE.get(), 5, 20);
        registerFlammable(fireBlock, VietnamDelightModBlocks.FLAMBOYANT_DOOR.get(), 5, 20);
        registerFlammable(fireBlock, VietnamDelightModBlocks.FLAMBOYANT_TRAPDOOR.get(), 5, 20);
        registerFlammable(fireBlock, VietnamDelightModBlocks.FLAMBOYANT_PRESSURE_PLATE.get(), 5, 20);
        registerFlammable(fireBlock, VietnamDelightModBlocks.FLAMBOYANT_BUTTON.get(), 5, 20);
        registerFlammable(fireBlock, VietnamDelightModBlocks.FLAMBOYANT_SIGN.get(), 5, 20);
        registerFlammable(fireBlock, VietnamDelightModBlocks.FLAMBOYANT_WALL_SIGN.get(), 5, 20);
        registerFlammable(fireBlock, VietnamDelightModBlocks.FLAMBOYANT_HANGING_SIGN.get(), 5, 20);
        registerFlammable(fireBlock, VietnamDelightModBlocks.FLAMBOYANT_WALL_HANGING_SIGN.get(), 5, 20);
        registerFlammable(fireBlock, VietnamDelightModBlocks.FLAMBOYANT_LEAVES.get(), 30, 60);
        registerFlammable(fireBlock, VietnamDelightModBlocks.FLOWERING_FLAMBOYANT_LEAVES.get(), 30, 60);
        registerFlammable(fireBlock, VietnamDelightModBlocks.BLOOMING_FLAMBOYANT_LEAVES.get(), 30, 60);
        registerFlammable(fireBlock, VietnamDelightModBlocks.FLOWERING_GOLDEN_FLAMBOYANT_LEAVES.get(), 30, 60);
        registerFlammable(fireBlock, VietnamDelightModBlocks.BLOOMING_GOLDEN_FLAMBOYANT_LEAVES.get(), 30, 60);

        // Other leaves & vegetation in mod
        registerFlammable(fireBlock, VietnamDelightModBlocks.BANYAN_LEAVES.get(), 30, 60);
        registerFlammable(fireBlock, VietnamDelightModBlocks.FLOWERING_BANYAN_LEAVES.get(), 30, 60);
        registerFlammable(fireBlock, VietnamDelightModBlocks.BANYAN_VINE.get(), 15, 60);
        registerFlammable(fireBlock, VietnamDelightModBlocks.BANYAN_ROOTS.get(), 15, 60);
        registerFlammable(fireBlock, VietnamDelightModBlocks.BANYAN_ROOTS_PLANT.get(), 15, 60);
        registerFlammable(fireBlock, VietnamDelightModBlocks.TAMARIND_LEAVES.get(), 30, 60);
        registerFlammable(fireBlock, VietnamDelightModBlocks.FLOWERING_TAMARIND_LEAVES.get(), 30, 60);
        registerFlammable(fireBlock, VietnamDelightModBlocks.MARIGOLD.get(), 60, 100);
        registerFlammable(fireBlock, VietnamDelightModBlocks.MARIGOLD_BUSH.get(), 60, 100);
        registerFlammable(fireBlock, VietnamDelightModBlocks.HIBISCUS_FLOWER.get(), 60, 100);
        registerFlammable(fireBlock, VietnamDelightModBlocks.HIBISCUS_LEAVES.get(), 30, 60);
        registerFlammable(fireBlock, VietnamDelightModBlocks.FLOWERING_HIBISCUS_LEAVES.get(), 30, 60);
        registerFlammable(fireBlock, VietnamDelightModBlocks.BOUGAINVILLEA_SAPLING.get(), 60, 100);
        registerFlammable(fireBlock, VietnamDelightModBlocks.BOUGAINVILLEA.get(), 60, 100);
        registerFlammable(fireBlock, VietnamDelightModBlocks.DRIED_BOUGAINVILLEA.get(), 60, 100);
        registerFlammable(fireBlock, VietnamDelightModBlocks.BOUGAINVILLEA_LEAVES.get(), 30, 60);
        registerFlammable(fireBlock, VietnamDelightModBlocks.FLOWERING_ORANGE_BOUGAINVILLEA_LEAVES.get(), 30, 60);
        registerFlammable(fireBlock, VietnamDelightModBlocks.FLOWERING_PINK_BOUGAINVILLEA_LEAVES.get(), 30, 60);
        registerFlammable(fireBlock, VietnamDelightModBlocks.WHITE_BOUGAINVILLEA_LEAVES.get(), 30, 60);
        registerFlammable(fireBlock, VietnamDelightModBlocks.BOUGAINVILLEA_TRUNK.get(), 5, 20);
    }

    private static Method setFlammableMethod;

    private static void registerFlammable(FireBlock fireBlock, Block block, int flameOdds, int burnOdds) {
        if (block != null) {
            try {
                if (setFlammableMethod == null) {
                    try {
                        setFlammableMethod = FireBlock.class.getDeclaredMethod("setFlammable", Block.class, int.class, int.class);
                    } catch (NoSuchMethodException e) {
                        setFlammableMethod = ObfuscationReflectionHelper.findMethod(FireBlock.class, "m_53444_", Block.class, int.class, int.class);
                    }
                    setFlammableMethod.setAccessible(true);
                }
                setFlammableMethod.invoke(fireBlock, block, flameOdds, burnOdds);
            } catch (Exception e) {
                VietnamDelightMod.LOGGER.error("Failed to register flammable block: " + block, e);
            }
        }
    }

    @SubscribeEvent
    public static void onToolModification(BlockEvent.BlockToolModificationEvent event) {
        if (event.getToolAction() == ToolActions.AXE_STRIP) {
            BlockState state = event.getState();
            if (state.is(VietnamDelightModBlocks.DURIAN_LOG.get())) {
                event.setFinalState(VietnamDelightModBlocks.STRIPPED_DURIAN_LOG.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS)));
            } else if (state.is(VietnamDelightModBlocks.DURIAN_WOOD.get())) {
                event.setFinalState(VietnamDelightModBlocks.STRIPPED_DURIAN_WOOD.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS)));
            } else if (state.is(VietnamDelightModBlocks.FLAMBOYANT_LOG.get())) {
                event.setFinalState(VietnamDelightModBlocks.STRIPPED_FLAMBOYANT_LOG.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS)));
            } else if (state.is(VietnamDelightModBlocks.FLAMBOYANT_WOOD.get())) {
                event.setFinalState(VietnamDelightModBlocks.STRIPPED_FLAMBOYANT_WOOD.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS)));
            }
        }
    }
}
