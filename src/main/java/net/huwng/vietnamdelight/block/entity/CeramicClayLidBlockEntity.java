package net.huwng.vietnamdelight.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.state.BlockState;

import net.huwng.vietnamdelight.block.CeramicClayLidBlock;
import net.huwng.vietnamdelight.init.VietnamDelightModBlockEntities;

public class CeramicClayLidBlockEntity extends ColorLayerBlockEntity implements IColorLayerBE {
    private int currentColor = 0xFFFFFF;

    public CeramicClayLidBlockEntity(BlockPos pos, BlockState state) {
        super(VietnamDelightModBlockEntities.CERAMIC_CLAY_LID.get(), pos, state);
    }

    @Override
    public void addColorLayer(int rgb) {
        this.currentColor = rgb;
        setChanged();

        if (level != null && !level.isClientSide) {
            BlockState state = getBlockState();
            if (state.hasProperty(CeramicClayLidBlock.DIRTY)) {
                level.setBlock(worldPosition, state.cycle(CeramicClayLidBlock.DIRTY), 11);
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

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("CurrentColor", currentColor);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("CurrentColor")) {
            currentColor = tag.getInt("CurrentColor");
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putInt("CurrentColor", currentColor);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        if (tag.contains("CurrentColor")) {
            currentColor = tag.getInt("CurrentColor");
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
