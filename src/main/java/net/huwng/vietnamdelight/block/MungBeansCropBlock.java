package net.huwng.vietnamdelight.block;

import net.huwng.vietnamdelight.init.VietnamDelightModItems;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.*;
import net.minecraft.core.Direction;

import java.util.List;

public class MungBeansCropBlock extends Block implements BonemealableBlock {
    public static final IntegerProperty BLOCKSTATE = IntegerProperty.create("blockstate", 0, 4); // 0 -> 4

    public MungBeansCropBlock() {
        super(BlockBehaviour.Properties.of()
            .sound(SoundType.CROP)
            .instabreak()
            .noCollission()
            .randomTicks()
            .ignitedByLava()
            .isRedstoneConductor((bs, br, bp) -> false));
        registerDefaultState(this.stateDefinition.any().setValue(BLOCKSTATE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BLOCKSTATE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(BLOCKSTATE, 0);
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
        return new ItemStack(VietnamDelightModItems.MUNG_BEANS.get());
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(BLOCKSTATE) < 4;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (random.nextInt(3) == 0) {
            int stage = state.getValue(BLOCKSTATE);
            if (stage < 4) {
                level.setBlock(pos, state.setValue(BLOCKSTATE, stage + 1), 3);
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
        int stage = state.getValue(BLOCKSTATE);
        if (stage < 4) {
            level.setBlock(pos, state.setValue(BLOCKSTATE, stage + 1), 3);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        int stage = state.getValue(BLOCKSTATE);
        switch (stage) {
            case 0: return box(0, 0, 0, 16, 2, 16);
            case 1: return box(0, 0, 0, 16, 4, 16);
            case 2: return box(0, 0, 0, 16, 8, 16);
            case 3: return box(2, 0, 2, 14, 12, 14);
            case 4: return box(2, 0, 2, 14, 14, 14);
            default: return box(0, 0, 0, 16, 16, 16);
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos currentPos, BlockPos neighborPos) {
        if (direction == Direction.DOWN && !world.getBlockState(currentPos.below()).is(Blocks.FARMLAND)) {
            return Blocks.AIR.defaultBlockState();
        }
        return state;
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
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        int stage = state.getValue(BLOCKSTATE);
        RandomSource random = builder.getLevel().random;

        if (stage == 1) {
            int count = 1 + random.nextInt(3); // 1–3 sprouts
            return List.of(new ItemStack(VietnamDelightModItems.BEAN_SPROUTS.get(), count));
        } else if (stage == 4) {
            int count = 1 + random.nextInt(3); // 1–3 beans
            return List.of(new ItemStack(VietnamDelightModItems.MUNG_BEANS.get(), count));
        }
        return List.of();
    }
}
