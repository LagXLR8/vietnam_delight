package net.huwng.vietnamdelight.block;

import net.minecraft.world.level.material.MapColor;

public class PurifyingCandleBlock extends BaseCustomCandleBlock {
    public PurifyingCandleBlock() {
        super(MapColor.COLOR_GREEN);
    }

    @Override
    public CandleType getCandleType() {
        return CandleType.PURIFYING;
    }
}
