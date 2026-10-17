package net.huwng.vietnamdelight.block;

import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;
import net.huwng.vietnamdelight.init.VietnamDelightModItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.*;

public class NipaFrondSidesBlock extends Block implements SimpleWaterloggedBlock {
    public static final IntegerProperty BLOCKSTATE = IntegerProperty.create("blockstate", 0, 1);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public NipaFrondSidesBlock() {
        super(BlockBehaviour.Properties.of()
                .ignitedByLava()
                .sound(SoundType.BIG_DRIPLEAF)
                .instabreak()
                .noOcclusion()
                .randomTicks()
                .lightLevel(s -> s.getValue(BLOCKSTATE) == 1 ? 2 : 0)
                .isRedstoneConductor((bs, br, bp) -> false));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(WATERLOGGED, false)
                .setValue(BLOCKSTATE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED, BLOCKSTATE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean flag = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(WATERLOGGED, flag)
                .setValue(BLOCKSTATE, 0);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED)
                ? Fluids.WATER.getSource(false)
                : super.getFluidState(state);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return state.getFluidState().isEmpty();
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 0;
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            default -> box(4, 2, 0, 12, 10, 3);
            case NORTH -> box(4, 2, 13, 12, 10, 16);
            case EAST -> box(0, 2, 4, 3, 10, 12);
            case WEST -> box(13, 2, 4, 16, 10, 12);
        };
    }

    public boolean canSurvive(BlockState state, LevelAccessor world, BlockPos pos) {
	    Direction facing = state.getValue(FACING);
	    BlockPos supportPos = pos.relative(facing.getOpposite());
	    BlockState supportState = world.getBlockState(supportPos);
	    return supportState.is(VietnamDelightModBlocks.NIPA_ROOT.get());
	}
	
	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState,
	                              LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
	    if (state.getValue(WATERLOGGED)) {
	        world.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
	    }
	    if (!canSurvive(state, world, currentPos)) {
	        world.destroyBlock(currentPos, true);
	    }
	
	    return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
	}

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        return new ItemStack(VietnamDelightModItems.NIPA_LEAF.get());
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(BLOCKSTATE) == 0;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (random.nextFloat() > 0.25f)
            return;

        if (!canSurvive(state, world, pos)) {
            world.destroyBlock(pos, true);
            return;
        }

        int radius = 3;
        BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    checkPos.set(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz);
                    BlockState nearby = world.getBlockState(checkPos);
                    if (nearby.is(VietnamDelightModBlocks.NIPA_FROND_SIDES.get()) &&
                        nearby.getValue(BLOCKSTATE) == 1) {
                        return;
                    }
                }
            }
        }
        
        BlockPos belowPos = pos.below();
        BlockState belowState = world.getBlockState(belowPos);
        boolean isAir = belowState.isAir();
        boolean isWater = belowState.getFluidState().is(Fluids.WATER);

        if (isAir || isWater) {
            boolean growFruit = random.nextFloat() < 0.3f;

            BlockState newState = (growFruit
                    ? VietnamDelightModBlocks.NIPA_FRUIT.get()
                    : VietnamDelightModBlocks.NIPA_FRUIT_CROP.get()).defaultBlockState();

            if (isWater && newState.hasProperty(BlockStateProperties.WATERLOGGED)) {
                newState = newState.setValue(BlockStateProperties.WATERLOGGED, true);
            }

            world.setBlock(belowPos, newState, 3);
            world.setBlock(pos, state.setValue(BLOCKSTATE, 1), 3);
        }
    }
}
