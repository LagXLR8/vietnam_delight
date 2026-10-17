package net.huwng.vietnamdelight.init;

import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

public class VietnamDelightWoodTypes {
    public static final BlockSetType DURIAN_BLOCK_SET_TYPE = BlockSetType.register(new BlockSetType("vietnam_delight:durian"));
    public static final WoodType DURIAN = WoodType.register(new WoodType("vietnam_delight:durian", DURIAN_BLOCK_SET_TYPE));
    public static final BlockSetType FLAMBOYANT_BLOCK_SET_TYPE = BlockSetType.register(new BlockSetType("vietnam_delight:flamboyant"));
    public static final WoodType FLAMBOYANT = WoodType.register(new WoodType("vietnam_delight:flamboyant", FLAMBOYANT_BLOCK_SET_TYPE));
}
