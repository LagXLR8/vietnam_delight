package net.huwng.vietnamdelight.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.huwng.vietnamdelight.block.entity.PolishedCeramicClayStairsBlockEntity;

public class PolishedCeramicClayStairsBlock extends StairBlock implements EntityBlock {
    public static final BooleanProperty DIRTY = BooleanProperty.create("dirty");

    public PolishedCeramicClayStairsBlock() {
        super(() -> net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(),
            BlockBehaviour.Properties.of()
                .instrument(NoteBlockInstrument.BASEDRUM)
                .sound(SoundType.DECORATED_POT)
                .strength(2f, 6f)
                .requiresCorrectToolForDrops()
                .dynamicShape());
        this.registerDefaultState(this.defaultBlockState().setValue(DIRTY, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(DIRTY);
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 0;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter world, BlockPos pos) {
        return getShape(state, world, pos, net.minecraft.world.phys.shapes.CollisionContext.empty());
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof PolishedCeramicClayStairsBlockEntity stairsBE) {
                stairsBE.setCurrentColor(0xFFFFFF);
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PolishedCeramicClayStairsBlockEntity(pos, state);
    }
}
