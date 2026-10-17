package net.huwng.vietnamdelight;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;
import net.huwng.vietnamdelight.block.LotusStemBlock;
import net.huwng.vietnamdelight.block.LotusLilypadBlock;

public class LotusStemGrowHandler {
    public static void tick(BlockState state, LevelAccessor world, BlockPos pos) {
        RandomSource random = world.getRandom();
        int value = state.getValue(LotusStemBlock.BLOCKSTATE);
        BlockPos above = pos.above();

        // 🌸 BLOCKSTATE = 0 → Có 10% chuyển sang 1 hoặc 2
        if (value == 0 && random.nextFloat() < 0.1f) {
            int nextState = (random.nextInt(100) < 30) ? 1 : 2;
            world.setBlock(pos, state.setValue(LotusStemBlock.BLOCKSTATE, nextState), 3);
            return;
        }

        // 🍃 BLOCKSTATE = 1 → Có 5% mọc lên phía trên
        if (value == 1 && random.nextFloat() < 0.05f) {
            handleStemGrowth(world, above, random, true);
        }

        // 🍃 BLOCKSTATE = 2 → Có 5% mọc lên phía trên
        if (value == 2 && random.nextFloat() < 0.05f) {
            handleStemGrowth(world, above, random, false);
        }

        // 🌰 Tạo rễ sen nếu dưới là mud
        if ((value == 1 || value == 2) && world.getBlockState(pos.below()).is(Blocks.MUD)) {
            if (random.nextFloat() < 0.2f) {
                world.setBlock(pos.below(), VietnamDelightModBlocks.LOTUS_ROOT.get().defaultBlockState(), 3);
            }
        }
    }
    private static void handleStemGrowth(LevelAccessor world, BlockPos above, RandomSource random, boolean fromState1) {
    if (world.getBlockState(above).is(Blocks.WATER)) {
        int growState = fromState1 ? 1 : 2;
        world.setBlock(above, VietnamDelightModBlocks.LOTUS_STEM.get()
            .defaultBlockState()
            .setValue(LotusStemBlock.BLOCKSTATE, growState)
            .setValue(LotusStemBlock.WATERLOGGED, true), 3);
    } else if (world.isEmptyBlock(above)) {
        Direction[] directions = Direction.Plane.HORIZONTAL.stream().toArray(Direction[]::new);
        Direction randomDirection = directions[random.nextInt(directions.length)];
        int randomType = random.nextInt(5); // 0–4: 5 loại với 20% mỗi cái

        switch (randomType) {
            case 0 -> {
                int flowerState = 4; // hoặc state riêng cho lotus_flower
                world.setBlock(above, VietnamDelightModBlocks.LOTUS_STEM.get()
                    .defaultBlockState()
                    .setValue(LotusStemBlock.BLOCKSTATE, flowerState)
                    .setValue(LotusStemBlock.FACING, randomDirection)
                    .setValue(LotusStemBlock.WATERLOGGED, false), 3);
            }
            case 1 -> {
                int whiteState = 6;
                world.setBlock(above, VietnamDelightModBlocks.LOTUS_STEM.get()
                    .defaultBlockState()
                    .setValue(LotusStemBlock.BLOCKSTATE, whiteState)
                    .setValue(LotusStemBlock.FACING, randomDirection)
                    .setValue(LotusStemBlock.WATERLOGGED, false), 3);
            }
            case 2 -> {
                int purpleState = 7;
                world.setBlock(above, VietnamDelightModBlocks.LOTUS_STEM.get()
                    .defaultBlockState()
                    .setValue(LotusStemBlock.BLOCKSTATE, purpleState)
                    .setValue(LotusStemBlock.FACING, randomDirection)
                    .setValue(LotusStemBlock.WATERLOGGED, false), 3);
            }
            case 3 -> {
                int stemState = 3;
                world.setBlock(above, VietnamDelightModBlocks.LOTUS_STEM.get()
                    .defaultBlockState()
                    .setValue(LotusStemBlock.BLOCKSTATE, stemState)
                    .setValue(LotusStemBlock.FACING, randomDirection)
                    .setValue(LotusStemBlock.WATERLOGGED, false), 3);
            }
            case 4 -> {
                int lilypadState = random.nextInt(3); // nếu bạn có nhiều kiểu lá
                world.setBlock(above, VietnamDelightModBlocks.LOTUS_LILYPAD.get()
                    .defaultBlockState()
                    .setValue(LotusLilypadBlock.BLOCKSTATE, lilypadState)
                    .setValue(LotusLilypadBlock.FACING, randomDirection), 3);
            }
        }
    }
}
    private static int rollFlowerState(RandomSource random) {
        int roll = random.nextInt(3);
        return switch (roll) {
        case 0 -> 2;
        case 1 -> 4;
        case 2 -> 6;
        default -> 7;
    };
}
}
