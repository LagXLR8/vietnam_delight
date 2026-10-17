package net.huwng.vietnamdelight.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import vectorwing.farmersdelight.common.registry.ModEffects;

public class PlainStickyRiceItem extends Item {
	public PlainStickyRiceItem() {
		super(new Item.Properties()
			.stacksTo(16)
			.rarity(Rarity.COMMON)
			.food(new FoodProperties.Builder()
				.nutrition(8)
				.saturationMod(0.375f)
				.effect(() -> new MobEffectInstance(ModEffects.COMFORT.get(), 1000, 0, false, false), 1.0f)
				.build()));
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack retval = new ItemStack(Items.BOWL);
		super.finishUsingItem(itemstack, world, entity);
		if (itemstack.isEmpty()) {
			return retval;
		} else {
			if (entity instanceof Player player && !player.getAbilities().instabuild) {
				if (!player.getInventory().add(retval))
					player.drop(retval, false);
			}
			return itemstack;
		}
	}
}
