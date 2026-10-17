package net.huwng.vietnamdelight.block.entity;

import net.huwng.vietnamdelight.init.VietnamDelightModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BloomingGoldenFlamboyantLeavesBlockEntity extends BlockEntity {
    public BloomingGoldenFlamboyantLeavesBlockEntity(BlockPos pos, BlockState state) {
        super(VietnamDelightModBlockEntities.BLOOMING_GOLDEN_FLAMBOYANT_LEAVES.get(), pos, state);
    }
}
