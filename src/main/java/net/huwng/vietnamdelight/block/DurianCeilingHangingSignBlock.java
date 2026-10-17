package net.huwng.vietnamdelight.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.huwng.vietnamdelight.init.VietnamDelightWoodTypes;
import net.huwng.vietnamdelight.block.entity.DurianHangingSignBlockEntity;

public class DurianCeilingHangingSignBlock extends CeilingHangingSignBlock {
    public DurianCeilingHangingSignBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_YELLOW)
                .forceSolidOn()
                .noCollission()
                .strength(1.0F)
                .sound(SoundType.HANGING_SIGN)
                .ignitedByLava(),
                VietnamDelightWoodTypes.DURIAN
        );
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DurianHangingSignBlockEntity(pos, state);
    }
}
