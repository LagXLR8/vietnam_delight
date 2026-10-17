package net.huwng.vietnamdelight.block;

import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;
import net.huwng.vietnamdelight.init.VietnamDelightModItems;
import net.huwng.vietnamdelight.init.VietnamDelightModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.DecoratedPotBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BougainvilleaTrunkBlock extends Block implements SimpleWaterloggedBlock, BonemealableBlock {
    public enum LeafType implements StringRepresentable {
        NONE("none"),
        BOUGAINVILLEA("bougainvillea"),
        ORANGE("orange"),
        PINK("pink"),
        WHITE("white"),
        HIBISCUS("hibiscus"),
        FLOWERING_HIBISCUS("flowering_hibiscus"),
        OAK("oak"),
        SPRUCE("spruce"),
        BIRCH("birch"),
        JUNGLE("jungle"),
        ACACIA("acacia"),
        DARK_OAK("dark_oak"),
        MANGROVE("mangrove"),
        CHERRY("cherry"),
        AZALEA("azalea"),
        FLOWERING_AZALEA("flowering_azalea");

        private final String name;

        LeafType(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public boolean isBougainvillea() {
            return this == BOUGAINVILLEA || this == ORANGE || this == PINK || this == WHITE;
        }
    }

    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<LeafType> LEAF = EnumProperty.create("leaf", LeafType.class);

    private static final VoxelShape CORE_SHAPE = Block.box(4, 4, 4, 12, 12, 12);
    private static final VoxelShape UP_SHAPE = Block.box(4, 12, 4, 12, 16, 12);
    private static final VoxelShape DOWN_SHAPE = Block.box(4, 0, 4, 12, 4, 12);
    private static final VoxelShape NORTH_SHAPE = Block.box(4, 4, 0, 12, 12, 4);
    private static final VoxelShape SOUTH_SHAPE = Block.box(4, 4, 12, 12, 12, 16);
    private static final VoxelShape WEST_SHAPE = Block.box(0, 4, 4, 4, 12, 12);
    private static final VoxelShape EAST_SHAPE = Block.box(12, 4, 4, 16, 12, 12);

    private final Map<BlockState, VoxelShape> shapeCache = new HashMap<>();

    public BougainvilleaTrunkBlock() {
        super(BlockBehaviour.Properties.of()
                .ignitedByLava()
                .instrument(NoteBlockInstrument.BASS)
                .sound(SoundType.WOOD)
                .strength(2.0f)
                .noOcclusion());

        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, false)
                .setValue(EAST, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
                .setValue(UP, false)
                .setValue(DOWN, false)
                .setValue(WATERLOGGED, false)
                .setValue(LEAF, LeafType.NONE));

        this.precomputeShapes();
    }

    private void precomputeShapes() {
        for (BlockState state : this.stateDefinition.getPossibleStates()) {
            VoxelShape shape = CORE_SHAPE;
            if (state.getValue(UP)) shape = Shapes.or(shape, UP_SHAPE);
            if (state.getValue(DOWN)) shape = Shapes.or(shape, DOWN_SHAPE);
            if (state.getValue(NORTH)) shape = Shapes.or(shape, NORTH_SHAPE);
            if (state.getValue(SOUTH)) shape = Shapes.or(shape, SOUTH_SHAPE);
            if (state.getValue(WEST)) shape = Shapes.or(shape, WEST_SHAPE);
            if (state.getValue(EAST)) shape = Shapes.or(shape, EAST_SHAPE);
            this.shapeCache.put(state, shape);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return this.shapeCache.getOrDefault(state, CORE_SHAPE);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return this.shapeCache.getOrDefault(state, CORE_SHAPE);
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState state, BlockGetter reader, BlockPos pos) {
        return Shapes.block();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, WATERLOGGED, LEAF);
    }

    public boolean canConnect(BlockGetter level, BlockPos currentPos, Direction direction, BlockState neighborState) {
        if (neighborState.getBlock() instanceof BougainvilleaTrunkBlock) {
            return true;
        }
        if (direction == Direction.DOWN) {
            if (neighborState.getBlock() instanceof LanternBlock && (!neighborState.hasProperty(LanternBlock.HANGING) || neighborState.getValue(LanternBlock.HANGING))) {
                return true;
            }
            if (neighborState.getBlock() instanceof CeilingHangingSignBlock || (neighborState.is(BlockTags.ALL_HANGING_SIGNS) && !(neighborState.getBlock() instanceof WallHangingSignBlock))) {
                return true;
            }
            BlockPos belowPos = currentPos.below();
            return neighborState.isFaceSturdy(level, belowPos, Direction.UP)
                    || neighborState.is(BlockTags.DIRT)
                    || neighborState.is(BlockTags.SAND);
        }
        if (direction == Direction.UP) {
            if (neighborState.getBlock() instanceof StandingSignBlock || (neighborState.is(BlockTags.ALL_SIGNS) && !(neighborState.getBlock() instanceof WallSignBlock))) {
                return true;
            }
            return isSupportedTopDecor(neighborState);
        }
        if (direction.getAxis().isHorizontal()) {
            if (neighborState.getBlock() instanceof WallSignBlock && neighborState.getValue(WallSignBlock.FACING) == direction) {
                return true;
            }
            if (neighborState.getBlock() instanceof WallHangingSignBlock && neighborState.getValue(WallHangingSignBlock.FACING) == direction) {
                return true;
            }
            if (neighborState.getBlock() instanceof WallTorchBlock && neighborState.getValue(WallTorchBlock.FACING) == direction) {
                return true;
            }
        }
        return false;
    }

    private boolean isSupportedTopDecor(BlockState state) {
        Block block = state.getBlock();
        if (block instanceof TorchBlock && !(block instanceof WallTorchBlock)) {
            return true;
        }
        if (block instanceof LanternBlock && state.hasProperty(LanternBlock.HANGING) && !state.getValue(LanternBlock.HANGING)) {
            return true;
        }
        if (state.is(BlockTags.CANDLES) || block instanceof CandleBlock) {
            return true;
        }
        if (state.is(BlockTags.FLOWER_POTS) || block instanceof FlowerPotBlock) {
            return true;
        }
        if (block instanceof DecoratedPotBlock) {
            return true;
        }
        if (block instanceof CeramicClayPotBlock || block instanceof CeramicClayVaseBlock) {
            return true;
        }
        return false;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        FluidState fluidState = level.getFluidState(pos);

        return this.defaultBlockState()
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER)
                .setValue(NORTH, this.canConnect(level, pos, Direction.NORTH, level.getBlockState(pos.north())))
                .setValue(SOUTH, this.canConnect(level, pos, Direction.SOUTH, level.getBlockState(pos.south())))
                .setValue(EAST, this.canConnect(level, pos, Direction.EAST, level.getBlockState(pos.east())))
                .setValue(WEST, this.canConnect(level, pos, Direction.WEST, level.getBlockState(pos.west())))
                .setValue(UP, this.canConnect(level, pos, Direction.UP, level.getBlockState(pos.above())))
                .setValue(DOWN, this.canConnect(level, pos, Direction.DOWN, level.getBlockState(pos.below())));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        BooleanProperty prop = getPropertyForDirection(direction);
        boolean connects = this.canConnect(level, currentPos, direction, neighborState);
        return state.setValue(prop, connects);
    }

    public static BooleanProperty getPropertyForDirection(Direction direction) {
        return switch (direction) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
            case UP -> UP;
            case DOWN -> DOWN;
        };
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);

        // 1. Thân đang có lá lồng bên trong -> Dùng kéo (Shears) để cắt tỉa tách lá ra
        if (state.getValue(LEAF) != LeafType.NONE) {
            if (heldItem.is(Items.SHEARS)) {
                if (!level.isClientSide) {
                    ItemStack drop = getLeafItemFromType(state.getValue(LEAF));
                    if (!drop.isEmpty()) {
                        popResource(level, pos, drop);
                    }
                    heldItem.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
                    level.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
                    level.setBlock(pos, state.setValue(LEAF, LeafType.NONE), 3);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        // 2. Thân chưa có lá -> Đặt khối lá lồng vào trong thân
        if (state.getValue(LEAF) == LeafType.NONE) {
            LeafType leafType = getLeafTypeFromItem(heldItem.getItem());
            if (leafType != LeafType.NONE) {
                if (!level.isClientSide) {
                    level.setBlock(pos, state.setValue(LEAF, leafType), 3);
                    level.playSound(null, pos, SoundType.GRASS.getPlaceSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
                    if (!player.getAbilities().instabuild) {
                        heldItem.shrink(1);
                    }
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        super.animateTick(state, world, pos, random);

        // Nếu có lồng lá hoa giấy, rơi particle tương ứng
        LeafType leaf = state.getValue(LEAF);
        if (leaf == LeafType.NONE) return;

        if (!world.getBlockState(pos.below()).isAir()) return;

        int chance = 14;
        if (random.nextInt(chance) != 0) return;

        boolean spawnLeaf = random.nextFloat() < 0.35f;

        SimpleParticleType particle = switch (leaf) {
            case ORANGE -> spawnLeaf
                    ? VietnamDelightModParticleTypes.LEAF_BOUGAINVILLEA_PARTICLE.get()
                    : VietnamDelightModParticleTypes.ORANGE_BOUGAINVILLEA_PARTICLE.get();
            case PINK -> spawnLeaf
                    ? VietnamDelightModParticleTypes.LEAF_BOUGAINVILLEA_PARTICLE.get()
                    : VietnamDelightModParticleTypes.PINK_BOUGAINVILLEA_PARTICLE.get();
            case WHITE -> spawnLeaf
                    ? VietnamDelightModParticleTypes.LEAF_BOUGAINVILLEA_PARTICLE.get()
                    : VietnamDelightModParticleTypes.WHITE_BOUGAINVILLEA_PARTICLE.get();
            case BOUGAINVILLEA -> {
                if (random.nextFloat() < 0.60f) {
                    yield VietnamDelightModParticleTypes.LEAF_BOUGAINVILLEA_PARTICLE.get();
                } else {
                    int r = random.nextInt(3);
                    yield (r == 0) ? VietnamDelightModParticleTypes.ORANGE_BOUGAINVILLEA_PARTICLE.get()
                            : (r == 1) ? VietnamDelightModParticleTypes.PINK_BOUGAINVILLEA_PARTICLE.get()
                            : VietnamDelightModParticleTypes.WHITE_BOUGAINVILLEA_PARTICLE.get();
                }
            }
            default -> null;
        };

        if (particle != null) {
            double x = (double) pos.getX() + random.nextDouble();
            double y = (double) pos.getY() - 0.05;
            double z = (double) pos.getZ() + random.nextDouble();
            world.addParticle(particle, x, y, z, (random.nextDouble() - 0.5) * 0.06, -0.02, (random.nextDouble() - 0.5) * 0.06);
        }
    }

    public static LeafType getLeafTypeFromBlock(Block block) {
        if (block == VietnamDelightModBlocks.BOUGAINVILLEA_LEAVES.get()) return LeafType.BOUGAINVILLEA;
        if (block == VietnamDelightModBlocks.FLOWERING_ORANGE_BOUGAINVILLEA_LEAVES.get()) return LeafType.ORANGE;
        if (block == VietnamDelightModBlocks.FLOWERING_PINK_BOUGAINVILLEA_LEAVES.get()) return LeafType.PINK;
        if (block == VietnamDelightModBlocks.WHITE_BOUGAINVILLEA_LEAVES.get()) return LeafType.WHITE;
        if (block == VietnamDelightModBlocks.HIBISCUS_LEAVES.get()) return LeafType.HIBISCUS;
        if (block == VietnamDelightModBlocks.FLOWERING_HIBISCUS_LEAVES.get()) return LeafType.FLOWERING_HIBISCUS;
        if (block == Blocks.OAK_LEAVES) return LeafType.OAK;
        if (block == Blocks.SPRUCE_LEAVES) return LeafType.SPRUCE;
        if (block == Blocks.BIRCH_LEAVES) return LeafType.BIRCH;
        if (block == Blocks.JUNGLE_LEAVES) return LeafType.JUNGLE;
        if (block == Blocks.ACACIA_LEAVES) return LeafType.ACACIA;
        if (block == Blocks.DARK_OAK_LEAVES) return LeafType.DARK_OAK;
        if (block == Blocks.MANGROVE_LEAVES) return LeafType.MANGROVE;
        if (block == Blocks.CHERRY_LEAVES) return LeafType.CHERRY;
        if (block == Blocks.AZALEA_LEAVES) return LeafType.AZALEA;
        if (block == Blocks.FLOWERING_AZALEA_LEAVES) return LeafType.FLOWERING_AZALEA;
        return LeafType.NONE;
    }

    public static LeafType getLeafTypeFromItem(Item item) {
        if (item == VietnamDelightModBlocks.BOUGAINVILLEA_LEAVES.get().asItem()) return LeafType.BOUGAINVILLEA;
        if (item == VietnamDelightModBlocks.FLOWERING_ORANGE_BOUGAINVILLEA_LEAVES.get().asItem()) return LeafType.ORANGE;
        if (item == VietnamDelightModBlocks.FLOWERING_PINK_BOUGAINVILLEA_LEAVES.get().asItem()) return LeafType.PINK;
        if (item == VietnamDelightModBlocks.WHITE_BOUGAINVILLEA_LEAVES.get().asItem()) return LeafType.WHITE;
        if (item == VietnamDelightModBlocks.HIBISCUS_LEAVES.get().asItem()) return LeafType.HIBISCUS;
        if (item == VietnamDelightModBlocks.FLOWERING_HIBISCUS_LEAVES.get().asItem()) return LeafType.FLOWERING_HIBISCUS;
        if (item == Blocks.OAK_LEAVES.asItem()) return LeafType.OAK;
        if (item == Blocks.SPRUCE_LEAVES.asItem()) return LeafType.SPRUCE;
        if (item == Blocks.BIRCH_LEAVES.asItem()) return LeafType.BIRCH;
        if (item == Blocks.JUNGLE_LEAVES.asItem()) return LeafType.JUNGLE;
        if (item == Blocks.ACACIA_LEAVES.asItem()) return LeafType.ACACIA;
        if (item == Blocks.DARK_OAK_LEAVES.asItem()) return LeafType.DARK_OAK;
        if (item == Blocks.MANGROVE_LEAVES.asItem()) return LeafType.MANGROVE;
        if (item == Blocks.CHERRY_LEAVES.asItem()) return LeafType.CHERRY;
        if (item == Blocks.AZALEA_LEAVES.asItem()) return LeafType.AZALEA;
        if (item == Blocks.FLOWERING_AZALEA_LEAVES.asItem()) return LeafType.FLOWERING_AZALEA;
        return LeafType.NONE;
    }

    public static ItemStack getLeafItemFromType(LeafType type) {
        return switch (type) {
            case BOUGAINVILLEA -> new ItemStack(VietnamDelightModBlocks.BOUGAINVILLEA_LEAVES.get());
            case ORANGE -> new ItemStack(VietnamDelightModBlocks.FLOWERING_ORANGE_BOUGAINVILLEA_LEAVES.get());
            case PINK -> new ItemStack(VietnamDelightModBlocks.FLOWERING_PINK_BOUGAINVILLEA_LEAVES.get());
            case WHITE -> new ItemStack(VietnamDelightModBlocks.WHITE_BOUGAINVILLEA_LEAVES.get());
            case HIBISCUS -> new ItemStack(VietnamDelightModBlocks.HIBISCUS_LEAVES.get());
            case FLOWERING_HIBISCUS -> new ItemStack(VietnamDelightModBlocks.FLOWERING_HIBISCUS_LEAVES.get());
            case OAK -> new ItemStack(Blocks.OAK_LEAVES);
            case SPRUCE -> new ItemStack(Blocks.SPRUCE_LEAVES);
            case BIRCH -> new ItemStack(Blocks.BIRCH_LEAVES);
            case JUNGLE -> new ItemStack(Blocks.JUNGLE_LEAVES);
            case ACACIA -> new ItemStack(Blocks.ACACIA_LEAVES);
            case DARK_OAK -> new ItemStack(Blocks.DARK_OAK_LEAVES);
            case MANGROVE -> new ItemStack(Blocks.MANGROVE_LEAVES);
            case CHERRY -> new ItemStack(Blocks.CHERRY_LEAVES);
            case AZALEA -> new ItemStack(Blocks.AZALEA_LEAVES);
            case FLOWERING_AZALEA -> new ItemStack(Blocks.FLOWERING_AZALEA_LEAVES);
            default -> ItemStack.EMPTY;
        };
    }

    public static Block getLeafBlockFromType(LeafType type) {
        return switch (type) {
            case BOUGAINVILLEA -> VietnamDelightModBlocks.BOUGAINVILLEA_LEAVES.get();
            case ORANGE -> VietnamDelightModBlocks.FLOWERING_ORANGE_BOUGAINVILLEA_LEAVES.get();
            case PINK -> VietnamDelightModBlocks.FLOWERING_PINK_BOUGAINVILLEA_LEAVES.get();
            case WHITE -> VietnamDelightModBlocks.WHITE_BOUGAINVILLEA_LEAVES.get();
            case HIBISCUS -> VietnamDelightModBlocks.HIBISCUS_LEAVES.get();
            case FLOWERING_HIBISCUS -> VietnamDelightModBlocks.FLOWERING_HIBISCUS_LEAVES.get();
            case OAK -> Blocks.OAK_LEAVES;
            case SPRUCE -> Blocks.SPRUCE_LEAVES;
            case BIRCH -> Blocks.BIRCH_LEAVES;
            case JUNGLE -> Blocks.JUNGLE_LEAVES;
            case ACACIA -> Blocks.ACACIA_LEAVES;
            case DARK_OAK -> Blocks.DARK_OAK_LEAVES;
            case MANGROVE -> Blocks.MANGROVE_LEAVES;
            case CHERRY -> Blocks.CHERRY_LEAVES;
            case AZALEA -> Blocks.AZALEA_LEAVES;
            case FLOWERING_AZALEA -> Blocks.FLOWERING_AZALEA_LEAVES;
            default -> null;
        };
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return (state.getValue(LEAF) != LeafType.NONE) ? 30 : 5;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return (state.getValue(LEAF) != LeafType.NONE) ? 60 : 20;
    }

    @Override
    public SoundType getSoundType(BlockState state, net.minecraft.world.level.LevelReader level, BlockPos pos, net.minecraft.world.entity.Entity entity) {
        return (state.getValue(LEAF) != LeafType.NONE) ? SoundType.GRASS : SoundType.WOOD;
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && !player.isCreative()) {
            LeafType leaf = state.getValue(LEAF);
            if (leaf != LeafType.NONE) {
                ItemStack drop = getLeafItemFromType(leaf);
                if (!drop.isEmpty()) {
                    popResource(level, pos, drop);
                }
            }
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (state.getValue(LEAF).isBougainvillea()) {
            if (entity instanceof LivingEntity && entity.getType() != EntityType.FOX && entity.getType() != EntityType.BEE) {
                entity.makeStuckInBlock(state, new Vec3(0.8D, 0.75D, 0.8D));
                if (!level.isClientSide && (entity.xOld != entity.getX() || entity.zOld != entity.getZ())) {
                    double d0 = Math.abs(entity.getX() - entity.xOld);
                    double d1 = Math.abs(entity.getZ() - entity.zOld);
                    if (d0 >= 0.003D || d1 >= 0.003D) {
                        entity.hurt(level.damageSources().sweetBerryBush(), 1.0F);
                    }
                }
            }
        }
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
        if (!state.getValue(LEAF).isBougainvillea()) {
            return false;
        }
        for (Direction dir : Direction.values()) {
            if (dir == Direction.DOWN) continue;
            BlockPos checkPos = pos.relative(dir);
            BlockState checkState = level.getBlockState(checkPos);
            if (checkState.isAir() || checkState.canBeReplaced()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        List<Direction> candidates = new ArrayList<>();
        List<Direction> wallCandidates = new ArrayList<>();

        for (Direction dir : Direction.values()) {
            if (dir == Direction.DOWN) continue;
            BlockPos targetPos = pos.relative(dir);
            BlockState targetState = level.getBlockState(targetPos);
            if (targetState.isAir() || targetState.canBeReplaced()) {
                candidates.add(dir);
                for (Direction horizontal : Direction.Plane.HORIZONTAL) {
                    BlockPos wallPos = targetPos.relative(horizontal);
                    BlockState wallState = level.getBlockState(wallPos);
                    if (!wallState.isAir() && !(wallState.getBlock() instanceof BougainvilleaTrunkBlock)
                            && !(wallState.getBlock() instanceof LeavesBlock)
                            && (wallState.isSolidRender(level, wallPos) || wallState.isCollisionShapeFullBlock(level, wallPos))) {
                        wallCandidates.add(dir);
                        break;
                    }
                }
            }
        }

        if (candidates.isEmpty()) return;

        Direction chosenDir;
        if (!wallCandidates.isEmpty() && random.nextFloat() < 0.75f) {
            chosenDir = wallCandidates.get(random.nextInt(wallCandidates.size()));
        } else {
            if (candidates.contains(Direction.UP) && random.nextBoolean()) {
                chosenDir = Direction.UP;
            } else {
                chosenDir = candidates.get(random.nextInt(candidates.size()));
            }
        }

        BlockPos newPos = pos.relative(chosenDir);
        FluidState fluidState = level.getFluidState(newPos);
        BlockState newTrunkState = this.defaultBlockState()
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER)
                .setValue(LEAF, state.getValue(LEAF));

        for (Direction d : Direction.values()) {
            newTrunkState = newTrunkState.setValue(getPropertyForDirection(d),
                    this.canConnect(level, newPos, d, level.getBlockState(newPos.relative(d))));
        }

        level.setBlock(newPos, newTrunkState, 3);

        // 40% tỉ lệ phát triển thêm 1 đoạn trunk thứ 2 nối tiếp
        BlockPos secondPos = null;
        if (random.nextFloat() < 0.40f) {
            List<Direction> secondCandidates = new ArrayList<>();
            for (Direction dir : Direction.values()) {
                if (dir == Direction.DOWN || dir == chosenDir.getOpposite()) continue;
                BlockPos p2 = newPos.relative(dir);
                if (level.getBlockState(p2).isAir() || level.getBlockState(p2).canBeReplaced()) {
                    secondCandidates.add(dir);
                }
            }
            if (!secondCandidates.isEmpty()) {
                Direction secondDir = secondCandidates.contains(chosenDir) && random.nextBoolean()
                        ? chosenDir
                        : secondCandidates.get(random.nextInt(secondCandidates.size()));
                secondPos = newPos.relative(secondDir);
                FluidState f2 = level.getFluidState(secondPos);
                BlockState state2 = this.defaultBlockState()
                        .setValue(WATERLOGGED, f2.getType() == Fluids.WATER)
                        .setValue(LEAF, state.getValue(LEAF));

                for (Direction d : Direction.values()) {
                    state2 = state2.setValue(getPropertyForDirection(d),
                            this.canConnect(level, secondPos, d, level.getBlockState(secondPos.relative(d))));
                }
                level.setBlock(secondPos, state2, 3);
            }
        }

        // Mọc thêm các khối lá hoa giấy thuần (không có trunk) xung quanh để tạo thành một cành hoa lá xum xuê
        Block leafBlock = getLeafBlockFromType(state.getValue(LEAF));
        if (leafBlock != null) {
            BlockPos tipPos = (secondPos != null) ? secondPos : newPos;
            int pureLeafCount = 2 + random.nextInt(3); // 2 - 4 khối lá
            List<BlockPos> leafSpots = new ArrayList<>();
            for (Direction d : Direction.values()) {
                leafSpots.add(tipPos.relative(d));
                leafSpots.add(newPos.relative(d));
            }
            java.util.Collections.shuffle(leafSpots);

            for (BlockPos leafSpot : leafSpots) {
                if (pureLeafCount <= 0) break;
                BlockState cur = level.getBlockState(leafSpot);
                if (cur.isAir() || cur.canBeReplaced()) {
                    BlockState leafState = leafBlock.defaultBlockState();
                    if (leafState.hasProperty(LeavesBlock.PERSISTENT)) {
                        leafState = leafState.setValue(LeavesBlock.PERSISTENT, true);
                    }
                    if (leafState.hasProperty(LeavesBlock.DISTANCE)) {
                        leafState = leafState.setValue(LeavesBlock.DISTANCE, 1);
                    }
                    level.setBlock(leafSpot, leafState, 3);
                    pureLeafCount--;
                }
            }
        }

        level.levelEvent(1505, (secondPos != null) ? secondPos : newPos, 0);
    }
}
