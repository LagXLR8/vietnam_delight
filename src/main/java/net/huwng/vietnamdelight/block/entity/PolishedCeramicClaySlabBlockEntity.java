package net.huwng.vietnamdelight.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.huwng.vietnamdelight.init.VietnamDelightModBlockEntities;
import net.huwng.vietnamdelight.block.PolishedCeramicClaySlabBlock;

public class PolishedCeramicClaySlabBlockEntity extends ColorLayerBlockEntity implements IColorLayerBE {
    public PolishedCeramicClaySlabBlockEntity(BlockPos pos, BlockState state) {
        super(VietnamDelightModBlockEntities.POLISHED_CERAMIC_CLAY_SLAB.get(), pos, state);
    }

    @Override
    public void addColorLayer(int rgb) {
        this.currentColor = rgb;
        setChanged();

        if (level != null && !level.isClientSide) {
            BlockState state = getBlockState();
            if (state.hasProperty(PolishedCeramicClaySlabBlock.DIRTY)) {
                level.setBlock(worldPosition, state.cycle(PolishedCeramicClaySlabBlock.DIRTY), 11);
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
