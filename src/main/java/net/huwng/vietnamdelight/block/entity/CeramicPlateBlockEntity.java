package net.huwng.vietnamdelight.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.huwng.vietnamdelight.init.VietnamDelightModBlockEntities;
import net.huwng.vietnamdelight.block.CeramicPlateBlock;

public class CeramicPlateBlockEntity extends ColorLayerBlockEntity implements IColorLayerBE {
    private ItemStack storedItem = ItemStack.EMPTY;
    private float itemRotation = 0.0F;

    public CeramicPlateBlockEntity(BlockPos pos, BlockState state) {
        super(VietnamDelightModBlockEntities.CERAMIC_PLATE.get(), pos, state);
        this.currentColor = 0xFFFFFF;
    }

    @Override
    public void addColorLayer(int rgb) {
        this.currentColor = rgb;
        setChanged();

        if (level != null && !level.isClientSide) {
            BlockState state = getBlockState();
            if (state.hasProperty(CeramicPlateBlock.DIRTY)) {
                level.setBlock(worldPosition, state.cycle(CeramicPlateBlock.DIRTY), 11);
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

    public ItemStack getStoredItem() {
        return storedItem;
    }

    public float getItemRotation() {
        return itemRotation;
    }

    public void setStoredItem(ItemStack stack, float rotation) {
        this.storedItem = stack;
        this.itemRotation = rotation;
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public void setStoredItem(ItemStack stack) {
        setStoredItem(stack, this.itemRotation);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (!storedItem.isEmpty()) {
            tag.put("StoredItem", storedItem.save(new CompoundTag()));
        }
        tag.putFloat("ItemRotation", itemRotation);
        tag.putInt("CurrentColor", currentColor);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("StoredItem")) {
            storedItem = ItemStack.of(tag.getCompound("StoredItem"));
        } else {
            storedItem = ItemStack.EMPTY;
        }
        if (tag.contains("ItemRotation")) {
            itemRotation = tag.getFloat("ItemRotation");
        }
        if (tag.contains("CurrentColor")) {
            currentColor = tag.getInt("CurrentColor");
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        if (!storedItem.isEmpty()) {
            tag.put("StoredItem", storedItem.save(new CompoundTag()));
        }
        tag.putFloat("ItemRotation", itemRotation);
        tag.putInt("CurrentColor", currentColor);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        if (tag.contains("StoredItem")) {
            storedItem = ItemStack.of(tag.getCompound("StoredItem"));
        } else {
            storedItem = ItemStack.EMPTY;
        }
        if (tag.contains("ItemRotation")) {
            itemRotation = tag.getFloat("ItemRotation");
        }
        if (tag.contains("CurrentColor")) {
            currentColor = tag.getInt("CurrentColor");
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
