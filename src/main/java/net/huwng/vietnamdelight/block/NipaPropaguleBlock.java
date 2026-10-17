package net.huwng.vietnamdelight.block;

import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;

import net.huwng.vietnamdelight.init.VietnamDelightModItems;
import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;
import net.huwng.vietnamdelight.world.features.NipaPalmGrower;

public class NipaPropaguleBlock extends Block implements SimpleWaterloggedBlock, BonemealableBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final VoxelShape SHAPE = Block.box(4, 0, 4, 12, 14, 12);
    private static final float NATURAL_GROWTH_CHANCE = 0.05f;

    public NipaPropaguleBlock() {
        super(BlockBehaviour.Properties.of()
                .ignitedByLava()
                .sound(SoundType.GRASS)
                .instabreak()
                .noCollission()
                .noOcclusion()
                .randomTicks()
                .isRedstoneConductor((bs, br, bp) -> false));
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return state.getFluidState().isEmpty();
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
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, 
                                  LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
        if (state.getValue(WATERLOGGED)) {
            world.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }
        
        // Kiểm tra điều kiện tồn tại - chỉ khi block dưới thay đổi
        if (facing == Direction.DOWN && !canSurvive(state, world, currentPos)) {
            return Blocks.AIR.defaultBlockState();
        }
        
        return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        return new ItemStack(VietnamDelightModItems.NIPA_NUT.get());
    }

    private boolean canSurvive(BlockState state, LevelAccessor world, BlockPos pos) {
        // Phải waterlogged (trong nước)
        if (!state.getValue(WATERLOGGED)) {
            return false;
        }
        
        // Phía dưới phải là bùn, đất, hoặc cát
        BlockPos below = pos.below();
        BlockState belowState = world.getBlockState(below);
        return belowState.is(Blocks.MUD) || 
               belowState.is(Blocks.DIRT) || 
               belowState.is(Blocks.SAND);
    }

    // Random tick - tự nhiên grow (rất chậm)
    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (!canSurvive(state, world, pos)) {
            world.destroyBlock(pos, false);
            return;
        }
        
        // 5% cơ hội grow mỗi random tick
        if (random.nextFloat() < NATURAL_GROWTH_CHANCE) {
            tryGrow(world, pos, random);
        }
    }

    // BonemealableBlock implementation
    @Override
    public boolean isValidBonemealTarget(LevelReader world, BlockPos pos, BlockState state, boolean isClient) {
        // Chỉ có thể bonemeal nếu waterlogged và có nền phù hợp
        if (!state.getValue(WATERLOGGED)) {
            return false;
        }
        
        BlockPos below = pos.below();
        BlockState belowState = world.getBlockState(below);
        return belowState.is(Blocks.MUD) || 
               belowState.is(Blocks.DIRT) || 
               belowState.is(Blocks.SAND);
    }

    @Override
    public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState state) {
        return random.nextFloat() < 0.4f; // 40% cơ hội thành công
    }

    @Override
    public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState state) {
        tryGrow(world, pos, random);
    }

    private void tryGrow(ServerLevel world, BlockPos pos, RandomSource random) {
        BlockState currentState = world.getBlockState(pos);
        if (!canSurvive(currentState, world, pos)) {
            return;
        }
        
        BlockPos basePos = pos.below();
        
        boolean success = NipaPalmGrower.growNipaPalm(world, basePos, pos, random);
        
        if (success) {
            // Thay thế propagule bằng nipa_root
            BlockState rootState = VietnamDelightModBlocks.NIPA_ROOT.get().defaultBlockState();
            world.setBlock(pos, rootState, 3);
        }
    }
}