package net.huwng.vietnamdelight.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.level.block.Block;

/**
 * Item dạng block của Cicadas (ve sầu).
 * Giống FlamboyantFlowerBlockItem: chỉ "ăn" khi click chuột phải vào không khí
 * (không nhắm vào block nào) - lúc đó game gọi Item#use() thay vì BlockItem#useOn().
 * Khi nhắm vào block, hành vi đặt block (gắn lên mặt bên) vẫn hoạt động bình thường.
 */
public class CicadasBlockItem extends BlockItem {
    public CicadasBlockItem(Block block) {
        super(block, new Item.Properties()
            .rarity(Rarity.COMMON)
            .food(new FoodProperties.Builder()
                .nutrition(3)
                .saturationMod(0.33333334f) // 3 nutrition * 0.33333334 * 2 = 2.0 saturation
                .fast()
                .build()));
    }
}
