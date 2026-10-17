package net.huwng.vietnamdelight.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import io.netty.buffer.Unpooled;

import net.huwng.vietnamdelight.init.VietnamDelightModBlockEntities;
import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;
import net.huwng.vietnamdelight.world.inventory.CeramicClayPotGuiMenu;
import net.huwng.vietnamdelight.CeramicClayPotRecipe;
import net.huwng.vietnamdelight.block.CeramicClayPotBlock;

import javax.annotation.Nullable;

public class CeramicClayPotBlockEntity extends BlockEntity implements IColorLayerBE, MenuProvider {

    private final ItemStackHandler items = new ItemStackHandler(7) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    };

    private final LazyOptional<IItemHandler> itemHandler = LazyOptional.of(() -> items);

    private int currentColor = 0xFFFFFF;
    private int cookTime = 0;
    private int cookTimeTotal = 0;

    public CeramicClayPotBlockEntity(BlockPos pos, BlockState state) {
        super(VietnamDelightModBlockEntities.CERAMIC_CLAY_POT.get(), pos, state);
    }

    public ItemStackHandler getItems() {
        return items;
    }

    public ItemStack getStoredItem() {
        return items.getStackInSlot(0);
    }

    @Override
    public void addColorLayer(int rgb) {
        this.currentColor = rgb;
        setChanged();

        if (level != null && !level.isClientSide) {
            BlockState state = getBlockState();
            if (state.hasProperty(CeramicClayPotBlock.DIRTY)) {
                level.setBlock(worldPosition, state.cycle(CeramicClayPotBlock.DIRTY), 11);
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
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable net.minecraft.core.Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", items.serializeNBT());
        tag.putInt("CurrentColor", currentColor);
        tag.putInt("CookTime", cookTime);
        tag.putInt("CookTimeTotal", cookTimeTotal);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Inventory")) {
            items.deserializeNBT(tag.getCompound("Inventory"));
        }
        if (tag.contains("CurrentColor")) {
            currentColor = tag.getInt("CurrentColor");
        }
        if (tag.contains("CookTime")) {
            cookTime = tag.getInt("CookTime");
        }
        if (tag.contains("CookTimeTotal")) {
            cookTimeTotal = tag.getInt("CookTimeTotal");
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.put("Inventory", items.serializeNBT());
        tag.putInt("CurrentColor", currentColor);
        tag.putInt("CookTime", cookTime);
        tag.putInt("CookTimeTotal", cookTimeTotal);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        if (tag.contains("Inventory")) {
            items.deserializeNBT(tag.getCompound("Inventory"));
        }
        if (tag.contains("CurrentColor")) {
            currentColor = tag.getInt("CurrentColor");
        }
        if (tag.contains("CookTime")) {
            cookTime = tag.getInt("CookTime");
        }
        if (tag.contains("CookTimeTotal")) {
            cookTimeTotal = tag.getInt("CookTimeTotal");
        }
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            handleUpdateTag(tag);
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Ceramic Clay Pot");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player) {
        return new CeramicClayPotGuiMenu(
                id, playerInv,
                new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(this.worldPosition)
        );
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CeramicClayPotBlockEntity be) {
        if (level.isClientSide) return;
        if (level.getBlockState(pos.above()).getBlock() != VietnamDelightModBlocks.CERAMIC_CLAY_LID.get()) {
            be.cookTime = 0;
            return;
        }
        SimpleContainer container = new SimpleContainer(6);
        for (int i = 0; i < 6; i++) {
            container.setItem(i, be.getItems().getStackInSlot(i));
        }

        level.getRecipeManager().getRecipeFor(CeramicClayPotRecipe.Type.INSTANCE, container, level)
            .ifPresentOrElse(recipe -> {
                be.cookTimeTotal = recipe.getCookingTime();
                be.cookTime++;

                if (be.cookTime >= be.cookTimeTotal) {
                    ItemStack result = recipe.getResultItem(level.registryAccess()).copy();
                    ItemStack output = be.getItems().getStackInSlot(6);
                    int maxSize = Math.min(result.getMaxStackSize(), be.getItems().getSlotLimit(6));
                    boolean canInsert;
                    if (output.isEmpty()) {
                        canInsert = result.getCount() <= maxSize;
                    } else if (ItemStack.isSameItemSameTags(output, result)) {
                        canInsert = output.getCount() + result.getCount() <= maxSize;
                    } else {
                        canInsert = false;
                    }

                    if (canInsert) {
                        if (output.isEmpty()) {
                            be.getItems().setStackInSlot(6, result);
                        } else {
                            ItemStack newOut = output.copy();
                            newOut.grow(result.getCount());
                            be.getItems().setStackInSlot(6, newOut);
                        }
                        for (int i = 0; i < 6; i++) {
                            ItemStack slot = be.getItems().getStackInSlot(i);
                            if (!slot.isEmpty()) {
                                ItemStack remaining = slot.copy();
                                remaining.shrink(1);
                                if (remaining.isEmpty()) {
                                    be.getItems().setStackInSlot(i, ItemStack.EMPTY);
                                } else {
                                    be.getItems().setStackInSlot(i, remaining);
                                }
                            }
                        }
                        be.cookTime = 0;
                        be.setChanged();
                        level.sendBlockUpdated(pos, state, state, 3);
                    } else {
                        be.cookTime = be.cookTimeTotal;
                    }
                }
            }, () -> {
                be.cookTime = 0;
            });
    }
}