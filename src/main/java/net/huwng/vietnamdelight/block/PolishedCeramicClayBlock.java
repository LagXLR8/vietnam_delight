package net.huwng.vietnamdelight.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.entity.BlockEntity;

import net.huwng.vietnamdelight.block.entity.PolishedCeramicClayBlockEntity;

public class PolishedCeramicClayBlock extends Block implements EntityBlock {
    public static final BooleanProperty DIRTY = BooleanProperty.create("dirty");

    public PolishedCeramicClayBlock() {
        super(BlockBehaviour.Properties.of()
            .instrument(NoteBlockInstrument.BASEDRUM)
            .sound(SoundType.DECORATED_POT)
            .strength(2f, 6f)
            .requiresCorrectToolForDrops());
        this.registerDefaultState(this.defaultBlockState().setValue(DIRTY, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(DIRTY);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof PolishedCeramicClayBlockEntity blockBE) {
                blockBE.setCurrentColor(0xFFFFFF);
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PolishedCeramicClayBlockEntity(pos, state);
    }
}
