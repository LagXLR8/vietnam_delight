package net.huwng.vietnamdelight.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.huwng.vietnamdelight.init.VietnamDelightWoodTypes;
import net.huwng.vietnamdelight.block.entity.FlamboyantHangingSignBlockEntity;

public class FlamboyantCeilingHangingSignBlock extends CeilingHangingSignBlock {
    public FlamboyantCeilingHangingSignBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_RED)
                .forceSolidOn()
                .noCollission()
                .strength(1.0F)
                .sound(SoundType.HANGING_SIGN)
                .ignitedByLava(),
                VietnamDelightWoodTypes.FLAMBOYANT
        );
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FlamboyantHangingSignBlockEntity(pos, state);
    }
}
