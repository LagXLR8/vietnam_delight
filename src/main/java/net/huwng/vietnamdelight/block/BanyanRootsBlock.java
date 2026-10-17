package net.huwng.vietnamdelight.block;

import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.GrowingPlantBodyBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.tags.BlockTags;
import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;

public class BanyanRootsBlock extends GrowingPlantHeadBlock {
    private static final VoxelShape SHAPE =
            Shapes.box(3.0D / 16.0D, 7.0D / 16.0D, 3.0D / 16.0D, 13.0D / 16.0D, 1.0D, 13.0D / 16.0D);

    public BanyanRootsBlock() {
        super(BlockBehaviour.Properties.of()
                        .mapColor(MapColor.WOOD)
                        .noCollission()
                        .instabreak()
                        .sound(SoundType.ROOTS)
                        .randomTicks(),
              Direction.DOWN, // hướng mọc xuống
              SHAPE,          // hình dạng
              false,          // không offset
              0.1D            // growth chance (xác suất tick mọc)
        );
    }
    
	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
	    BlockPos above = pos.above();
	    BlockState blockAbove = world.getBlockState(above);
	
	    // bám vào chính nó (head)
	    if (blockAbove.is(this)) {
	        return true;
	    }
	
	    // bám vào block thân rễ (plant)
	    if (blockAbove.is(VietnamDelightModBlocks.BANYAN_ROOTS_PLANT.get())) {
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
    protected GrowingPlantBodyBlock getBodyBlock() {
        return (GrowingPlantBodyBlock) VietnamDelightModBlocks.BANYAN_ROOTS_PLANT.get();
    }

    @Override
    protected boolean canGrowInto(BlockState state) {
        return state.isAir(); // chỉ mọc trong không khí
    }

    @Override
    public int getBlocksToGrowWhenBonemealed(RandomSource random) {
        return 1 + random.nextInt(2); // giống weeping vines, mọc 1–2 khối
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
