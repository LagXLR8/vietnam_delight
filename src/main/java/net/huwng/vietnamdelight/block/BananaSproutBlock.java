package net.huwng.vietnamdelight.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;

public class BananaSproutBlock extends BushBlock implements BonemealableBlock {
    public BananaSproutBlock() {
        super(BlockBehaviour.Properties.of()
                .ignitedByLava()
                .sound(SoundType.GRASS)
                .instabreak()
                .noCollission()
                .noOcclusion()
                .randomTicks()
        );
    }
    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter world, BlockPos pos) {
        return state.is(BlockTags.DIRT);
    }
    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (random.nextInt(10) == 0) {
            grow(level, pos, random);
        }
    }
    @Override
    public boolean isValidBonemealTarget(LevelReader world, BlockPos pos, BlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState state) {
        return random.nextFloat() < 0.4f; // 40% cơ hội thành công
    }
    
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return box(2, 0, 2, 14, 13, 14);
	}

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        grow(level, pos, random);
    }

	private void grow(ServerLevel level, BlockPos pos, RandomSource random) {
    int height = 4 + random.nextInt(3);
    for (int i = 1; i <= height + 1; i++) {
        if (!level.getBlockState(pos.above(i)).isAir()) return;
    }
    BlockPos rootPos = pos.below();
    if (!level.getBlockState(rootPos).is(BlockTags.DIRT)) return;
    level.removeBlock(pos, false);
    level.setBlock(rootPos,
            VietnamDelightModBlocks.BANANA_ROOT.get().defaultBlockState()
                    .setValue(BananaRootBlock.BLOCKSTATE, 1), 3);
    for (int i = 0; i < height; i++) {
        BlockPos stemPos = pos.above(i);
        int stemState = (i == height - 1) ? 6 : (i == 1 ? 2 : (i >= 2 ? 4 : 0));
        BlockState stem = VietnamDelightModBlocks.BANANA_PSEUDOSTEM.get().defaultBlockState()
                .setValue(BananaPseudostemBlock.BLOCKSTATE, stemState);
        level.setBlock(stemPos, stem, 3);
    }
    BlockPos top = pos.above(height);
    level.setBlock(top, VietnamDelightModBlocks.BANANA_FROND.get().defaultBlockState(), 3);
    BlockPos frondBase = pos.above(height - 1);
    level.scheduleTick(frondBase, VietnamDelightModBlocks.BANANA_PSEUDOSTEM.get(), 20);
}

	@Override
	public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 60;
	}

	@Override
	public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 100;
	}
}
