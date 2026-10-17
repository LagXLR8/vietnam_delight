
package net.huwng.vietnamdelight.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;

public class CeramicClayItem extends Item {
	public CeramicClayItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON));
	}
}
