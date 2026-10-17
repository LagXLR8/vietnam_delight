package net.huwng.vietnamdelight.block;

import net.huwng.vietnamdelight.init.VietnamDelightModItems;

public class RageCandleHolderBlock extends BaseCandleHolderBlock {
    public RageCandleHolderBlock() {
        super(() -> VietnamDelightModItems.RAGE_CANDLE.get());
    }

    @Override
    public CandleType getCandleType() {
        return CandleType.RAGE;
    }
}
