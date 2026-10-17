package net.huwng.vietnamdelight.item;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;

public class NipaNutItem extends Item {
    public NipaNutItem() {
        super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockPos placePos = pos.above();
        if (level.getBlockState(pos).is(Blocks.MUD)) {
            if (level.getBlockState(placePos).canBeReplaced()) {
                if (!level.isClientSide) {
                    level.setBlock(placePos, VietnamDelightModBlocks.NIPA_PROPAGULE.get().defaultBlockState(), 3);
                    level.playSound(null, placePos, SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 1.0F, 1.0F);
                    context.getItemInHand().shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.FAIL;
    }
}
