package net.huwng.vietnamdelight.world.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.server.level.ServerLevel;
import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;

import java.util.ArrayList;
import java.util.List;

// Cây phượng vàng: dùng chung khung thân/cành Acacia và lá xanh (Flamboyant Leaves)
// với cây phượng đỏ, chỉ khác ở hoa và cánh hoa nở rộ mang tông vàng.
public class GoldenFlamboyantTreeFeature extends Feature<NoneFeatureConfiguration> {

    public GoldenFlamboyantTreeFeature() {
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

        // ===== GIAI ĐOẠN 1: dựng toàn bộ khung cây (thân + cành) trước =====
        // Khung cây luôn được đặt cứng (ghi đè), lá chỉ đặt sau vào chỗ trống,
        // nên log sẽ không bao giờ bị lá "nuốt mất" nữa.

        // 1. Thân chính: Acacia Log cao 2-4 block, block trên cùng đổi thành Acacia Wood
        int trunkHeight = 2 + random.nextInt(3); // 2-4
        List<BlockPos> woodPositions = new ArrayList<>(); // toàn bộ vị trí gỗ (thân + cành), dùng để gắn ve sầu
        BlockPos trunkTopWood = generateTrunk(world, pos, trunkHeight, random, woodPositions);

        List<BlockPos> smallClusterAnchors = new ArrayList<>();
        List<BlockPos> canopyAnchors = new ArrayList<>();
        List<BlockPos> branchNestCandidates = new ArrayList<>(); // các điểm cành có thể gắn tổ ong

        // Gốc thân cây (chỉ lấy toạ độ ngang X/Z) dùng làm tâm tham chiếu,
        // để mọi cành sinh ra sau đều được lọc theo hướng "đi ra xa dần" khỏi tâm.
        BlockPos trunkOrigin = pos;

        // 2. Sinh cành lớn từ đỉnh thân chính
        List<BlockPos> bigBranchPositions = pickBranchPositions(trunkTopWood, random, trunkOrigin, world);
        for (BlockPos branchBase : bigBranchPositions) {
            BlockPos bigBranchTop = growBranch(world, branchBase, 1 + random.nextInt(3), woodPositions); // 1-3 block
            branchNestCandidates.add(bigBranchTop);

            // 3. Sinh cành giữa (mới) từ đỉnh mỗi cành lớn
            List<BlockPos> middlePositions = pickBranchPositions(bigBranchTop, random, trunkOrigin, world);
            for (BlockPos middleBase : middlePositions) {
                BlockPos middleTop = growBranch(world, middleBase, 1 + random.nextInt(2), woodPositions); // 1-2 block
                branchNestCandidates.add(middleTop);

                // 4. Sinh cành phụ từ đỉnh mỗi cành giữa
                List<BlockPos> secondaryPositions = pickBranchPositions(middleTop, random, trunkOrigin, world);
                for (BlockPos secondaryBase : secondaryPositions) {
                    BlockPos secondaryTop = growBranch(world, secondaryBase, 1 + random.nextInt(2), woodPositions); // 1-2 block
                    branchNestCandidates.add(secondaryTop);

                    if (random.nextFloat() < 0.45f) {
                        // Kết thúc luôn bằng tán lá chính tại đây
                        canopyAnchors.add(secondaryTop);
                    } else {
                        // Cụm lá nhỏ tại đây, rồi tiếp tục sinh cành lá
                        smallClusterAnchors.add(secondaryTop);

                        // 5. Cành lá: một nhánh Acacia Log cao 2-3, đỉnh đổi thành Acacia Wood
                        BlockPos leafBranchTop = growBranch(world, secondaryTop, 2 + random.nextInt(2), woodPositions); // 2-3 block
                        branchNestCandidates.add(leafBranchTop);

                        // 6. Tán lá chính tại điểm cuối cành lá
                        canopyAnchors.add(leafBranchTop);
                    }
                }
            }
        }

        // ===== Tổ ong: cả cây chỉ có 30% cơ hội mọc ĐÚNG 1 tổ ong (có sẵn ong bên trong) =====
        if (!branchNestCandidates.isEmpty() && random.nextFloat() < 0.3f) {
            placeSingleBeeNest(world, branchNestCandidates, random);
        }

        // ===== Ve sầu (Cicadas): gắn 3-6 con lên các mặt bên có gỗ (thân/cành), không bao giờ gắn lên lá =====
        placeCicadas(world, woodPositions, random);

        // ===== GIAI ĐOẠN 2: phủ lá, chỉ đặt vào ô trống nên không đè lên khung cành =====
        // Hoa KHÔNG được đặt ngay trong lúc rải lá: mọi vị trí "ứng viên hoa" chỉ được
        // gom lại vào flowerCandidates, để đảm bảo lá của toàn bộ cây (mọi tán, mọi lớp)
        // được đặt xong hết trước, tránh trường hợp hoa "chiếm" mất 1 ô lẽ ra sẽ có lá
        // (do lá ở tán/lớp khác chưa kịp sinh ra) rồi sau đó lá khác lại không thể đè lên hoa.
        List<BlockPos> flowerCandidates = new ArrayList<>();
        for (BlockPos anchor : smallClusterAnchors) {
            generateSmallLeafCluster(world, anchor, random);
        }
        for (BlockPos anchor : canopyAnchors) {
            generateUmbrellaCanopy(world, anchor, random, flowerCandidates);
        }

        // ===== GIAI ĐOẠN 2.5: đặt hoa sau cùng, khi toàn bộ lá đã cố định =====
        // Nhờ đặt sau khi mọi lá đã có mặt, hàm placeFlowerAround chỉ còn tìm được các ô
        // KHÔNG KHÍ thật sự (mặt ngoài tán lá), nên hoa không bao giờ đè lên block lá nào nữa.
        for (BlockPos leafPos : flowerCandidates) {
            placeFlowerAround(world, leafPos, random);
        }

        // ===== GIAI ĐOẠN 3: rải thật nhiều cánh hoa (petal) xuống nền đất bên dưới cả tán cây =====
        scatterPetalsBelowCanopy(world, canopyAnchors, random);

        return true;
    }

    private boolean canPlaceTree(WorldGenLevel world, BlockPos pos) {
        BlockState belowState = world.getBlockState(pos.below());
        return belowState.is(Blocks.GRASS_BLOCK) || belowState.is(Blocks.DIRT);
    }

    // Thân chính + rễ nổi ở gốc
    // Khung thân luôn ghi đè cứng, không kiểm tra isAir, để không bao giờ bị lá chiếm chỗ trước.
    private BlockPos generateTrunk(WorldGenLevel world, BlockPos pos, int height, RandomSource random, List<BlockPos> woodPositions) {
        BlockState log = VietnamDelightModBlocks.FLAMBOYANT_LOG.get().defaultBlockState();

        for (int y = 0; y < height; y++) {
            BlockPos trunkPos = pos.above(y);
            world.setBlock(trunkPos, log, 3);
            woodPositions.add(trunkPos);

            if (y == 0) {
                for (Direction dir : Direction.Plane.HORIZONTAL) {
                    BlockPos woodPos = trunkPos.relative(dir);
                    if (world.getBlockState(woodPos).isAir()) {
                        world.setBlock(woodPos, VietnamDelightModBlocks.FLAMBOYANT_WOOD.get().defaultBlockState(), 3);
                        woodPositions.add(woodPos);
                        BlockPos belowWoodPos = woodPos.below();
                        if (world.getBlockState(belowWoodPos).isAir()) {
                            world.setBlock(belowWoodPos, VietnamDelightModBlocks.FLAMBOYANT_WOOD.get().defaultBlockState(), 3);
                            woodPositions.add(belowWoodPos);
                        }
                    }
                }
            }
        }

        // Block trên cùng của thân chính là điểm sinh cành
        BlockPos topPos = pos.above(height - 1);
        world.setBlock(topPos, VietnamDelightModBlocks.FLAMBOYANT_WOOD.get().defaultBlockState(), 3);
        woodPositions.add(topPos);
        return topPos;
    }

    // Khoảng cách ngang bình phương (chỉ X/Z) từ 1 điểm tới gốc thân cây
    private long horizontalDistSqr(BlockPos point, BlockPos trunkOrigin) {
        long dx = point.getX() - trunkOrigin.getX();
        long dz = point.getZ() - trunkOrigin.getZ();
        return dx * dx + dz * dz;
    }

    // Kiểm tra xem quanh 1 vị trí ứng viên (trong bán kính "checkRadius", cả chiều dọc lẫn ngang)
    // đã có sẵn gỗ/log của MỘT CÀNH KHÁC (đã dựng xong trước đó ở phase 1) hay chưa.
    private boolean hasNearbyBranchWood(WorldGenLevel world, BlockPos candidate, BlockPos exclude, int checkRadius) {
        for (int dx = -checkRadius; dx <= checkRadius; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -checkRadius; dz <= checkRadius; dz++) {
                    BlockPos check = candidate.offset(dx, dy, dz);
                    if (check.equals(exclude) || check.equals(candidate)) continue;
                    BlockState state = world.getBlockState(check);
                    if (state.is(VietnamDelightModBlocks.FLAMBOYANT_LOG.get()) || state.is(VietnamDelightModBlocks.FLAMBOYANT_WOOD.get())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Chọn 1-3 vị trí trong phạm vi 3x3 quanh 1 điểm, loại trừ tâm, cách nhau tối thiểu 1 block.
    // Áp dụng 2 lớp lọc:
    // 1) Chỉ giữ lại các vị trí có khoảng cách ngang tới gốc thân (trunkOrigin) LỚN HƠN HOẶC BẰNG
    //    khoảng cách của "center" hiện tại, để cành luôn mọc toả ra xa dần, không vòng lại gần thân.
    // 2) Loại các vị trí đã có gỗ/log của một nhánh khác (đã dựng trước đó) nằm gần đó, để các
    //    cành tự né nhau, tạo khoảng hở thay vì dính sát vào nhau.
    private List<BlockPos> pickBranchPositions(BlockPos center, RandomSource random, BlockPos trunkOrigin, WorldGenLevel world) {
        List<BlockPos> candidates = new ArrayList<>();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) continue; // không chọn vị trí trung tâm
                candidates.add(center.offset(dx, 0, dz));
            }
        }

        // Xáo trộn thủ công (Fisher-Yates) bằng RandomSource
        for (int i = candidates.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            BlockPos tmp = candidates.get(i);
            candidates.set(i, candidates.get(j));
            candidates.set(j, tmp);
        }

        // Lớp lọc 1: chỉ giữ các vị trí đi ra xa (hoặc bằng) so với gốc thân cây
        long centerDistSqr = horizontalDistSqr(center, trunkOrigin);
        List<BlockPos> outwardOnly = new ArrayList<>();
        for (BlockPos candidate : candidates) {
            if (horizontalDistSqr(candidate, trunkOrigin) >= centerDistSqr) {
                outwardOnly.add(candidate);
            }
        }
        List<BlockPos> outwardPool = outwardOnly.isEmpty() ? candidates : outwardOnly;

        // Lớp lọc 2: loại các vị trí đã có gỗ của nhánh khác gần đó (né va chạm, tạo khoảng hở)
        List<BlockPos> noCollisionPool = new ArrayList<>();
        for (BlockPos candidate : outwardPool) {
            if (!hasNearbyBranchWood(world, candidate, center, 1)) {
                noCollisionPool.add(candidate);
            }
        }
        List<BlockPos> finalPool = noCollisionPool.isEmpty() ? outwardPool : noCollisionPool;

        int wanted = 1 + random.nextInt(3); // 1-3 vị trí
        List<BlockPos> selected = new ArrayList<>();
        for (BlockPos candidate : finalPool) {
            if (selected.size() >= wanted) break;

            boolean tooClose = false;
            for (BlockPos sel : selected) {
                if (sel.distSqr(candidate) < 5) { // tăng khoảng cách tối thiểu giữa các cành cùng lớp, tạo hở rõ hơn
                    tooClose = true;
                    break;
                }
            }
            if (!tooClose) {
                selected.add(candidate);
            }
        }
        return selected;
    }

    // Đặt Acacia Wood tại gốc cành, mọc Acacia Log lên cao "height" block, block trên cùng đổi thành Acacia Wood.
    // Khung cành luôn ghi đè cứng (không kiểm tra isAir) để đảm bảo cành luôn liền mạch,
    // không bị lá của cành khác (đã sinh trước đó) chiếm mất chỗ.
    private BlockPos growBranch(WorldGenLevel world, BlockPos base, int height, List<BlockPos> woodPositions) {
        world.setBlock(base, VietnamDelightModBlocks.FLAMBOYANT_WOOD.get().defaultBlockState(), 3);
        woodPositions.add(base);

        BlockPos current = base;
        for (int i = 0; i < height; i++) {
            current = current.above();
            world.setBlock(current, VietnamDelightModBlocks.FLAMBOYANT_LOG.get().defaultBlockState(), 3);
            woodPositions.add(current);
        }

        world.setBlock(current, VietnamDelightModBlocks.FLAMBOYANT_WOOD.get().defaultBlockState(), 3);
        woodPositions.add(current);
        return current;
    }

    // Cụm lá nhỏ tại điểm cành phụ tiếp tục sinh cành lá (đã thu nhỏ bán kính so với trước)
    private void generateSmallLeafCluster(WorldGenLevel world, BlockPos center, RandomSource random) {
        int radius = 1;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double dist = Math.sqrt(x * x + y * y * 2.0 + z * z);
                    if (dist > radius + random.nextFloat() * 0.25) continue;

                    BlockPos leafPos = center.offset(x, y, z);
                    if (!world.getBlockState(leafPos).isAir()) continue;

                    placeLeaf(world, leafPos, random);
                }
            }
        }
    }

    // Tán lá chính hình chiếc ô: lớp giữa bán kính 4, lớp trên nhỏ hơn bán kính 2, lớp dưới nhỏ bán kính 1
    // flowerCandidates: gom các vị trí lá "Flowering" đã trúng roll để CÓ THỂ mọc hoa quanh đó -
    // hoa chưa được đặt ở đây, chỉ đặt sau khi toàn bộ lá của cả cây đã xong (xem place()).
    private void generateUmbrellaCanopy(WorldGenLevel world, BlockPos center, RandomSource random, List<BlockPos> flowerCandidates) {
        List<BlockPos> petalDropPositions = new ArrayList<>();

        generateLeafDisc(world, center, 3, random, petalDropPositions, flowerCandidates);
        generateLeafDisc(world, center.above(), 2, random, petalDropPositions, flowerCandidates);
        generateLeafDisc(world, center.below(), 1, random, petalDropPositions, flowerCandidates);

        // Rải vài cánh hoa xuống nền đất bên dưới tán đang nở rộ
        for (BlockPos leafPos : petalDropPositions) {
            if (random.nextFloat() < 0.5f) {
                dropPetalsBelow(world, leafPos);
            }
        }
    }

    private void generateLeafDisc(WorldGenLevel world, BlockPos center, int radius, RandomSource random, List<BlockPos> petalDropPositions, List<BlockPos> flowerCandidates) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                double dist = Math.sqrt(x * x + z * z);
                if (dist > radius + random.nextFloat() * 0.5) continue;

                BlockPos leafPos = center.offset(x, 0, z);
                if (!world.getBlockState(leafPos).isAir()) continue;

                BlockState leafBlock = placeLeaf(world, leafPos, random);

                if (leafBlock.is(VietnamDelightModBlocks.BLOOMING_GOLDEN_FLAMBOYANT_LEAVES.get())) {
                    petalDropPositions.add(leafPos);
                } else if (leafBlock.is(VietnamDelightModBlocks.FLOWERING_GOLDEN_FLAMBOYANT_LEAVES.get())
                        && random.nextFloat() < 0.4f) {
                    // Chỉ ghi nhận ứng viên, KHÔNG đặt hoa ngay - tránh chiếm chỗ của lá
                    // thuộc lớp/tán khác chưa kịp sinh ra ở thời điểm này.
                    flowerCandidates.add(leafPos);
                }
            }
        }
    }

    private BlockState placeLeaf(WorldGenLevel world, BlockPos leafPos, RandomSource random) {
        BlockState leafBlock = getRandomFlamboyantLeaves(random);
        if (leafBlock.hasProperty(BlockStateProperties.PERSISTENT)) {
            leafBlock = leafBlock.setValue(BlockStateProperties.PERSISTENT, false);
        }
        if (leafBlock.hasProperty(BlockStateProperties.DISTANCE)) {
            leafBlock = leafBlock.setValue(BlockStateProperties.DISTANCE, 4);
        }
        world.setBlock(leafPos, leafBlock, 3);
        return leafBlock;
    }

    // Hoa phượng vàng có 6 mặt (FACING dùng cả 6 hướng: lên/xuống/4 hướng ngang),
    // nên hoa có thể mọc ở bất kỳ mặt nào quanh lá, không chỉ 4 hướng ngang.
    // Xáo trộn cả 6 hướng rồi thử lần lượt tới khi tìm được chỗ trống để đặt hoa,
    // tránh trường hợp random 1 lần trúng ngay hướng đã có block nên không đặt được gì.
    private void placeFlowerAround(WorldGenLevel world, BlockPos leafPos, RandomSource random) {
        Direction[] directions = Direction.values().clone();
        for (int i = directions.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Direction tmp = directions[i];
            directions[i] = directions[j];
            directions[j] = tmp;
        }

        for (Direction dir : directions) {
            BlockPos flowerPos = leafPos.relative(dir);
            if (world.getBlockState(flowerPos).isAir()) {
                // FACING = hướng từ lá (bề mặt bám) ra ngoài phía hoa, khớp với canAttachTo
                // trong GoldenFlamboyantFlowerBlock (attachPos = flowerPos.relative(facing.getOpposite())).
                BlockState flowerState = VietnamDelightModBlocks.GOLDEN_FLAMBOYANT_FLOWER.get()
                        .defaultBlockState()
                        .setValue(net.huwng.vietnamdelight.block.GoldenFlamboyantFlowerBlock.FACING, dir);
                world.setBlock(flowerPos, flowerState, 3);
                return;
            }
        }
    }

    // Chỉ đặt ĐÚNG 1 tổ ong cho cả cây: xáo trộn danh sách các điểm cành ứng viên,
    // thử từng điểm cho tới khi tìm được 1 mặt bên (hướng ngang) đang trống để gắn tổ ong.
    // Ngay khi đặt thành công, dừng luôn (không thử thêm điểm khác) để đảm bảo cả cây
    // chỉ có duy nhất 1 tổ ong.
    private void placeSingleBeeNest(WorldGenLevel world, List<BlockPos> branchCandidates, RandomSource random) {
        List<BlockPos> shuffledBranches = new ArrayList<>(branchCandidates);
        for (int i = shuffledBranches.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            BlockPos tmp = shuffledBranches.get(i);
            shuffledBranches.set(i, shuffledBranches.get(j));
            shuffledBranches.set(j, tmp);
        }

        Direction[] horizontalDirs = Direction.Plane.HORIZONTAL.stream().toArray(Direction[]::new);

        for (BlockPos branchPos : shuffledBranches) {
            Direction[] directions = horizontalDirs.clone();
            for (int i = directions.length - 1; i > 0; i--) {
                int j = random.nextInt(i + 1);
                Direction tmp = directions[i];
                directions[i] = directions[j];
                directions[j] = tmp;
            }

            for (Direction dir : directions) {
                BlockPos nestPos = branchPos.relative(dir);
                if (!world.getBlockState(nestPos).isAir()) continue;

                BlockState nestState = Blocks.BEE_NEST.defaultBlockState()
                        .setValue(BeehiveBlock.FACING, dir)
                        .setValue(BeehiveBlock.HONEY_LEVEL, random.nextInt(3)); // 0-2 mật ban đầu
                world.setBlock(nestPos, nestState, 3);

                populateBeeNest(world, nestPos, random);
                return; // đã đặt xong 1 tổ ong duy nhất, không tìm thêm nữa
            }
        }
    }

    // Nhồi 1-3 con ong vào bên trong tổ ong vừa đặt (giống cơ chế "occupant" của BeehiveBlockEntity),
    // để tổ ong không bị trống khi người chơi phá ra.
    private void populateBeeNest(WorldGenLevel world, BlockPos nestPos, RandomSource random) {
        if (!(world.getBlockEntity(nestPos) instanceof BeehiveBlockEntity beehive)) return;

        ServerLevel serverLevel = world.getLevel();
        int beeCount = 1 + random.nextInt(3); // 1-3 con ong bên trong
        for (int i = 0; i < beeCount; i++) {
            Bee bee = EntityType.BEE.create(serverLevel);
            if (bee == null) continue;
            bee.moveTo(nestPos.getX() + 0.5, nestPos.getY() + 0.5, nestPos.getZ() + 0.5, 0.0F, 0.0F);
            beehive.addOccupant(bee, false);
        }
    }

    // Gắn 3-10 con ve sầu (Cicadas) lên các mặt bên (4 hướng ngang) của thân/cành CÓ GỖ
    // (Acacia Log/Wood) - không bao giờ gắn lên lá. Xáo trộn danh sách vị trí gỗ rồi thử
    // từng vị trí, mỗi vị trí xáo trộn tiếp 4 hướng ngang để tìm 1 mặt đang trống, gắn ve sầu
    // vào đó rồi chuyển sang vị trí gỗ khác, dừng khi đã đủ số lượng mong muốn.
    // Mỗi con ve sầu được roll ngẫu nhiên 1 trong các varient: normal/couple/nerd/rich/sad.
    private void placeCicadas(WorldGenLevel world, List<BlockPos> woodPositions, RandomSource random) {
        if (woodPositions.isEmpty()) return;

        List<BlockPos> shuffledWood = new ArrayList<>(woodPositions);
        for (int i = shuffledWood.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            BlockPos tmp = shuffledWood.get(i);
            shuffledWood.set(i, shuffledWood.get(j));
            shuffledWood.set(j, tmp);
        }

        Direction[] horizontalDirs = Direction.Plane.HORIZONTAL.stream().toArray(Direction[]::new);
        int wanted = 3 + random.nextInt(8); // 3-10 con
        int placed = 0;

        for (BlockPos woodPos : shuffledWood) {
            if (placed >= wanted) break;

            BlockState woodState = world.getBlockState(woodPos);
            if (!(woodState.is(VietnamDelightModBlocks.FLAMBOYANT_LOG.get()) || woodState.is(VietnamDelightModBlocks.FLAMBOYANT_WOOD.get()))) continue;

            Direction[] directions = horizontalDirs.clone();
            for (int i = directions.length - 1; i > 0; i--) {
                int j = random.nextInt(i + 1);
                Direction tmp = directions[i];
                directions[i] = directions[j];
                directions[j] = tmp;
            }

            for (Direction dir : directions) {
                BlockPos cicadaPos = woodPos.relative(dir);
                if (!world.getBlockState(cicadaPos).isAir()) continue;

                // FACING = hướng từ mặt gỗ (bề mặt bám) ra ngoài phía ve sầu, khớp với canAttachTo
                // trong CicadasBlock (attachPos = cicadaPos.relative(facing.getOpposite())).
                BlockState cicadaState = VietnamDelightModBlocks.CICADAS.get()
                        .defaultBlockState()
                        .setValue(net.huwng.vietnamdelight.block.CicadasBlock.FACING, dir)
                        .setValue(net.huwng.vietnamdelight.block.CicadasBlock.VARIANT, rollCicadaVariant(random));
                world.setBlock(cicadaPos, cicadaState, 3);
                placed++;
                break;
            }
        }
    }

    // Roll ngẫu nhiên varient cho 1 con ve sầu sinh ra lúc worldgen: phần lớn là normal,
    // còn lại rải đều các varient đặc biệt (couple/nerd/rich/sad).
    private net.huwng.vietnamdelight.block.CicadasBlock.Variant rollCicadaVariant(RandomSource random) {
        float roll = random.nextFloat();
        if (roll < 0.10f) return net.huwng.vietnamdelight.block.CicadasBlock.Variant.COUPLE;
        if (roll < 0.25f) return net.huwng.vietnamdelight.block.CicadasBlock.Variant.NERD;
        if (roll < 0.35f) return net.huwng.vietnamdelight.block.CicadasBlock.Variant.RICH;
        if (roll < 0.45f) return net.huwng.vietnamdelight.block.CicadasBlock.Variant.SAD;
        return net.huwng.vietnamdelight.block.CicadasBlock.Variant.NORMAL;
    }

    // Rải một "tấm thảm" cánh hoa dày đặc trên nền đất bên dưới toàn bộ tán ô (umbrella canopy),
    // thay vì chỉ rải lác đác từ vài lá đang nở rộ như trước.
    private void scatterPetalsBelowCanopy(WorldGenLevel world, List<BlockPos> canopyAnchors, RandomSource random) {
        int radius = 4; // khớp với bán kính lớp lá ngoài cùng của tán ô (generateLeafDisc bán kính 3-4)
        for (BlockPos anchor : canopyAnchors) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    double dist = Math.sqrt(x * x + z * z);
                    if (dist > radius) continue;

                    // Càng gần tâm tán càng nhiều hoa rơi, càng ra rìa càng thưa dần
                    float dropChance = 0.6f - (float) (dist / radius) * 0.35f;
                    if (random.nextFloat() < dropChance) {
                        dropPetalsBelow(world, anchor.offset(x, 0, z));
                    }
                }
            }
        }
    }

    private void dropPetalsBelow(WorldGenLevel world, BlockPos leafPos) {
        BlockPos checkPos = leafPos.below();
        for (int i = 0; i < 24; i++) {
            BlockState belowState = world.getBlockState(checkPos);
            if (belowState.isAir()) {
                checkPos = checkPos.below();
                continue;
            }
            BlockPos groundPos = checkPos.above();
            if (world.getBlockState(groundPos).isAir() && belowState.isFaceSturdy(world, checkPos, Direction.UP)) {
                world.setBlock(groundPos, VietnamDelightModBlocks.GOLDEN_FLAMBOYANT_PETALS.get().defaultBlockState(), 3);
            }
            return;
        }
    }

    // Lá cây phượng vàng giờ luôn là 1 trong 2 trạng thái glow: nở rộ (Blooming)
    // hoặc ra hoa (Flowering) -- đã bỏ trạng thái lá xanh thường (Flamboyant Leaves,
    // dùng chung với cây phượng đỏ) theo yêu cầu, giữ nguyên tỉ lệ tương đối
    // 15:30 (~1:2) giữa 2 loại còn lại.
    private BlockState getRandomFlamboyantLeaves(RandomSource random) {
        float roll = random.nextFloat();
        try {
            if (roll < 0.45f && VietnamDelightModBlocks.BLOOMING_GOLDEN_FLAMBOYANT_LEAVES.get() != null) {
                return VietnamDelightModBlocks.BLOOMING_GOLDEN_FLAMBOYANT_LEAVES.get().defaultBlockState();
            } else if (VietnamDelightModBlocks.FLOWERING_GOLDEN_FLAMBOYANT_LEAVES.get() != null) {
                return VietnamDelightModBlocks.FLOWERING_GOLDEN_FLAMBOYANT_LEAVES.get().defaultBlockState();
            }
        } catch (Exception e) {
            // Silent fallback
        }
        return Blocks.ACACIA_LEAVES.defaultBlockState();
    }
}
