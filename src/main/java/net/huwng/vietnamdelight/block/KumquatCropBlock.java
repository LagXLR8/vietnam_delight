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
import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;

public class KumquatCropBlock extends BaseCropBlock {

    public static final IntegerProperty AGE = BaseCropBlock.AGE_4;

    private static final VoxelShape[] SHAPES = new VoxelShape[]{
        box(0, 0, 0, 16, 2, 16),
        box(0, 0, 0, 16, 4, 16),
        box(2, 0, 2, 14, 8, 14),
        box(2, 0, 2, 14, 12, 14),
        box(2, 0, 2, 14, 16, 14)
    };

    public KumquatCropBlock() {
        super(4, SHAPES, SoundType.AZALEA);
        registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block,
            BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public IntegerProperty getAgeProperty() { return AGE; }

    @Override
    public ItemStack getSeedStack() {
        return new ItemStack(VietnamDelightModItems.KUMQUAT_SEEDS.get());
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isAreaLoaded(pos, 1)) return;
        if (level.getRawBrightness(pos, 0) < 9) return;
        int age = getAge(state);
        float speed = getGrowthSpeed(level, pos);
        if (random.nextInt((int) (25.0F / speed) + 1) == 0) {
            if (age < getMaxAge()) {
                level.setBlock(pos, withAge(age + 1), 2);
            } else {
                level.setBlock(pos, VietnamDelightModBlocks.KUMQUAT_BUSH.get().defaultBlockState(), 2);
            }
        }
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        int age = getAge(state);
        if (age < getMaxAge()) {
            level.setBlock(pos, withAge(age + 1), 2);
        } else {
            level.setBlock(pos, VietnamDelightModBlocks.KUMQUAT_BUSH.get().defaultBlockState(), 2);
        }
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
        return List.of(); // drops come from KumquatBush
    }


}
