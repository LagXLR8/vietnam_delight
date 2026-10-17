package net.huwng.vietnamdelight.block;

import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.phys.AABB;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import net.huwng.vietnamdelight.init.VietnamDelightModItems;

import java.util.List;
import java.util.Collections;

public class WaterHyacinthLayerBlock extends Block {
    public static final IntegerProperty LAYERS = IntegerProperty.create("layers", 1, 8);
    
    protected static final VoxelShape[] SHAPE_BY_LAYER = new VoxelShape[]{
        Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
        Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D),
        Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D),
        Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D),
        Block.box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D),
        Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D),
        Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D),
        Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)
    };

    public WaterHyacinthLayerBlock() {
        super(BlockBehaviour.Properties.of()
            .ignitedByLava()
            .sound(SoundType.WET_GRASS)
            .strength(0.1f)
            .randomTicks()  // Cho phép randomTick để feed pig
            .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(LAYERS, 1));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LAYERS);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE_BY_LAYER[state.getValue(LAYERS) - 1];
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE_BY_LAYER[state.getValue(LAYERS) - 1];
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        int layers = state.getValue(LAYERS);
        if (context.getItemInHand().getItem() == VietnamDelightModItems.BUNCH_OF_WATER_HYACINTH.get() && layers < 8) {
            if (context.replacingClickedOnBlock()) {
                return context.getClickedFace() == Direction.UP;
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockstate = context.getLevel().getBlockState(context.getClickedPos());
        if (blockstate.is(this)) {
            int layers = blockstate.getValue(LAYERS);
            return blockstate.setValue(LAYERS, Math.min(8, layers + 1));
        }
        return super.getStateForPlacement(context);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockState below = world.getBlockState(pos.below());
        return Block.isFaceFull(below.getCollisionShape(world, pos.below()), Direction.UP) 
            || below.is(this) && below.getValue(LAYERS) == 8;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            // Block bị phá hoàn toàn
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (!level.isClientSide) {
            if (!state.canSurvive(level, pos)) {
                // Phá block nếu không có block phía dưới
                level.destroyBlock(pos, true);
            }
        }
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        int layers = state.getValue(LAYERS);
        ItemStack drop = new ItemStack(VietnamDelightModItems.BUNCH_OF_WATER_HYACINTH.get(), layers);
        return Collections.singletonList(drop);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, net.minecraft.util.RandomSource random) {
        // Tìm động vật trong phạm vi 5 blocks
        AABB area = new AABB(pos).inflate(3.0D);
        
        // Tìm tất cả các loại động vật có thể ăn
        List<Animal> animals = level.getEntitiesOfClass(Animal.class, area, 
            animal -> (animal instanceof Pig || animal instanceof Chicken || 
                      animal instanceof Rabbit || animal instanceof Goat) &&
                      !animal.isBaby() && animal.getAge() == 0);
        
        if (!animals.isEmpty() && random.nextInt(100) < 25) {
            // Chọn ngẫu nhiên 1 con để ăn
            Animal animal = animals.get(random.nextInt(animals.size()));
            int currentLayers = state.getValue(LAYERS);
            
            // Particle block bị phá tại vị trí động vật
            for (int i = 0; i < 10; i++) {
                double offsetX = (random.nextDouble() - 0.5D) * 0.6D;
                double offsetY = random.nextDouble() * 0.5D;
                double offsetZ = (random.nextDouble() - 0.5D) * 0.6D;
                level.sendParticles(
                    new net.minecraft.core.particles.BlockParticleOption(
                        net.minecraft.core.particles.ParticleTypes.BLOCK, state
                    ),
                    animal.getX() + offsetX,
                    animal.getY() + 0.5D + offsetY,
                    animal.getZ() + offsetZ,
                    1, 
                    (random.nextDouble() - 0.5D) * 0.15D,
                    (random.nextDouble() - 0.5D) * 0.15D,
                    (random.nextDouble() - 0.5D) * 0.15D,
                    0.05D
                );
            }
            
            // Giảm 1 layer
            if (currentLayers > 1) {
                level.setBlock(pos, state.setValue(LAYERS, currentLayers - 1), 3);
            } else {
                // Nếu chỉ còn 1 layer thì phá block hoàn toàn
                level.destroyBlock(pos, false);
            }
            
            // Âm thanh ăn
            level.playSound(null, animal.blockPosition(), 
                net.minecraft.sounds.SoundEvents.GENERIC_EAT, 
                net.minecraft.sounds.SoundSource.NEUTRAL, 1.0F, 1.0F);
            
            // Cho động vật vào chế độ breeding
            animal.setInLoveTime(400); // 30 giây breeding mode
        }
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter world, BlockPos pos) {
        return 1;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        return new ItemStack(VietnamDelightModItems.BUNCH_OF_WATER_HYACINTH.get());
    }
}