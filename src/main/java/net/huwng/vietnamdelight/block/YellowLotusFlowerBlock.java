package net.huwng.vietnamdelight.block;

import net.huwng.vietnamdelight.init.VietnamDelightModItems;
import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import java.util.Collections;
import java.util.List;

public class YellowLotusFlowerBlock extends Block {
    public static final IntegerProperty BLOCKSTATE = IntegerProperty.create("blockstate", 0, 2);

    public YellowLotusFlowerBlock() {
        super(BlockBehaviour.Properties.of()
            .sound(SoundType.GRASS)
            .instabreak()
            .noCollission()
            .noOcclusion()
            .randomTicks()
            .isRedstoneConductor((state, world, pos) -> false));
        this.registerDefaultState(this.defaultBlockState().setValue(BLOCKSTATE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BLOCKSTATE);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        int stage = state.getValue(BLOCKSTATE);
        return stage == 0
            ? Shapes.box(0.125, 0.0, 0.125, 0.875, 0.75, 0.875)
            : Shapes.box(0.375, 0.0, 0.375, 0.625, 1.0, 0.625);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
        return true;
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter world, BlockPos pos) {
        return 0;
    }

    public boolean canPlaceOn(BlockState below) {
        return below.is(net.minecraft.tags.BlockTags.DIRT)
            || below.is(net.minecraft.tags.BlockTags.SAND)
            || below.is(VietnamDelightModBlocks.LOTUS_STEM.get());
    }

    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return canPlaceOn(world.getBlockState(pos.below()));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        return canSurvive(this.defaultBlockState(), context.getLevel(), pos)
            ? this.defaultBlockState().setValue(BLOCKSTATE, 0)
            : null;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState,
                                  LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
        if (facing == Direction.DOWN && !canSurvive(state, world, currentPos)) {
            world.removeBlock(currentPos, false);
        }
        return state;
    }

    @Override
public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
    if (!world.getBlockState(pos.below()).is(VietnamDelightModBlocks.LOTUS_STEM.get())) {
        return;
    }

    // 🛑 Nếu phát hiện thân sen BLOCKSTATE == 3 gần đó thì không cho grow
    if (hasNearbyStemStage3(world, pos)) {
        return;
    }

    int stage = state.getValue(BLOCKSTATE);
    if (stage < 2 && random.nextInt(5) == 0) {
        world.setBlock(pos, state.setValue(BLOCKSTATE, stage + 1), 3);
    }
}
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        int stage = state.getValue(BLOCKSTATE);
        RandomSource random = builder.getLevel().random;

        if (stage == 0) {
            return Collections.singletonList(new ItemStack(VietnamDelightModItems.YELLOW_LOTUS_FLOWER.get()));
        } else if (stage == 2) {
            return Collections.singletonList(new ItemStack(VietnamDelightModItems.LOTUS_SEEDS.get(), 1 + random.nextInt(8)));
        }

        return Collections.emptyList();
    }
    
    @Override
public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
    super.animateTick(state, world, pos, random);

    // 🎇 Kiểm tra nếu phía dưới là thân sen
    if (world.getBlockState(pos.below()).is(VietnamDelightModBlocks.LOTUS_STEM.get())) {
        // ✨ Phát ra particle tia điện
        for (int i = 0; i < 3; i++) {
            double offsetX = 0.5 + (random.nextDouble() - 0.5) * 0.4;
            double offsetY = 0.7 + (random.nextDouble() * 0.3);
            double offsetZ = 0.5 + (random.nextDouble() - 0.5) * 0.4;
            world.addParticle(net.minecraft.core.particles.ParticleTypes.ELECTRIC_SPARK,
                pos.getX() + offsetX, pos.getY() + offsetY, pos.getZ() + offsetZ,
                0, 0.01, 0);
        }

        // 🔊 Phát âm thanh nhẹ nhàng
        world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(),
            net.minecraft.sounds.SoundEvents.ALLAY_ITEM_GIVEN,
            net.minecraft.sounds.SoundSource.BLOCKS,
            0.2f, 1.3f, false);
    }
}
    @Override
    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        if (!world.isClientSide) {
            int stage = state.getValue(BLOCKSTATE);
            RandomSource random = world.random;

            if (stage == 0) {
                Block.popResource(world, pos, new ItemStack(VietnamDelightModItems.YELLOW_LOTUS_FLOWER.get()));
            } else if (stage == 2) {
                Block.popResource(world, pos, new ItemStack(VietnamDelightModItems.LOTUS_SEEDS.get(), 1 + random.nextInt(8)));
            }

            // 💥 Phá thân sen phía dưới
            BlockPos current = pos.below();
            while (world.getBlockState(current).is(VietnamDelightModBlocks.LOTUS_STEM.get())) {
                world.destroyBlock(current, true);
                current = current.below();
            }
        }

        super.playerWillDestroy(world, pos, state, player);
    }
    private boolean hasNearbyStemStage3(Level world, BlockPos center) {
    for (int dx = -2; dx <= 2; dx++) {
        for (int dy = -2; dy <= 2; dy++) {
            for (int dz = -2; dz <= 2; dz++) {
                BlockPos check = center.offset(dx, dy, dz);
                BlockState nearby = world.getBlockState(check);

                if (nearby.is(VietnamDelightModBlocks.LOTUS_STEM.get())
                    && nearby.hasProperty(LotusStemBlock.BLOCKSTATE)
                    && nearby.getValue(LotusStemBlock.BLOCKSTATE) == 3) {
                    return true;
                }
            }
        }
    }
    return false;
}
}
