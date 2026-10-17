package net.huwng.vietnamdelight.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.huwng.vietnamdelight.init.VietnamDelightModBlockEntities;
import net.huwng.vietnamdelight.block.CeramicClayStairsBlock;

public class CeramicClayStairsBlockEntity extends ColorLayerBlockEntity implements IColorLayerBE {
	public CeramicClayStairsBlockEntity(BlockPos pos, BlockState state) {
	    super(VietnamDelightModBlockEntities.CERAMIC_CLAY_STAIRS.get(), pos, state);
	    this.currentColor = 0xFFFFFF;
	}

    @Override
    public void addColorLayer(int rgb) {
        this.currentColor = rgb;
        setChanged();

        if (level != null && !level.isClientSide) {
            BlockState state = getBlockState();
            if (state.hasProperty(CeramicClayStairsBlock.DIRTY)) {
                level.setBlock(worldPosition, state.cycle(CeramicClayStairsBlock.DIRTY), 11);
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
