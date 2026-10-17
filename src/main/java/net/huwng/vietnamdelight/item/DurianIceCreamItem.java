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

public class DurianIceCreamItem extends Item {
    public DurianIceCreamItem() {
        super(new Item.Properties()
                .stacksTo(16)
                .rarity(Rarity.COMMON)
                .food(new FoodProperties.Builder()
                        .nutrition(5)
                        .saturationMod(0.7F)
                        .alwaysEat()
                        .build()));
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemstack) {
        return UseAnim.EAT;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
        if (!world.isClientSide) {
            // Xóa toàn bộ hiệu ứng giống như uống sữa
            entity.removeAllEffects();
        }

        super.finishUsingItem(itemstack, world, entity);

        ItemStack bowl = new ItemStack(Items.BOWL);
        if (itemstack.isEmpty()) {
            return bowl;
        } else {
            if (entity instanceof Player player && !player.getAbilities().instabuild) {
                if (!player.getInventory().add(bowl)) {
                    player.drop(bowl, false);
                }
            }
            return itemstack;
        }
    }
}
