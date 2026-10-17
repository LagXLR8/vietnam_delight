package net.huwng.vietnamdelight;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CommonSetupHook {
    public static void init(FMLCommonSetupEvent event) {
        CommonSetupHandler.init(event);
        
    }
}
