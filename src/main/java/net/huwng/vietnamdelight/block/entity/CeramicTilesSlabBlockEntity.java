package net.huwng.vietnamdelight.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.huwng.vietnamdelight.init.VietnamDelightModBlockEntities;
import net.huwng.vietnamdelight.block.CeramicTilesSlabBlock;

public class CeramicTilesSlabBlockEntity extends ColorLayerBlockEntity implements IColorLayerBE {
	public CeramicTilesSlabBlockEntity(BlockPos pos, BlockState state) {
	    super(VietnamDelightModBlockEntities.CERAMIC_TILES_SLAB.get(), pos, state);
	    this.currentColor = 0xFFFFFF;
	}

    @Override
    public void addColorLayer(int rgb) {
        this.currentColor = rgb;
        setChanged();

        if (level != null && !level.isClientSide) {
            BlockState state = getBlockState();
            if (state.hasProperty(CeramicTilesSlabBlock.DIRTY)) {
                level.setBlock(worldPosition, state.cycle(CeramicTilesSlabBlock.DIRTY), 11);
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
