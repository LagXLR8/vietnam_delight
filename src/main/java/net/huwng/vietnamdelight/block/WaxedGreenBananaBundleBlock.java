
package net.huwng.vietnamdelight.block;

import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;

public class WaxedGreenBananaBundleBlock extends FallingBlock {
	public WaxedGreenBananaBundleBlock() {
		super(BlockBehaviour.Properties.of().ignitedByLava().instrument(NoteBlockInstrument.BASS).sound(SoundType.HONEY_BLOCK).strength(2f));
	}
	
	@Override
public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                             InteractionHand hand, BlockHitResult hit) {
    ItemStack item = player.getItemInHand(hand);

    if (item.getItem() instanceof AxeItem) {
        level.setBlock(pos, VietnamDelightModBlocks.GREEN_BANANA_BUNDLE.get().defaultBlockState(), 3);
        level.playSound(null, pos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0f, 1.0f);
        level.levelEvent(3004, pos, 0);
        return InteractionResult.SUCCESS;
    }
    return super.use(state, level, pos, player, hand, hit);
}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 15;
	}
}
