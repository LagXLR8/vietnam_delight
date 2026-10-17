package net.huwng.vietnamdelight.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import vectorwing.farmersdelight.common.block.RichSoilFarmlandBlock;

import java.util.List;

/**
 * Shared base for all Vietnam Delight farmland crops.
 *
 * Subclasses only need to supply:
 *  - MAX_AGE           (constructor arg)
 *  - SHAPES array      (constructor arg)
 *  - getSeedStack()    → item dropped as seed / used for planting
 *  - getDrops()        → full drop list (override per crop)
 *
 * All canSurvive / mayPlaceOn / growth / bonemeal / IPlantable logic
 * lives here so it doesn't have to be copied 13 times.
 *
 * Property name is "age" (vanilla standard) so Farmer's Delight,
 * Patchouli, and other mods that inspect crop state work correctly.
 */
public abstract class BaseCropBlock extends BushBlock implements BonemealableBlock, IPlantable {

    public static final IntegerProperty AGE_4  = IntegerProperty.create("age", 0, 4);
    public static final IntegerProperty AGE_5  = IntegerProperty.create("age", 0, 5);
    public static final IntegerProperty AGE_6  = IntegerProperty.create("age", 0, 6);
    public static final IntegerProperty AGE_7  = IntegerProperty.create("age", 0, 7);
    public static final IntegerProperty AGE_8  = IntegerProperty.create("age", 0, 8);
    public static final IntegerProperty AGE_9  = IntegerProperty.create("age", 0, 9);

    // Concrete subclasses expose their own public static final IntegerProperty AGE
    // pointing to one of the three constants above (avoids creating duplicate properties).

    private final int maxAge;
    private final VoxelShape[] shapes;

    protected BaseCropBlock(int maxAge, VoxelShape[] shapes) {
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
        this.maxAge = maxAge;
        this.shapes = shapes;
    }

    protected BaseCropBlock(int maxAge, VoxelShape[] shapes, SoundType sound) {
        super(BlockBehaviour.Properties.of()
                .sound(sound)
                .instabreak()
                .noCollission()
                .noOcclusion()
                .randomTicks()
                .ignitedByLava()
                .lightLevel(s -> 0)
                .isRedstoneConductor((bs, br, bp) -> false)
        );
        this.maxAge = maxAge;
        this.shapes = shapes;
    }

    // ── Subclass contract ────────────────────────────────────────────────────

    /** The IntegerProperty used by this crop. Must be "age". */
    public abstract IntegerProperty getAgeProperty();

    /** Stack returned when the player middle-clicks the block. */
    public abstract ItemStack getSeedStack();

    // ── Age helpers ──────────────────────────────────────────────────────────

    public int getAge(BlockState state) {
        return state.getValue(getAgeProperty());
    }

    public boolean isMaxAge(BlockState state) {
        return getAge(state) == maxAge;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public BlockState withAge(int age) {
        return defaultBlockState().setValue(getAgeProperty(), age);
    }

    // ── IPlantable ───────────────────────────────────────────────────────────

    @Override
    public PlantType getPlantType(BlockGetter world, BlockPos pos) {
        return PlantType.CROP;
    }

    @Override
    public BlockState getPlant(BlockGetter world, BlockPos pos) {
        return world.getBlockState(pos);
    }

    // ── Survival / placement ─────────────────────────────────────────────────

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        return below.is(Blocks.FARMLAND) || below.getBlock() instanceof RichSoilFarmlandBlock;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter getter, BlockPos pos) {
        return state.is(Blocks.FARMLAND) || state.getBlock() instanceof RichSoilFarmlandBlock;
    }

    // ── Growth ───────────────────────────────────────────────────────────────

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return !isMaxAge(state);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isAreaLoaded(pos, 1)) return;
        if (level.getRawBrightness(pos, 0) < 9) return;

        int age = getAge(state);
        if (age < maxAge) {
            float speed = getGrowthSpeed(level, pos);
            if (random.nextInt((int) (25.0F / speed) + 1) == 0) {
                level.setBlock(pos, withAge(age + 1), 2);
            }
        }
    }

    protected float getGrowthSpeed(LevelReader level, BlockPos pos) {
        float speed = 1.0F;
        BlockPos soilPos = pos.below();
        for (int dx = -1; dx <= 1; ++dx) {
            for (int dz = -1; dz <= 1; ++dz) {
                float bonus = 0.0F;
                BlockState soil = level.getBlockState(soilPos.offset(dx, 0, dz));
                if (soil.is(Blocks.FARMLAND)) {
                    bonus = 1.0F;
                    if (soil.hasProperty(FarmBlock.MOISTURE)
                            && soil.getValue(FarmBlock.MOISTURE) > 0) {
                        bonus = 3.0F;
                    }
                } else if (soil.getBlock() instanceof RichSoilFarmlandBlock) {
                    // Rich Soil farmland is always moist → treat as maximum bonus
                    bonus = 3.0F;
                }
                if (dx != 0 || dz != 0) bonus /= 4.0F;
                speed += bonus;
            }
        }
        return speed;
    }

    // ── Bonemeal ─────────────────────────────────────────────────────────────

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
        int newAge = Math.min(getAge(state) + 1 + random.nextInt(2), maxAge);
        level.setBlock(pos, withAge(newAge), 2);
    }

    // ── updateShape: pop when support gone ───────────────────────────────────

    @Override
    public BlockState updateShape(BlockState state, Direction dir, BlockState neighborState,
                                   LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if (!canSurvive(state, world, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, dir, neighborState, world, pos, neighborPos);
    }

    // ── Shape & light ────────────────────────────────────────────────────────

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        int age = getAge(state);
        return (age < shapes.length) ? shapes[age] : shapes[shapes.length - 1];
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter world, BlockPos pos) {
        return 0;
    }

    // ── Middle-click clone ───────────────────────────────────────────────────

    @Override
    public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
        return getSeedStack();
    }

    // ── Drops: base implementation drops only the seed ───────────────────────
    // Most crops override this to also drop the harvest item at max age.

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return List.of(getSeedStack());
    }
}
