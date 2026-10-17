package net.huwng.vietnamdelight.item;

import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.Rarity;

public class ScallionItem extends ItemNameBlockItem {

    public ScallionItem() {
        super(
            VietnamDelightModBlocks.SCALLION_CROP.get(),
            new Properties()
                .stacksTo(64)
                .rarity(Rarity.COMMON)
                .food(new FoodProperties.Builder()
                    .nutrition(1)
                    .saturationMod(0.1f)
                    .alwaysEat()
                    .build())
        );
    }
}
