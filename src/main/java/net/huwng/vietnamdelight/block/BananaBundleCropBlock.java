package net.huwng.vietnamdelight.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.huwng.vietnamdelight.init.VietnamDelightModItems;
import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;

public class BananaBundleCropBlock extends Block implements BonemealableBlock {
    public static final IntegerProperty BLOCKSTATE = IntegerProperty.create("blockstate", 0, 4);

    private static final VoxelShape SMALL_SHAPE = box(3, 7, 3, 13, 16, 13);

    public BananaBundleCropBlock() {
        super(BlockBehaviour.Properties.of()
            .ignitedByLava()
            .sound(SoundType.HONEY_BLOCK)
            .instabreak()
            .lightLevel(s -> 0)
            .noCollission()
            .noOcclusion()
            .randomTicks()
            .isRedstoneConductor((bs, br, bp) -> false));
        this.registerDefaultState(this.stateDefinition.any().setValue(BLOCKSTATE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BLOCKSTATE);
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
        return box(3, 0, 3, 13, 16, 13);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        return new ItemStack(VietnamDelightModItems.BANANA_BUNCH.get());
    }
    
	@Override
	public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state,
	                          net.minecraft.world.level.block.entity.BlockEntity be, ItemStack stack) {
	    super.playerDestroy(level, player, pos, state, be, stack);
	
	    if (!level.isClientSide) {
	        int stage = state.getValue(BLOCKSTATE);
	
	        if (stage == 4) {
	            int count = 1 + level.random.nextInt(3);
	            popResource(level, pos, new ItemStack(VietnamDelightModItems.BANANA_BUNCH.get(), count));
	
	        } else if (stage >= 1 && stage <= 3) {
	            int count = 1 + level.random.nextInt(3);
	            popResource(level, pos, new ItemStack(VietnamDelightModItems.GREEN_BANANA_BUNCH.get(), count));
	        }
	    }
	}
	
	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
	    super.onRemove(state, level, pos, newState, isMoving);
	
	    if (!level.isClientSide
	        && state.getBlock() == this
	        && (newState.getBlock() != this || newState.getValue(BLOCKSTATE) < state.getValue(BLOCKSTATE))) {
	
	        // ✅ Chỉ xử lý khi block bị phá hoặc bị thay bằng block khác
	        BlockPos above = pos.above();
	        BlockState aboveState = level.getBlockState(above);
	        if (aboveState.getBlock() == VietnamDelightModBlocks.WALL_BANANA_FROND.get()) {
	            level.setBlock(above, aboveState.setValue(WallBananaFrondBlock.BLOCKSTATE, 5), 3);
	        }
	    }
	}

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState above = level.getBlockState(pos.above());
        return above.getBlock() instanceof WallBananaFrondBlock;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (!canSurvive(state, level, pos) && level instanceof Level lvl) {
            lvl.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(BLOCKSTATE) < 4;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        growTick(state, level, pos, random, false);
    }

    private void growTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, boolean bonemeal) {
        if (!canSurvive(state, level, pos)) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            return;
        }

        int stage = state.getValue(BLOCKSTATE);

        if (stage == 0) {
            // sang stage 1
            if (bonemeal || random.nextInt(5) == 0) {
                level.setBlock(pos, state.setValue(BLOCKSTATE, 1), 3);

                // đặt hoa bên dưới
                BlockPos below = pos.below();
                if (level.getBlockState(below).isAir()) {
                    level.setBlock(below, VietnamDelightModBlocks.BANANA_FLOWER.get().defaultBlockState(), 3);
                }
            }
        } else if (stage == 1 || stage == 2) {
            // tiếp tục lớn
            if (bonemeal || random.nextInt(5) == 0) {
                level.setBlock(pos, state.setValue(BLOCKSTATE, stage + 1), 3);
            }
        } else if (stage == 3) {
            // sang stage 4 hoặc Green Banana Bundle
            if (bonemeal || random.nextInt(5) == 0) {
                if (random.nextFloat() < 0.7f) {
                    level.setBlock(pos, state.setValue(BLOCKSTATE, 4), 3);
                } else {
                    level.setBlock(pos, VietnamDelightModBlocks.GREEN_BANANA_BUNDLE.get().defaultBlockState(), 3);
                }
            }
        }
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
        return state.getValue(BLOCKSTATE) < 4;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        growTick(state, level, pos, random, true);
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
