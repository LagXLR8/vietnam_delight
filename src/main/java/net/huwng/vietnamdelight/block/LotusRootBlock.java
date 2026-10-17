package net.huwng.vietnamdelight.block;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.BlockPos;

import net.huwng.vietnamdelight.init.VietnamDelightModItems;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

import java.util.List;

public class LotusRootBlock extends Block {
    public LotusRootBlock() {
        super(BlockBehaviour.Properties.of()
            .instrument(NoteBlockInstrument.SNARE)
            .sound(SoundType.MUDDY_MANGROVE_ROOTS)
            .strength(0.5f));
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 15;
    }

    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        return List.of(
            new ItemStack(net.minecraft.world.level.block.Blocks.MUD),
            new ItemStack(VietnamDelightModItems.LOTUS_RHIZOME.get())
        );
    }
}
