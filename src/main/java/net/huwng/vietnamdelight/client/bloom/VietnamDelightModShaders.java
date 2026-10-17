package net.huwng.vietnamdelight.client.bloom;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;

@Mod.EventBusSubscriber(modid = "vietnam_delight", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class VietnamDelightModShaders {

    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(
                new ShaderInstance(event.getResourceProvider(), new ResourceLocation("vietnam_delight", "bloom_blur"), DefaultVertexFormat.POSITION),
                shader -> BloomRenderer.blurShader = shader
        );
        event.registerShader(
                new ShaderInstance(event.getResourceProvider(), new ResourceLocation("vietnam_delight", "bloom_composite"), DefaultVertexFormat.POSITION),
                shader -> BloomRenderer.compositeShader = shader
        );
    }
}
