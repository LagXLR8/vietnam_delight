package net.huwng.vietnamdelight.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.huwng.vietnamdelight.network.ModNetworking;

public class ModNetworking {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("vietnam_delight", "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 0;
        CHANNEL.registerMessage(id++, SyncRopeConnectionsPacket.class,
                SyncRopeConnectionsPacket::encode,
                SyncRopeConnectionsPacket::decode,
                SyncRopeConnectionsPacket::handle);
    }
	    public static void init(final FMLCommonSetupEvent event) {
	    ModNetworking.register();
	}
}
