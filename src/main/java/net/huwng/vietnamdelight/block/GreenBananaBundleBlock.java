package net.huwng.vietnamdelight.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.huwng.vietnamdelight.block.WallBananaFrondBlock;
import net.huwng.vietnamdelight.block.BananaFlowerBlock;
import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;

public class GreenBananaBundleBlock extends Block {
    public GreenBananaBundleBlock() {
        super(BlockBehaviour.Properties.of()
            .ignitedByLava()
            .instrument(NoteBlockInstrument.BASS)
            .sound(SoundType.HONEY_BLOCK)
            .strength(2f)
            .randomTicks());
    }

    @Override
public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
    BlockState above = level.getBlockState(pos.above());
    BlockState below = level.getBlockState(pos.below());

    boolean isFrondAbove = above.getBlock() instanceof WallBananaFrondBlock;

    if (!isFrondAbove && below.isAir()) {
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        FallingBlockEntity.fall(level, pos, state);
    }

    super.onPlace(state, level, pos, oldState, isMoving);
}

@Override
public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                              LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
    if (level instanceof Level lvl && direction == Direction.UP) {
        BlockState above = lvl.getBlockState(pos.above());
        BlockState below = lvl.getBlockState(pos.below());

        boolean isFrondState4 = (
            above.getBlock() instanceof WallBananaFrondBlock &&
            above.hasProperty(WallBananaFrondBlock.BLOCKSTATE) &&
            above.getValue(WallBananaFrondBlock.BLOCKSTATE) == 4
        );

        if (!isFrondState4) {
            if (below.getBlock() instanceof BananaFlowerBlock) {
                lvl.destroyBlock(pos.below(), true); 
            }

            if (lvl.getBlockState(pos.below()).isAir()) {
                lvl.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                FallingBlockEntity.fall(lvl, pos, state);
            }
        }
    }

    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
}


    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockPos above = pos.above();
            BlockState aboveState = level.getBlockState(above);

            if (aboveState.getBlock() instanceof WallBananaFrondBlock &&
                aboveState.hasProperty(WallBananaFrondBlock.BLOCKSTATE) &&
                aboveState.getValue(WallBananaFrondBlock.BLOCKSTATE) == 4) {
                level.setBlock(above, aboveState.setValue(WallBananaFrondBlock.BLOCKSTATE, 5), 3);
            }

            level.playSound(null, pos, SoundEvents.HONEY_BLOCK_PLACE, SoundSource.BLOCKS, 1.0f, 1.0f);
        }

        super.onRemove(state, level, pos, newState, isMoving);
    }
    
    @Override
public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                             InteractionHand hand, BlockHitResult hit) {
    ItemStack item = player.getItemInHand(hand);

    if (item.getItem() == Items.HONEYCOMB) {
        level.setBlock(pos, VietnamDelightModBlocks.WAXED_GREEN_BANANA_BUNDLE.get().defaultBlockState(), 3);
        level.playSound(null, pos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0f, 1.0f);
        level.levelEvent(3003, pos, 0);
        if (!player.isCreative()) {
            item.shrink(1);
        }
        return InteractionResult.SUCCESS;
    }

    return super.use(state, level, pos, player, hand, hit);
}


@Override
public boolean isRandomlyTicking(BlockState state) {
    return true;
}

@Override
public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
    if (random.nextInt(5) == 0) {
        level.setBlock(pos, VietnamDelightModBlocks.BANANA_BUNDLE.get().defaultBlockState(), 3);
        BlockPos above = pos.above();
        BlockState aboveState = level.getBlockState(above);

        if (aboveState.getBlock() instanceof WallBananaFrondBlock &&
            aboveState.hasProperty(WallBananaFrondBlock.BLOCKSTATE)) {

            level.setBlock(above, aboveState.setValue(WallBananaFrondBlock.BLOCKSTATE, 4), 3);
        }
    }
}
}
