package net.huwng.vietnamdelight.item;

import net.huwng.vietnamdelight.block.LotusStemBlock;
import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class LotusSeedsItem extends Item {
    public LotusSeedsItem() {
        super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos mudPos = context.getClickedPos();
        BlockPos above = mudPos.above();

        // Kiểm tra mud và phía trên là nước
        if (world.getBlockState(mudPos).is(Blocks.MUD) && world.getBlockState(above).is(Blocks.WATER)) {
            world.setBlock(above, VietnamDelightModBlocks.LOTUS_STEM.get().defaultBlockState()
    		.setValue(LotusStemBlock.BLOCKSTATE, 0)
    		.setValue(BlockStateProperties.WATERLOGGED, true), 3);

            world.playSound(null, above, net.minecraft.sounds.SoundEvents.CROP_PLANTED,
                net.minecraft.sounds.SoundSource.BLOCKS, 1.0f, 1.0f);

            if (!context.getPlayer().isCreative()) {
                context.getItemInHand().shrink(1);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }
}
