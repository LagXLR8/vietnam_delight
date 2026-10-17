package net.huwng.vietnamdelight.world.feature;

import net.huwng.vietnamdelight.block.BougainvilleaTrunkBlock;
import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BougainvilleaTreeGrower {

    public static boolean grow(LevelAccessor level, BlockPos pos, RandomSource random) {
        // Chọn ngẫu nhiên 1 trong 4 loại lá hoa giấy cho cây
        Block leafBlock = pickRandomLeaves(random);

        // Kiểm tra xem xung quanh có tường/vật thể để bám lên hay không
        Direction wallDir = findWallDirection(level, pos);

        if (wallDir != null) {
            // Có tường/vật bám: mọc dạng hoa giấy leo tường với cành bẻ góc chéo
            return growClimbing(level, pos, leafBlock, wallDir, random);
        } else {
            // Không có tường/vật bám: kiểm tra độ thoáng chiều cao và chọn dạng cây
            boolean canFitTall = true;
            for (int y = 1; y <= 7; y++) {
                if (!isReplaceable(level, pos.above(y))) {
                    canFitTall = false;
                    break;
                }
            }

            if (canFitTall && random.nextBoolean()) {
                // Mọc thành cây hoa giấy dáng cao, nhiều cành
                return growTallTree(level, pos, leafBlock, random);
            } else {
                // Mọc thành cây hoa giấy nhỏ dáng bonsai với cành bẻ góc chéo
                return growSmallTree(level, pos, leafBlock, random);
            }
        }
    }

    private static Block pickRandomLeaves(RandomSource random) {
        return switch (random.nextInt(4)) {
            case 0 -> VietnamDelightModBlocks.BOUGAINVILLEA_LEAVES.get();
            case 1 -> VietnamDelightModBlocks.FLOWERING_ORANGE_BOUGAINVILLEA_LEAVES.get();
            case 2 -> VietnamDelightModBlocks.FLOWERING_PINK_BOUGAINVILLEA_LEAVES.get();
            default -> VietnamDelightModBlocks.WHITE_BOUGAINVILLEA_LEAVES.get();
        };
    }

    /**
     * Tìm hướng có tường/công trình ở gần để bám lên (trong phạm vi 1-2 block xung quanh, độ cao 1-3)
     */
    private static Direction findWallDirection(LevelAccessor level, BlockPos pos) {
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            // Kiểm tra cách 1 block
            for (int y = 1; y <= 3; y++) {
                BlockPos checkPos = pos.above(y).relative(dir);
                if (isValidSupport(level, checkPos, level.getBlockState(checkPos))) {
                    return dir;
                }
            }
            // Kiểm tra cách 2 block
            for (int y = 1; y <= 3; y++) {
                BlockPos checkPos = pos.above(y).relative(dir, 2);
                if (isValidSupport(level, checkPos, level.getBlockState(checkPos))) {
                    return dir;
                }
            }
        }
        return null;
    }

    private static boolean isValidSupport(LevelAccessor level, BlockPos pos, BlockState state) {
        if (state.isAir() || state.getBlock() instanceof LeavesBlock || state.getBlock() instanceof BougainvilleaTrunkBlock) {
            return false;
        }
        // Chỉ các block đầy solid mới là tường (chính trunk không được coi là block đầy)
        return state.isSolidRender(level, pos) || (state.isCollisionShapeFullBlock(level, pos) && state.canOcclude());
    }

    private static boolean isReplaceable(LevelAccessor level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.isAir() || state.canBeReplaced() || state.is(BlockTags.REPLACEABLE_BY_TREES) || state.is(BlockTags.FLOWERS);
    }

    /**
     * DẠNG 1: Cây hoa giấy nhỏ (khi không có tường bám)
     * Thân cây và cành bẻ góc chéo dáng bonsai nghệ thuật, tán lá đan xen cành gỗ 100% liền mạch.
     */
    private static boolean growSmallTree(LevelAccessor level, BlockPos origin, Block leafBlock, RandomSource random) {
        List<BlockPos> allTrunks = new ArrayList<>();
        Set<BlockPos> leafyTrunks = new HashSet<>();

        // 1. Gốc & thân chính (bare trunk)
        placeTrunkBlock(level, origin, allTrunks);

        Direction primaryDir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        Direction secondaryDir = random.nextBoolean() ? primaryDir.getClockWise() : primaryDir.getCounterClockWise();
        Direction oppDir = primaryDir.getOpposite();

        BlockPos trunk1 = origin.above();
        placeTrunkBlock(level, trunk1, allTrunks);

        BlockPos bend1 = trunk1.relative(primaryDir);
        placeTrunkBlock(level, bend1, allTrunks);

        BlockPos trunkCenter = bend1.above();
        placeTrunkBlock(level, trunkCenter, allTrunks);

        // 2. Cành 1 (vươn theo primaryDir vào tán lá)
        BlockPos b1_1 = trunkCenter.relative(primaryDir);
        placeTrunkBlock(level, b1_1, allTrunks);

        BlockPos b1_2 = b1_1.above();
        placeLeafyBlock(level, b1_2, allTrunks, leafyTrunks);

        BlockPos b1_3 = b1_2.relative(primaryDir);
        placeLeafyBlock(level, b1_3, allTrunks, leafyTrunks);

        // 3. Cành 2 (vươn theo secondaryDir vào tán lá)
        BlockPos b2_1 = trunkCenter.relative(secondaryDir);
        placeTrunkBlock(level, b2_1, allTrunks);

        BlockPos b2_2 = b2_1.above();
        placeLeafyBlock(level, b2_2, allTrunks, leafyTrunks);

        BlockPos b2_3 = b2_2.relative(secondaryDir);
        placeLeafyBlock(level, b2_3, allTrunks, leafyTrunks);

        // 4. Cành 3 (vươn lên cao và sang hướng oppDir vào tán lá)
        BlockPos b3_1 = trunkCenter.above();
        placeTrunkBlock(level, b3_1, allTrunks);

        BlockPos b3_2 = b3_1.relative(oppDir);
        placeLeafyBlock(level, b3_2, allTrunks, leafyTrunks);

        BlockPos b3_3 = b3_2.above();
        placeLeafyBlock(level, b3_3, allTrunks, leafyTrunks);

        // 5. Tán hoa lá đan quanh các đầu cành ngọn (khối lá thuần, không chứa trunk)
        BlockPos[] tips = new BlockPos[]{ b1_3, b2_3, b3_3 };
        for (BlockPos tip : tips) {
            placeLeafClusterAroundTip(level, tip, leafBlock, random);
        }

        // 6. Điểm xuyết hoa lá quanh các đốt cành giữa (khối lá thuần)
        BlockPos[] midBranches = new BlockPos[]{ b1_2, b2_2, b3_2 };
        for (BlockPos mid : midBranches) {
            if (random.nextFloat() < 0.6f) {
                placePureLeafBlock(level, mid.above(), leafBlock);
            }
            Direction rDir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            if (random.nextFloat() < 0.5f) {
                placePureLeafBlock(level, mid.relative(rDir), leafBlock);
            }
        }

        // 7. Cập nhật kết nối và áp dụng LEAF
        finalizeTrunkConnections(level, allTrunks, leafyTrunks, leafBlock);

        // Rải 1 cánh hoa giấy dưới gốc
        dropPetalAt(level, origin.offset(random.nextInt(3) - 1, 0, random.nextInt(3) - 1));

        return true;
    }

    /**
     * DẠNG 1B: Cây hoa giấy dáng cao, xòe nhiều cành (khi không có tường bám)
     * Thân vươn cao 6-8 block, uốn lượn nghệ thuật với 5 cành bẻ góc đa tầng (tầng thấp, tầng giữa, tầng ngọn)
     * vươn tỏa ra 4 hướng, tán lá hoa đan xen cành gỗ 100% liền mạch.
     */
    private static boolean growTallTree(LevelAccessor level, BlockPos origin, Block leafBlock, RandomSource random) {
        List<BlockPos> allTrunks = new ArrayList<>();
        Set<BlockPos> leafyTrunks = new HashSet<>();

        // Hướng uốn lượn chính và phụ
        Direction mainDir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        Direction sideDir = random.nextBoolean() ? mainDir.getClockWise() : mainDir.getCounterClockWise();
        Direction oppDir = mainDir.getOpposite();
        Direction oppSideDir = sideDir.getOpposite();

        // 1. Thân chính vươn cao với các khúc uốn lượn nhẹ
        BlockPos t0 = origin;
        placeTrunkBlock(level, t0, allTrunks);

        BlockPos t1 = t0.above();
        placeTrunkBlock(level, t1, allTrunks);

        BlockPos t2 = t1.above();
        placeTrunkBlock(level, t2, allTrunks);

        BlockPos t2_bend = t2.relative(mainDir);
        placeTrunkBlock(level, t2_bend, allTrunks);

        BlockPos t3 = t2_bend.above();
        placeTrunkBlock(level, t3, allTrunks);

        BlockPos t4 = t3.above();
        placeTrunkBlock(level, t4, allTrunks);

        BlockPos t4_bend = t4.relative(sideDir);
        placeTrunkBlock(level, t4_bend, allTrunks);

        BlockPos t5 = t4_bend.above();
        placeTrunkBlock(level, t5, allTrunks);

        // 2. Cành 1: Tầng thấp (vươn sang oppDir)
        BlockPos b1_1 = t3.relative(oppDir);
        placeTrunkBlock(level, b1_1, allTrunks);

        BlockPos b1_2 = b1_1.relative(oppDir);
        placeLeafyBlock(level, b1_2, allTrunks, leafyTrunks);

        BlockPos b1_3 = b1_2.above();
        placeLeafyBlock(level, b1_3, allTrunks, leafyTrunks);

        // 3. Cành 2: Tầng giữa bên 1 (vươn sang oppSideDir)
        BlockPos b2_1 = t4.relative(oppSideDir);
        placeTrunkBlock(level, b2_1, allTrunks);

        BlockPos b2_2 = b2_1.above();
        placeLeafyBlock(level, b2_2, allTrunks, leafyTrunks);

        BlockPos b2_3 = b2_2.relative(oppSideDir);
        placeLeafyBlock(level, b2_3, allTrunks, leafyTrunks);

        // 4. Cành 3: Tầng giữa bên 2 (vươn tiếp sang sideDir)
        BlockPos b3_1 = t4_bend.relative(sideDir);
        placeTrunkBlock(level, b3_1, allTrunks);

        BlockPos b3_2 = b3_1.relative(sideDir);
        placeLeafyBlock(level, b3_2, allTrunks, leafyTrunks);

        BlockPos b3_3 = b3_2.above();
        placeLeafyBlock(level, b3_3, allTrunks, leafyTrunks);

        // 5. Cành 4: Tầng ngọn 1 (vươn sang mainDir)
        BlockPos b4_1 = t5.relative(mainDir);
        placeTrunkBlock(level, b4_1, allTrunks);

        BlockPos b4_2 = b4_1.above();
        placeLeafyBlock(level, b4_2, allTrunks, leafyTrunks);

        BlockPos b4_3 = b4_2.relative(mainDir);
        placeLeafyBlock(level, b4_3, allTrunks, leafyTrunks);

        // 6. Cành 5: Tầng ngọn 2 (vươn cao vút lên đỉnh trung tâm và sang oppDir)
        BlockPos b5_1 = t5.above();
        placeTrunkBlock(level, b5_1, allTrunks);

        BlockPos b5_2 = b5_1.relative(oppDir);
        placeLeafyBlock(level, b5_2, allTrunks, leafyTrunks);

        BlockPos b5_3 = b5_2.above();
        placeLeafyBlock(level, b5_3, allTrunks, leafyTrunks);

        // 7. Tán hoa lá quanh 5 đầu cành ngọn (khối lá thuần, không chứa trunk)
        BlockPos[] tips = new BlockPos[]{ b1_3, b2_3, b3_3, b4_3, b5_3 };
        for (BlockPos tip : tips) {
            placeLeafClusterAroundTip(level, tip, leafBlock, random);
        }

        // 8. Điểm xuyết hoa lá quanh các đốt cành giữa
        BlockPos[] midBranches = new BlockPos[]{ b1_2, b2_2, b3_2, b4_2, b5_2 };
        for (BlockPos mid : midBranches) {
            if (random.nextFloat() < 0.65f) {
                placePureLeafBlock(level, mid.above(), leafBlock);
            }
            Direction rDir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            if (random.nextFloat() < 0.55f) {
                placePureLeafBlock(level, mid.relative(rDir), leafBlock);
            }
        }

        // 9. Cập nhật kết nối và áp dụng LEAF
        finalizeTrunkConnections(level, allTrunks, leafyTrunks, leafBlock);

        // Rải 1-2 cánh hoa giấy dưới gốc
        for (int i = 0; i < 2; i++) {
            dropPetalAt(level, origin.offset(random.nextInt(3) - 1, 0, random.nextInt(3) - 1));
        }

        return true;
    }

    /**
     * Tìm hướng tường solid tiếp giáp trực tiếp với pos
     */
    private static Direction getAdjacentWallDirection(LevelAccessor level, BlockPos pos) {
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos check = pos.relative(dir);
            if (isValidSupport(level, check, level.getBlockState(check))) {
                return dir;
            }
        }
        return null;
    }

    private static void placeTrunkOrLeafy(LevelAccessor level, BlockPos pos, List<BlockPos> allTrunks, Set<BlockPos> leafyTrunks, boolean isLeafy) {
        if (isLeafy) {
            placeLeafyBlock(level, pos, allTrunks, leafyTrunks);
        } else {
            placeTrunkBlock(level, pos, allTrunks);
        }
    }

    /**
     * DẠNG 2: Hoa giấy leo giàn/tường Dynamic
     * Thân cây linh hoạt bám sát mọi cấu trúc tường: tường phẳng, tường góc bẻ hướng (lồi/lõm),
     * tường thụt ra/thụt vào, gờ tường, cột nhà... cành vươn phủ xanh tự nhiên.
     */
    private static boolean growClimbing(LevelAccessor level, BlockPos origin, Block leafBlock, Direction wallDir, RandomSource random) {
        List<BlockPos> allTrunks = new ArrayList<>();
        Set<BlockPos> leafyTrunks = new HashSet<>();
        List<BlockPos> tips = new ArrayList<>();

        // 1. Gốc cây tại vị trí sapling
        BlockPos currentPos = origin;
        placeTrunkBlock(level, currentPos, allTrunks);

        // Tiếp cận sát chân tường nếu sapling cách 1-2 block
        Direction currentWall = getAdjacentWallDirection(level, currentPos);
        if (currentWall == null) {
            BlockPos step1 = currentPos.relative(wallDir);
            if (isReplaceable(level, step1)) {
                placeTrunkBlock(level, step1, allTrunks);
                currentPos = step1;
                currentWall = getAdjacentWallDirection(level, currentPos);
            }
        }
        if (currentWall == null) {
            currentWall = wallDir;
        }

        int totalHeight = 4 + random.nextInt(3); // Cao 4-6 block
        int diagDir = random.nextBoolean() ? 1 : -1;

        for (int y = 1; y <= totalHeight; y++) {
            boolean isUpper = y >= 2;

            // BƯỚC 1: Leo lên theo chiều cao (Thích ứng vượt gờ tường hoặc tường thụt vào)
            BlockPos upPos = currentPos.above();
            if (isReplaceable(level, upPos)) {
                Direction upWall = getAdjacentWallDirection(level, upPos);
                if (upWall != null) {
                    currentWall = upWall;
                    currentPos = upPos;
                } else {
                    // Kiểm tra xem tường có bị thụt sâu vào trong (recessed niche / cửa sổ) không
                    BlockPos inward = upPos.relative(currentWall);
                    if (isReplaceable(level, inward) && getAdjacentWallDirection(level, inward) != null) {
                        placeTrunkOrLeafy(level, upPos, allTrunks, leafyTrunks, isUpper);
                        currentPos = inward;
                        currentWall = getAdjacentWallDirection(level, currentPos);
                    } else {
                        currentPos = upPos;
                    }
                }
            } else {
                // Phía trên bị cản bởi gờ nhô ra / mái hiên: bước nhô ra ngoài (outward) để vòng qua
                Direction outward = currentWall.getOpposite();
                BlockPos stepOut = currentPos.relative(outward);
                if (isReplaceable(level, stepOut)) {
                    placeTrunkOrLeafy(level, stepOut, allTrunks, leafyTrunks, isUpper);
                    currentPos = stepOut;
                    BlockPos aboveStep = currentPos.above();
                    if (isReplaceable(level, aboveStep)) {
                        currentPos = aboveStep;
                        Direction newWall = getAdjacentWallDirection(level, currentPos);
                        if (newWall != null) currentWall = newWall;
                    }
                }
            }
            placeTrunkOrLeafy(level, currentPos, allTrunks, leafyTrunks, isUpper);

            // BƯỚC 2: Bò ngang, zíc-zắc và bẻ góc tường (Wrapping Corner & Inside Corners)
            if (random.nextFloat() < 0.75f && currentWall != null) {
                Direction tangent = (diagDir > 0) ? currentWall.getClockWise() : currentWall.getCounterClockWise();
                BlockPos sidePos = currentPos.relative(tangent);

                if (isReplaceable(level, sidePos)) {
                    Direction sideWall = getAdjacentWallDirection(level, sidePos);
                    if (sideWall != null) {
                        // Tường phẳng tiếp tục hoặc có tường bên
                        currentPos = sidePos;
                        currentWall = sideWall;
                        placeTrunkOrLeafy(level, currentPos, allTrunks, leafyTrunks, isUpper);
                    } else {
                        // Tường bẻ góc 90 độ ra phía ngoài (góc cột / mép tường ngoài)
                        BlockPos wrapPos = sidePos.relative(currentWall);
                        if (isReplaceable(level, wrapPos) && getAdjacentWallDirection(level, wrapPos) != null) {
                            placeTrunkOrLeafy(level, sidePos, allTrunks, leafyTrunks, isUpper);
                            currentPos = wrapPos;
                            currentWall = getAdjacentWallDirection(level, currentPos);
                            placeTrunkOrLeafy(level, currentPos, allTrunks, leafyTrunks, isUpper);
                        }
                    }
                } else if (isValidSupport(level, sidePos, level.getBlockState(sidePos))) {
                    // Gặp góc trong (bị tường vuông góc chặn phía trước) -> Đổi hướng bám vào tường mới
                    currentWall = tangent;
                }
            }

            // Đổi hướng zíc-zắc định kỳ
            if (y % 2 == 0 && random.nextFloat() < 0.65f) {
                diagDir = -diagDir;
            }

            // BƯỚC 3: Phân cành phụ bám tường sang hai bên ở tầng trên
            if (y >= 2 && random.nextFloat() < 0.65f && currentWall != null) {
                int branchSide = (random.nextFloat() < 0.5f) ? 1 : -1;
                Direction branchTangent = (branchSide > 0) ? currentWall.getClockWise() : currentWall.getCounterClockWise();
                BlockPos b1 = currentPos.relative(branchTangent);

                if (isReplaceable(level, b1)) {
                    placeLeafyBlock(level, b1, allTrunks, leafyTrunks);

                    BlockPos b2 = b1.above();
                    if (isReplaceable(level, b2)) {
                        placeLeafyBlock(level, b2, allTrunks, leafyTrunks);

                        BlockPos b3 = b2.relative(branchTangent);
                        if (isReplaceable(level, b3) && random.nextFloat() < 0.65f) {
                            placeLeafyBlock(level, b3, allTrunks, leafyTrunks);
                            tips.add(b3);
                        } else {
                            tips.add(b2);
                        }
                    } else {
                        tips.add(b1);
                    }
                }
            }
        }
        tips.add(currentPos); // Ngọn chính

        // BƯỚC 4: Đặt chùm lá hoa nở quanh các đầu cành ngọn (khối lá thuần)
        for (BlockPos tip : tips) {
            placeLeafClusterAroundTip(level, tip, leafBlock, random);
        }

        // BƯỚC 5: Điểm xuyết hoa lá hướng ra không gian mở quanh thân leo (khối lá thuần)
        for (BlockPos trunk : new ArrayList<>(allTrunks)) {
            if (leafyTrunks.contains(trunk)) {
                for (Direction d : Direction.Plane.HORIZONTAL) {
                    BlockPos openSide = trunk.relative(d);
                    if (isReplaceable(level, openSide) && !isValidSupport(level, openSide, level.getBlockState(openSide))) {
                        if (random.nextFloat() < 0.40f) {
                            placePureLeafBlock(level, openSide, leafBlock);
                        }
                    }
                }
                if (isReplaceable(level, trunk.below()) && random.nextFloat() < 0.25f) {
                    placePureLeafBlock(level, trunk.below(), leafBlock);
                }
            }
        }

        // Cập nhật kết nối và áp dụng LEAF
        finalizeTrunkConnections(level, allTrunks, leafyTrunks, leafBlock);

        // Rải 1-2 cánh hoa dưới chân
        for (int i = 0; i < 2; i++) {
            dropPetalAt(level, origin.offset(random.nextInt(3) - 1, 0, random.nextInt(3) - 1));
        }

        return true;
    }

    private static void placeTrunkBlock(LevelAccessor level, BlockPos pos, List<BlockPos> allTrunks) {
        if (isReplaceable(level, pos) || level.getBlockState(pos).getBlock() instanceof net.huwng.vietnamdelight.block.BougainvilleaSaplingBlock) {
            level.setBlock(pos, VietnamDelightModBlocks.BOUGAINVILLEA_TRUNK.get().defaultBlockState(), 3);
            if (!allTrunks.contains(pos)) {
                allTrunks.add(pos);
            }
        }
    }

    private static void placeLeafyBlock(LevelAccessor level, BlockPos pos, List<BlockPos> allTrunks, Set<BlockPos> leafyTrunks) {
        if (isReplaceable(level, pos)) {
            level.setBlock(pos, VietnamDelightModBlocks.BOUGAINVILLEA_TRUNK.get().defaultBlockState(), 3);
            if (!allTrunks.contains(pos)) {
                allTrunks.add(pos);
            }
            leafyTrunks.add(pos);
        }
    }

    private static void placePureLeafBlock(LevelAccessor level, BlockPos pos, Block leafBlock) {
        if (isReplaceable(level, pos)) {
            BlockState state = leafBlock.defaultBlockState();
            if (state.hasProperty(LeavesBlock.PERSISTENT)) {
                state = state.setValue(LeavesBlock.PERSISTENT, false);
            }
            if (state.hasProperty(LeavesBlock.DISTANCE)) {
                state = state.setValue(LeavesBlock.DISTANCE, 1);
            }
            level.setBlock(pos, state, 3);
        }
    }

    private static void placeLeafClusterAroundTip(LevelAccessor level, BlockPos tip, Block leafBlock, RandomSource random) {
        // Khối phía trên đầu cành
        BlockPos above = tip.above();
        placePureLeafBlock(level, above, leafBlock);

        // Khối nhô cao hơn (40%)
        if (random.nextFloat() < 0.4f) {
            placePureLeafBlock(level, above.above(), leafBlock);
        }

        // Các hướng ngang quanh đầu cành
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            if (random.nextFloat() < 0.8f) {
                BlockPos side = tip.relative(dir);
                placePureLeafBlock(level, side, leafBlock);

                if (random.nextFloat() < 0.45f) {
                    placePureLeafBlock(level, side.above(), leafBlock);
                }
            }
        }
    }

    private static void finalizeTrunkConnections(LevelAccessor level, List<BlockPos> allTrunks, Set<BlockPos> leafyTrunks, Block leafBlock) {
        BougainvilleaTrunkBlock trunkBlock = (BougainvilleaTrunkBlock) VietnamDelightModBlocks.BOUGAINVILLEA_TRUNK.get();
        BougainvilleaTrunkBlock.LeafType leafType = BougainvilleaTrunkBlock.getLeafTypeFromBlock(leafBlock);

        for (BlockPos pos : allTrunks) {
            BlockState currentState = level.getBlockState(pos);
            if (currentState.getBlock() == trunkBlock) {
                boolean isLeafy = leafyTrunks.contains(pos);
                BlockState updated = currentState
                        .setValue(BougainvilleaTrunkBlock.LEAF, isLeafy ? leafType : BougainvilleaTrunkBlock.LeafType.NONE)
                        .setValue(BougainvilleaTrunkBlock.NORTH, trunkBlock.canConnect(level, pos, Direction.NORTH, level.getBlockState(pos.north())))
                        .setValue(BougainvilleaTrunkBlock.SOUTH, trunkBlock.canConnect(level, pos, Direction.SOUTH, level.getBlockState(pos.south())))
                        .setValue(BougainvilleaTrunkBlock.EAST, trunkBlock.canConnect(level, pos, Direction.EAST, level.getBlockState(pos.east())))
                        .setValue(BougainvilleaTrunkBlock.WEST, trunkBlock.canConnect(level, pos, Direction.WEST, level.getBlockState(pos.west())))
                        .setValue(BougainvilleaTrunkBlock.UP, trunkBlock.canConnect(level, pos, Direction.UP, level.getBlockState(pos.above())))
                        .setValue(BougainvilleaTrunkBlock.DOWN, trunkBlock.canConnect(level, pos, Direction.DOWN, level.getBlockState(pos.below())));

                level.setBlock(pos, updated, 3);
            }
        }
    }

    private static void dropPetalAt(LevelAccessor level, BlockPos pos) {
        BlockPos check = pos;
        for (int i = 0; i < 4; i++) {
            BlockPos below = check.below();
            BlockState belowState = level.getBlockState(below);
            if (level.getBlockState(check).isAir() && belowState.isFaceSturdy(level, below, Direction.UP)) {
                level.setBlock(check, VietnamDelightModBlocks.BOUGAINVILLEA.get().defaultBlockState(), 3);
                return;
            }
            check = below;
        }
    }
}
