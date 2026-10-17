package net.huwng.vietnamdelight.block;

import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;

public class TamarindFlowersBlock extends Block {
	public static final IntegerProperty BLOCKSTATE = IntegerProperty.create("blockstate", 0, 7);
	public static final DirectionProperty FACING = DirectionalBlock.FACING;

	public TamarindFlowersBlock() {
		super(BlockBehaviour.Properties.of()
				.sound(SoundType.PINK_PETALS)
				.instabreak()
				.noOcclusion()
				.noCollission()
				.randomTicks());
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(BLOCKSTATE, 0));
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		int currentState = state.getValue(BLOCKSTATE);
		
		// Nếu state >= 2: chỉ kiểm tra phía trên
		if (currentState >= 2) {
			BlockPos abovePos = pos.above();
			BlockState aboveState = world.getBlockState(abovePos);
			return aboveState.is(VietnamDelightModBlocks.FLOWERING_TAMARIND_LEAVES.get());
		}
		
		// Nếu state < 2: kiểm tra theo hướng FACING
		Direction dir = state.getValue(FACING).getOpposite();
		BlockPos supportPos = pos.relative(dir);
		BlockState supportState = world.getBlockState(supportPos);
		return supportState.is(VietnamDelightModBlocks.FLOWERING_TAMARIND_LEAVES.get());
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState,
			LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
		if (!canSurvive(state, world, currentPos)) {
			world.scheduleTick(currentPos, this, 1);
		}
		return state;
	}

	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		if (!canSurvive(state, world, pos)) {
			world.destroyBlock(pos, true);
		}
	}

	@Override
	public boolean isRandomlyTicking(BlockState state) {
		return true;
	}

	@Override
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		BlockPos abovePos = pos.above();
		BlockState aboveState = world.getBlockState(abovePos);

		if (aboveState.is(VietnamDelightModBlocks.FLOWERING_TAMARIND_LEAVES.get())) {
			int current = state.getValue(BLOCKSTATE);

			// Grow từ 0 -> 5 với 30% xác suất
			if (current < 5 && random.nextFloat() < 0.3f) {
				int newState = current + 1;
				BlockState newBlockState = state.setValue(BLOCKSTATE, newState);
				
				// Nếu vừa chuyển sang state >= 2, chỉnh hướng về NORTH luôn
				if (newState >= 2) {
					newBlockState = newBlockState.setValue(FACING, Direction.NORTH);
				}
				
				world.setBlock(pos, newBlockState, 2);
			}
		}
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		int stage = state.getValue(BLOCKSTATE);
		
		// State 0: shape theo hướng FACING
		if (stage == 0) {
			return switch (state.getValue(FACING)) {
				default -> box(0, 0, 0, 16, 16, 1);
				case NORTH -> box(0, 0, 15, 16, 16, 16);
				case EAST -> box(0, 0, 0, 1, 16, 16);
				case WEST -> box(15, 0, 0, 16, 16, 16);
				case UP -> box(0, 0, 0, 16, 1, 16);
				case DOWN -> box(0, 15, 0, 16, 16, 16);
			};
		}
		
		// State 1: shape theo hướng FACING
		if (stage == 1) {
			return switch (state.getValue(FACING)) {
				default -> box(0, 0, 0, 16, 16, 1);
				case NORTH -> box(0, 0, 15, 16, 16, 16);
				case EAST -> box(0, 0, 0, 1, 16, 16);
				case WEST -> box(15, 0, 0, 16, 16, 16);
				case UP -> box(0, 0, 0, 16, 1, 16);
				case DOWN -> box(0, 15, 0, 16, 16, 16);
			};
		}
		
		// State >= 2: chỉ có shape hướng NORTH (luôn quay về phía trước)
		return box(3, 7, 3, 13, 17, 13);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, BLOCKSTATE);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getClickedFace()).setValue(BLOCKSTATE, 0);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
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