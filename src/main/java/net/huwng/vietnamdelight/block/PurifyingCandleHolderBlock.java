package net.huwng.vietnamdelight.block;

import net.huwng.vietnamdelight.init.VietnamDelightModItems;

public class PurifyingCandleHolderBlock extends BaseCandleHolderBlock {
    public PurifyingCandleHolderBlock() {
        super(() -> VietnamDelightModItems.PURIFYING_CANDLE.get());
    }

    @Override
    public CandleType getCandleType() {
        return CandleType.PURIFYING;
    }
}
