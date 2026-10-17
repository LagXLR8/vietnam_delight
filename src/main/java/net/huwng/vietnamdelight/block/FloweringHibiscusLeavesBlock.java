package net.huwng.vietnamdelight.block;

import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class FloweringHibiscusLeavesBlock extends LeavesBlock implements BonemealableBlock {
    public FloweringHibiscusLeavesBlock() {
        super(BlockBehaviour.Properties.of()
                .ignitedByLava()
                .sound(SoundType.GRASS)
                .strength(0.2f)
                .noOcclusion());
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 1;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader world, BlockPos pos, BlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState state) {
        for (Direction dir : Direction.values()) {
            BlockPos flowerPos = pos.relative(dir);
            if (world.getBlockState(flowerPos).isAir()) {
                BlockState flowerState = VietnamDelightModBlocks.HIBISCUS_FLOWER.get()
                        .defaultBlockState()
                        .setValue(HibiscusFlowerBlock.FACING, dir);
                world.setBlock(flowerPos, flowerState, 3);
                world.playSound(null, flowerPos, SoundEvents.SPORE_BLOSSOM_BREAK, SoundSource.BLOCKS, 0.6f, 1.0f);
                world.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                        flowerPos.getX() + 0.5, flowerPos.getY() + 0.5, flowerPos.getZ() + 0.5,
                        6, 0.3, 0.3, 0.3, 0.1);
                break;
            }
        }
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return 30;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return 60;
    }
}
