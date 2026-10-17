package net.huwng.vietnamdelight.world.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DurianTreeFeature extends Feature<NoneFeatureConfiguration> {

    public DurianTreeFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel world = context.level();
        BlockPos pos = context.origin();
        RandomSource random = context.random();

        if (!canPlaceTree(world, pos)) {
            return false;
        }

        // Chiều cao thân cây sầu riêng (13 - 16 blocks)
        int trunkHeight = 13 + random.nextInt(4);

        Set<BlockPos> woodPositions = new HashSet<>();
        List<BlockPos> branchTips = new ArrayList<>();

        // ===== 1. GỐC VÀ RỄ NỔI XÒE (Root flares - đúng 1 ô chiều ngang quanh gốc) =====
        BlockState logY = VietnamDelightModBlocks.DURIAN_LOG.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, Direction.Axis.Y);
        setWood(world, pos, logY, woodPositions);

        BlockState woodDefault = VietnamDelightModBlocks.DURIAN_WOOD.get().defaultBlockState();
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos rootPos = pos.relative(dir);
            if (canReplaceWithWood(world, rootPos)) {
                setWood(world, rootPos, woodDefault, woodPositions);

                // Kéo rễ cắm sâu xuống đất theo phương thẳng đứng nếu gặp địa hình dốc
                BlockPos belowRootPos = rootPos.below();
                if (canReplaceWithWood(world, belowRootPos)) {
                    setWood(world, belowRootPos, woodDefault, woodPositions);
                    BlockPos deepRootPos = belowRootPos.below();
                    if (canReplaceWithWood(world, deepRootPos)) {
                        setWood(world, deepRootPos, woodDefault, woodPositions);
                    }
                }
            }
        }

        // ===== 2. THÂN CHÍNH (Central Trunk) =====
        for (int y = 1; y < trunkHeight; y++) {
            setWood(world, pos.above(y), logY, woodPositions);
        }

        // ===== 3. HỆ THỐNG CÀNH PHÂN BỔ: NHIỀU Ở DƯỚI, THON DẦN LÊN TRÊN =====
        List<int[]> directions = new ArrayList<>();
        directions.add(new int[]{1, 0});
        directions.add(new int[]{-1, 0});
        directions.add(new int[]{0, 1});
        directions.add(new int[]{0, -1});
        directions.add(new int[]{1, 1});
        directions.add(new int[]{-1, 1});
        directions.add(new int[]{1, -1});
        directions.add(new int[]{-1, -1});
        Collections.shuffle(directions, new java.util.Random(random.nextLong()));

        int dirIndex = 0;

        // TẦNG 1: DƯỚI (Y = 3..6) - Nhiều cành nhất (4 cành dài 3-5 blocks xòe rộng tứ phía)
        for (int i = 0; i < 4; i++) {
            int[] dir = directions.get(dirIndex++ % directions.size());
            int startY = 3 + random.nextInt(4); // Y = 3..6
            int length = 3 + random.nextInt(3); // dài 3-5 blocks
            BlockPos tip = generateBranch(world, pos.above(startY), dir[0], dir[1], length, random, woodPositions);
            branchTips.add(tip);
        }

        // TẦNG 2: GIỮA (Y = 7..9) - 2 đến 3 cành vừa (dài 2-4 blocks)
        int midBranchCount = 2 + random.nextInt(2); // 2-3 cành
        for (int i = 0; i < midBranchCount; i++) {
            int[] dir = directions.get(dirIndex++ % directions.size());
            int startY = 7 + random.nextInt(3); // Y = 7..9
            int length = 2 + random.nextInt(3); // dài 2-4 blocks
            BlockPos tip = generateBranch(world, pos.above(startY), dir[0], dir[1], length, random, woodPositions);
            branchTips.add(tip);
        }

        // TẦNG 3: TRÊN CAO (Y = 10..12) - Ít cành (1 cành nhỏ dài 2-3 blocks)
        int upperY = Math.min(10 + random.nextInt(3), trunkHeight - 2);
        int[] upperDir = directions.get(dirIndex++ % directions.size());
        BlockPos upperTip = generateBranch(world, pos.above(upperY), upperDir[0], upperDir[1], 2 + random.nextInt(2), random, woodPositions);
        branchTips.add(upperTip);

        // Đỉnh thân cây
        branchTips.add(pos.above(trunkHeight - 1));

        // ===== 4. PHỦ CÁC CỤM TÁN LÁ VỚI THUẬT TOÁN CẮT GÓC TỰ NHIÊN (Organic Leaf Clusters) =====
        for (BlockPos tip : branchTips) {
            generateOrganicLeafCluster(world, tip, random, woodPositions);
        }

        // Tán lá đỉnh ngọn (Crown Canopy)
        generateCrownDome(world, pos.above(trunkHeight - 1), random, woodPositions);

        // ===== 5. SINH LICHEN VÀ NỤ / TRÁI SẦU RIÊNG TREO TRÊN CÀNH =====
        for (BlockPos woodPos : woodPositions) {
            if (woodPos.getY() >= pos.getY() + 2 && !woodPos.equals(pos)) {
                BlockState woodState = world.getBlockState(woodPos);
                if (woodState.is(VietnamDelightModBlocks.DURIAN_WOOD.get())) {
                    BlockPos belowPos = woodPos.below();
                    boolean hasAirBelow = world.getBlockState(belowPos).isAir();

                    // Ưu tiên những durian wood có 1 block khoảng trống phía dưới xuất hiện lichen
                    if (hasAirBelow && random.nextFloat() < 0.40f) {
                        woodState = woodState.setValue(net.huwng.vietnamdelight.block.DurianWoodBlock.LICHEN, true);
                        world.setBlock(woodPos, woodState, 3);
                    } else if (!hasAirBelow && random.nextFloat() < 0.10f) {
                        woodState = woodState.setValue(net.huwng.vietnamdelight.block.DurianWoodBlock.LICHEN, true);
                        world.setBlock(woodPos, woodState, 3);
                    }

                    // Treo nụ sầu riêng (random stage 0-3) hoặc trái sầu riêng hoàn chỉnh
                    if (hasAirBelow) {
                        float roll = random.nextFloat();
                        if (roll < 0.18f) {
                            int budAge = random.nextInt(4);
                            world.setBlock(belowPos, VietnamDelightModBlocks.DURIAN_BUD.get().defaultBlockState().setValue(net.huwng.vietnamdelight.block.DurianBudBlock.AGE, budAge), 3);
                        } else if (roll < 0.23f) {
                            world.setBlock(belowPos, VietnamDelightModBlocks.DURIAN.get().defaultBlockState(), 3);
                        }
                    }
                }
            }
        }

        return true;
    }

    private boolean canPlaceTree(WorldGenLevel world, BlockPos pos) {
        BlockState belowState = world.getBlockState(pos.below());
        if (!belowState.is(Blocks.GRASS_BLOCK) && !belowState.is(Blocks.DIRT) && !belowState.is(Blocks.PODZOL) && !belowState.is(Blocks.FARMLAND)) {
            return false;
        }

        for (int y = 1; y < 8; y++) {
            BlockPos checkPos = pos.above(y);
            BlockState state = world.getBlockState(checkPos);
            if (!state.isAir() &&
                !state.is(Blocks.GRASS) &&
                !state.is(Blocks.TALL_GRASS) &&
                !state.is(VietnamDelightModBlocks.DURIAN_SAPLING.get()) &&
                !state.is(VietnamDelightModBlocks.DURIAN_LEAVES.get())) {
                return false;
            }
        }
        return true;
    }

    private boolean canReplaceWithWood(WorldGenLevel world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.isAir() || state.is(Blocks.GRASS) || state.is(Blocks.TALL_GRASS) || state.is(VietnamDelightModBlocks.DURIAN_LEAVES.get()) || state.is(VietnamDelightModBlocks.DURIAN_SAPLING.get());
    }

    private void setWood(WorldGenLevel world, BlockPos pos, BlockState state, Set<BlockPos> woodPositions) {
        world.setBlock(pos, state, 3);
        woodPositions.add(pos);
    }

    private BlockPos generateBranch(WorldGenLevel world, BlockPos startPos, int dx, int dz, int length, RandomSource random, Set<BlockPos> woodPositions) {
        BlockPos.MutableBlockPos currentPos = startPos.mutable();

        int stepX = Integer.signum(dx);
        int stepZ = Integer.signum(dz);

        for (int i = 1; i <= length; i++) {
            boolean movedX = false;
            boolean movedZ = false;

            if (i == 1) {
                if (stepX != 0) { currentPos.move(stepX, 0, 0); movedX = true; }
                if (stepZ != 0) { currentPos.move(0, 0, stepZ); movedZ = true; }
            } else {
                if (stepX != 0 && random.nextFloat() < 0.75f) {
                    currentPos.move(stepX, 0, 0);
                    movedX = true;
                }
                if (stepZ != 0 && random.nextFloat() < 0.75f) {
                    currentPos.move(0, 0, stepZ);
                    movedZ = true;
                }
                if (random.nextFloat() < 0.35f) {
                    currentPos.move(0, 1, 0);
                }
            }

            Direction.Axis axis = Direction.Axis.Y;
            if (movedX && !movedZ) axis = Direction.Axis.X;
            else if (movedZ && !movedX) axis = Direction.Axis.Z;

            BlockState branchState = VietnamDelightModBlocks.DURIAN_WOOD.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, axis);
            setWood(world, currentPos.immutable(), branchState, woodPositions);
        }

        return currentPos.immutable();
    }

    /**
     * Sinh cụm tán lá tự nhiên với thuật toán cắt góc (Organic carving):
     * Cắt bỏ các góc vuông của khối 3x3x3 / 5x5x5, tạo hình vòm bầu dục tự nhiên, không bị vuông vức.
     */
    private void generateOrganicLeafCluster(WorldGenLevel world, BlockPos center, RandomSource random, Set<BlockPos> woodPositions) {
        BlockState leafState = VietnamDelightModBlocks.DURIAN_LEAVES.get().defaultBlockState().setValue(LeavesBlock.PERSISTENT, false);

        for (int dy = -2; dy <= 2; dy++) {
            for (int rx = -2; rx <= 2; rx++) {
                for (int rz = -2; rz <= 2; rz++) {
                    int absX = Math.abs(rx);
                    int absZ = Math.abs(rz);

                    // 1. Cắt triệt để 4 góc ngoài cùng ở mọi tầng (tuyệt đối không để thành khối vuông 5x5)
                    if (absX == 2 && absZ == 2) {
                        continue;
                    }

                    // 2. Phân tầng theo chiều dọc (dy)
                    if (dy == -2) {
                        // Tầng đáy rủ xuống: chỉ các block gần tâm và có tỉ lệ ngẫu nhiên
                        if (absX + absZ <= 2 && random.nextFloat() < 0.40f) {
                            placeLeaf(world, center.offset(rx, dy, rz), leafState, woodPositions);
                        }
                    } else if (dy == -1) {
                        // Tầng dưới-giữa: đĩa tròn bo góc (hình thoi nở rộng)
                        if (absX + absZ <= 3) {
                            if (absX == 2 || absZ == 2) {
                                if (random.nextFloat() < 0.60f) {
                                    placeLeaf(world, center.offset(rx, dy, rz), leafState, woodPositions);
                                }
                            } else {
                                // Cắt góc ngẫu nhiên ở 4 góc chéo trong (±1, ±1) để tạo độ xù xì
                                if (!(absX == 1 && absZ == 1 && random.nextFloat() < 0.25f)) {
                                    placeLeaf(world, center.offset(rx, dy, rz), leafState, woodPositions);
                                }
                            }
                        }
                    } else if (dy == 0) {
                        // Tầng ngang tâm đầu cành: đĩa tròn chính
                        if (absX + absZ <= 3) {
                            if (absX == 2 || absZ == 2) {
                                if (random.nextFloat() < 0.50f) {
                                    placeLeaf(world, center.offset(rx, dy, rz), leafState, woodPositions);
                                }
                            } else {
                                // Cắt góc ngẫu nhiên
                                if (!(absX == 1 && absZ == 1 && random.nextFloat() < 0.20f)) {
                                    placeLeaf(world, center.offset(rx, dy, rz), leafState, woodPositions);
                                }
                            }
                        }
                    } else if (dy == 1) {
                        // Tầng trên-giữa: hình chữ thập / bo tròn nhỏ hơn
                        if (absX + absZ <= 2) {
                            if (absX == 1 && absZ == 1) {
                                if (random.nextFloat() < 0.55f) {
                                    placeLeaf(world, center.offset(rx, dy, rz), leafState, woodPositions);
                                }
                            } else {
                                if (random.nextFloat() < 0.85f) {
                                    placeLeaf(world, center.offset(rx, dy, rz), leafState, woodPositions);
                                }
                            }
                        }
                    } else if (dy == 2) {
                        // Tầng chóp nón nhẹ: 1 vài lá đỉnh
                        if (absX + absZ <= 1) {
                            if (random.nextFloat() < 0.40f) {
                                placeLeaf(world, center.offset(rx, dy, rz), leafState, woodPositions);
                            }
                        }
                    }
                }
            }
        }
    }

    private void generateCrownDome(WorldGenLevel world, BlockPos topCenter, RandomSource random, Set<BlockPos> woodPositions) {
        BlockState leafState = VietnamDelightModBlocks.DURIAN_LEAVES.get().defaultBlockState().setValue(LeavesBlock.PERSISTENT, false);

        // Vòm tán lá đỉnh ngọn
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (Math.abs(x) + Math.abs(z) <= 1 || (Math.abs(x) == 1 && Math.abs(z) == 1 && random.nextFloat() < 0.5f)) {
                    placeLeaf(world, topCenter.offset(x, 1, z), leafState, woodPositions);
                }
            }
        }

        // Đỉnh cao nhất (+2)
        if (random.nextFloat() < 0.65f) {
            placeLeaf(world, topCenter.above(2), leafState, woodPositions);
        }
    }

    private void placeLeaf(WorldGenLevel world, BlockPos pos, BlockState leafState, Set<BlockPos> woodPositions) {
        if (!woodPositions.contains(pos)) {
            BlockState state = world.getBlockState(pos);
            if (state.isAir() || state.is(Blocks.GRASS) || state.is(Blocks.TALL_GRASS) || state.is(VietnamDelightModBlocks.DURIAN_LEAVES.get())) {
                world.setBlock(pos, leafState, 3);
            }
        }
    }
}
