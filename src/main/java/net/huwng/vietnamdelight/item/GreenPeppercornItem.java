package net.huwng.vietnamdelight.item;

import net.huwng.vietnamdelight.block.GreenPeppercornCropBlock;
import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class GreenPeppercornItem extends Item {
    public GreenPeppercornItem() {
        super(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.COMMON)
            .food(new FoodProperties.Builder().nutrition(1).saturationMod(0.1f).alwaysEat().build()));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos basePos = context.getClickedPos();
        BlockPos placePos = basePos.relative(context.getClickedFace());

        if (world.getBlockState(basePos).is(Blocks.FARMLAND) && world.isEmptyBlock(placePos)) {
            world.setBlock(placePos, VietnamDelightModBlocks.GREEN_PEPPERCORN_CROP.get().defaultBlockState()
    				.setValue(GreenPeppercornCropBlock.BLOCKSTATE, 0), 3);

            world.playSound(null, placePos, SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (!context.getPlayer().isCreative()) {
                context.getItemInHand().shrink(1);
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }
}
