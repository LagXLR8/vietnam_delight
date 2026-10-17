package net.huwng.vietnamdelight.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.ChatFormatting;

import java.util.List;

public class UnbakedMooncakeItem extends Item {

    public UnbakedMooncakeItem() {
        super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON));
    }
    
    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        
        String type = getMooncakeType(stack);
		if (type != null && !type.equals("plain")) {
		    tooltip.add(
		        Component.translatable("tooltip.vietnam_delight.type." + type)
		            .withStyle(ChatFormatting.GOLD, ChatFormatting.ITALIC)
		    );
		}
    }
    
    public static String getMooncakeType(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("mooncake_type")) {
            return tag.getString("mooncake_type");
        }
        return "plain";
    }
    
    public static ItemStack setMooncakeType(ItemStack stack, String type) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString("mooncake_type", type);
        return stack;
    }
    
    public static ItemStack createTyped(Item item, String type) {
        ItemStack stack = new ItemStack(item);
        return setMooncakeType(stack, type);
    }
}
