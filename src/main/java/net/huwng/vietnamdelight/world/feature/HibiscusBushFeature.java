package net.huwng.vietnamdelight.world.feature;

import com.mojang.serialization.Codec;
import net.huwng.vietnamdelight.block.HibiscusFlowerBlock;
import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.ArrayList;
import java.util.List;

public class HibiscusBushFeature extends Feature<NoneFeatureConfiguration> {
    public HibiscusBushFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    public HibiscusBushFeature() {
        this(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        // Kiểm tra nền đất bên dưới: hỗ trợ cả đất rừng rậm và bùn đầm lầy ngập mặn
        BlockPos below = origin.below();
        BlockState belowState = level.getBlockState(below);
        if (!belowState.is(BlockTags.DIRT) && !belowState.is(Blocks.MUD) && !belowState.is(Blocks.MUDDY_MANGROVE_ROOTS)) {
            return false;
        }

        // Điểm đặt gốc phải là không khí hoặc khối có thể bị thay thế (như cỏ, hoa dại, bụi cây)
        BlockState originState = level.getBlockState(origin);
        if (!isReplaceable(level, origin)) {
            return false;
        }

        // Đặt thân trung tâm (chuẩn jungle bush: đúng 1 block gỗ ở tâm sát mặt đất)
        level.setBlock(origin, Blocks.JUNGLE_LOG.defaultBlockState(), 2);

        List<BlockPos> outerLeaves = new ArrayList<>();

        // Tầng 0 (ngang thân): bán kính 2 quanh gốc
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                if (dx == 0 && dz == 0) {
                    continue; // Vị trí thân cây đã đặt log
                }

                // Cắt 4 góc ở bán kính 2 để tạo tán tròn tự nhiên
                if (Math.abs(dx) == 2 && Math.abs(dz) == 2 && random.nextFloat() < 0.8f) {
                    continue;
                }

                BlockPos leafPos = origin.offset(dx, 0, dz);
                if (isReplaceable(level, leafPos)) {
                    placeLeaf(level, leafPos, Math.max(Math.abs(dx), Math.abs(dz)), random);
                    outerLeaves.add(leafPos);
                }
            }
        }

        // Tầng 1 (đỉnh bụi): bán kính 1 chụp lên trên gốc
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                // Giảm bớt 4 góc trên đỉnh để đỉnh vòm tròn tự nhiên
                if (Math.abs(dx) == 1 && Math.abs(dz) == 1 && random.nextFloat() < 0.5f) {
                    continue;
                }

                BlockPos leafPos = origin.offset(dx, 1, dz);
                if (isReplaceable(level, leafPos)) {
                    placeLeaf(level, leafPos, Math.max(Math.abs(dx), Math.abs(dz)) + 1, random);
                    outerLeaves.add(leafPos);
                }
            }
        }

        // Gắn thêm các bông hoa dâm bụt trên các mặt bên của bụi cây (tuyệt đối không thay thế vào các khối lá)
        int flowerCount = 2 + random.nextInt(4); // 2 - 5 bông hoa
        List<BlockPos> candidateLeaves = new ArrayList<>(outerLeaves);
        java.util.Collections.shuffle(candidateLeaves, new java.util.Random(random.nextLong()));

        Direction[] horizontalDirs = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
        int placedFlowers = 0;

        for (BlockPos leafPos : candidateLeaves) {
            if (placedFlowers >= flowerCount) {
                break;
            }

            List<Direction> dirs = new ArrayList<>(List.of(horizontalDirs));
            java.util.Collections.shuffle(dirs, new java.util.Random(random.nextLong()));

            for (Direction dir : dirs) {
                BlockPos flowerPos = leafPos.relative(dir);
                if (isReplaceableForFlower(level, flowerPos)) {
                    BlockState flowerState = VietnamDelightModBlocks.HIBISCUS_FLOWER.get().defaultBlockState()
                            .setValue(HibiscusFlowerBlock.FACING, dir);
                    level.setBlock(flowerPos, flowerState, 2);
                    placedFlowers++;
                    break;
                }
            }
        }

        return true;
    }

    private void placeLeaf(WorldGenLevel level, BlockPos pos, int distance, RandomSource random) {
        // Tỉ lệ: 55% lá thường, 45% lá có hoa
        boolean flowering = random.nextFloat() < 0.45f;
        BlockState leafState = flowering
                ? VietnamDelightModBlocks.FLOWERING_HIBISCUS_LEAVES.get().defaultBlockState()
                : VietnamDelightModBlocks.HIBISCUS_LEAVES.get().defaultBlockState();

        leafState = leafState.setValue(LeavesBlock.DISTANCE, Math.min(7, Math.max(1, distance)));
        level.setBlock(pos, leafState, 2);
    }

    private boolean isReplaceable(WorldGenLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.isAir()
                || state.canBeReplaced()
                || state.is(BlockTags.REPLACEABLE_BY_TREES)
                || state.is(BlockTags.FLOWERS);
    }

    private boolean isReplaceableForFlower(WorldGenLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.is(BlockTags.LEAVES) || state.getBlock() instanceof LeavesBlock) {
            return false;
        }
        if (state.is(BlockTags.LOGS) || !state.getFluidState().isEmpty()) {
            return false;
        }
        return state.isAir() || state.canBeReplaced();
    }
}
