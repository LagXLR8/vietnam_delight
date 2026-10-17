package net.huwng.vietnamdelight.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;

public class DurianWoodBlock extends RotatedPillarBlock {
    public static final BooleanProperty LICHEN = BooleanProperty.create("lichen");

    public DurianWoodBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_BROWN)
                .instrument(NoteBlockInstrument.BASS)
                .strength(2.0F)
                .sound(SoundType.WOOD)
                .randomTicks()
                .ignitedByLava());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(AXIS, Direction.Axis.Y)
                .setValue(LICHEN, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LICHEN);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(LICHEN, false);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(LICHEN);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        super.randomTick(state, world, pos, random);

        // Đối với durian wood lichen, nếu phía dưới không có gì thì sau một thời gian sẽ mọc durian_bud
        if (state.getValue(LICHEN)) {
            BlockPos belowPos = pos.below();
            if (world.isEmptyBlock(belowPos) || world.getBlockState(belowPos).isAir()) {
                if (random.nextInt(4) == 0) { // Tỉ lệ mọc nụ sầu riêng
                    world.setBlock(belowPos, VietnamDelightModBlocks.DURIAN_BUD.get().defaultBlockState(), 3);
                }
            }
        }
    }
}
