package net.huwng.vietnamdelight;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.huwng.vietnamdelight.block.DurianBlock;

@Mod.EventBusSubscriber(modid = VietnamDelightMod.MODID)
public class DurianFallEventHandler {

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.level.isClientSide()) {
            return;
        }

        if (event.level instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof FallingBlockEntity fallingBlock && !fallingBlock.isRemoved()) {
                    if (fallingBlock.getBlockState().getBlock() instanceof DurianBlock) {
                        BlockPos currentPos = fallingBlock.blockPosition();
                        BlockPos belowPos = currentPos.below();

                        BlockState curState = serverLevel.getBlockState(currentPos);
                        if (curState.getBlock() instanceof LeavesBlock) {
                            serverLevel.destroyBlock(currentPos, true);
                        }

                        BlockState belowState = serverLevel.getBlockState(belowPos);
                        if (belowState.getBlock() instanceof LeavesBlock) {
                            serverLevel.destroyBlock(belowPos, true);
                        }
                    }
                }
            }
        }
    }
}
