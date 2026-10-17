package net.huwng.vietnamdelight.rope;

import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.server.level.ServerLevel;

@Mod.EventBusSubscriber(modid = "vietnam_delight")
public class RopeWorldEvents {

    @SubscribeEvent
    public static void onWorldLoad(LevelEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel server)) return;

        // Lấy RopeSavedData từ world
        RopeSavedData saved = RopeSavedData.get(server);

        // Deserialize để đưa data vào RopeConnectionManager
        net.huwng.vietnamdelight.rope.RopeConnectionManager.deserializeConnectionsForDimension(server, saved.getData());
    }
}
