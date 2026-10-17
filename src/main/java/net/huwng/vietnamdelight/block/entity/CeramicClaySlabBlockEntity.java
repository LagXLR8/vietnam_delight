package net.huwng.vietnamdelight.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.huwng.vietnamdelight.init.VietnamDelightModBlockEntities;
import net.huwng.vietnamdelight.block.CeramicClaySlabBlock;

public class CeramicClaySlabBlockEntity extends ColorLayerBlockEntity implements IColorLayerBE {
	public CeramicClaySlabBlockEntity(BlockPos pos, BlockState state) {
	    super(VietnamDelightModBlockEntities.CERAMIC_CLAY_SLAB.get(), pos, state);
	    this.currentColor = 0xFFFFFF;
	}

    @Override
    public void addColorLayer(int rgb) {
        this.currentColor = rgb;
        setChanged();

        if (level != null && !level.isClientSide) {
            BlockState state = getBlockState();
            if (state.hasProperty(CeramicClaySlabBlock.DIRTY)) {
                level.setBlock(worldPosition, state.cycle(CeramicClaySlabBlock.DIRTY), 11);
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
