package net.huwng.vietnamdelight.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.level.block.Block;

/**
 * Item dạng block của Flamboyant Flower.
 * Vì đây là block nên chỉ có thể "ăn" khi người chơi click chuột phải vào không khí
 * (không nhắm vào block nào) - lúc đó game sẽ gọi Item#use() (ăn) thay vì
 * BlockItem#useOn() (đặt block). Khi nhắm vào block, hành vi đặt block vẫn hoạt động bình thường.
 */
public class FlamboyantFlowerBlockItem extends BlockItem {
    public FlamboyantFlowerBlockItem(Block block) {
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
