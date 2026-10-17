package net.huwng.vietnamdelight.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.huwng.vietnamdelight.init.VietnamDelightModBlockEntities;
import net.huwng.vietnamdelight.block.PolishedCeramicClayBlock;

public class PolishedCeramicClayBlockEntity extends ColorLayerBlockEntity implements IColorLayerBE {
    public PolishedCeramicClayBlockEntity(BlockPos pos, BlockState state) {
        super(VietnamDelightModBlockEntities.POLISHED_CERAMIC_CLAY.get(), pos, state);
    }

    @Override
    public void addColorLayer(int rgb) {
        this.currentColor = rgb;
        setChanged();

        if (level != null && !level.isClientSide) {
            BlockState state = getBlockState();
            if (state.hasProperty(PolishedCeramicClayBlock.DIRTY)) {
                level.setBlock(worldPosition, state.cycle(PolishedCeramicClayBlock.DIRTY), 11);
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
