package net.huwng.vietnamdelight.setup;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.resources.ResourceLocation;

import net.huwng.vietnamdelight.VietnamDelightMod;
import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;

public class VietnamDelightModHooks {
    public static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // ✅ Cho phép trồng Kumquat Bush vào chậu
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(
                new ResourceLocation(VietnamDelightMod.MODID, "kumquat_bush"),
                VietnamDelightModBlocks.POTTED_KUMQUAT_BUSH
            );
        });
    }
}
