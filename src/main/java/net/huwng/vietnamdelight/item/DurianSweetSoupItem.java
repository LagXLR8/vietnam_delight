package net.huwng.vietnamdelight.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class DurianSweetSoupItem extends Item {
    public DurianSweetSoupItem() {
        super(new Item.Properties()
                .stacksTo(16)
                .rarity(Rarity.COMMON)
                .food(new FoodProperties.Builder()
                        .nutrition(6)
                        .saturationMod(0.5833333F)
                        .alwaysEat()
                        .build()));
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemstack) {
        return UseAnim.DRINK;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
        if (!world.isClientSide) {
            // Xóa toàn bộ hiệu ứng giống như uống sữa
            entity.removeAllEffects();
        }

        super.finishUsingItem(itemstack, world, entity);

        ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
        if (itemstack.isEmpty()) {
            return bottle;
        } else {
            if (entity instanceof Player player && !player.getAbilities().instabuild) {
                if (!player.getInventory().add(bottle)) {
                    player.drop(bottle, false);
                }
            }
            return itemstack;
        }
    }
}
