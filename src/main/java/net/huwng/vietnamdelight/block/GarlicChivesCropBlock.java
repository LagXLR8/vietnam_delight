package net.huwng.vietnamdelight.block;

import net.huwng.vietnamdelight.init.VietnamDelightModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.VoxelShape;
import java.util.List;


public class GarlicChivesCropBlock extends BaseCropBlock {

    public static final IntegerProperty AGE = BaseCropBlock.AGE_6;

    private static final VoxelShape[] SHAPES = new VoxelShape[]{
            box(0, 0, 0, 16, 2, 16),
            box(0, 0, 0, 16, 2, 16),
            box(0, 0, 0, 16, 5, 16),
            box(2, 0, 2, 14, 8, 14),
            box(2, 0, 2, 14, 10, 14),
            box(2, 0, 2, 14, 13, 14),
            box(2, 0, 2, 14, 16, 14)
    };

    public GarlicChivesCropBlock() {
        super(6, SHAPES);
        registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public IntegerProperty getAgeProperty() { return AGE; }

    @Override
    public ItemStack getSeedStack() {
        return new ItemStack(VietnamDelightModItems.GARLIC_CHIVES.get());
    }

    /**
     *  ow normally to the next age.
     * Age 3: branch randomly into age 4, 5, or 6 (the three flower/bud variants).
     * Ages 4, 5, 6: terminal — no further growth. Only age 6 is the harvest age.
     */
    @Override
    public boolean isRandomlyTicking(BlockState state) {
        int age = getAge(state);
        return age < 4; // ages 0-3 still tick; ages 4/5/6 are terminal
    }

    @Override
    public boolean isValidBonemealTarget(net.minecraft.world.level.LevelReader level,
                                         BlockPos pos, BlockState state, boolean isClient) {
        return getAge(state) < 4; // bonemeal only works on pre-branch stages
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isAreaLoaded(pos, 1)) return;
        if (level.getRawBrightness(pos, 0) < 9) return;
        int age = getAge(state);
        if (age >= 4) return; // terminal
        float speed = getGrowthSpeed(level, pos);
        if (random.nextInt((int) (25.0F / speed) + 1) == 0) {
            int next = (age == 3) ? (4 + random.nextInt(3)) : age + 1;
            level.setBlock(pos, withAge(next), 2);
        }
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        int age = getAge(state);
        if (age >= 4) return; // terminal, bonemeal does nothing
        int next = (age == 3) ? (4 + random.nextInt(3)) : Math.min(age + 1 + random.nextInt(2), 3);
        level.setBlock(pos, withAge(next), 2);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state,
                                    net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
        List<ItemStack> drops = new java.util.ArrayList<>();
        drops.add(getSeedStack());
        // Only age 6 (full flower) drops the harvest
        if (getAge(state) == 6) {
            if (builder.getLevel().random.nextFloat() < 0.5f) drops.add(getSeedStack());
            drops.add(new ItemStack(VietnamDelightModItems.GARLIC_CHIVES.get(),
                    1 + builder.getLevel().random.nextInt(3)));
        }
        return drops;
    }
}
