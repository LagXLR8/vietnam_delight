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


public class TurmericCropBlock extends BaseCropBlock {

    public static final IntegerProperty AGE = BaseCropBlock.AGE_4;

    private static final VoxelShape[] SHAPES = new VoxelShape[]{
        box(0, 0, 0, 16, 2, 16),
        box(0, 0, 0, 16, 3, 16),
        box(2, 0, 2, 14, 6, 14),
        box(2, 0, 2, 14, 9, 14),
        box(2, 0, 2, 14, 16, 14)
    };

    public TurmericCropBlock() {
        super(4, SHAPES);
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
        return new ItemStack(VietnamDelightModItems.TURMERIC.get());
    }


    @Override
    public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
        List<ItemStack> drops = new java.util.ArrayList<>();
        drops.add(getSeedStack());
        if (isMaxAge(state)) {
            if (builder.getLevel().random.nextFloat() < 0.5f) drops.add(getSeedStack());
            drops.add(new ItemStack(VietnamDelightModItems.TURMERIC.get(),
                1 + builder.getLevel().random.nextInt(3)));
        }
        return drops;
    }

}
