package net.huwng.vietnamdelight.block;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

import net.huwng.vietnamdelight.block.entity.PolishedCeramicClayWallBlockEntity;

public class PolishedCeramicClayWallBlock extends WallBlock implements EntityBlock {
    public static final BooleanProperty DIRTY = BooleanProperty.create("dirty");

    private final Map<BlockState, VoxelShape> shapeMap;
    private final Map<BlockState, VoxelShape> collisionShapeMap;

    public PolishedCeramicClayWallBlock() {
        super(BlockBehaviour.Properties.of()
            .instrument(NoteBlockInstrument.BASEDRUM)
            .sound(SoundType.DECORATED_POT)
            .strength(2f, 6f)
            .requiresCorrectToolForDrops()
            .dynamicShape()
            .noOcclusion());
        this.registerDefaultState(this.defaultBlockState().setValue(DIRTY, false));
        this.shapeMap = this.makeCustomShapes(4.0F, 3.0F, 16.0F, 0.0F, 14.0F, 16.0F);
        this.collisionShapeMap = this.makeCustomShapes(4.0F, 3.0F, 24.0F, 0.0F, 24.0F, 24.0F);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(DIRTY);
    }

    private Map<BlockState, VoxelShape> makeCustomShapes(float postW, float sideW, float postH, float sideLowMin, float sideLowMax, float sideTallMax) {
        float f = 8.0F - postW;
        float f1 = 8.0F + postW;
        float f2 = 8.0F - sideW;
        float f3 = 8.0F + sideW;
        VoxelShape postShape = Block.box(f, 0.0D, f, f1, postH, f1);
        VoxelShape northLow = Block.box(f2, sideLowMin, 0.0D, f3, sideLowMax, f3);
        VoxelShape southLow = Block.box(f2, sideLowMin, f2, f3, sideLowMax, 16.0D);
        VoxelShape westLow = Block.box(0.0D, sideLowMin, f2, f3, sideLowMax, f3);
        VoxelShape eastLow = Block.box(f2, sideLowMin, f2, 16.0D, sideLowMax, f3);
        VoxelShape northTall = Block.box(f2, sideLowMin, 0.0D, f3, sideTallMax, f3);
        VoxelShape southTall = Block.box(f2, sideLowMin, f2, f3, sideTallMax, 16.0D);
        VoxelShape westTall = Block.box(0.0D, sideLowMin, f2, f3, sideTallMax, f3);
        VoxelShape eastTall = Block.box(f2, sideLowMin, f2, 16.0D, sideTallMax, f3);

        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (Boolean up : UP.getPossibleValues()) {
            for (WallSide east : EAST_WALL.getPossibleValues()) {
                for (WallSide north : NORTH_WALL.getPossibleValues()) {
                    for (WallSide west : WEST_WALL.getPossibleValues()) {
                        for (WallSide south : SOUTH_WALL.getPossibleValues()) {
                            for (Boolean water : WATERLOGGED.getPossibleValues()) {
                                for (Boolean dirty : DIRTY.getPossibleValues()) {
                                    VoxelShape shape = Shapes.empty();
                                    if (up) {
                                        shape = postShape;
                                    }
                                    if (north == WallSide.LOW) {
                                        shape = Shapes.or(shape, northLow);
                                    } else if (north == WallSide.TALL) {
                                        shape = Shapes.or(shape, northTall);
                                    }
                                    if (east == WallSide.LOW) {
                                        shape = Shapes.or(shape, eastLow);
                                    } else if (east == WallSide.TALL) {
                                        shape = Shapes.or(shape, eastTall);
                                    }
                                    if (south == WallSide.LOW) {
                                        shape = Shapes.or(shape, southLow);
                                    } else if (south == WallSide.TALL) {
                                        shape = Shapes.or(shape, southTall);
                                    }
                                    if (west == WallSide.LOW) {
                                        shape = Shapes.or(shape, westLow);
                                    } else if (west == WallSide.TALL) {
                                        shape = Shapes.or(shape, westTall);
                                    }

                                    BlockState state = this.defaultBlockState()
                                            .setValue(UP, up)
                                            .setValue(EAST_WALL, east)
                                            .setValue(WEST_WALL, west)
                                            .setValue(NORTH_WALL, north)
                                            .setValue(SOUTH_WALL, south)
                                            .setValue(WATERLOGGED, water)
                                            .setValue(DIRTY, dirty);
                                    builder.put(state, shape);
                                }
                            }
                        }
                    }
                }
            }
        }
        return builder.build();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        VoxelShape shape = this.shapeMap.get(state);
        return shape != null ? shape : Shapes.block();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        VoxelShape shape = this.collisionShapeMap.get(state);
        return shape != null ? shape : Shapes.block();
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter world, BlockPos pos) {
        return getShape(state, world, pos, CollisionContext.empty());
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 0;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof PolishedCeramicClayWallBlockEntity wallBE) {
                wallBE.setCurrentColor(0xFFFFFF);
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PolishedCeramicClayWallBlockEntity(pos, state);
    }
}
