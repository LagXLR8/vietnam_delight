package net.huwng.vietnamdelight.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;

public class DurianBudBlock extends Block implements BonemealableBlock {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;

    private static final VoxelShape SHAPE_0 = Block.box(2, 6, 2, 14, 16, 14);
    private static final VoxelShape SHAPE_1 = Block.box(2, 4, 2, 14, 16, 14);
    private static final VoxelShape SHAPE_2 = Block.box(1, 2, 1, 15, 16, 15);
    private static final VoxelShape SHAPE_3 = Block.box(0, 0, 0, 16, 16, 16);

    public DurianBudBlock() {
        super(BlockBehaviour.Properties.of()
                .sound(SoundType.GRASS)
                .strength(0.5F)
                .noOcclusion()
                .noCollission()
                .offsetType(BlockBehaviour.OffsetType.XZ)
                .randomTicks());
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Vec3 offset = state.getOffset(world, pos);
        VoxelShape baseShape = switch (state.getValue(AGE)) {
            case 1 -> SHAPE_1;
            case 2 -> SHAPE_2;
            case 3 -> SHAPE_3;
            default -> SHAPE_0;
        };
        return baseShape.move(offset.x, offset.y, offset.z);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return net.minecraft.world.phys.shapes.Shapes.empty();
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter world, BlockPos pos) {
        return net.minecraft.world.phys.shapes.Shapes.empty();
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return net.minecraft.world.phys.shapes.Shapes.empty();
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
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockPos abovePos = pos.above();
        BlockState aboveState = world.getBlockState(abovePos);
        return aboveState.is(VietnamDelightModBlocks.DURIAN_WOOD.get())
                || aboveState.is(VietnamDelightModBlocks.DURIAN_LOG.get())
                || aboveState.is(VietnamDelightModBlocks.STRIPPED_DURIAN_WOOD.get())
                || aboveState.is(VietnamDelightModBlocks.STRIPPED_DURIAN_LOG.get())
                || aboveState.is(VietnamDelightModBlocks.DURIAN_LEAVES.get())
                || aboveState.isFaceSturdy(world, abovePos, Direction.DOWN);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState,
                                  LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
        if (!canSurvive(state, world, currentPos)) {
            world.scheduleTick(currentPos, this, 1);
        }
        return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (!canSurvive(state, world, pos)) {
            world.destroyBlock(pos, true);
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (!canSurvive(state, world, pos)) {
            world.destroyBlock(pos, true);
            return;
        }

        // Tăng trưởng theo thời gian như cây trồng
        if (random.nextInt(5) == 0) {
            int age = state.getValue(AGE);
            if (age < 3) {
                world.setBlock(pos, state.setValue(AGE, age + 1), 2);
            } else {
                // Sau durian_stage_3, biến thành durian block đầy đủ
                world.setBlock(pos, VietnamDelightModBlocks.DURIAN.get().defaultBlockState(), 3);
            }
        }
    }

    // === BonemealableBlock Implementation ===
    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        int age = state.getValue(AGE);
        if (age < 3) {
            level.setBlock(pos, state.setValue(AGE, age + 1), 2);
        } else {
            // Sau stage 3 biến thành trái sầu riêng hoàn chỉnh
            level.setBlock(pos, VietnamDelightModBlocks.DURIAN.get().defaultBlockState(), 3);
        }
    }
}
