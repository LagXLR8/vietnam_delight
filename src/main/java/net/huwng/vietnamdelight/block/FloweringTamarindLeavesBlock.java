package net.huwng.vietnamdelight.block;

import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.particles.ParticleTypes;

import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;

public class FloweringTamarindLeavesBlock extends LeavesBlock implements BonemealableBlock {
    public FloweringTamarindLeavesBlock() {
        super(BlockBehaviour.Properties.of().ignitedByLava().sound(SoundType.GRASS).strength(0.2f).noOcclusion());
    }

    // Màu lá
    @OnlyIn(Dist.CLIENT)
    public static void blockColorLoad(RegisterColorHandlersEvent.Block event) {
        event.getBlockColors().register((bs, world, pos, index) -> {
            return world != null && pos != null ? BiomeColors.getAverageFoliageColor(world, pos) : FoliageColor.getDefaultColor();
        }, VietnamDelightModBlocks.FLOWERING_TAMARIND_LEAVES.get());
    }

    @OnlyIn(Dist.CLIENT)
    public static void itemColorLoad(RegisterColorHandlersEvent.Item event) {
        event.getItemColors().register((stack, index) -> {
            return FoliageColor.getDefaultColor();
        }, VietnamDelightModBlocks.FLOWERING_TAMARIND_LEAVES.get());
    }

    // Bonemeal behavior
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

            if (targetState.is(this)) {
                for (Direction dir : Direction.values()) {
                    if (random.nextFloat() < 0.25f) {
                        BlockPos flowerPos = targetPos.relative(dir);
                        if (world.getBlockState(flowerPos).isAir()) {
                            BlockState flowerState = VietnamDelightModBlocks.TAMARIND_FLOWERS.get()
                                    .defaultBlockState()
                                    .setValue(TamarindFlowersBlock.FACING, dir);

                            world.setBlock(flowerPos, flowerState, 3);
                            world.playSound(null, flowerPos, SoundEvents.SPORE_BLOSSOM_BREAK, SoundSource.BLOCKS, 0.6f, 1.0f);
                            world.sendParticles(useEndRod ? ParticleTypes.END_ROD : ParticleTypes.HAPPY_VILLAGER,
                                    flowerPos.getX() + 0.5, flowerPos.getY() + 0.5, flowerPos.getZ() + 0.5,
                                    6, 0.3, 0.3, 0.3, 0.1);
                            break;
                        }
                    }
                }
            }
        });
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 1;
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
