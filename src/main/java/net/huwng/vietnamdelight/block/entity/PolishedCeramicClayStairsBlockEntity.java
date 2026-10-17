package net.huwng.vietnamdelight.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.huwng.vietnamdelight.init.VietnamDelightModBlockEntities;
import net.huwng.vietnamdelight.block.PolishedCeramicClayStairsBlock;

public class PolishedCeramicClayStairsBlockEntity extends ColorLayerBlockEntity implements IColorLayerBE {
    public PolishedCeramicClayStairsBlockEntity(BlockPos pos, BlockState state) {
        super(VietnamDelightModBlockEntities.POLISHED_CERAMIC_CLAY_STAIRS.get(), pos, state);
    }

    @Override
    public void addColorLayer(int rgb) {
        this.currentColor = rgb;
        setChanged();

        if (level != null && !level.isClientSide) {
            BlockState state = getBlockState();
            if (state.hasProperty(PolishedCeramicClayStairsBlock.DIRTY)) {
                level.setBlock(worldPosition, state.cycle(PolishedCeramicClayStairsBlock.DIRTY), 11);
            } else {
                level.sendBlockUpdated(worldPosition, state, state, 11);
            }
        }
    }

    @Override
    public int getCurrentColor() {
        return currentColor;
    }
}
