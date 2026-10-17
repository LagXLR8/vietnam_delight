package net.huwng.vietnamdelight.block;

import net.huwng.vietnamdelight.init.VietnamDelightModItems;
import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;
import net.huwng.vietnamdelight.LotusStemGrowHandler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.*;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.storage.DerivedLevelData;

public class LotusStemBlock extends Block implements SimpleWaterloggedBlock {
    public static final IntegerProperty BLOCKSTATE = IntegerProperty.create("blockstate", 0, 7);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public LotusStemBlock() {
        super(BlockBehaviour.Properties.of()
            .sound(SoundType.WET_GRASS)
            .instabreak()
            .noCollission()
            .noOcclusion()
            .ignitedByLava()
            .isRedstoneConductor((bs, br, bp) -> false)
            .lightLevel(s -> 0)
            .randomTicks());

        this.registerDefaultState(this.stateDefinition.any()
            .setValue(FACING, Direction.NORTH)
            .setValue(WATERLOGGED, false)
            .setValue(BLOCKSTATE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED, BLOCKSTATE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean isWater = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;

        // Random FACING khi đặt BLOCKSTATE = 3
        Direction[] directions = Direction.Plane.HORIZONTAL.stream().toArray(Direction[]::new);
		Direction randomFacing = directions[context.getLevel().getRandom().nextInt(directions.length)];
        int defaultState = 3; // Có thể tuỳ biến theo nhu cầu

        return this.defaultBlockState()
            .setValue(FACING, defaultState == 3 ? randomFacing : context.getHorizontalDirection().getOpposite())
            .setValue(BLOCKSTATE, defaultState)
            .setValue(WATERLOGGED, isWater);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState,
                                  LevelAccessor level, BlockPos pos, BlockPos facingPos) {
        BlockState below = level.getBlockState(pos.below());
        boolean validBelow = below.is(Blocks.MUD)
            || below.is(VietnamDelightModBlocks.LOTUS_ROOT.get())
            || below.is(VietnamDelightModBlocks.LOTUS_STEM.get());

        if (!validBelow) {
            level.levelEvent(2001, pos, Block.getId(state));
            level.playSound(null, pos, SoundEvents.WET_GRASS_HIT, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.destroyBlock(pos, false);
            return Blocks.AIR.defaultBlockState();
        }

        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return super.updateShape(state, facing, facingState, level, pos, facingPos);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        int value = state.getValue(BLOCKSTATE);
        boolean isWaterlogged = state.getValue(WATERLOGGED);
        BlockState below = world.getBlockState(pos.below());
        boolean validBelow = below.is(Blocks.MUD)
            || below.is(VietnamDelightModBlocks.LOTUS_ROOT.get())
            || below.is(VietnamDelightModBlocks.LOTUS_STEM.get());

        if (!validBelow || (value <= 2 && !isWaterlogged) || (value >= 3 && isWaterlogged)) {
            world.destroyBlock(pos, false);
            return;
        }

        LotusStemGrowHandler.tick(state, world, pos);

        BlockPos flowerPos = pos.above();
        if (world.getBlockState(flowerPos).isAir()) {
            BlockState flower = switch (value) {
                case 4 -> VietnamDelightModBlocks.LOTUS_FLOWER.get().defaultBlockState();
                case 6 -> VietnamDelightModBlocks.WHITE_LOTUS_FLOWER.get().defaultBlockState();
                case 7 -> VietnamDelightModBlocks.PURPLE_LOTUS_FLOWER.get().defaultBlockState();
                default -> null;
            };

            if (flower != null) {
                world.setBlock(pos, state.setValue(BLOCKSTATE, 5), 3);
                world.setBlock(flowerPos, flower, 3);
            }
        }
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return context.getItemInHand().getItem() != this.asItem();
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return state.getFluidState().isEmpty();
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter world, BlockPos pos) {
        return 0;
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        int value = state.getValue(BLOCKSTATE);
        return switch (value) {
            case 1, 2, 3, 4, 5, 6, 7 -> box(6, 0, 6, 10, 16, 10);
            default -> box(0, 0, 0, 16, 1, 16);
        };
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world,
                                       BlockPos pos, Player player) {
        return new ItemStack(VietnamDelightModItems.LOTUS_SEEDS.get());
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!world.isClientSide && !state.is(newState.getBlock())) {
            if (state.getValue(BLOCKSTATE) == 2) {
                Player player = world.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 5, false);

                if (player != null && !player.isCreative()) {
                    if (world.random.nextFloat() < 0.3f) {
                        ItemEntity drop = new ItemEntity(world,
                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            new ItemStack(VietnamDelightModItems.YOUNG_LOTUS_STEM.get()));
                        world.addFreshEntity(drop);
                    }
                }
            }
        }

        super.onRemove(state, world, pos, newState, isMoving);
    }
}
