package net.huwng.vietnamdelight.block;

import net.minecraft.world.level.material.MapColor;

public class RageCandleBlock extends BaseCustomCandleBlock {
    public RageCandleBlock() {
        super(MapColor.COLOR_RED);
    }

    @Override
    public CandleType getCandleType() {
        return CandleType.RAGE;
    }
}
