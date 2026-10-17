package net.huwng.vietnamdelight;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.PathPackResources;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.nio.file.Path;

@Mod.EventBusSubscriber(modid = VietnamDelightMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ResourcePackRegistry {
    
	@SubscribeEvent
	public static void addPackFinder(AddPackFindersEvent event) {
	    if (event.getPackType() == PackType.CLIENT_RESOURCES) {
	        registerResourcePack(event, "vietnam_delight_legacy", "Vietnam Delight Legacy", false);
	    }
	}
	    
    private static void registerResourcePack(AddPackFindersEvent event, String packName, String displayName, boolean alwaysEnabled) {
        try {
            Path resourcePath = ModList.get()
                    .getModFileById(VietnamDelightMod.MODID)
                    .getFile()
                    .findResource("resourcepacks", packName);
            
            event.addRepositorySource((packConsumer) -> {
                Pack pack = Pack.readMetaAndCreate(
                        "builtin/" + packName,
                        Component.literal(displayName),
                        alwaysEnabled,
                        (path) -> new PathPackResources(path, resourcePath, true),
                        PackType.CLIENT_RESOURCES,
                        Pack.Position.TOP,
                        PackSource.BUILT_IN
                );
                
                if (pack != null) {
                    packConsumer.accept(pack);
                }
            });
        } catch (Exception e) {
            System.err.println("Failed to load resource pack: " + packName);
            e.printStackTrace();
        }
    }
}