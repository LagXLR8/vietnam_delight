package net.huwng.vietnamdelight.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.huwng.vietnamdelight.init.VietnamDelightModBlockEntities;
import net.huwng.vietnamdelight.block.PolishedCeramicClayWallBlock;

public class PolishedCeramicClayWallBlockEntity extends ColorLayerBlockEntity implements IColorLayerBE {
    public PolishedCeramicClayWallBlockEntity(BlockPos pos, BlockState state) {
        super(VietnamDelightModBlockEntities.POLISHED_CERAMIC_CLAY_WALL.get(), pos, state);
    }

    @Override
    public void addColorLayer(int rgb) {
        this.currentColor = rgb;
        setChanged();

        if (level != null && !level.isClientSide) {
            BlockState state = getBlockState();
            if (state.hasProperty(PolishedCeramicClayWallBlock.DIRTY)) {
                level.setBlock(worldPosition, state.cycle(PolishedCeramicClayWallBlock.DIRTY), 11);
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
