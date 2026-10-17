package net.huwng.vietnamdelight.item;

import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.Rarity;

public class BirdEyeChilisSeedsItem extends ItemNameBlockItem {

    public BirdEyeChilisSeedsItem() {
        super(
                VietnamDelightModBlocks.CHILIS_CROP.get(),
                new Properties()
                        .stacksTo(64)
                        .rarity(Rarity.COMMON)
        );
    }
}