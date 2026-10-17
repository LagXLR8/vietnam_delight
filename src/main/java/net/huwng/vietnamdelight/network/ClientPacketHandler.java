package net.huwng.vietnamdelight.network;

import net.huwng.vietnamdelight.rope.RopeConnection;
import net.huwng.vietnamdelight.rope.RopeConnectionManager;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ClientPacketHandler {
    
    public static void handleRopeSync(ResourceKey<Level> dimension, List<RopeConnection> connections) {
        Minecraft mc = Minecraft.getInstance();
        Level clientLevel = mc.level;
        
        if (clientLevel != null && clientLevel.dimension().equals(dimension)) {
            RopeConnectionManager.clear(clientLevel);
            connections.forEach(c -> RopeConnectionManager.addConnection(clientLevel, c));
        }
    }
}