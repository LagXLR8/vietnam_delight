package net.huwng.vietnamdelight.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.core.Direction;

import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;
import net.huwng.vietnamdelight.block.BananaRootBlock;
import net.huwng.vietnamdelight.block.BananaPseudostemBlock;
import net.huwng.vietnamdelight.block.BananaSuckerBlock;
import net.huwng.vietnamdelight.block.WallBananaFrondBlock;
import net.huwng.vietnamdelight.block.BananaFlowerBlock;
import net.huwng.vietnamdelight.block.BananaBundleCropBlock;

public class BananaTreeFeature extends Feature<NoneFeatureConfiguration> {
    public BananaTreeFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
	    WorldGenLevel level = context.level();
	    BlockPos pos = context.origin();
	    RandomSource random = context.random();
	
	    // kiểm tra block phía dưới có phải đất không
	    BlockPos ground = pos.below();
	    BlockState groundState = level.getBlockState(ground);
	    if (!(groundState.is(Blocks.GRASS_BLOCK) || groundState.is(Blocks.DIRT) || groundState.is(Blocks.COARSE_DIRT))) {
	        return false;
	    }
	
	    // chiều cao ngẫu nhiên 4–6
	    int height = 4 + random.nextInt(3);
	
	    // kiểm tra không gian phía trên
	    for (int i = 0; i <= height + 3; i++) {
	        if (!level.getBlockState(pos.above(i)).isAir()) {
	            return false;
	        }
	    }

	    // đặt thêm root phụ xung quanh (state 0), có thể mọc sucker
	    for (Direction dir : Direction.Plane.HORIZONTAL) {
	        BlockPos sideRoot = ground.relative(dir);
	        if (level.getBlockState(sideRoot).isAir() || level.getBlockState(sideRoot).is(Blocks.GRASS_BLOCK)) {
	            level.setBlock(sideRoot, VietnamDelightModBlocks.BANANA_ROOT.get().defaultBlockState()
	                    .setValue(BananaRootBlock.BLOCKSTATE, 0), 2);
	
	            if (random.nextBoolean()) {
	                BlockPos suckerPos = sideRoot.above();
	                int suckerState = random.nextInt(3); // 0–2
	                BlockState sucker = VietnamDelightModBlocks.BANANA_SUCKER.get().defaultBlockState()
	                        .setValue(BananaSuckerBlock.BLOCKSTATE, suckerState);
	                level.setBlock(suckerPos, sucker, 2);
	            }
	        }
	    }
			
	    // đặt root chính (state 1)
	    level.setBlock(ground, VietnamDelightModBlocks.BANANA_ROOT.get().defaultBlockState()
	            .setValue(BananaRootBlock.BLOCKSTATE, 1), 2);
	
	    // đặt pseudostem
	    for (int i = 0; i < height; i++) {
	        int state = (i == height - 1) ? 6 : (i == 1 ? 2 : (i >= 2 ? 4 : 0));
	        BlockPos stemPos = pos.above(i);
	        BlockState stem = VietnamDelightModBlocks.BANANA_PSEUDOSTEM.get().defaultBlockState()
	                .setValue(BananaPseudostemBlock.BLOCKSTATE, state);
	        level.setBlock(stemPos, stem, 2);
	    }
	
	    // lá trên cùng
	    BlockPos top = pos.above(height);
	    level.setBlock(top, VietnamDelightModBlocks.BANANA_FROND.get().defaultBlockState(), 2);
	
	    // wall fronds xung quanh thân ngay dưới frond top
	    BlockPos frondBase = pos.above(height - 1);
	    Direction specialDir = Direction.Plane.HORIZONTAL.getRandomDirection(random); // chọn một hướng đặc biệt
	
	    for (Direction dir : Direction.Plane.HORIZONTAL) {
	        BlockPos side = frondBase.relative(dir);
	        if (level.getBlockState(side).isAir()) {
	            BlockState wallFrond = VietnamDelightModBlocks.WALL_BANANA_FROND.get()
	                    .defaultBlockState()
	                    .setValue(WallBananaFrondBlock.FACING, dir); // quay ngược lại (ra ngoài)
	
	            if (dir == specialDir) {
				    // frond đặc biệt state 4
				    wallFrond = wallFrond.setValue(WallBananaFrondBlock.BLOCKSTATE, 4);
				    level.setBlock(side, wallFrond, 2);
				
				    // dưới frond đặc biệt: bundle với tỉ lệ
				    BlockPos bundlePos = side.below();
				int roll = random.nextInt(100);
				BlockState bundle;
				
				if (roll < 60) {
				    // 60% Banana_Bundle_Crop với state 1–4
				    int cropState = 1 + random.nextInt(4);
				    bundle = VietnamDelightModBlocks.BANANA_BUNDLE_CROP.get()
				            .defaultBlockState()
				            .setValue(BananaBundleCropBlock.BLOCKSTATE, cropState);
				} else if (roll < 80) {
				    // 20% Green_Banana_Bundle
				    bundle = VietnamDelightModBlocks.GREEN_BANANA_BUNDLE.get().defaultBlockState();
				} else {
				    // 20% Banana_Bundle
				    bundle = VietnamDelightModBlocks.BANANA_BUNDLE.get().defaultBlockState();
				}
				
				level.setBlock(bundlePos, bundle, 2);
				
				// dưới bundle: flower state 1–5
				BlockPos flowerPos = bundlePos.below();
				int flowerState = 1 + random.nextInt(5);
				BlockState flower = VietnamDelightModBlocks.BANANA_FLOWER.get()
				        .defaultBlockState()
				        .setValue(BananaFlowerBlock.BLOCKSTATE, flowerState);
				level.setBlock(flowerPos, flower, 2);
				} else {
				    // frond thường
				    level.setBlock(side, wallFrond, 2);
				}
	        }
	    }
	
	    return true;
	}
}