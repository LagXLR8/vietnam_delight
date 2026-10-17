package net.huwng.vietnamdelight.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import net.huwng.vietnamdelight.init.VietnamDelightModMobEffects;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class SaltyOverlayRenderer {
    private static final ResourceLocation SALTY_HEARTS =
            new ResourceLocation("vietnam_delight", "textures/screens/salty_hearts.png");

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Pre event) {
        var mc = Minecraft.getInstance();
        var player = mc.player;
        if (player == null) return;

        // Không render tim nếu người chơi ở chế độ Sáng tạo (gamemode 1), Khán giả (gamemode 3), hoặc không nhận sát thương
        if (mc.gameMode == null || !mc.gameMode.canHurtPlayer() || player.isCreative() || player.isSpectator()) {
            return;
        }

        // chỉ thay tim nếu có hiệu ứng SALTY
        if (!player.hasEffect(VietnamDelightModMobEffects.SALTY.get())) return;

        // chỉ hook phần tim
        if (!event.getOverlay().id().toString().equals("minecraft:player_health")) return;

        // huỷ render mặc định
        event.setCanceled(true);

        GuiGraphics guiGraphics = event.getGuiGraphics();

        // bind texture tim custom
        RenderSystem.setShaderTexture(0, SALTY_HEARTS);

        // gọi render tim (copy code từ vanilla renderHearts)
        renderSaltyHearts(guiGraphics, mc);
    }

	private static void renderSaltyHearts(GuiGraphics guiGraphics, Minecraft mc) {
	    var player = mc.player;
	    if (player == null) return;
	
	    int health = (int) Math.ceil(player.getHealth());
	    int maxHealth = (int) player.getMaxHealth();
	
	    int hearts = maxHealth / 2; // số tim cần hiển thị
	    int x = mc.getWindow().getGuiScaledWidth() / 2 - 91;
	    int y = mc.getWindow().getGuiScaledHeight() - 39;
	
	    for (int i = 0; i < hearts; i++) {
	        int heartX = x + i * 8;
	
	        // vẽ tim trống
	        guiGraphics.blit(SALTY_HEARTS, heartX, y, 16, 0, 9, 9);
	
	        int heartHealth = i * 2 + 1; // giá trị máu ứng với tim này
	
	        if (health >= i * 2 + 2) {
	            // tim đầy
	            guiGraphics.blit(SALTY_HEARTS, heartX, y, 52, 0, 9, 9);
	        } else if (health == heartHealth) {
	            // nửa tim
	            guiGraphics.blit(SALTY_HEARTS, heartX, y, 61, 0, 9, 9);
	        }
	    }
	}
}