package net.huwng.vietnamdelight.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;
import net.huwng.vietnamdelight.init.VietnamDelightModItems;

import java.util.List;

public class DurianBlock extends FallingBlock {
    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 3);

    private static final VoxelShape SHAPE_STAGE_0 = Shapes.block();
    private static final VoxelShape SHAPE_STAGE_1 = Shapes.or(Block.box(0, 0, 8, 16, 16, 16),
            Block.box(8, 0, 0, 16, 16, 8));
    private static final VoxelShape SHAPE_STAGE_2 = Block.box(0, 0, 8, 16, 16, 16);
    private static final VoxelShape SHAPE_STAGE_3 = Block.box(8, 0, 8, 16, 16, 16);

    public DurianBlock() {
        super(BlockBehaviour.Properties.of()
                .sound(SoundType.WOOD)
                .strength(1.2F, 2.0F)
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(STAGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STAGE);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(STAGE)) {
            case 1 -> SHAPE_STAGE_1;
            case 2 -> SHAPE_STAGE_2;
            case 3 -> SHAPE_STAGE_3;
            default -> SHAPE_STAGE_0;
        };
    }

    // 1. Sát thương khi giẫm lên hoặc chạm vào (như xương rồng)
    @Override
    public void stepOn(Level world, BlockPos pos, BlockState state, Entity entity) {
        entity.hurt(world.damageSources().cactus(), 1.0F);
        super.stepOn(world, pos, state, entity);
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        entity.hurt(world.damageSources().cactus(), 1.0F);
        super.entityInside(state, world, pos, entity);
    }

    // 2. Kiểm tra xem khối có đang treo an toàn trên cành/thân gỗ hay không
    public static boolean isHangingSecurely(LevelAccessor world, BlockPos pos) {
        BlockState above = world.getBlockState(pos.above());
        return above.is(VietnamDelightModBlocks.DURIAN_LOG.get())
                || above.is(VietnamDelightModBlocks.DURIAN_WOOD.get())
                || above.is(VietnamDelightModBlocks.STRIPPED_DURIAN_LOG.get())
                || above.is(VietnamDelightModBlocks.STRIPPED_DURIAN_WOOD.get())
                || above.is(BlockTags.LOGS);
    }

    // Kiểm tra xem phía dưới khối này có quả sầu riêng nào không (cả dạng block lẫn falling entity)
    public static boolean hasDurianBelow(Level world, BlockPos pos) {
        int maxDy = Math.min(24, pos.getY() - world.getMinBuildHeight());
        // 1. Quét toàn bộ khối ở phía dưới cùng cột
        for (int dy = 1; dy <= maxDy; dy++) {
            BlockPos checkPos = pos.below(dy);
            BlockState checkState = world.getBlockState(checkPos);

            if (checkState.getBlock() instanceof DurianBlock || checkState.is(VietnamDelightModBlocks.DURIAN.get())) {
                return true;
            }

            // Nếu gặp khối rắn (không phải lá cây và không thể thay thế) -> dừng quét block
            if (!checkState.isAir() && !(checkState.getBlock() instanceof LeavesBlock) && !checkState.canBeReplaced()) {
                break;
            }
        }

        // 2. Quét tìm FallingBlockEntity sầu riêng trong toàn bộ cột dọc
        AABB columnBox = new AABB(
                pos.getX() - 0.5, pos.getY() - maxDy, pos.getZ() - 0.5,
                pos.getX() + 1.5, pos.getY(), pos.getZ() + 1.5
        );
        List<FallingBlockEntity> fallingDurians = world.getEntitiesOfClass(FallingBlockEntity.class, columnBox,
                fb -> fb.getBlockState().getBlock() instanceof DurianBlock || fb.getBlockState().is(VietnamDelightModBlocks.DURIAN.get()));
        if (!fallingDurians.isEmpty()) {
            return true;
        }

        return false;
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean isMoving) {
        world.scheduleTick(pos, this, this.getDelayAfterPlace());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world,
            BlockPos pos, BlockPos facingPos) {
        world.scheduleTick(pos, this, this.getDelayAfterPlace());
        return super.updateShape(state, facing, facingState, world, pos, facingPos);
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        // Nếu không có log/wood treo bên trên -> Rơi tự do (mọi states đều rơi)
        if (!isHangingSecurely(world, pos)) {
            BlockPos belowPos = pos.below();
            BlockState belowState = world.getBlockState(belowPos);
            if ((isFree(belowState) || belowState.getBlock() instanceof LeavesBlock)
                    && pos.getY() >= world.getMinBuildHeight()) {
                FallingBlockEntity fallingBlock = FallingBlockEntity.fall(world, pos, state);
                this.falling(fallingBlock);
            }
            return;
        }

        // Nếu CÓ log/wood treo bên trên:
        // Bước 1: Kiểm tra xem đã có quả sầu riêng nào ở bên dưới hay chưa. Nếu CÓ -> KHÔNG RƠI!
        if (hasDurianBelow(world, pos)) {
            world.scheduleTick(pos, this, 10);
            return;
        }

        // Bước 2: Quét tìm entity bên dưới xuyên qua cả lá cây (khoảng cách 16 block)
        LivingEntity foundTarget = null;
        for (int dy = 1; dy <= 16; dy++) {
            BlockPos checkPos = pos.below(dy);
            BlockState checkState = world.getBlockState(checkPos);

            // Kiểm tra xem có LivingEntity nào ở vị trí này không
            AABB checkAABB = new AABB(pos.getX(), checkPos.getY(), pos.getZ(),
                    pos.getX() + 1, checkPos.getY() + 1, pos.getZ() + 1);
            List<LivingEntity> targets = world.getEntitiesOfClass(LivingEntity.class, checkAABB,
                    entity -> entity.isAlive() && !entity.isSpectator());
            if (!targets.isEmpty()) {
                foundTarget = targets.get(0);
                break;
            }

            // Nếu gặp khối rắn không phải lá cây (như đá, đất, gỗ) -> bị chặn lại không rơi được
            if (!checkState.isAir() && !(checkState.getBlock() instanceof LeavesBlock) && !checkState.canBeReplaced()) {
                break;
            }
        }

        if (foundTarget != null) {
            // Có entity bên dưới và không có sầu riêng nào bên dưới -> Rụng rơi xuống
            FallingBlockEntity fallingBlock = FallingBlockEntity.fall(world, pos, state);
            this.falling(fallingBlock);
        } else {
            // Lên lịch kiểm tra tiếp tục
            world.scheduleTick(pos, this, 4); // kiểm tra mỗi 4 tick (0.2 giây)
        }
    }

    @Override
    protected void falling(FallingBlockEntity fallingEntity) {
        // Sát thương 1 damage / block, tối đa 40 damage
        fallingEntity.setHurtsEntities(1.0F, 40);
    }

    @Override
    public void onLand(Level world, BlockPos pos, BlockState fallingState, BlockState hitState,
            FallingBlockEntity fallingBlock) {
        world.playSound(null, pos, SoundEvents.WOOD_FALL, SoundSource.BLOCKS, 1.2F, 0.8F);
        world.playSound(null, pos, SoundEvents.PLAYER_BIG_FALL, SoundSource.BLOCKS, 0.35F, 1.4F);
        super.onLand(world, pos, fallingState, hitState, fallingBlock);
    }

    // 3. Cơ chế dùng dao (Knife) hoặc rìu (Axe) để bổ sầu riêng:
    // TẤT CẢ STATES KHI CẮT ĐỀU RƠI TỪ 1-3 DURIAN FLESH, LUÔN CÓ 1 DURIAN SHELL, VÀ RANDOM DURIAN SEED
    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand,
            BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);

        if (isKnifeOrAxe(heldItem)) {
            int currentStage = state.getValue(STAGE);

            // Giảm độ bền dụng cụ
            heldItem.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));

            // Âm thanh và hiệu ứng hạt khi cắt
            world.playSound(null, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);

            if (world instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state),
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        10, 0.25, 0.25, 0.25, 0.05);
            }

            if (!world.isClientSide) {
                // 1. Rơi từ 1-3 durian flesh
                int fleshCount = 1 + world.random.nextInt(3);
                popResource(world, pos, new ItemStack(VietnamDelightModItems.DURIAN_FLESH.get(), fleshCount));

                // 2. Luôn có 1 durian shell
                popResource(world, pos, new ItemStack(VietnamDelightModItems.DURIAN_SHELL.get(), 1));

                // 3. Durian seed random ở mọi states (có thể có hoặc không)
                if (world.random.nextFloat() < 0.5f) {
                    popResource(world, pos, new ItemStack(VietnamDelightModItems.DURIAN_SEED.get(), 1));
                }

                if (currentStage < 3) {
                    // Chuyển sang stage tiếp theo (0->1, 1->2, 2->3)
                    world.setBlock(pos, state.setValue(STAGE, currentStage + 1), 3);
                } else {
                    // Ở stage 3 nhát cắt cuối cùng: Phá khối hoàn toàn
                    world.destroyBlock(pos, false);
                }
            }

            return InteractionResult.sidedSuccess(world.isClientSide);
        }

        return super.use(state, world, pos, player, hand, hit);
    }

    private boolean isKnifeOrAxe(ItemStack stack) {
        if (stack.isEmpty())
            return false;
        if (stack.getItem() instanceof AxeItem || stack.is(ItemTags.AXES))
            return true;
        String name = stack.getItem().getClass().getSimpleName().toLowerCase();
        if (name.contains("knife") || name.contains("dagger"))
            return true;
        return stack.is(ItemTags.create(new net.minecraft.resources.ResourceLocation("farmersdelight", "tools/knives")))
                || stack.is(ItemTags.create(new net.minecraft.resources.ResourceLocation("forge", "tools/knives")));
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter world, BlockPos pos, PathComputationType type) {
        return false;
    }
}
