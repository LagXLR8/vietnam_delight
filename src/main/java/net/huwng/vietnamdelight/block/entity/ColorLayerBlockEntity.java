package net.huwng.vietnamdelight.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntityType;

public abstract class ColorLayerBlockEntity extends BlockEntity {
    protected int currentColor = 0xFFFFFF;

    public ColorLayerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public int getCurrentColor() {
        return currentColor;
    }

    public void setCurrentColor(int rgb) {
        this.currentColor = rgb;
        setChanged();
        syncToClient();
    }

    /** Ghi màu mới (server) + ép đồng bộ */
    public void addColorLayer(int rgb) {
        setCurrentColor(rgb);
    }

    /** Đồng bộ lên client - GỌI TỪ SERVER SIDE */
    private void syncToClient() {
        if (level != null && !level.isClientSide) {
            // Bước 1: Đánh dấu chunk cần lưu
            setChanged();
            
            // Bước 2: Gửi packet update
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, 3);
            
            // Bước 3: Force re-render (quan trọng!)
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.getChunkSource().blockChanged(worldPosition);
            }
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

    // ---- sync packet (Forge 1.20.1) ----
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            load(tag);
            // QUAN TRỌNG: Trigger re-render trên client
            if (level != null && level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                // Request model data update nếu dùng custom model
                requestModelDataUpdate();
                // THÊM DÒNG NÀY nếu dùng BlockEntityRenderer custom
                level.getModelDataManager().requestRefresh(this);
            }
        }
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag);
        // QUAN TRỌNG: Trigger re-render khi load từ chunk
        if (level != null && level.isClientSide) {
            requestModelDataUpdate();
            // THÊM DÒNG NÀY nếu dùng BlockEntityRenderer custom
            level.getModelDataManager().requestRefresh(this);
        }
    }
}