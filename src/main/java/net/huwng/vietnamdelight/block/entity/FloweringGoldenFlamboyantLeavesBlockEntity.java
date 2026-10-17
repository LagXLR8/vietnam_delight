package net.huwng.vietnamdelight.block.entity;

import net.huwng.vietnamdelight.init.VietnamDelightModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class FloweringGoldenFlamboyantLeavesBlockEntity extends BlockEntity {
    public FloweringGoldenFlamboyantLeavesBlockEntity(BlockPos pos, BlockState state) {
        super(VietnamDelightModBlockEntities.FLOWERING_GOLDEN_FLAMBOYANT_LEAVES.get(), pos, state);
    }
}
