package net.huwng.vietnamdelight.block;

import net.huwng.vietnamdelight.init.VietnamDelightModItems;
import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;
import net.huwng.vietnamdelight.init.VietnamDelightModSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.HitResult;

public class WhitePeppercornCropBlock extends Block implements BonemealableBlock {
    public static final IntegerProperty BLOCKSTATE = IntegerProperty.create("blockstate", 0, 13);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public WhitePeppercornCropBlock() {
        super(Properties.of()
            .noCollission()
            .instabreak()
            .sound(SoundType.VINE)
            .randomTicks()
            .isRedstoneConductor((s, b, p) -> false));
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(FACING, Direction.NORTH)
            .setValue(BLOCKSTATE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, BLOCKSTATE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        boolean hasLogNearby = hasLogAround(level, pos);
        boolean hasSupportBelow = !level.getBlockState(pos.below()).isAir();

        if (!hasLogNearby || !hasSupportBelow) {
            level.destroyBlock(pos, false);
            return null;
        }

        return this.defaultBlockState()
            .setValue(FACING, context.getHorizontalDirection().getOpposite())
            .setValue(BLOCKSTATE, 0);
    }

@Override
public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
    int stage = state.getValue(BLOCKSTATE);

    // Kiểm tra nền phía dưới
    boolean hasSupportBelow = !level.getBlockState(pos.below()).isAir();
    if (!hasSupportBelow) return false;

    // Nếu stage >= 1 thì cần có log xung quanh
    if (stage >= 1) {
        boolean hasLogNearby = hasLogAround(level, pos);
        if (!hasLogNearby) return false;
    }

    return true;
}

@Override
public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                              LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
    if (!state.canSurvive(level, pos)) {
        level.destroyBlock(pos, false);
    }
    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
}

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int stage = state.getValue(BLOCKSTATE);

        if (stage == 0) {
            Direction logDir = getFacingFromLogs(level, pos);
            if (logDir != null) {
                level.setBlock(pos, state.setValue(FACING, logDir.getOpposite()).setValue(BLOCKSTATE, 1), 3);
            }
            return;
        }

        if (stage >= 1 && stage < 9) {
            level.setBlock(pos, state.setValue(BLOCKSTATE, stage + 1), 3);
            return;
        }

        if (stage == 9 || stage == 12 || stage == 13) {
            BlockPos above = pos.above();
            if (level.getBlockState(above).isAir()) {
                boolean hasLogAbove = hasLogAround(level, above);
                if (hasLogAbove) {
                    BlockState topState = this.defaultBlockState()
                        .setValue(FACING, state.getValue(FACING))
                        .setValue(BLOCKSTATE, 10);
                    level.setBlock(above, topState, 3);
                }
            }
        }

        if (stage == 10) {
            BlockPos above = pos.above();
            if (hasLogAround(level, above)) {
                level.setBlock(pos, state.setValue(BLOCKSTATE, 11), 3);
            }
            return;
        }

        if (stage == 11 || stage == 12) {
            level.setBlock(pos, state.setValue(BLOCKSTATE, stage + 1), 3);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        int stage = state.getValue(BLOCKSTATE);
        RandomSource random = level.getRandom();

        if (stage == 9 || stage == 13) {
            int amount = 1 + random.nextInt(2);
            ItemStack drop = new ItemStack(VietnamDelightModItems.WHITE_PEPPERCORN.get(), amount);
            popResource(level, pos, drop);

            if (!level.isClientSide) {
                level.playSound(null, pos,
                    random.nextBoolean() ? VietnamDelightModSounds.PICK_FRUIT_1.get() : VietnamDelightModSounds.PICK_FRUIT_2.get(),
                    SoundSource.BLOCKS, 1.0F, 1.0F);
            }

            int newStage = (stage == 9) ? 8 : 12;
            level.setBlock(pos, state.setValue(BLOCKSTATE, newStage), 3);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    private Direction getFacingFromLogs(Level level, BlockPos pos) {
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos checkPos = pos.relative(dir);
            if (level.getBlockState(checkPos).is(BlockTags.LOGS)) {
                return dir;
            }
        }
        return null;
    }

private boolean hasLogAround(LevelReader level, BlockPos pos) {
    for (Direction dir : Direction.Plane.HORIZONTAL) {
        BlockPos checkPos = pos.relative(dir);
        if (level.getBlockState(checkPos).is(BlockTags.LOGS)) {
            return true;
        }
    }
    return false;
}
	    	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		if (state.getValue(BLOCKSTATE) == 1) {
			return switch (state.getValue(FACING)) {
				default -> box(0, 0, 0, 16, 16, 1);
				case NORTH -> box(0, 0, 15, 16, 16, 16);
				case EAST -> box(0, 0, 0, 1, 16, 16);
				case WEST -> box(15, 0, 0, 16, 16, 16);
			};
		}
		if (state.getValue(BLOCKSTATE) == 2) {
			return switch (state.getValue(FACING)) {
				default -> box(0, 0, 0, 16, 16, 1);
				case NORTH -> box(0, 0, 15, 16, 16, 16);
				case EAST -> box(0, 0, 0, 1, 16, 16);
				case WEST -> box(15, 0, 0, 16, 16, 16);
			};
		}
		if (state.getValue(BLOCKSTATE) == 3) {
			return switch (state.getValue(FACING)) {
				default -> box(0, 0, 0, 16, 16, 1);
				case NORTH -> box(0, 0, 15, 16, 16, 16);
				case EAST -> box(0, 0, 0, 1, 16, 16);
				case WEST -> box(15, 0, 0, 16, 16, 16);
			};
		}
		if (state.getValue(BLOCKSTATE) == 4) {
			return switch (state.getValue(FACING)) {
				default -> box(0, 0, 0, 16, 16, 1);
				case NORTH -> box(0, 0, 15, 16, 16, 16);
				case EAST -> box(0, 0, 0, 1, 16, 16);
				case WEST -> box(15, 0, 0, 16, 16, 16);
			};
		}
		if (state.getValue(BLOCKSTATE) == 5) {
			return switch (state.getValue(FACING)) {
				default -> box(0, 0, 0, 16, 16, 1);
				case NORTH -> box(0, 0, 15, 16, 16, 16);
				case EAST -> box(0, 0, 0, 1, 16, 16);
				case WEST -> box(15, 0, 0, 16, 16, 16);
			};
		}
		if (state.getValue(BLOCKSTATE) == 6) {
			return switch (state.getValue(FACING)) {
				default -> box(0, 0, 0, 16, 16, 1);
				case NORTH -> box(0, 0, 15, 16, 16, 16);
				case EAST -> box(0, 0, 0, 1, 16, 16);
				case WEST -> box(15, 0, 0, 16, 16, 16);
			};
		}
		if (state.getValue(BLOCKSTATE) == 7) {
			return switch (state.getValue(FACING)) {
				default -> box(0, 0, 0, 16, 16, 1);
				case NORTH -> box(0, 0, 15, 16, 16, 16);
				case EAST -> box(0, 0, 0, 1, 16, 16);
				case WEST -> box(15, 0, 0, 16, 16, 16);
			};
		}
		if (state.getValue(BLOCKSTATE) == 8) {
			return switch (state.getValue(FACING)) {
				default -> box(0, 0, 0, 16, 16, 1);
				case NORTH -> box(0, 0, 15, 16, 16, 16);
				case EAST -> box(0, 0, 0, 1, 16, 16);
				case WEST -> box(15, 0, 0, 16, 16, 16);
			};
		}
		if (state.getValue(BLOCKSTATE) == 9) {
			return switch (state.getValue(FACING)) {
				default -> box(0, 0, 0, 16, 16, 1);
				case NORTH -> box(0, 0, 15, 16, 16, 16);
				case EAST -> box(0, 0, 0, 1, 16, 16);
				case WEST -> box(15, 0, 0, 16, 16, 16);
			};
		}
		if (state.getValue(BLOCKSTATE) == 10) {
			return switch (state.getValue(FACING)) {
				default -> box(0, 0, 0, 16, 16, 1);
				case NORTH -> box(0, 0, 15, 16, 16, 16);
				case EAST -> box(0, 0, 0, 1, 16, 16);
				case WEST -> box(15, 0, 0, 16, 16, 16);
			};
		}
		if (state.getValue(BLOCKSTATE) == 11) {
			return switch (state.getValue(FACING)) {
				default -> box(0, 0, 0, 16, 16, 1);
				case NORTH -> box(0, 0, 15, 16, 16, 16);
				case EAST -> box(0, 0, 0, 1, 16, 16);
				case WEST -> box(15, 0, 0, 16, 16, 16);
			};
		}
		if (state.getValue(BLOCKSTATE) == 12) {
			return switch (state.getValue(FACING)) {
				default -> box(0, 0, 0, 16, 16, 1);
				case NORTH -> box(0, 0, 15, 16, 16, 16);
				case EAST -> box(0, 0, 0, 1, 16, 16);
				case WEST -> box(15, 0, 0, 16, 16, 16);
			};
		}
		if (state.getValue(BLOCKSTATE) == 13) {
			return switch (state.getValue(FACING)) {
				default -> box(0, 0, 0, 16, 16, 1);
				case NORTH -> box(0, 0, 15, 16, 16, 16);
				case EAST -> box(0, 0, 0, 1, 16, 16);
				case WEST -> box(15, 0, 0, 16, 16, 16);
			};
		}
		return switch (state.getValue(FACING)) {
			default -> box(2, 0, 2, 14, 2, 14);
			case NORTH -> box(2, 0, 2, 14, 2, 14);
			case EAST -> box(2, 0, 2, 14, 2, 14);
			case WEST -> box(2, 0, 2, 14, 2, 14);
			};
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
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
		return new ItemStack(VietnamDelightModItems.WHITE_PEPPERCORN.get());
	}
	
    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
        return state.getValue(BLOCKSTATE) < 13;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

@Override
public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
    int stage = state.getValue(BLOCKSTATE);

    if (stage == 0) {
        // Từ 0 → 1 và bám vào log
        Direction logDir = getFacingFromLogs(level, pos);
        if (logDir != null) {
            BlockState newState = state.setValue(FACING, logDir.getOpposite()).setValue(BLOCKSTATE, 1);
            level.setBlock(pos, newState, 3);
        }
    } else if (stage >= 1 && stage < 8) {
        level.setBlock(pos, state.setValue(BLOCKSTATE, stage + 1), 3);
    } else if (stage == 8) {
        BlockPos above = pos.above();
        if (level.getBlockState(above).isAir() && hasLogAround(level, above)) {
            BlockState newPepper = VietnamDelightModBlocks.WHITE_PEPPERCORN_CROP.get()
                .defaultBlockState()
                .setValue(FACING, state.getValue(FACING))
                .setValue(BLOCKSTATE, 10);
            level.setBlock(above, newPepper, 3);
        }
    } else if (stage == 10) {
        BlockPos above = pos.above();
        if (hasLogAround(level, above)) {
            level.setBlock(pos, state.setValue(BLOCKSTATE, 11), 3);
        }
    } else if (stage == 11) {
        level.setBlock(pos, state.setValue(BLOCKSTATE, 12), 3);
    } else if (stage == 12) {
        BlockPos above = pos.above();
        if (level.getBlockState(above).isAir() && hasLogAround(level, above)) {
            BlockState newPepper = VietnamDelightModBlocks.WHITE_PEPPERCORN_CROP.get()
                .defaultBlockState()
                .setValue(FACING, state.getValue(FACING))
                .setValue(BLOCKSTATE, 10);
            level.setBlock(above, newPepper, 3);
        }
    }
}


}