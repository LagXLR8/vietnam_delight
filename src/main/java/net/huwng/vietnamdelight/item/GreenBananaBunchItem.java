package net.huwng.vietnamdelight.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;
import net.huwng.vietnamdelight.init.VietnamDelightModItems;
import net.huwng.vietnamdelight.init.VietnamDelightModSounds;

public class GreenBananaBunchItem extends Item {
    public GreenBananaBunchItem() {
        super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON));
    }

    @Override
public InteractionResult useOn(UseOnContext context) {
    Level level = context.getLevel();
    Player player = context.getPlayer();
    BlockPos clickedPos = context.getClickedPos();
    ItemStack stack = context.getItemInHand();
    RandomSource random = level.getRandom();

    if (!level.isClientSide) {
        SoundEvent sound = random.nextBoolean()
            ? VietnamDelightModSounds.CRACK_1.get()
            : VietnamDelightModSounds.CRACK_2.get();
        level.playSound(null, clickedPos, sound, SoundSource.PLAYERS, 1.0F, 1.0F);
        if (!player.isCreative()) {
            stack.shrink(1);
        }
        BlockState clickedState = level.getBlockState(clickedPos);
        BlockPos abovePos = clickedPos.above();
        BlockState aboveState = level.getBlockState(abovePos);

        if (clickedState.isSolidRender(level, clickedPos) && aboveState.isAir()) {
            level.setBlock(abovePos, net.huwng.vietnamdelight.init.VietnamDelightModBlocks.BRUISED_GREEN_BANANA.get().defaultBlockState(), 3);
        }
    }

    return InteractionResult.SUCCESS;
}
}
