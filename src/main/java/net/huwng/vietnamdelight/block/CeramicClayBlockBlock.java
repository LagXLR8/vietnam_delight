package net.huwng.vietnamdelight.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;

import net.huwng.vietnamdelight.block.entity.CeramicClayBlockBlockEntity;

import javax.annotation.Nullable;

public class CeramicClayBlockBlock extends Block implements EntityBlock {
    public static final BooleanProperty DIRTY = BooleanProperty.create("dirty");

    public CeramicClayBlockBlock() {
        super(BlockBehaviour.Properties.of()
                .strength(1.5f, 6f)
                .sound(SoundType.DECORATED_POT));
        this.registerDefaultState(this.defaultBlockState().setValue(DIRTY, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DIRTY);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CeramicClayBlockBlockEntity(pos, state);
    }
}
