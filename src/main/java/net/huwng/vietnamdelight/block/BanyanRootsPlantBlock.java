package net.huwng.vietnamdelight.block;

import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.GrowingPlantBodyBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.tags.BlockTags;
import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;

public class BanyanRootsPlantBlock extends GrowingPlantBodyBlock {
    private static final VoxelShape SHAPE =
            Shapes.box(3.0D / 16.0D, 0.0D, 3.0D / 16.0D, 13.0D / 16.0D, 1.0D, 13.0D / 16.0D);

    public BanyanRootsPlantBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.WOOD)
                .noCollission()
                .instabreak()
                .sound(SoundType.ROOTS),
              Direction.DOWN, // mọc xuống
              SHAPE,
              false // không offset
        );
    }
    
    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockPos above = pos.above();
        BlockState blockAbove = world.getBlockState(above);

        // bám vào chính nó
        if (blockAbove.is(this)) {
            return true;
        }

        // bám vào block có mặt đáy cứng
        if (blockAbove.isFaceSturdy(world, above, Direction.DOWN)) {
            return true;
        }

        // bám vào block lá
        if (blockAbove.is(BlockTags.LEAVES)) {
            return true;
        }

        return false;
    }

    @Override
    protected GrowingPlantHeadBlock getHeadBlock() {
        return (GrowingPlantHeadBlock) VietnamDelightModBlocks.BANYAN_ROOTS.get();
    }

	@Override
	public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 15;
	}

	@Override
	public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 100;
	}
}
