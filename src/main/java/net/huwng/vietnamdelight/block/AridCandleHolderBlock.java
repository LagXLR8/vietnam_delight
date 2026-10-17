package net.huwng.vietnamdelight.block;

import net.huwng.vietnamdelight.init.VietnamDelightModItems;

public class AridCandleHolderBlock extends BaseCandleHolderBlock {
    public AridCandleHolderBlock() {
        super(() -> VietnamDelightModItems.ARID_CANDLE.get());
    }

    @Override
    public CandleType getCandleType() {
        return CandleType.ARID;
    }
}
