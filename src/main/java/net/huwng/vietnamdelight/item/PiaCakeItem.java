package net.huwng.vietnamdelight.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;

public class PiaCakeItem extends Item {
	public PiaCakeItem() {
		super(new Item.Properties()
				.stacksTo(64)
				.rarity(Rarity.COMMON)
				.food((new FoodProperties.Builder())
						.nutrition(8)
						.saturationMod(0.5f)
						.fast()
						.build()
				)
		);
	}

	@Override
	public int getUseDuration(ItemStack itemstack) {
		return 16;
	}
}
