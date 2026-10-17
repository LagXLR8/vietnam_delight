package net.huwng.vietnamdelight.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.huwng.vietnamdelight.init.VietnamDelightModParticleTypes;

public class DurianLeavesBlock extends LeavesBlock {
    public DurianLeavesBlock() {
        super(BlockBehaviour.Properties.of()
                .ignitedByLava()
                .sound(SoundType.GRASS)
                .strength(0.2f)
                .randomTicks()
                .noOcclusion()
                .isSuffocating((bs, br, bp) -> false)
                .isViewBlocking((bs, br, bp) -> false)
        );
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 1;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return 30;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return 60;
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        super.animateTick(state, world, pos, random);

        // Sinh lá sầu riêng rơi với tần suất vừa phải, nhẹ nhàng
        if (random.nextInt(32) == 0) {
            BlockPos below = pos.below();
            BlockState belowState = world.getBlockState(below);

            if (!belowState.isSolid() && !(belowState.getBlock() instanceof LeavesBlock)) {
                double x = (double) pos.getX() + random.nextDouble();
                double y = (double) pos.getY() - 0.05;
                double z = (double) pos.getZ() + random.nextDouble();

                world.addParticle(
                        VietnamDelightModParticleTypes.DURIAN_LEAF.get(),
                        x, y, z,
                        (random.nextDouble() - 0.5) * 0.08,
                        -0.015,
                        (random.nextDouble() - 0.5) * 0.08
                );
            }
        }
    }
}
