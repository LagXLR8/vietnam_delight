package net.huwng.vietnamdelight.block;

import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.huwng.vietnamdelight.init.VietnamDelightModItems;
import net.minecraft.world.level.storage.loot.LootParams;
import java.util.List;
import java.util.Random;
import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;

public class WallBananaFrondBlock extends Block {
	public static final IntegerProperty BLOCKSTATE = IntegerProperty.create("blockstate", 0, 7);
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

	public WallBananaFrondBlock() {
		super(BlockBehaviour.Properties.of().ignitedByLava().sound(SoundType.SPORE_BLOSSOM).instabreak().lightLevel(s -> (new Object() {
			public int getLightLevel() {
				if (s.getValue(BLOCKSTATE) == 1)
					return 0;
				if (s.getValue(BLOCKSTATE) == 2)
					return 0;
				if (s.getValue(BLOCKSTATE) == 3)
					return 0;
				if (s.getValue(BLOCKSTATE) == 4)
					return 0;
				if (s.getValue(BLOCKSTATE) == 5)
					return 0;
				if (s.getValue(BLOCKSTATE) == 6)
					return 0;
				if (s.getValue(BLOCKSTATE) == 7)
					return 0;
				return 0;
			}
		}.getLightLevel())).noCollission().noOcclusion().isRedstoneConductor((bs, br, bp) -> false));
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
		return true;
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
		if (state.getValue(BLOCKSTATE) == 1) {
			return switch (state.getValue(FACING)) {
				default -> box(2, 0, 0, 14, 16, 14);
				case NORTH -> box(2, 0, 2, 14, 16, 16);
				case EAST -> box(0, 0, 2, 14, 16, 14);
				case WEST -> box(2, 0, 2, 16, 16, 14);
			};
		}
		if (state.getValue(BLOCKSTATE) == 2) {
			return switch (state.getValue(FACING)) {
				default -> box(2, 0, 0, 14, 16, 14);
				case NORTH -> box(2, 0, 2, 14, 16, 16);
				case EAST -> box(0, 0, 2, 14, 16, 14);
				case WEST -> box(2, 0, 2, 16, 16, 14);
			};
		}
		if (state.getValue(BLOCKSTATE) == 3) {
			return switch (state.getValue(FACING)) {
				default -> box(2, 0, 0, 14, 16, 14);
				case NORTH -> box(2, 0, 2, 14, 16, 16);
				case EAST -> box(0, 0, 2, 14, 16, 14);
				case WEST -> box(2, 0, 2, 16, 16, 14);
			};
		}
		if (state.getValue(BLOCKSTATE) == 4) {
			return switch (state.getValue(FACING)) {
				default -> box(2, 0, 0, 14, 16, 14);
				case NORTH -> box(2, 0, 2, 14, 16, 16);
				case EAST -> box(0, 0, 2, 14, 16, 14);
				case WEST -> box(2, 0, 2, 16, 16, 14);
			};
		}
		if (state.getValue(BLOCKSTATE) == 5) {
			return switch (state.getValue(FACING)) {
				default -> box(2, 0, 0, 14, 16, 14);
				case NORTH -> box(2, 0, 2, 14, 16, 16);
				case EAST -> box(0, 0, 2, 14, 16, 14);
				case WEST -> box(2, 0, 2, 16, 16, 14);
			};
		}
		if (state.getValue(BLOCKSTATE) == 6) {
			return switch (state.getValue(FACING)) {
				default -> box(2, 0, 0, 14, 16, 14);
				case NORTH -> box(2, 0, 2, 14, 16, 16);
				case EAST -> box(0, 0, 2, 14, 16, 14);
				case WEST -> box(2, 0, 2, 16, 16, 14);
			};
		}
		if (state.getValue(BLOCKSTATE) == 7) {
			return switch (state.getValue(FACING)) {
				default -> box(2, 0, 0, 14, 16, 14);
				case NORTH -> box(2, 0, 2, 14, 16, 16);
				case EAST -> box(0, 0, 2, 14, 16, 14);
				case WEST -> box(2, 0, 2, 16, 16, 14);
			};
		}
		return switch (state.getValue(FACING)) {
			default -> box(0, 0, 0, 16, 16, 16);
			case NORTH -> box(0, 0, 0, 16, 16, 16);
			case EAST -> box(0, 0, 0, 16, 16, 16);
			case WEST -> box(0, 0, 0, 16, 16, 16);
		};
	}

@Override
public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
    for (Direction dir : Direction.Plane.HORIZONTAL) {
        BlockPos checkPos = pos.relative(dir);
        BlockState neighbor = level.getBlockState(checkPos);
        if (neighbor.getBlock().getClass().getSimpleName().equals("BananaPseudostemBlock")) {
            return true;
        }
    }
    return false;
}

@Override
public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                              LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
    if (!canSurvive(state, level, pos)) {
        level.destroyBlock(pos, true);
    } else if (level instanceof Level lvl) {
        lvl.scheduleTick(pos, this, 1); // Tick ngay sau khi có thay đổi
    }

    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
}

@Override
public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
    if (!canSurvive(state, level, pos)) {
        level.destroyBlock(pos, true);
        return;
    }

    int currentState = state.getValue(BLOCKSTATE);
    boolean isRaining = level.isRaining();

    // Chuyển trạng thái theo thời tiết
    if (isRaining) {
        if (currentState == 0) {
            level.setBlock(pos, state.setValue(BLOCKSTATE, 1), 3);
        } else if (currentState == 2) {
            level.setBlock(pos, state.setValue(BLOCKSTATE, 3), 3);
        } else if (currentState == 6) {
            level.setBlock(pos, state.setValue(BLOCKSTATE, 7), 3);
        }
    } else {
        if (currentState == 1) {
            level.setBlock(pos, state.setValue(BLOCKSTATE, 0), 3);
        } else if (currentState == 3) {
            level.setBlock(pos, state.setValue(BLOCKSTATE, 2), 3);
        } else if (currentState == 7) {
            level.setBlock(pos, state.setValue(BLOCKSTATE, 6), 3);
        }

        // Nếu đang ở state 0 và không có state 2 xung quanh → chuyển sang state 2
        if (currentState == 0) {
            boolean hasNearbyState2 = false;

            for (BlockPos checkPos : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) {
                BlockState nearby = level.getBlockState(checkPos);
                if (nearby.getBlock() instanceof WallBananaFrondBlock &&
                    nearby.getValue(BLOCKSTATE) == 2) {
                    hasNearbyState2 = true;
                    break;
                }
            }

            if (!hasNearbyState2) {
                level.setBlock(pos, state.setValue(BLOCKSTATE, 2), 3);
            }
        }
    }

    // Lên lịch tick lại để duy trì kiểm tra thời tiết
    level.scheduleTick(pos, this, 20); // Tick lại sau 1 giây
}

@Override
public boolean isRandomlyTicking(BlockState state) {
    return true;
}

@Override
public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
    if (!canSurvive(state, level, pos)) {
        level.destroyBlock(pos, true);
        return;
    }

    int currentState = state.getValue(BLOCKSTATE);
    boolean isRaining = level.isRaining();

    // Xử lý thời tiết
    if (isRaining) {
        if (currentState == 0 && random.nextFloat() < 0.8f) {
            level.setBlock(pos, state.setValue(BLOCKSTATE, 1), 3);
            return;
        } else if (currentState == 2 && random.nextFloat() < 0.8f) {
            level.setBlock(pos, state.setValue(BLOCKSTATE, 3), 3);
            return;
        } else if (currentState == 6 && random.nextFloat() < 0.8f) {
            level.setBlock(pos, state.setValue(BLOCKSTATE, 7), 3);
            return;
        }
    } else {
        if (currentState == 1 && random.nextFloat() < 0.8f) {
            level.setBlock(pos, state.setValue(BLOCKSTATE, 0), 3);
            return;
        } else if (currentState == 3 && random.nextFloat() < 0.8f) {
            level.setBlock(pos, state.setValue(BLOCKSTATE, 2), 3);
            return;
        } else if (currentState == 7 && random.nextFloat() < 0.8f) {
            level.setBlock(pos, state.setValue(BLOCKSTATE, 6), 3);
            return;
        }

        // Nếu đang ở state 0 kiểm tra vùng 3x3x3 để chuyển sang state 2
        if (currentState == 0 && random.nextFloat() < 0.8f) {
            boolean hasNearbyState2 = false;

            for (BlockPos checkPos : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) {
                BlockState nearby = level.getBlockState(checkPos);
                if (nearby.getBlock() instanceof WallBananaFrondBlock &&
                    nearby.getValue(BLOCKSTATE) == 2) {
                    hasNearbyState2 = true;
                    break;
                }
            }

            if (!hasNearbyState2) {
                level.setBlock(pos, state.setValue(BLOCKSTATE, 2), 3);
                return;
            }
        }
    }

    // Nếu đang ở state 0 kiểm tra vùng 5x5x5 để chuyển sang state 4
    if (currentState == 0 && random.nextFloat() < 0.8f) {
        boolean hasNearbyState4 = false;

        for (BlockPos checkPos : BlockPos.betweenClosed(pos.offset(-2, -2, -2), pos.offset(2, 2, 2))) {
            BlockState nearby = level.getBlockState(checkPos);
            if (nearby.getBlock() instanceof WallBananaFrondBlock &&
                nearby.getValue(BLOCKSTATE) == 4) {
                hasNearbyState4 = true;
                break;
            }
        }

        if (!hasNearbyState4) {
            level.setBlock(pos, state.setValue(BLOCKSTATE, 4), 3);
            return;
        }
    }

    // Nếu đang ở state 4 lan truyền và mọc Banana_Bundle
    if (currentState == 4) {
        for (BlockPos checkPos : BlockPos.betweenClosed(pos.offset(-2, -2, -2), pos.offset(2, 2, 2))) {
            BlockState nearby = level.getBlockState(checkPos);
            if (nearby.getBlock() instanceof WallBananaFrondBlock &&
                nearby.getValue(BLOCKSTATE) == 0 &&
                random.nextFloat() < 0.8f) {
                level.setBlock(checkPos, nearby.setValue(BLOCKSTATE, 6), 3);
            }
        }

        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);

        if (belowState.isAir()) {
            level.setBlock(below, VietnamDelightModBlocks.BANANA_BUNDLE_CROP.get().defaultBlockState(), 3);
        }
    }
}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, BLOCKSTATE);
	}

@Override
public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
    if (!level.isClientSide) return;
    int currentState = state.getValue(BLOCKSTATE);
    if (currentState == 1 || currentState == 3 || currentState == 7) {
        double minX = pos.getX() - 0.5;
        double maxX = pos.getX() + 1.5;
        double minY = pos.getY() + 0.9;
        double maxY = pos.getY() + 1.2;
        double minZ = pos.getZ() - 0.5;
        double maxZ = pos.getZ() + 1.5;

        if (random.nextFloat() < 0.4f) {
            double x = minX + random.nextDouble() * (maxX - minX);
            double y = minY + random.nextDouble() * (maxY - minY);
            double z = minZ + random.nextDouble() * (maxZ - minZ);

            level.addParticle(ParticleTypes.FALLING_DRIPSTONE_WATER, x, y, z, 0, -0.05, 0);
        }
    }
}
	
	@Override
	public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
    return new ItemStack(VietnamDelightModItems.BANANA_LEAF.get());
	}

@Override
public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
    Random random = new Random();
    int currentState = state.getValue(BLOCKSTATE);
    if (currentState == 0 || currentState == 1 ||
        currentState == 4 || currentState == 5 ||
        currentState == 6 || currentState == 7) {

        int leafCount = 1 + random.nextInt(4); // 1–4
        return List.of(new ItemStack(VietnamDelightModItems.BANANA_LEAF.get(), leafCount));
    }
    if (currentState == 2 || currentState == 3) {
        int leafCount = 1 + random.nextInt(2);  // 1–2
        int driedCount = 1 + random.nextInt(2); // 1–2
        return List.of(
            new ItemStack(VietnamDelightModItems.BANANA_LEAF.get(), leafCount),
            new ItemStack(VietnamDelightModItems.DRIED_BANANA_LEAF.get(), driedCount)
        );
    }
    return List.of();
}
    
@Override
public BlockState getStateForPlacement(BlockPlaceContext context) {
    Level level = context.getLevel();
    BlockPos pos = context.getClickedPos();

    for (Direction dir : Direction.Plane.HORIZONTAL) {
        BlockPos checkPos = pos.relative(dir);
        BlockState neighbor = level.getBlockState(checkPos);
        if (neighbor.getBlock().getClass().getSimpleName().equals("BananaPseudostemBlock")) {
            BlockState placedState = this.defaultBlockState()
                .setValue(FACING, dir.getOpposite())
                .setValue(BLOCKSTATE, 0);

            level.scheduleTick(pos, this, 1);
            return placedState;
        }
    }

    return null;
}
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
	}
}
