
package net.huwng.vietnamdelight.item;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;

import java.util.List;

public class WarningItem extends Item {
	public WarningItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
	}

	@Override
	public float getDestroySpeed(ItemStack par1ItemStack, BlockState par2Block) {
		return 0f;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.vietnam_delight.warning.description_0"));
		list.add(Component.translatable("item.vietnam_delight.warning.description_1"));
		list.add(Component.translatable("item.vietnam_delight.warning.description_2"));
		list.add(Component.translatable("item.vietnam_delight.warning.description_3"));
	}
}
