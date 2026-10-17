package net.huwng.vietnamdelight.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.Direction;
import net.huwng.vietnamdelight.block.WallBananaFrondBlock;

public class BananaBundleBlock extends Block {
    public BananaBundleBlock() {
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

        boolean isFrondAbove = above.getBlock() instanceof WallBananaFrondBlock;

        if (!isFrondAbove) {
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
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 15;
    }
}
