package net.huwng.vietnamdelight.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.huwng.vietnamdelight.init.VietnamDelightWoodTypes;
import net.huwng.vietnamdelight.block.entity.FlamboyantSignBlockEntity;

public class FlamboyantWallSignBlock extends WallSignBlock {
    public FlamboyantWallSignBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_RED)
                .forceSolidOn()
                .noCollission()
                .strength(1.0F)
                .sound(SoundType.WOOD)
                .ignitedByLava(),
                VietnamDelightWoodTypes.FLAMBOYANT
        );
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FlamboyantSignBlockEntity(pos, state);
    }
}
