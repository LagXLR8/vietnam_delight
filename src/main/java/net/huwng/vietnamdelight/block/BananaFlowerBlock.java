package net.huwng.vietnamdelight.block;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.entity.player.Player;
import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;
import net.huwng.vietnamdelight.init.VietnamDelightModItems;
import java.util.List;

public class BananaFlowerBlock extends Block implements BonemealableBlock {
    public static final IntegerProperty BLOCKSTATE = IntegerProperty.create("blockstate", 0, 5);

    public BananaFlowerBlock() {
        super(BlockBehaviour.Properties.of()
            .ignitedByLava()
            .sound(SoundType.CAVE_VINES)
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
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        return new ItemStack(VietnamDelightModItems.BANANA_BLOSSOM.get());
    }
    
    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return box(3, 7, 3, 13, 16, 13);
    }

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
	    BlockState above = level.getBlockState(pos.above());
	    Block blockAbove = above.getBlock();
	
	    return blockAbove == VietnamDelightModBlocks.BANANA_BUNDLE.get()
	        || blockAbove == VietnamDelightModBlocks.GREEN_BANANA_BUNDLE.get()
	        || blockAbove == VietnamDelightModBlocks.BANANA_BUNDLE_CROP.get();
	}

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (!canSurvive(state, level, pos) && level instanceof Level lvl) {
            if (state.getValue(BLOCKSTATE) == 5) {
                Block.popResource(lvl, pos, new ItemStack(VietnamDelightModItems.BANANA_BLOSSOM.get(), 1));
            }
            lvl.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }
    
	@Override
    public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(BLOCKSTATE) < 5;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (random.nextInt(5) == 0) {
            int stage = state.getValue(BLOCKSTATE);
            if (stage < 5) {
                level.setBlock(pos, state.setValue(BLOCKSTATE, stage + 1), 3);
            }
        }
    }
    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        if (state.getValue(BLOCKSTATE) == 5) {
            return List.of(new ItemStack(VietnamDelightModItems.BANANA_BLOSSOM.get(), 1));
        }
        return List.of();
    }
    
    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
        return state.getValue(BLOCKSTATE) < 5;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        int stage = state.getValue(BLOCKSTATE);
        if (stage < 5) {
            level.setBlock(pos, state.setValue(BLOCKSTATE, stage + 1), 3);
        }
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
