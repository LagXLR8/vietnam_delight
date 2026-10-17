package net.huwng.vietnamdelight.block;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.server.level.ServerLevel;

import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;
import net.huwng.vietnamdelight.init.VietnamDelightModParticleTypes;

public class BloomingFlamboyantLeavesBlock extends LeavesBlock {
	public BloomingFlamboyantLeavesBlock() {
		super(BlockBehaviour.Properties.of().ignitedByLava().sound(SoundType.GRASS).strength(0.2f).noOcclusion().randomTicks());
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 1;
	}

	@Override
	public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
		super.animateTick(state, world, pos, random);

		// Hoa phượng rơi lả tả xuống bên dưới
		if (random.nextInt(6) == 0) {
			double x = (double) pos.getX() + random.nextDouble();
			double y = (double) pos.getY() - 0.05;
			double z = (double) pos.getZ() + random.nextDouble();

			world.addParticle(
				VietnamDelightModParticleTypes.FLAMBOYANT_PETAL.get(),
				x, y, z,
				(random.nextDouble() - 0.5) * 0.1,  // vận tốc X
				-0.02,                              // vận tốc Y
				(random.nextDouble() - 0.5) * 0.1   // vận tốc Z
			);
		}
	}

	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		// Xử lý decay chuẩn của LeavesBlock trước (dựa theo khoảng cách tới gỗ + cờ persistent).
		super.randomTick(state, level, pos, random);
		if (!level.getBlockState(pos).is(this)) {
			return;
		}

		// Rải cánh hoa xuống nền đất bên dưới tán cây
		if (random.nextInt(8) == 0) {
			dropPetalsBelow(level, pos, random);
		}

		// Sau khi nở rộ một thời gian, hoa tàn dần và lùi về lại giai đoạn Flowering
		// (không nhảy thẳng về lá thường - phải tàn từ từ giống chiều mọc lên: flamboyant -> flowering -> blooming)
		if (random.nextInt(60) == 0) {
			level.setBlock(pos, VietnamDelightModBlocks.FLOWERING_FLAMBOYANT_LEAVES.get().defaultBlockState(), 3);
		}
	}

	private void dropPetalsBelow(ServerLevel level, BlockPos leafPos, RandomSource random) {
		BlockPos checkPos = leafPos.below();
		int maxFall = 32;
		for (int i = 0; i < maxFall; i++) {
			BlockState belowState = level.getBlockState(checkPos);
			if (belowState.isAir()) {
				checkPos = checkPos.below();
				continue;
			}
			BlockPos groundPos = checkPos.above();
			if (level.getBlockState(groundPos).isAir() && belowState.isFaceSturdy(level, checkPos, Direction.UP)) {
				level.setBlock(groundPos, VietnamDelightModBlocks.FLAMBOYANT_PETALS.get().defaultBlockState(), 3);
			}
			return;
		}
	}

	@Override
	public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 30;
	}

	@Override
	public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 60;
	}
}
