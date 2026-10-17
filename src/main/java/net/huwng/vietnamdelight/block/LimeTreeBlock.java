package net.huwng.vietnamdelight.block;

import net.huwng.vietnamdelight.init.VietnamDelightModItems;
import net.huwng.vietnamdelight.init.VietnamDelightModSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.*;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.block.BonemealableBlock;

public class LimeTreeBlock extends Block implements BonemealableBlock {
    public static final IntegerProperty BLOCKSTATE = IntegerProperty.create("blockstate", 0, 3);

    public LimeTreeBlock() {
        super(BlockBehaviour.Properties.of()
            .sound(SoundType.AZALEA)
            .instabreak()
            .randomTicks()
            .noCollission()
            .noOcclusion()
            .ignitedByLava()
            .isRedstoneConductor((bs, br, bp) -> false));
        registerDefaultState(this.stateDefinition.any().setValue(BLOCKSTATE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BLOCKSTATE);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(BLOCKSTATE) < 3;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int stage = state.getValue(BLOCKSTATE);
        if (random.nextInt(5) == 0 && stage < 3) {
            level.setBlock(pos, state.setValue(BLOCKSTATE, stage + 1), 3);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (state.getValue(BLOCKSTATE) == 3) {
            RandomSource random = level.getRandom();
            int amount = 1 + random.nextInt(4); // 1–4 quả Lemon
            ItemStack drop = new ItemStack(VietnamDelightModItems.LIME.get(), amount);

            popResource(level, pos, drop);
            level.setBlock(pos, state.setValue(BLOCKSTATE, 0), 3);

            if (!level.isClientSide) {
                level.playSound(null, pos,
                    random.nextBoolean() ? VietnamDelightModSounds.PICK_FRUIT_1.get() : VietnamDelightModSounds.PICK_FRUIT_2.get(),
                    SoundSource.BLOCKS, 1.0F, 1.0F);
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
        return state.getValue(BLOCKSTATE) < 3;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        int stage = state.getValue(BLOCKSTATE);
        if (stage < 3) {
            level.setBlock(pos, state.setValue(BLOCKSTATE, stage + 1), 3);
        }
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        entity.makeStuckInBlock(state, new Vec3(0.8D, 0.75D, 0.8D));
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        Block blockBelow = below.getBlock();

        return blockBelow == Blocks.GRASS_BLOCK ||
               blockBelow == Blocks.DIRT ||
               blockBelow == Blocks.COARSE_DIRT ||
               blockBelow == Blocks.PODZOL ||
               blockBelow == Blocks.FARMLAND;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        LevelReader level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        if (!canSurvive(defaultBlockState(), level, pos)) {
            return null;
        }

        return defaultBlockState().setValue(BLOCKSTATE, 0);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.or(box(0, 5, 0, 16, 16, 16), box(6, 0, 6, 10, 11, 10));
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 0;
    }
}
