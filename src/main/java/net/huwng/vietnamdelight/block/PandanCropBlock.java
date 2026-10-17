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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

public class PandanCropBlock extends BaseCropBlock {

    public static final IntegerProperty AGE = BaseCropBlock.AGE_4;

    private static final VoxelShape[] SHAPES = new VoxelShape[]{
        box(0, 0, 0, 16, 2, 16),
        box(0, 0, 0, 16, 3, 16),
        box(2, 0, 2, 14, 6, 14),
        box(2, 0, 2, 14, 9, 14),
        box(2, 0, 2, 14, 16, 14)
    };

    public PandanCropBlock() {
        super(4, SHAPES, SoundType.GRASS);
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
        return new ItemStack(VietnamDelightModItems.PANDAN_SPROUT.get());
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                  Player player, InteractionHand hand, BlockHitResult hit) {
        if (!isMaxAge(state)) return InteractionResult.PASS;
        if (!level.isClientSide) {
            int count = 1 + level.random.nextInt(3);
            Block.popResource(level, pos, new ItemStack(VietnamDelightModItems.PANDAN_LEAF.get(), count));
            level.setBlock(pos, withAge(3), 2);
            level.playSound(null, pos, SoundEvents.GRASS_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }


    @Override
    public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
        List<ItemStack> drops = new java.util.ArrayList<>();
        drops.add(getSeedStack());
        if (isMaxAge(state)) {
            if (builder.getLevel().random.nextFloat() < 0.5f) drops.add(getSeedStack());
            drops.add(new ItemStack(VietnamDelightModItems.PANDAN_LEAF.get(),
                1 + builder.getLevel().random.nextInt(3)));
        }
        return drops;
    }

}
