package net.huwng.vietnamdelight.item;

import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.Rarity;

/**
 * Planting item for the associated crop block.
 * Extends ItemNameBlockItem — vanilla standard for seed items (wheat, beetroot, etc).
 * Right-click on farmland or Rich Soil farmland places the crop at age 0.
 * No manual useOn() needed.
 */
public class LimeSeedsItem extends ItemNameBlockItem {

    public LimeSeedsItem() {
        super(
            VietnamDelightModBlocks.LIME_CROP.get(),
            new Properties().stacksTo(64).rarity(Rarity.COMMON)
        );
    }
}
