package net.huwng.vietnamdelight;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import net.huwng.vietnamdelight.block.PandanCropBlock;

public class PandanGrowHandler {
    public static void tickGrow(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (state.getBlock() instanceof PandanCropBlock) {
            int stage = state.getValue(PandanCropBlock.AGE);

            // Tăng trưởng: 20% khi mưa, 10% khi khô
            float chance = world.isRainingAt(pos) ? 0.2f : 0.1f;

            if (stage < 4 && random.nextFloat() < chance) {
                world.setBlock(pos, state.setValue(PandanCropBlock.AGE, stage + 1), 3);
            }
        }
    }
}
