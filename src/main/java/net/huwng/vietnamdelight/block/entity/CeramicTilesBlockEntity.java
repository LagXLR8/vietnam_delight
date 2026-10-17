package net.huwng.vietnamdelight.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.huwng.vietnamdelight.init.VietnamDelightModBlockEntities;
import net.huwng.vietnamdelight.block.CeramicTilesBlock;

public class CeramicTilesBlockEntity extends ColorLayerBlockEntity implements IColorLayerBE {
    public CeramicTilesBlockEntity(BlockPos pos, BlockState state) {
        super(VietnamDelightModBlockEntities.CERAMIC_TILES.get(), pos, state);
    }

    @Override
    public void addColorLayer(int rgb) {
        this.currentColor = rgb;
        setChanged();

        if (level != null && !level.isClientSide) {
            BlockState state = getBlockState();
            if (state.hasProperty(CeramicTilesBlock.DIRTY)) {
                level.setBlock(worldPosition, state.cycle(CeramicTilesBlock.DIRTY), 11);
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
