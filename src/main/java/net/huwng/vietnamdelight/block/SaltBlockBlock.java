package net.huwng.vietnamdelight.block;

import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.FluidState;
import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;
import net.minecraft.util.RandomSource;

import java.util.Random;

public class SaltBlockBlock extends Block {
    public SaltBlockBlock() {
        super(BlockBehaviour.Properties
                .of()
                .instrument(NoteBlockInstrument.BASEDRUM)
                .sound(SoundType.CALCITE)
                .strength(1.5f, 6f)
                .randomTicks());
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
	    if (level.isClientSide()) return;
	    BlockPos above = pos.above();
	    if (!level.getBlockState(above).isAir()) return;
	    boolean foundDripstone = false;
	    for (int offset = 2; offset <= 10; offset++) {
	        BlockPos checkPos = pos.above(offset);
	        if (level.getBlockState(checkPos).is(Blocks.POINTED_DRIPSTONE)) {
	            foundDripstone = true;
	            break;
	        }
	    }
	    if (!foundDripstone) return;
	    if (random.nextInt(5) == 0) {
	        level.setBlockAndUpdate(above, VietnamDelightModBlocks.SALT_CLUSTER.get().defaultBlockState());
	    }
	}
}
