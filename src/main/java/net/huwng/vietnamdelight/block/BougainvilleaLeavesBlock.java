package net.huwng.vietnamdelight.block;

import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;
import net.huwng.vietnamdelight.init.VietnamDelightModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class BougainvilleaLeavesBlock extends LeavesBlock {
    public enum Variant {
        REGULAR,
        ORANGE,
        PINK,
        WHITE
    }

    private final Variant variant;

    public BougainvilleaLeavesBlock(Variant variant) {
        super(BlockBehaviour.Properties.of()
                .ignitedByLava()
                .sound(SoundType.GRASS)
                .strength(0.2f)
                .randomTicks()
                .noOcclusion());
        this.variant = variant;
    }

    public Variant getVariant() {
        return this.variant;
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 1;
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        super.animateTick(state, world, pos, random);

        // Chỉ sinh hạt nếu bên dưới là không khí để hạt có không gian rơi
        if (!world.getBlockState(pos.below()).isAir()) {
            return;
        }

        // Tăng số lượng particle rơi lên một mức vừa phải, sinh động
        int chance = 14;
        if (random.nextInt(chance) != 0) {
            return;
        }

        double x = (double) pos.getX() + random.nextDouble();
        double y = (double) pos.getY() - 0.05;
        double z = (double) pos.getZ() + random.nextDouble();

        SimpleParticleType particle;
        boolean spawnLeaf = random.nextFloat() < 0.35f;

        switch (this.variant) {
            case ORANGE -> particle = spawnLeaf
                    ? VietnamDelightModParticleTypes.LEAF_BOUGAINVILLEA_PARTICLE.get()
                    : VietnamDelightModParticleTypes.ORANGE_BOUGAINVILLEA_PARTICLE.get();
            case PINK -> particle = spawnLeaf
                    ? VietnamDelightModParticleTypes.LEAF_BOUGAINVILLEA_PARTICLE.get()
                    : VietnamDelightModParticleTypes.PINK_BOUGAINVILLEA_PARTICLE.get();
            case WHITE -> particle = spawnLeaf
                    ? VietnamDelightModParticleTypes.LEAF_BOUGAINVILLEA_PARTICLE.get()
                    : VietnamDelightModParticleTypes.WHITE_BOUGAINVILLEA_PARTICLE.get();
            default -> { // REGULAR
                if (random.nextFloat() < 0.60f) {
                    particle = VietnamDelightModParticleTypes.LEAF_BOUGAINVILLEA_PARTICLE.get();
                } else {
                    int r = random.nextInt(3);
                    particle = (r == 0) ? VietnamDelightModParticleTypes.ORANGE_BOUGAINVILLEA_PARTICLE.get()
                            : (r == 1) ? VietnamDelightModParticleTypes.PINK_BOUGAINVILLEA_PARTICLE.get()
                            : VietnamDelightModParticleTypes.WHITE_BOUGAINVILLEA_PARTICLE.get();
                }
            }
        }

        world.addParticle(
                particle,
                x, y, z,
                (random.nextDouble() - 0.5) * 0.06,
                -0.02,
                (random.nextDouble() - 0.5) * 0.06
        );
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.randomTick(state, level, pos, random);
        if (!level.getBlockState(pos).is(this)) {
            return;
        }

        // Rải cánh hoa hoa giấy xuống mặt đất bên dưới tán cây với tần suất vừa phải
        if (random.nextInt(30) == 0) {
            dropPetalsBelow(level, pos, random);
        }
    }

    private void dropPetalsBelow(ServerLevel level, BlockPos leafPos, RandomSource random) {
        BlockPos checkPos = leafPos.below();
        int maxFall = 24;
        for (int i = 0; i < maxFall; i++) {
            BlockState belowState = level.getBlockState(checkPos);
            if (belowState.isAir()) {
                checkPos = checkPos.below();
                continue;
            }
            BlockPos groundPos = checkPos.above();
            if (level.getBlockState(groundPos).isAir() && belowState.isFaceSturdy(level, checkPos, Direction.UP)) {
                level.setBlock(groundPos, VietnamDelightModBlocks.BOUGAINVILLEA.get().defaultBlockState(), 3);
            }
            return;
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
