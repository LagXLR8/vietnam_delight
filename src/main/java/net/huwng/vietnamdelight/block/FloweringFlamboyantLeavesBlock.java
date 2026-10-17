package net.huwng.vietnamdelight.block;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.particles.ParticleTypes;

import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;

public class FloweringFlamboyantLeavesBlock extends LeavesBlock implements BonemealableBlock {
    public FloweringFlamboyantLeavesBlock() {
        super(BlockBehaviour.Properties.of()
            .ignitedByLava()
            .sound(SoundType.GRASS)
            .strength(0.2f)
            .noOcclusion()
            .randomTicks());
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 1;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader world, BlockPos pos, BlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState state) {
        applyGrowth(world, pos, random, 2, false);
    }

    public void applyGrowth(ServerLevel world, BlockPos pos, RandomSource random, int radius, boolean useEndRod) {
        BlockPos.betweenClosed(pos.offset(-radius, -radius, -radius), pos.offset(radius, radius, radius)).forEach(targetPos -> {
            BlockState targetState = world.getBlockState(targetPos);

            // Chỉ áp dụng cho Flowering_Flamboyant_Leaves
            if (targetState.is(this)) {
                for (Direction dir : Direction.values()) {
                    if (random.nextFloat() < 0.25f) { // 25% xác suất mỗi hướng
                        BlockPos flowerPos = targetPos.relative(dir);

                        if (world.getBlockState(flowerPos).isAir()) {
                            BlockState flowerState = VietnamDelightModBlocks.FLAMBOYANT_FLOWER.get()
                                .defaultBlockState()
                                .setValue(FlamboyantFlowerBlock.FACING, dir);

                            world.setBlock(flowerPos, flowerState, 3);

                            world.playSound(null, flowerPos, SoundEvents.SPORE_BLOSSOM_BREAK, SoundSource.BLOCKS, 0.6f, 1.0f);
                            world.sendParticles(
                                useEndRod ? ParticleTypes.END_ROD : ParticleTypes.HAPPY_VILLAGER,
                                flowerPos.getX() + 0.5,
                                flowerPos.getY() + 0.5,
                                flowerPos.getZ() + 0.5,
                                6, 0.3, 0.3, 0.3, 0.1);

                            // Chỉ đặt 1 hoa mỗi leaves block để tránh spam
                            break;
                        }
                    }
                }
            }
        });
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // Xử lý decay chuẩn của LeavesBlock trước (dựa theo khoảng cách tới gỗ + cờ persistent).
        // Nếu lá đã bị decay/xoá ở bước này thì dừng lại luôn, không chạy tiếp logic nở hoa bên dưới.
        super.randomTick(state, level, pos, random);
        if (!level.getBlockState(pos).is(this)) {
            return;
        }

        // Khi lá đã ra vài bông hoa xung quanh, có cơ hội nhỏ nở rộ thành blooming leaves
        if (random.nextInt(50) == 0 && hasNeighboringFlower(level, pos)) {
            level.setBlock(pos, VietnamDelightModBlocks.BLOOMING_FLAMBOYANT_LEAVES.get().defaultBlockState(), 3);
        }
    }

    private boolean hasNeighboringFlower(ServerLevel level, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            if (level.getBlockState(pos.relative(dir)).is(VietnamDelightModBlocks.FLAMBOYANT_FLOWER.get())) {
                return true;
            }
        }
        return false;
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
