package net.huwng.vietnamdelight.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.huwng.vietnamdelight.init.VietnamDelightModBlockEntities;
import net.huwng.vietnamdelight.block.CeramicTilesStairsBlock;

public class CeramicTilesStairsBlockEntity extends ColorLayerBlockEntity implements IColorLayerBE {
	public CeramicTilesStairsBlockEntity(BlockPos pos, BlockState state) {
	    super(VietnamDelightModBlockEntities.CERAMIC_TILES_STAIRS.get(), pos, state);
	    this.currentColor = 0xFFFFFF;
	}

    @Override
    public void addColorLayer(int rgb) {
        this.currentColor = rgb;
        setChanged();

        if (level != null && !level.isClientSide) {
            BlockState state = getBlockState();
            if (state.hasProperty(CeramicTilesStairsBlock.DIRTY)) {
                level.setBlock(worldPosition, state.cycle(CeramicTilesStairsBlock.DIRTY), 11);
            } else {
                level.sendBlockUpdated(worldPosition, state, state, 11);
            }
        }
    }
	@Override
	public int getCurrentColor() {
	    return currentColor;
	}
	
	public void setCurrentColor(int rgb) {
	    this.currentColor = rgb;
	    setChanged();
	
	    if (level != null && !level.isClientSide) {
	        BlockState state = getBlockState();
	        level.sendBlockUpdated(worldPosition, state, state, 11);
	    }
	}
}