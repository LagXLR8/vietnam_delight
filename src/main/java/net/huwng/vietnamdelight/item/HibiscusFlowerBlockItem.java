package net.huwng.vietnamdelight.item;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;

public class HibiscusFlowerBlockItem extends BlockItem {
    public HibiscusFlowerBlockItem(Block block) {
        super(block, new Item.Properties()
                .rarity(Rarity.COMMON)
                .food(new FoodProperties.Builder()
                        .nutrition(1)
                        .saturationMod(0.1f)
                        .fast()
                        .alwaysEat()
                        .build()));
    }
}
