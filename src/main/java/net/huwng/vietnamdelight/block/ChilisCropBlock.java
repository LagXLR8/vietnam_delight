package net.huwng.vietnamdelight.block;

import net.huwng.vietnamdelight.init.VietnamDelightModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import vectorwing.farmersdelight.common.block.RichSoilFarmlandBlock;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

import java.util.ArrayList;
import java.util.List;

public class ChilisCropBlock extends BushBlock implements BonemealableBlock, IPlantable {

    public static final int MAX_AGE = 9;

    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, MAX_AGE);

    private static final VoxelShape[] SHAPES = new VoxelShape[]{
            box(0, 0, 0, 16,  2, 16), // 0 – seed just planted
            box(0, 0, 0, 16,  4, 16), // 1
            box(0, 0, 0, 16,  6, 16), // 2
            box(0, 0, 0, 16,  8, 16), // 3
            box(0, 0, 0, 16, 10, 16), // 4
            box(0, 0, 0, 16, 12, 16), // 5
            box(2, 0, 2, 14, 14, 14), // 6
            box(2, 0, 2, 14, 16, 14), // 7
            box(2, 0, 2, 14, 16, 14), // 8
            box(2, 0, 2, 14, 16, 14), // 9 – mature
    };

    public ChilisCropBlock() {
        super(BlockBehaviour.Properties.of()
                .sound(SoundType.CROP)
                .instabreak()
                .noCollission()
                .noOcclusion()
                .randomTicks()
                .ignitedByLava()
                .lightLevel(s -> 0)
                .isRedstoneConductor((bs, br, bp) -> false)
        );
        registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    public int getAge(BlockState state) {
        return state.getValue(AGE);
    }

    public boolean isMaxAge(BlockState state) {
        return getAge(state) == MAX_AGE;
    }

    public BlockState withAge(int age) {
        return defaultBlockState().setValue(AGE, age);
    }

    @Override
    public PlantType getPlantType(BlockGetter world, BlockPos pos) {
        return PlantType.CROP;
    }

    @Override
    public BlockState getPlant(BlockGetter world, BlockPos pos) {
        return world.getBlockState(pos);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        return below.is(Blocks.FARMLAND)
                || below.getBlock() instanceof RichSoilFarmlandBlock;
    }

    private static boolean isFarmersDelightRichSoil(BlockState state) {
        String className = state.getBlock().getClass().getName();
        return className.contains("RichSoilBlock") || className.contains("rich_soil");
    }



    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter getter, BlockPos pos) {
        return state.is(Blocks.FARMLAND)
                || state.getBlock() instanceof RichSoilFarmlandBlock;
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return !isMaxAge(state);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isAreaLoaded(pos, 1)) return;

        if (level.getRawBrightness(pos, 0) < 9) return;
        int age = getAge(state);
        if (age < MAX_AGE) {
            float growthSpeed = getGrowthSpeed(state, level, pos);
            if (random.nextInt((int) (25.0F / growthSpeed) + 1) == 0) {
                level.setBlock(pos, withAge(age + 1), 2);
            }
        }
    }

    private float getGrowthSpeed(BlockState state, LevelReader level, BlockPos pos) {
        float speed = 1.0F;
        BlockPos soilPos = pos.below();
        for (int dx = -1; dx <= 1; ++dx) {
            for (int dz = -1; dz <= 1; ++dz) {
                float soilBonus = 0.0F;
                BlockState soil = level.getBlockState(soilPos.offset(dx, 0, dz));
                if (soil.is(Blocks.FARMLAND)) {
                    soilBonus = 1.0F;
                    if (soil.hasProperty(net.minecraft.world.level.block.FarmBlock.MOISTURE)
                            && soil.getValue(net.minecraft.world.level.block.FarmBlock.MOISTURE) > 0) {
                        soilBonus = 3.0F;
                    }
                }
                if (dx != 0 || dz != 0) soilBonus /= 4.0F;
                speed += soilBonus;
            }
        }
        return speed;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return SHAPES[getAge(state)];
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
        if (!isMaxAge(state)) return InteractionResult.PASS;

        if (!level.isClientSide) {
            int chiliCount = 1 + level.random.nextInt(3);
            Block.popResource(level, pos, new ItemStack(VietnamDelightModItems.BIRD_EYE_CHILIS.get(), chiliCount));
            level.setBlock(pos, withAge(7), 2);
            level.playSound(null, pos, SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state,
                                    net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
        List<ItemStack> drops = new ArrayList<>();
        int age = getAge(state);

        drops.add(new ItemStack(VietnamDelightModItems.BIRD_EYE_CHILIS_SEEDS.get(), 1));

        if (isMaxAge(state)) {
            if (builder.getLevel().random.nextFloat() < 0.5f) {
                drops.add(new ItemStack(VietnamDelightModItems.BIRD_EYE_CHILIS_SEEDS.get(), 1));
            }
            int chiliCount = 1 + builder.getLevel().random.nextInt(4); // 1-4
            drops.add(new ItemStack(VietnamDelightModItems.BIRD_EYE_CHILIS.get(), chiliCount));
        }
        return drops;
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
        return new ItemStack(VietnamDelightModItems.BIRD_EYE_CHILIS_SEEDS.get());
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
        return !isMaxAge(state);
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        int newAge = Math.min(getAge(state) + 1 + random.nextInt(2), MAX_AGE);
        level.setBlock(pos, withAge(newAge), 2);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction dir, BlockState neighborState,
                                  LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if (!canSurvive(state, world, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, dir, neighborState, world, pos, neighborPos);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 0;
    }
}