package net.huwng.vietnamdelight.block;

import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.shapes.*;

import java.util.Collections;
import java.util.List;

public class LotusLilypadBlock extends BushBlock {
    public static final IntegerProperty BLOCKSTATE = IntegerProperty.create("blockstate", 0, 3);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public LotusLilypadBlock() {
        super(BlockBehaviour.Properties.of()
            .instabreak()
            .noOcclusion()
            .sound(SoundType.LILY_PAD)
            .replaceable()
            .isRedstoneConductor((s, g, p) -> false));
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(BLOCKSTATE, 0)
            .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BLOCKSTATE, FACING);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return box(1, 0, 1, 15, 1, 15);
    }

    @Override
    public boolean mayPlaceOn(BlockState below, BlockGetter world, BlockPos pos) {
        return below.getFluidState().isSource() || below.is(VietnamDelightModBlocks.LOTUS_STEM.get());
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        BlockState below = context.getLevel().getBlockState(pos.below());

        if (!mayPlaceOn(below, context.getLevel(), pos.below())) return null;

        RandomSource random = context.getLevel().random;
        int state = random.nextInt(4);
        Direction facing = Direction.Plane.HORIZONTAL.getRandomDirection(random);

        return this.defaultBlockState()
            .setValue(BLOCKSTATE, state)
            .setValue(FACING, facing);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState,
                                  LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
        BlockState below = world.getBlockState(currentPos.below());

        if (!mayPlaceOn(below, world, currentPos.below())) {
            world.removeBlock(currentPos, false);
        }

        return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
    }
}
