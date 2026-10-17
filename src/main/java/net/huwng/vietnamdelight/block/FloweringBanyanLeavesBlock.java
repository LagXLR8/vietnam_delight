package net.huwng.vietnamdelight.block;

import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.util.RandomSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.client.Minecraft;

import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;
import net.huwng.vietnamdelight.init.VietnamDelightModParticleTypes;

public class FloweringBanyanLeavesBlock extends LeavesBlock implements BonemealableBlock {
    public FloweringBanyanLeavesBlock() {
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
        applyGrowth(world, pos, random, 2, false); // Phạm vi 4x4x4 (2*2+1 = 5, nhưng thực tế là 4x4x4)
    }
    
    public void applyGrowth(ServerLevel world, BlockPos pos, RandomSource random, int radius, boolean useEndRod) {
        BlockPos.betweenClosed(pos.offset(-radius, -radius, -radius), pos.offset(radius, radius, radius)).forEach(targetPos -> {
            BlockState targetState = world.getBlockState(targetPos);
            
            // Chỉ áp dụng cho Flowering_Banyan_Leaves
            if (targetState.is(this)) {
                // Kiểm tra các hướng xung quanh để đặt hoa
                for (Direction dir : Direction.values()) {
                    if (random.nextFloat() < 0.25f) { // 25% xác suất mỗi hướng
                        BlockPos flowerPos = targetPos.relative(dir);
                        
                        // Kiểm tra vị trí trống để đặt hoa
                        if (world.getBlockState(flowerPos).isAir()) {
                            
                            // Đặt banyan_flower với hướng tương ứng
                            // Thay VietnamDelightModBlocks.BANYAN_FLOWER bằng tên block thực tế của bạn
                            BlockState flowerState = VietnamDelightModBlocks.BANYAN_FLOWER.get()
                                .defaultBlockState()
                                .setValue(BanyanFlowerBlock.FACING, dir); // Giả sử có property FACING
                            
                            world.setBlock(flowerPos, flowerState, 3);
                            
                            // Hiệu ứng âm thanh và particle
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

    @OnlyIn(Dist.CLIENT)
    public static void blockColorLoad(RegisterColorHandlersEvent.Block event) {
        Minecraft.getInstance().tell(() -> {
            event.getBlockColors().register((state, world, pos, index) -> {
                return world != null && pos != null ? BiomeColors.getAverageFoliageColor(world, pos) : FoliageColor.getDefaultColor();
            }, VietnamDelightModBlocks.FLOWERING_BANYAN_LEAVES.get());
        });
    }

    @OnlyIn(Dist.CLIENT)
    public static void itemColorLoad(RegisterColorHandlersEvent.Item event) {
        Minecraft.getInstance().tell(() -> {
            event.getItemColors().register((stack, index) -> {
                return FoliageColor.getDefaultColor();
            }, VietnamDelightModBlocks.FLOWERING_BANYAN_LEAVES.get());
        });
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        super.animateTick(state, world, pos, random);

        if (random.nextInt(36) == 0) {
            BlockPos below = pos.below();
            BlockState belowState = world.getBlockState(below);

            if (!belowState.isSolid() && !(belowState.getBlock() instanceof LeavesBlock)) {
                double x = (double) pos.getX() + random.nextDouble();
                double y = (double) pos.getY() - 0.05;
                double z = (double) pos.getZ() + random.nextDouble();

                world.addParticle(
                    VietnamDelightModParticleTypes.BANYAN_LEAF.get(),
                    x, y, z,
                    (random.nextDouble() - 0.5) * 0.08,
                    -0.015,
                    (random.nextDouble() - 0.5) * 0.08
                );
            }
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