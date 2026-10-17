package net.huwng.vietnamdelight.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.level.block.Block;

/**
 * Item dạng block của Golden Flamboyant Flower.
 * Vì đây là block nên chỉ có thể "ăn" khi người chơi click chuột phải vào không khí
 * (không nhắm vào block nào) - lúc đó game sẽ gọi Item#use() (ăn) thay vì
 * BlockItem#useOn() (đặt block). Khi nhắm vào block, hành vi đặt block vẫn hoạt động bình thường.
 *
 * Hồi 2 độ đói (1 icon), buff Regeneration I trong 10 giây và Fire Resistance trong 1 phút.
 * Ăn được ngay cả khi thanh đói đã đầy.
 */
public class GoldenFlamboyantFlowerBlockItem extends BlockItem {
    public GoldenFlamboyantFlowerBlockItem(Block block) {
        super(block, new Item.Properties()
            .rarity(Rarity.COMMON)
            .food(new FoodProperties.Builder()
                .nutrition(2)
                .saturationMod(0.1f)
                .alwaysEat()
                .fast()
                .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 200, 0, false, true), 1.0f)
                .effect(() -> new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 1200, 0, false, true), 1.0f)
                .build()));
    }
}
