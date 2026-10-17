package net.huwng.vietnamdelight.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

public class StarLanternBlock extends LanternBlock {
    // Thêm property HORIZONTAL_FACING cho 4 hướng ngang
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    
    public StarLanternBlock() {
        super(BlockBehaviour.Properties.of()
            .instrument(NoteBlockInstrument.BASEDRUM)
            .sound(SoundType.LANTERN)
            .strength(3.5f)
            .lightLevel(s -> 15)
            .noOcclusion()
        );

        // Đặt default state với hướng NORTH
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(HANGING, false)
            .setValue(BlockStateProperties.WATERLOGGED, false)
            .setValue(FACING, Direction.NORTH));
    }
    
    // Override để thêm FACING vào state definition
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }
    
    // Đặt hướng khi block được place
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state != null) {
            // Đặt hướng theo hướng player đang nhìn
            return state.setValue(FACING, context.getHorizontalDirection().getOpposite());
        }
        return state;
    }
}