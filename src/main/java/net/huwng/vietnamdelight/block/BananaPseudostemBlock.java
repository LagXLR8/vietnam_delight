package net.huwng.vietnamdelight.block;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;

public class BananaPseudostemBlock extends Block {
    public static final IntegerProperty BLOCKSTATE = IntegerProperty.create("blockstate", 0, 6);
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;

    public BananaPseudostemBlock() {
        super(BlockBehaviour.Properties.of()
            .ignitedByLava()
            .instrument(NoteBlockInstrument.BASS)
            .sound(SoundType.HARD_CROP)
            .strength(2f)
            .lightLevel(s -> 0)
            .randomTicks());

        this.registerDefaultState(this.stateDefinition.any()
            .setValue(AXIS, Direction.Axis.Y)
            .setValue(BLOCKSTATE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS, BLOCKSTATE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
            .setValue(AXIS, context.getClickedFace().getAxis())
            .setValue(BLOCKSTATE, 0);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        if (rot == Rotation.CLOCKWISE_90 || rot == Rotation.COUNTERCLOCKWISE_90) {
            if (state.getValue(AXIS) == Direction.Axis.X) {
                return state.setValue(AXIS, Direction.Axis.Z);
            } else if (state.getValue(AXIS) == Direction.Axis.Z) {
                return state.setValue(AXIS, Direction.Axis.X);
            }
        }
        return state;
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        int value = state.getValue(BLOCKSTATE);
        return value == 0 || value == 2 || value == 4;
    }
	
    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockPos above = pos.above();
        BlockState aboveState = level.getBlockState(above);

        if (aboveState.isAir()) {
            int current = state.getValue(BLOCKSTATE);
            if (current == 0) {
                level.setBlock(pos, state.setValue(BLOCKSTATE, 1), 3);
            } else if (current == 2) {
                level.setBlock(pos, state.setValue(BLOCKSTATE, 3), 3);
            } else if (current == 4) {
                level.setBlock(pos, state.setValue(BLOCKSTATE, 5), 3);
            }
        }
    }
    
	@Override
public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
    if (state.getValue(BLOCKSTATE) == 4 || state.getValue(BLOCKSTATE) == 6) {
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos side = pos.relative(dir);
            if (level.getBlockState(side).isAir()) {
                level.setBlock(side,
                        VietnamDelightModBlocks.WALL_BANANA_FROND.get().defaultBlockState()
                                .setValue(WallBananaFrondBlock.FACING, dir),3);
            }
        }
    }
}

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        int value = state.getValue(BLOCKSTATE);
        Direction.Axis axis = state.getValue(AXIS);

        return switch (value) {
            case 1 -> switch (axis) {
                case X, Y, Z -> box(0, 0, 0, 16, 16, 16);
            };
            case 2, 3 -> switch (axis) {
                case X -> box(0, 1, 1, 16, 15, 15);
                case Y -> box(1, 0, 1, 15, 16, 15);
                case Z -> box(1, 1, 0, 15, 15, 16);
            };
            case 4, 5 -> switch (axis) {
                case X -> box(0, 2, 2, 16, 14, 14);
                case Y -> box(2, 0, 2, 14, 16, 14);
                case Z -> box(2, 2, 0, 14, 14, 16);
            };
            case 6 -> switch (axis) {
                case X -> box(0, 3, 3, 16, 13, 13);
                case Y -> box(3, 0, 3, 13, 16, 13);
                case Z -> box(3, 3, 0, 13, 13, 16);
            };
            default -> box(0, 0, 0, 16, 16, 16);
        };
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
