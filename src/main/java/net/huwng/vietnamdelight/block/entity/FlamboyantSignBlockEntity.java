package net.huwng.vietnamdelight.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.huwng.vietnamdelight.init.VietnamDelightModBlockEntities;

public class FlamboyantSignBlockEntity extends SignBlockEntity {
    public FlamboyantSignBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public BlockEntityType<?> getType() {
        return VietnamDelightModBlockEntities.FLAMBOYANT_SIGN.get();
    }
}
