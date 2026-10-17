package net.huwng.vietnamdelight.block;

import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.huwng.vietnamdelight.init.VietnamDelightModItems;
import net.minecraft.world.level.storage.loot.LootParams;
import java.util.List;
import java.util.Random;

public class BananaFrondBlock extends Block {
    public static final IntegerProperty BLOCKSTATE = IntegerProperty.create("blockstate", 0, 1);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public BananaFrondBlock() {
        super(BlockBehaviour.Properties.of()
            .ignitedByLava()
            .sound(SoundType.BIG_DRIPLEAF)
            .instabreak()
            .lightLevel(s -> 0)
            .noCollission()
            .noOcclusion()
            .isRedstoneConductor((bs, br, bp) -> false)
            .randomTicks());

        this.registerDefaultState(this.stateDefinition.any()
            .setValue(FACING, Direction.NORTH)
            .setValue(BLOCKSTATE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, BLOCKSTATE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
            .setValue(FACING, context.getHorizontalDirection().getOpposite());
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
        return true;
    }
	
	@Override
	public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
    return new ItemStack(VietnamDelightModItems.BANANA_LEAF.get());
	}

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        Random random = new Random();
        int count = 1 + random.nextInt(4);
        return List.of(new ItemStack(VietnamDelightModItems.BANANA_LEAF.get(), count));
    }
    
	@Override
public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
    BlockState belowState = level.getBlockState(pos.below());
    return belowState.getBlock().getClass().getSimpleName().equals("BananaPseudostemBlock");
}

@Override
public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                              LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
    if (!canSurvive(state, level, pos)) {
        level.destroyBlock(pos, true); 
    }
    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
}

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return box(1, 0, 1, 15, 12, 15);
    }
    
@Override
public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
    int currentState = state.getValue(BLOCKSTATE);

    if (level.isRaining()) {
        if (currentState == 0) {
            level.setBlock(pos, state.setValue(BLOCKSTATE, 1), 3);
        }
    } else {
        if (currentState == 1) {
            level.setBlock(pos, state.setValue(BLOCKSTATE, 0), 3);
        }
    }
}

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!level.isClientSide) return;

        if (state.getValue(BLOCKSTATE) == 1 && level.isRaining()) {
            double minX = pos.getX() - 0.5;
            double maxX = pos.getX() + 1.5;
            double minY = pos.getY() + 0.9;
            double maxY = pos.getY() + 1.2;
            double minZ = pos.getZ() - 0.5;
            double maxZ = pos.getZ() + 1.5;

            if (random.nextFloat() < 0.4f) {
                double x = minX + random.nextDouble() * (maxX - minX);
                double y = minY + random.nextDouble() * (maxY - minY);
                double z = minZ + random.nextDouble() * (maxZ - minZ);

                level.addParticle(ParticleTypes.FALLING_DRIPSTONE_WATER, x, y, z, 0, -0.05, 0);
            }
        }
    }

	@Override
	public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 60;
	}

	@Override
	public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 100;
	}
}
