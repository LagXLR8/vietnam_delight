package net.huwng.vietnamdelight.block;

import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.AABB;

import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;
import net.huwng.vietnamdelight.init.VietnamDelightModParticleTypes;
import net.huwng.vietnamdelight.init.VietnamDelightModEntities;

public class BanyanFlowerBlock extends Block {
    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    private static final int MAX_JADE_RABBITS = 10; // Giới hạn tối đa 10 con thỏ
    private static final int SEARCH_RADIUS = 32; // Phạm vi tìm kiếm 64x64 (32*2)

    public BanyanFlowerBlock() {
        super(BlockBehaviour.Properties.of().ignitedByLava().sound(SoundType.PINK_PETALS).instabreak().noCollission().noOcclusion().isRedstoneConductor((bs, br, bp) -> false).randomTicks());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
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

    private static final VoxelShape SHAPE_DOWN = box(0, 15, 0, 16, 16, 16);
    private static final VoxelShape SHAPE_UP = box(0, 0, 0, 16, 1, 16);
    private static final VoxelShape SHAPE_NORTH = box(0, 0, 15, 16, 16, 16);
    private static final VoxelShape SHAPE_SOUTH = box(0, 0, 0, 16, 16, 1);
    private static final VoxelShape SHAPE_WEST = box(15, 0, 0, 16, 16, 16);
    private static final VoxelShape SHAPE_EAST = box(0, 0, 0, 1, 16, 16);

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction dir = state.getValue(FACING);
        if (dir == Direction.NORTH) return SHAPE_NORTH;
        if (dir == Direction.EAST) return SHAPE_EAST;
        if (dir == Direction.WEST) return SHAPE_WEST;
        if (dir == Direction.UP) return SHAPE_UP;
        if (dir == Direction.DOWN) return SHAPE_DOWN;
        return SHAPE_SOUTH;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
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
        if (attachState.is(VietnamDelightModBlocks.BANYAN_LEAVES.get()) || 
            attachState.is(VietnamDelightModBlocks.FLOWERING_BANYAN_LEAVES.get())) {
            return true;
        }
        
        return false;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (!canSurvive(state, level, pos)) {
            level.destroyBlock(pos, false);
        }
    }

    public boolean canSurvive(BlockState state, Level level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        return canAttachTo(level, pos, facing);
    }

    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        long dayTime = level.getDayTime() % 24000;
        if (dayTime >= 13000 && dayTime <= 23000) {
            if (random.nextFloat() < 0.1f) {
                // Kiểm tra số lượng Jade Rabbit trong khu vực trước khi spawn
                if (canSpawnJadeRabbit(level, pos)) {
                    spawnJadeRabbit(level, pos, random);
                    destroyFlowerWithParticles(level, pos);
                }
            }
        }
    }

    private boolean canSpawnJadeRabbit(ServerLevel level, BlockPos pos) {
        // Tạo bounding box 64x64x64 với tâm tại pos
        AABB searchArea = new AABB(
            pos.getX() - SEARCH_RADIUS, pos.getY() - SEARCH_RADIUS, pos.getZ() - SEARCH_RADIUS,
            pos.getX() + SEARCH_RADIUS, pos.getY() + SEARCH_RADIUS, pos.getZ() + SEARCH_RADIUS
        );
        
        // Đếm số lượng Jade Rabbit trong khu vực
        try {
            java.util.List<net.huwng.vietnamdelight.entity.JadeRabbitEntity> existingRabbits = 
                level.getEntitiesOfClass(net.huwng.vietnamdelight.entity.JadeRabbitEntity.class, searchArea);
            
            // Trả về true nếu chưa đạt giới hạn
            return existingRabbits.size() < MAX_JADE_RABBITS;
        } catch (Exception e) {
            // Nếu có lỗi, mặc định cho phép spawn (fallback)
            return true;
        }
    }

    private void spawnJadeRabbit(ServerLevel level, BlockPos flowerPos, RandomSource random) {
        BlockState flowerState = level.getBlockState(flowerPos);
        Direction facing = flowerState.getValue(FACING);
        BlockPos attachedPos = flowerPos.relative(facing.getOpposite());
        BlockState attachedBlock = level.getBlockState(attachedPos);
        if (!attachedBlock.is(VietnamDelightModBlocks.BANYAN_LEAVES.get()) && 
            !attachedBlock.is(VietnamDelightModBlocks.FLOWERING_BANYAN_LEAVES.get())) {
            return;
        }
        BlockPos groundPos = findGroundBelow(level, flowerPos);
        
        if (groundPos != null && isSafeSpawnPosition(level, groundPos.above())) {
            try {
                net.huwng.vietnamdelight.entity.JadeRabbitEntity jadeRabbit = 
                    new net.huwng.vietnamdelight.entity.JadeRabbitEntity(
                        VietnamDelightModEntities.JADE_RABBIT.get(), level);
                
                if (jadeRabbit != null) {
                    BlockPos spawnPos = groundPos.above();
                    jadeRabbit.setPos(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
                    level.addFreshEntity(jadeRabbit);
                }
            } catch (Exception e) {
                // Spawn thất bại, im lặng
            }
        }
    }

    private int countJadeRabbitsInArea(ServerLevel level, BlockPos pos) {
        AABB searchArea = new AABB(
            pos.getX() - SEARCH_RADIUS, pos.getY() - SEARCH_RADIUS, pos.getZ() - SEARCH_RADIUS,
            pos.getX() + SEARCH_RADIUS, pos.getY() + SEARCH_RADIUS, pos.getZ() + SEARCH_RADIUS
        );
        
        try {
            java.util.List<net.huwng.vietnamdelight.entity.JadeRabbitEntity> existingRabbits = 
                level.getEntitiesOfClass(net.huwng.vietnamdelight.entity.JadeRabbitEntity.class, searchArea);
            return existingRabbits.size();
        } catch (Exception e) {
            return 0;
        }
    }
    
    private BlockPos findGroundBelow(ServerLevel level, BlockPos startPos) {
        for (int y = startPos.getY(); y >= level.getMinBuildHeight(); y--) {
            BlockPos checkPos = new BlockPos(startPos.getX(), y, startPos.getZ());
            BlockState state = level.getBlockState(checkPos);
            if (state.isFaceSturdy(level, checkPos, Direction.UP) && !state.isAir()) {
                return checkPos;
            }
        }
        return null;
    }

    private boolean isSafeSpawnPosition(ServerLevel level, BlockPos pos) {
        if (!level.getBlockState(pos).isAir() || !level.getBlockState(pos.above()).isAir()) {
            return false;
        }
        BlockState below = level.getBlockState(pos.below());
        if (!below.isFaceSturdy(level, pos.below(), Direction.UP)) {
            return false;
        }
        return true;
    }

    private void destroyFlowerWithParticles(ServerLevel level, BlockPos pos) {
        try {
            if (VietnamDelightModParticleTypes.JADE_DROP != null) {
                for (int i = 0; i < 8; i++) {
                    double x = pos.getX() + 0.5 + (level.random.nextDouble() - 0.5) * 0.8;
                    double y = pos.getY() + 0.5 + (level.random.nextDouble() - 0.5) * 0.8;
                    double z = pos.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * 0.8;
                    
                    double velX = (level.random.nextDouble() - 0.5) * 0.2;
                    double velY = level.random.nextDouble() * 0.1;
                    double velZ = (level.random.nextDouble() - 0.5) * 0.2;
                    
                    level.sendParticles(VietnamDelightModParticleTypes.JADE_DROP.get(), x, y, z, 1, velX, velY, velZ, 0);
                }
            } else {
                level.sendParticles(ParticleTypes.HAPPY_VILLAGER, 
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 
                    5, 0.3, 0.3, 0.3, 0);
            }
        } catch (Exception e) {
            level.sendParticles(ParticleTypes.HAPPY_VILLAGER, 
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 
                5, 0.3, 0.3, 0.3, 0);
        }
        level.destroyBlock(pos, false);
    }

    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
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