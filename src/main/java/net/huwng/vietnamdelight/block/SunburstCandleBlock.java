package net.huwng.vietnamdelight.block;

import net.minecraft.world.level.material.MapColor;

public class SunburstCandleBlock extends BaseCustomCandleBlock {
    public SunburstCandleBlock() {
        super(MapColor.COLOR_ORANGE);
    }

    @Override
    public CandleType getCandleType() {
        return CandleType.SUNBURST;
    }
}
