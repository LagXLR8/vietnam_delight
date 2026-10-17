package net.huwng.vietnamdelight.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.huwng.vietnamdelight.init.VietnamDelightModBlockEntities;
import net.huwng.vietnamdelight.block.CeramicClayBlockBlock;

public class CeramicClayBlockBlockEntity extends ColorLayerBlockEntity implements IColorLayerBE {
    public CeramicClayBlockBlockEntity(BlockPos pos, BlockState state) {
        super(VietnamDelightModBlockEntities.CERAMIC_CLAY_BLOCK.get(), pos, state);
    }

    @Override
    public void addColorLayer(int rgb) {
        this.currentColor = rgb;
        setChanged();

        if (level != null && !level.isClientSide) {
            // Toggle property để ép rebuild chunk ngay
            BlockState state = getBlockState();
            if (state.hasProperty(CeramicClayBlockBlock.DIRTY)) {
                level.setBlock(worldPosition, state.cycle(CeramicClayBlockBlock.DIRTY), 11); // 11 = UPDATE + NOTIFY + CLIENTS
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
