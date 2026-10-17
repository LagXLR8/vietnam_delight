package net.huwng.vietnamdelight.block;

import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class PaperLanternBlock extends LanternBlock {
    public PaperLanternBlock() {
        super(BlockBehaviour.Properties.of()
            .instrument(NoteBlockInstrument.BASEDRUM)
            .sound(SoundType.LANTERN)
            .strength(3.5f)
            .lightLevel(s -> 15)
            .noOcclusion()
        );

        this.registerDefaultState(this.stateDefinition.any()
            .setValue(HANGING, false)
            .setValue(BlockStateProperties.WATERLOGGED, false));
    }
}
