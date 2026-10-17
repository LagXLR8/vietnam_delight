package net.huwng.vietnamdelight.block;

import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;

import org.jetbrains.annotations.Nullable;

import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;
import net.huwng.vietnamdelight.init.VietnamDelightModItems;
import net.huwng.vietnamdelight.block.entity.CicadasBlockEntity;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Block "Cicadas" (ve sầu) - bám lên mặt bên của block khác, tương tự FlamboyantFlowerBlock:
 * có va chạm (hitbox 6x9x3), không chiếm sáng, hiển thị dạng model phẳng gắn vào bề mặt (facing).
 * Ngoài chỗ bám tự nhiên trên gỗ (Acacia Log/Wood) do worldgen sinh ra, người chơi vẫn có thể
 * đặt thủ công lên bất kỳ mặt vững chắc nào hoặc lên lá phượng, giống cách hoa phượng hoạt động.
 * Thỉnh thoảng phát ra tiếng kêu ambient (entity.bee.loop).
 *
 * ===== Cơ chế Varient =====
 * - NORMAL: varient mặc định.
 *   + Chuột phải bằng Sách (Book)               -> chuyển thành NERD.
 *   + Chuột phải bằng Cục Vàng (Golden Nugget)   -> chuyển thành RICH (kèm particle happy_villager).
 * - COUPLE (couple_cicadas):
 *   + Chuột phải bằng Hoa Phượng / Hoa Phượng Vàng (không sneak):
 *       - Nếu đang cooldown (5 phút)  -> particle angry_villager (không báo thời gian còn lại).
 *       - Nếu hết cooldown và sinh ra được ve sầu con quanh cây phượng (Acacia) gần đó
 *         -> particle heart + nhả ra 1 ít exp.
 *   + Shift + Chuột phải (bất kỳ vật phẩm nào) -> rơi ra 1 item Cicadas, block chuyển thành SAD.
 *   + Khi bị đập vỡ -> rơi ra 2 item Cicadas (xem loot_tables/blocks/cicadas.json).
 * - NERD: Chuột phải (bất kỳ) -> hiện dòng chữ "bzzzz bzz bzzzz..." ngẫu nhiên độ dài lên action bar.
 * - RICH: Cho ăn (chuột phải) Hoa Phượng Vàng -> nhả ra ngẫu nhiên 1 Cục Vàng HOẶC 1 Cục Sắt
 *   (Gold/Iron Nugget), kèm particle happy_villager.
 * - SAD: thỉnh thoảng nhỏ giọt nước (particle dripping_water).
 *   + Cho ăn (chuột phải) Hoa Phượng (đỏ hoặc vàng) -> chuyển thành HAPPY.
 * - HAPPY: không có tương tác chuột phải; cứ 1-5 phút lại tự bonemeal 1 crop ngẫu nhiên
 *   trong khu vực ~8x8 quanh nó (xem getTicker()/tickHappyCicadas()).
 */
public class CicadasBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    public static final EnumProperty<Variant> VARIANT = EnumProperty.create("variant", Variant.class);

    /** Thời gian cooldown giữa 2 lần couple_cicadas sinh ve sầu con: 5 phút = 5*60*20 tick. */
    private static final long BREED_COOLDOWN_TICKS = 5 * 60 * 20L;
    /** Bán kính (block) quét tìm cành cây phượng (Acacia Log/Wood) quanh couple_cicadas để gắn ve sầu con. */
    private static final int BREED_SEARCH_RADIUS = 6;
    /** happy_cicadas: 1-5 phút = 1200-6000 tick giữa 2 lần thử bonemeal ngẫu nhiên. */
    private static final int HAPPY_ACTION_MIN_TICKS = 1 * 60 * 20;
    private static final int HAPPY_ACTION_RANGE_TICKS = 4 * 60 * 20; // 1 + [0,4) phút => 1-5 phút
    /** happy_cicadas: bán kính ngang quét crop để bonemeal, ~ khu vực 8x8. */
    private static final int HAPPY_BONEMEAL_RADIUS = 4;

    // {thickness, widthMin, widthMax, heightMin, heightMax} tính theo hệ 0-16, dùng để dựng hitbox theo facing.
    private static final Map<Variant, float[]> VARIANT_BOUNDS = new EnumMap<>(Variant.class);
    static {
        VARIANT_BOUNDS.put(Variant.NORMAL, new float[]{3, 5, 11, 3, 12});
        VARIANT_BOUNDS.put(Variant.NERD, new float[]{3, 5, 11, 3, 12});
        VARIANT_BOUNDS.put(Variant.RICH, new float[]{3, 5, 11, 3, 12});
        VARIANT_BOUNDS.put(Variant.SAD, new float[]{3, 5, 11, 3, 12});
        VARIANT_BOUNDS.put(Variant.HAPPY, new float[]{3, 5, 11, 3, 12});
        VARIANT_BOUNDS.put(Variant.COUPLE, new float[]{3, 1, 15, 0, 16});
    }

    public CicadasBlock() {
        super(BlockBehaviour.Properties.of().sound(SoundType.HONEY_BLOCK).instabreak().noOcclusion().isRedstoneConductor((bs, br, bp) -> false));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(VARIANT, Variant.NORMAL));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CicadasBlockEntity(pos, state);
    }

    // happy_cicadas: cứ 1-5 phút lại thử bonemeal 1 crop ngẫu nhiên trong khu vực ~8x8 quanh nó.
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) return null;
        return (lvl, pos, st, blockEntity) -> {
            if (blockEntity instanceof CicadasBlockEntity cicadasBE) {
                tickHappyCicadas(lvl, pos, st, cicadasBE);
            }
        };
    }

    private static void tickHappyCicadas(Level level, BlockPos pos, BlockState state, CicadasBlockEntity blockEntity) {
        if (state.getValue(VARIANT) != Variant.HAPPY) return;
        if (!(level instanceof ServerLevel serverLevel)) return;

        long gameTime = level.getGameTime();
        if (gameTime < blockEntity.getNextHappyActionTime()) return;

        RandomSource random = level.getRandom();
        boneMealRandomCropNearby(serverLevel, pos, random);

        blockEntity.setNextHappyActionTime(gameTime + HAPPY_ACTION_MIN_TICKS + random.nextInt(HAPPY_ACTION_RANGE_TICKS));
        blockEntity.setChanged();
    }

    /** Quét khu vực ~8x8 quanh happy_cicadas, chọn ngẫu nhiên 1 crop hợp lệ rồi bonemeal nó. */
    private static void boneMealRandomCropNearby(ServerLevel level, BlockPos center, RandomSource random) {
        int r = HAPPY_BONEMEAL_RADIUS;
        List<BlockPos> cropPositions = new ArrayList<>();
        BlockPos.betweenClosed(center.offset(-r, -r, -r), center.offset(r, r, r)).forEach(p -> {
            BlockState cropState = level.getBlockState(p);
            if (cropState.getBlock() instanceof BonemealableBlock bonemealable
                    && bonemealable.isValidBonemealTarget(level, p, cropState, false)) {
                cropPositions.add(p.immutable());
            }
        });
        if (cropPositions.isEmpty()) return;

        BlockPos cropPos = cropPositions.get(random.nextInt(cropPositions.size()));
        BlockState cropState = level.getBlockState(cropPos);
        if (cropState.getBlock() instanceof BonemealableBlock bonemealable
                && bonemealable.isBonemealSuccess(level, random, cropPos, cropState)) {
            bonemealable.performBonemeal(level, random, cropPos, cropState);
        }
        // Hiệu ứng hạt bột xương giống vanilla (LevelEvent 1505)
        level.levelEvent(1505, cropPos, 0);
    }

    /** Bắn 1 chùm particle tại vị trí cicadas, chỉ chạy phía server (broadcast cho người chơi gần đó). */
    private void spawnParticlesAt(Level level, BlockPos pos, ParticleOptions particle, int count) {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(particle, pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5, count, 0.25, 0.25, 0.25, 0.01);
        }
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 0;
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        float[] bounds = VARIANT_BOUNDS.getOrDefault(state.getValue(VARIANT), VARIANT_BOUNDS.get(Variant.NORMAL));
        float t = bounds[0], wMin = bounds[1], wMax = bounds[2], hMin = bounds[3], hMax = bounds[4];
        // Hitbox gắn vào mặt bám (facing), kích thước tuỳ theo varient (couple to hơn hẳn các varient khác).
        return switch (state.getValue(FACING)) {
            default -> box(wMin, hMin, 0, wMax, hMax, t);
            case NORTH -> box(wMin, hMin, 16 - t, wMax, hMax, 16);
            case EAST -> box(0, hMin, wMin, t, hMax, wMax);
            case WEST -> box(16 - t, hMin, wMin, 16, hMax, wMax);
            case UP -> box(wMin, 0, wMin, wMax, t, wMax);
            case DOWN -> box(wMin, 16 - t, wMin, wMax, 16, wMax);
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, VARIANT);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getClickedFace();
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        if (canAttachTo(level, pos, facing)) {
            return this.defaultBlockState().setValue(FACING, facing);
        }
        for (Direction dir : Direction.values()) {
            if (canAttachTo(level, pos, dir)) {
                return this.defaultBlockState().setValue(FACING, dir);
            }
        }

        return null;
    }

    private boolean canAttachTo(Level level, BlockPos pos, Direction facing) {
        BlockPos attachPos = pos.relative(facing.getOpposite());
        BlockState attachState = level.getBlockState(attachPos);
        if (attachState.isFaceSturdy(level, attachPos, facing)) {
            return true;
        }
        if (attachState.getBlock() instanceof LeavesBlock) {
            return true;
        }
        if (attachState.is(VietnamDelightModBlocks.FLAMBOYANT_LEAVES.get()) ||
            attachState.is(VietnamDelightModBlocks.FLOWERING_FLAMBOYANT_LEAVES.get()) ||
            attachState.is(VietnamDelightModBlocks.BLOOMING_FLAMBOYANT_LEAVES.get())) {
            return true;
        }

        return false;
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // Thỉnh thoảng phát ra tiếng kêu của ve sầu
        if (random.nextInt(30) == 0) {
            level.playSound(null, pos, SoundEvents.BEE_LOOP, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    // sad_cicadas: thỉnh thoảng nhỏ 1 giọt nước (particle) - animateTick chạy phía client mỗi tick,
    // đúng cơ chế vanilla dùng cho hiệu ứng nhỏ giọt (dripstone, composter, ...).
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(VARIANT) == Variant.SAD && random.nextInt(6) == 0) {
            double x = pos.getX() + 0.3 + random.nextFloat() * 0.4;
            double y = pos.getY() + 0.1 + random.nextFloat() * 0.3;
            double z = pos.getZ() + 0.3 + random.nextFloat() * 0.4;
            level.addParticle(ParticleTypes.DRIPPING_WATER, x, y, z, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (!canSurvive(state, level, pos)) {
            level.destroyBlock(pos, true);
        }
    }

    public boolean canSurvive(BlockState state, Level level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        return canAttachTo(level, pos, facing);
    }

    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    // ===================== Tương tác theo varient =====================

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        Variant variant = state.getValue(VARIANT);
        ItemStack heldItem = player.getItemInHand(hand);
        RandomSource random = level.getRandom();

        switch (variant) {
            case COUPLE -> {
                return useCoupleCicadas(state, level, pos, player, heldItem, random);
            }
            case NORMAL -> {
                return useNormalCicadas(state, level, pos, player, heldItem);
            }
            case NERD -> {
                if (!level.isClientSide) {
                    player.displayClientMessage(Component.literal(generateBuzzMessage(random)), true);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            case RICH -> {
                return useRichCicadas(level, pos, player, heldItem);
            }
            case SAD -> {
                return useSadCicadas(state, level, pos, player, heldItem);
            }
            default -> {
                return InteractionResult.PASS;
            }
        }
    }

    private InteractionResult useCoupleCicadas(BlockState state, Level level, BlockPos pos, Player player, ItemStack heldItem, RandomSource random) {
        // Shift + chuột phải (bất kỳ vật phẩm nào): rơi ra 1 con ve sầu, cặp trở nên "buồn" (SAD)
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                popResource(level, pos, new ItemStack(VietnamDelightModItems.CICADAS.get()));
                level.setBlock(pos, state.setValue(VARIANT, Variant.SAD), 3);
                level.playSound(null, pos, SoundEvents.BEE_LOOP, SoundSource.BLOCKS, 1.0F, 0.6F);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        boolean isFlower = heldItem.is(VietnamDelightModItems.FLAMBOYANT_FLOWER.get())
                || heldItem.is(VietnamDelightModItems.GOLDEN_FLAMBOYANT_FLOWER.get());
        if (!isFlower) {
            return InteractionResult.PASS;
        }

        if (!(level.getBlockEntity(pos) instanceof CicadasBlockEntity cicadasBE)) {
            return InteractionResult.PASS;
        }

        long gameTime = level.getGameTime();
        if (gameTime < cicadasBE.getCooldownEndTime()) {
            // Đang cooldown: biểu thị bằng particle angry_villager thay vì báo thời gian còn lại
            if (!level.isClientSide) {
                spawnParticlesAt(level, pos, ParticleTypes.ANGRY_VILLAGER, 3);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (!level.isClientSide) {
            int spawned = spawnVariantCicadasAround(level, pos, random);
            cicadasBE.setCooldownEndTime(gameTime + BREED_COOLDOWN_TICKS);
            cicadasBE.setChanged();

            if (!player.isCreative()) {
                heldItem.shrink(1);
            }
            level.playSound(null, pos, SoundEvents.BEE_LOOP, SoundSource.BLOCKS, 1.0F, 1.4F);

            if (spawned > 0) {
                // Sinh sản thành công: particle heart + nhả ra 1 ít exp
                spawnParticlesAt(level, pos, ParticleTypes.HEART, 2 + spawned);
                ExperienceOrb.award((ServerLevel) level, Vec3.atCenterOf(pos), 1 + random.nextInt(3));
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private InteractionResult useNormalCicadas(BlockState state, Level level, BlockPos pos, Player player, ItemStack heldItem) {
        if (heldItem.is(Items.BOOK)) {
            if (!level.isClientSide) {
                level.setBlock(pos, state.setValue(VARIANT, Variant.NERD), 3);
                if (!player.isCreative()) {
                    heldItem.shrink(1);
                }
                level.playSound(null, pos, SoundEvents.BOOK_PAGE_TURN, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (heldItem.is(Items.GOLD_NUGGET)) {
            if (!level.isClientSide) {
                level.setBlock(pos, state.setValue(VARIANT, Variant.RICH), 3);
                if (!player.isCreative()) {
                    heldItem.shrink(1);
                }
                level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, 1.2F);
                spawnParticlesAt(level, pos, ParticleTypes.HAPPY_VILLAGER, 5);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }

    private InteractionResult useRichCicadas(Level level, BlockPos pos, Player player, ItemStack heldItem) {
        if (heldItem.is(VietnamDelightModItems.GOLDEN_FLAMBOYANT_FLOWER.get())) {
            if (!level.isClientSide) {
                Item nugget = level.getRandom().nextBoolean() ? Items.GOLD_NUGGET : Items.IRON_NUGGET;
                popResource(level, pos, new ItemStack(nugget));
                if (!player.isCreative()) {
                    heldItem.shrink(1);
                }
                level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, 1.0F);
                spawnParticlesAt(level, pos, ParticleTypes.HAPPY_VILLAGER, 4);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    // sad_cicadas: cho ăn Hoa Phượng (đỏ hoặc vàng) sẽ trở nên vui vẻ trở lại (HAPPY),
    // và từ đó cứ 1-5 phút lại tự bonemeal 1 crop ngẫu nhiên quanh nó (xem getTicker()/tickHappyCicadas()).
    private InteractionResult useSadCicadas(BlockState state, Level level, BlockPos pos, Player player, ItemStack heldItem) {
        boolean isFlower = heldItem.is(VietnamDelightModItems.FLAMBOYANT_FLOWER.get())
                || heldItem.is(VietnamDelightModItems.GOLDEN_FLAMBOYANT_FLOWER.get());
        if (isFlower) {
            if (!level.isClientSide) {
                if (!player.isCreative()) {
                    heldItem.shrink(1);
                }
                level.setBlock(pos, state.setValue(VARIANT, Variant.HAPPY), 3);

                if (level.getBlockEntity(pos) instanceof CicadasBlockEntity cicadasBE) {
                    long gameTime = level.getGameTime();
                    RandomSource random = level.getRandom();
                    cicadasBE.setNextHappyActionTime(gameTime + HAPPY_ACTION_MIN_TICKS + random.nextInt(HAPPY_ACTION_RANGE_TICKS));
                    cicadasBE.setChanged();
                }

                level.playSound(null, pos, SoundEvents.BEE_LOOP, SoundSource.BLOCKS, 1.0F, 1.3F);
                spawnParticlesAt(level, pos, ParticleTypes.HAPPY_VILLAGER, 4);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    /**
     * Quét khối gỗ Acacia (Log/Wood) trong bán kính BREED_SEARCH_RADIUS quanh vị trí couple_cicadas,
     * rồi gắn 2-4 con ve sầu (varient ngẫu nhiên) lên các mặt bên đang trống, tương tự cách
     * FlamboyantTreeFeature#placeCicadas gắn ve sầu lúc worldgen.
     */
    private int spawnVariantCicadasAround(Level level, BlockPos center, RandomSource random) {
        List<BlockPos> woodPositions = new ArrayList<>();
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int dx = -BREED_SEARCH_RADIUS; dx <= BREED_SEARCH_RADIUS; dx++) {
            for (int dy = -BREED_SEARCH_RADIUS; dy <= BREED_SEARCH_RADIUS; dy++) {
                for (int dz = -BREED_SEARCH_RADIUS; dz <= BREED_SEARCH_RADIUS; dz++) {
                    cursor.setWithOffset(center, dx, dy, dz);
                    BlockState woodState = level.getBlockState(cursor);
                    if (woodState.is(Blocks.ACACIA_LOG) || woodState.is(Blocks.ACACIA_WOOD)) {
                        woodPositions.add(cursor.immutable());
                    }
                }
            }
        }
        if (woodPositions.isEmpty()) {
            return 0;
        }

        // Xáo trộn thủ công (Fisher-Yates) bằng RandomSource
        for (int i = woodPositions.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            BlockPos tmp = woodPositions.get(i);
            woodPositions.set(i, woodPositions.get(j));
            woodPositions.set(j, tmp);
        }

        Direction[] horizontalDirs = Direction.Plane.HORIZONTAL.stream().toArray(Direction[]::new);
        int wanted = 2 + random.nextInt(3); // 2-4 con
        int placed = 0;

        for (BlockPos woodPos : woodPositions) {
            if (placed >= wanted) break;

            Direction[] dirs = horizontalDirs.clone();
            for (int i = dirs.length - 1; i > 0; i--) {
                int j = random.nextInt(i + 1);
                Direction tmp = dirs[i];
                dirs[i] = dirs[j];
                dirs[j] = tmp;
            }

            for (Direction dir : dirs) {
                BlockPos cicadaPos = woodPos.relative(dir);
                if (!level.getBlockState(cicadaPos).isAir()) continue;

                BlockState cicadaState = VietnamDelightModBlocks.CICADAS.get().defaultBlockState()
                        .setValue(FACING, dir)
                        .setValue(VARIANT, rollBabyVariant(random));
                level.setBlock(cicadaPos, cicadaState, 3);
                placed++;
                break;
            }
        }

        return placed;
    }

    private Variant rollBabyVariant(RandomSource random) {
        float roll = random.nextFloat();
        if (roll < 0.12f) return Variant.RICH;
        if (roll < 0.24f) return Variant.NERD;
        if (roll < 0.32f) return Variant.SAD;
        return Variant.NORMAL;
    }

    /** Tạo dòng chữ "bzz..." vui vẻ, độ dài ngẫu nhiên mỗi lần chuột phải vào nerd_cicadas. */
    private static String generateBuzzMessage(RandomSource random) {
        int tokens = 2 + random.nextInt(4); // 2-5 cụm
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tokens; i++) {
            if (i > 0) sb.append(' ');
            int zCount = 1 + random.nextInt(4); // 1-4 chữ z thêm sau "bz"
            sb.append("bz");
            sb.append("z".repeat(zCount));
        }
        return sb.toString();
    }

    /** Varient của Cicadas: normal (mặc định), couple, nerd, rich, sad, happy. */
    public enum Variant implements StringRepresentable {
        NORMAL("normal"),
        COUPLE("couple"),
        NERD("nerd"),
        RICH("rich"),
        SAD("sad"),
        HAPPY("happy");

        private final String name;

        Variant(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
