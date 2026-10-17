package net.huwng.vietnamdelight.block;

import net.minecraft.world.level.material.MapColor;

public class AridCandleBlock extends BaseCustomCandleBlock {
    public AridCandleBlock() {
        super(MapColor.COLOR_YELLOW);
    }

    @Override
    public CandleType getCandleType() {
        return CandleType.ARID;
    }
}
