package net.huwng.vietnamdelight.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.huwng.vietnamdelight.init.VietnamDelightModBlockEntities;
import net.huwng.vietnamdelight.block.CeramicTilesWallBlock;

public class CeramicTilesWallBlockEntity extends ColorLayerBlockEntity implements IColorLayerBE {
    public CeramicTilesWallBlockEntity(BlockPos pos, BlockState state) {
        super(VietnamDelightModBlockEntities.CERAMIC_TILES_WALL.get(), pos, state);
    }

    @Override
    public void addColorLayer(int rgb) {
        this.currentColor = rgb;
        setChanged();

        if (level != null && !level.isClientSide) {
            BlockState state = getBlockState();
            if (state.hasProperty(CeramicTilesWallBlock.DIRTY)) {
                level.setBlock(worldPosition, state.cycle(CeramicTilesWallBlock.DIRTY), 11);
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
