package net.huwng.vietnamdelight.block;

import net.huwng.vietnamdelight.init.VietnamDelightModItems;

public class SunburstCandleHolderBlock extends BaseCandleHolderBlock {
    public SunburstCandleHolderBlock() {
        super(() -> VietnamDelightModItems.SUNBURST_CANDLE.get());
    }

    @Override
    public CandleType getCandleType() {
        return CandleType.SUNBURST;
    }
}
