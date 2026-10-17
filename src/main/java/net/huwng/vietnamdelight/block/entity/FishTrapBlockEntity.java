package net.huwng.vietnamdelight.block.entity;

import net.minecraftforge.items.wrapper.SidedInvWrapper;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.Capability;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.tags.TagKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;

import net.huwng.vietnamdelight.world.inventory.FishTrapGuiMenu;
import net.huwng.vietnamdelight.init.VietnamDelightModBlockEntities;
import net.huwng.vietnamdelight.block.FishTrapBlock;

import javax.annotation.Nullable;

import java.util.stream.IntStream;

import io.netty.buffer.Unpooled;

public class FishTrapBlockEntity extends RandomizableContainerBlockEntity implements WorldlyContainer {
	private NonNullList<ItemStack> stacks = NonNullList.<ItemStack>withSize(10, ItemStack.EMPTY);
	private final LazyOptional<? extends IItemHandler>[] handlers = SidedInvWrapper.create(this, Direction.values());
	private int fishingTime = 0;
	private static final int BASE_FISHING_TIME = 4000;
	
	// Phạm vi tìm kiếm biome (bán kính)
	private static final int BIOME_CHECK_RADIUS = 6; // Tăng từ 2 lên 8 (vùng 17x17)
	
	// Tag cho mồi
	private static final TagKey<net.minecraft.world.item.Item> BAIT_TAG = 
		TagKey.create(Registries.ITEM, new ResourceLocation("vietnam_delight", "bait"));
	
	// Biome River
	private static final ResourceKey<net.minecraft.world.level.biome.Biome> RIVER_BIOME = 
		ResourceKey.create(Registries.BIOME, new ResourceLocation("minecraft", "river"));

	public FishTrapBlockEntity(BlockPos position, BlockState state) {
		super(VietnamDelightModBlockEntities.FISH_TRAP.get(), position, state);
	}

	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
		if (!this.tryLoadLootTable(compound))
			this.stacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(compound, this.stacks);
		this.fishingTime = compound.getInt("FishingTime");
	}

	@Override
	public void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);
		if (!this.trySaveLootTable(compound)) {
			ContainerHelper.saveAllItems(compound, this.stacks);
		}
		compound.putInt("FishingTime", this.fishingTime);
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag() {
		return this.saveWithFullMetadata();
	}

	@Override
	public int getContainerSize() {
		return stacks.size();
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemstack : this.stacks)
			if (!itemstack.isEmpty())
				return false;
		return true;
	}

	@Override
	public Component getDefaultName() {
		return Component.literal("fish_trap");
	}

	@Override
	public int getMaxStackSize() {
		return 64;
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inventory) {
		return new FishTrapGuiMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(this.worldPosition));
	}

	@Override
	public Component getDisplayName() {
		return Component.literal("Fish Trap");
	}

	@Override
	protected NonNullList<ItemStack> getItems() {
		return this.stacks;
	}

	@Override
	protected void setItems(NonNullList<ItemStack> stacks) {
		this.stacks = stacks;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		if (index == 0) {
			return stack.is(BAIT_TAG);
		}
		return false;
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return IntStream.range(0, this.getContainerSize()).toArray();
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction) {
		return this.canPlaceItem(index, stack);
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		return true;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
		if (!this.remove && facing != null && capability == ForgeCapabilities.ITEM_HANDLER)
			return handlers[facing.ordinal()].cast();
		return super.getCapability(capability, facing);
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		for (LazyOptional<? extends IItemHandler> handler : handlers)
			handler.invalidate();
	}

	// Kiểm tra có biome River trong phạm vi được cấu hình
	private boolean isNearRiverBiome(Level level, BlockPos pos) {
		if (level == null)
			return false;

		// Kiểm tra biome tại vị trí hiện tại
		Holder<net.minecraft.world.level.biome.Biome> currentBiome = level.getBiome(pos);
		if (currentBiome.is(RIVER_BIOME)) {
			return true;
		}

		// Kiểm tra vùng xung quanh với bán kính BIOME_CHECK_RADIUS
		// Bán kính 8 = vùng 17x17 blocks
		for (int x = -BIOME_CHECK_RADIUS; x <= BIOME_CHECK_RADIUS; x++) {
			for (int z = -BIOME_CHECK_RADIUS; z <= BIOME_CHECK_RADIUS; z++) {
				BlockPos checkPos = pos.offset(x, 0, z);
				Holder<net.minecraft.world.level.biome.Biome> biome = level.getBiome(checkPos);
				if (biome.is(RIVER_BIOME)) {
					return true;
				}
			}
		}

		return false;
	}

	// Tick logic cho bẫy cá
	public static void serverTick(Level level, BlockPos pos, BlockState state, FishTrapBlockEntity blockEntity) {
		if (level == null || level.isClientSide)
			return;

		// Chỉ bắt cá khi có waterlogged
		if (!state.getValue(FishTrapBlock.WATERLOGGED)) {
			blockEntity.fishingTime = 0;
			return;
		}

		// Kiểm tra có ở biome River hoặc gần biome River
		if (!blockEntity.isNearRiverBiome(level, pos)) {
			blockEntity.fishingTime = 0;
			return;
		}

		// Kiểm tra có chỗ trống để chứa cá không (slot 1-9)
		boolean hasSpace = false;
		for (int i = 1; i < blockEntity.getContainerSize(); i++) {
			ItemStack stack = blockEntity.getItem(i);
			if (stack.isEmpty() || stack.getCount() < stack.getMaxStackSize()) {
				hasSpace = true;
				break;
			}
		}

		if (!hasSpace) {
			blockEntity.fishingTime = 0;
			return;
		}

		// Tăng thời gian đánh cá
		blockEntity.fishingTime++;

		// Kiểm tra có mồi không (slot 0)
		ItemStack bait = blockEntity.getItem(0);
		boolean hasBait = !bait.isEmpty();

		// Tính thời gian cần để bắt cá
		// Có mồi: 1 phút (2000 ticks)
		// Không mồi: 2 phút (4000 ticks)
		int requiredTime = hasBait ? BASE_FISHING_TIME / 2 : BASE_FISHING_TIME;

		// Khi đủ thời gian, bắt cá
		if (blockEntity.fishingTime >= requiredTime) {
			blockEntity.fishingTime = 0;

			// Tiêu hao mồi
			if (hasBait) {
				bait.shrink(1);
				blockEntity.setItem(0, bait);
			}

			// Thêm cá vào slot trống
			ItemStack fish = blockEntity.getFishLoot();
			if (!fish.isEmpty()) {
				blockEntity.addFishToInventory(fish);
			}

			blockEntity.setChanged();
		}
	}

	// Tag cho cá sống
	private static final TagKey<net.minecraft.world.item.Item> RAW_FISHES_TAG = 
		TagKey.create(Registries.ITEM, new ResourceLocation("forge", "raw_fishes"));

	// Lấy cá ngẫu nhiên
	private ItemStack getFishLoot() {
		if (level == null)
			return ItemStack.EMPTY;

		// Lấy tất cả items trong tag forge:raw_fishes
		java.util.List<net.minecraft.world.item.Item> rawFishes = new java.util.ArrayList<>();
		for (Holder<net.minecraft.world.item.Item> holder : net.minecraft.core.registries.BuiltInRegistries.ITEM.getTagOrEmpty(RAW_FISHES_TAG)) {
			rawFishes.add(holder.value());
		}

		// Nếu không có cá nào trong tag, không trả về gì
		if (rawFishes.isEmpty()) {
			return ItemStack.EMPTY;
		}

		// Random cá từ tag
		int index = level.random.nextInt(rawFishes.size());
		return new ItemStack(rawFishes.get(index));
	}

	// Thêm cá vào inventory
	private void addFishToInventory(ItemStack fish) {
		// Tìm slot trống hoặc slot có cùng loại cá (slot 1-9)
		for (int i = 1; i < getContainerSize(); i++) {
			ItemStack stack = getItem(i);
			
			if (stack.isEmpty()) {
				setItem(i, fish.copy());
				return;
			} else if (ItemStack.isSameItemSameTags(stack, fish) && stack.getCount() < stack.getMaxStackSize()) {
				stack.grow(1);
				setItem(i, stack);
				return;
			}
		}
	}
}