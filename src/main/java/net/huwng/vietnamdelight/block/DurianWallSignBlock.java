package net.huwng.vietnamdelight.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.huwng.vietnamdelight.init.VietnamDelightWoodTypes;
import net.huwng.vietnamdelight.block.entity.DurianSignBlockEntity;

public class DurianWallSignBlock extends WallSignBlock {
    public DurianWallSignBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_YELLOW)
                .forceSolidOn()
                .noCollission()
                .strength(1.0F)
                .sound(SoundType.WOOD)
                .ignitedByLava(),
                VietnamDelightWoodTypes.DURIAN
        );
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DurianSignBlockEntity(pos, state);
    }
}
