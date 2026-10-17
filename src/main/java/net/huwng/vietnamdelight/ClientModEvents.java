package net.huwng.vietnamdelight;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

import net.huwng.vietnamdelight.block.entity.*;
import net.huwng.vietnamdelight.item.PaintbrushItem;
import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;
import net.huwng.vietnamdelight.init.VietnamDelightModItems;
import net.huwng.vietnamdelight.init.VietnamDelightModBlockEntities;
import net.huwng.vietnamdelight.client.ScreenShakeHandler;
import net.huwng.vietnamdelight.client.renderer.CeramicPlateRenderer;
import net.huwng.vietnamdelight.client.renderer.CeramicClayPotRenderer;
import net.huwng.vietnamdelight.client.renderer.BloomingGoldenFlamboyantLeavesRenderer;
import net.huwng.vietnamdelight.client.renderer.FloweringGoldenFlamboyantLeavesRenderer;

@Mod.EventBusSubscriber(modid = VietnamDelightMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientModEvents {

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.getBlockColors().register(
            (state, world, pos, tintIndex) ->
                world != null && pos != null && world.getBlockEntity(pos) instanceof CeramicClayBlockBlockEntity be
                    ? be.getCurrentColor()
                    : 0xFFFFFF,
            VietnamDelightModBlocks.CERAMIC_CLAY_BLOCK.get()
        );
        event.getBlockColors().register(
            (state, world, pos, tintIndex) ->
                world != null && pos != null && world.getBlockEntity(pos) instanceof CeramicClayStairsBlockEntity be
                    ? be.getCurrentColor()
                    : 0xFFFFFF,
            VietnamDelightModBlocks.CERAMIC_CLAY_STAIRS.get()
        );
        event.getBlockColors().register(
            (state, world, pos, tintIndex) ->
                world != null && pos != null && world.getBlockEntity(pos) instanceof CeramicClaySlabBlockEntity be
                    ? be.getCurrentColor()
                    : 0xFFFFFF,
            VietnamDelightModBlocks.CERAMIC_CLAY_SLAB.get()
        );
        event.getBlockColors().register(
            (state, world, pos, tintIndex) ->
                world != null && pos != null && world.getBlockEntity(pos) instanceof CeramicTilesBlockEntity be
                    ? be.getCurrentColor()
                    : 0xFFFFFF,
            VietnamDelightModBlocks.CERAMIC_TILES.get()
        );
        event.getBlockColors().register(
            (state, world, pos, tintIndex) ->
                world != null && pos != null && world.getBlockEntity(pos) instanceof CeramicTilesStairsBlockEntity be
                    ? be.getCurrentColor()
                    : 0xFFFFFF,
            VietnamDelightModBlocks.CERAMIC_TILES_STAIRS.get()
        );
        event.getBlockColors().register(
            (state, world, pos, tintIndex) ->
                world != null && pos != null && world.getBlockEntity(pos) instanceof CeramicTilesSlabBlockEntity be
                    ? be.getCurrentColor()
                    : 0xFFFFFF,
            VietnamDelightModBlocks.CERAMIC_TILES_SLAB.get()
        );
        event.getBlockColors().register(
            (state, world, pos, tintIndex) ->
                world != null && pos != null && world.getBlockEntity(pos) instanceof CeramicPlateBlockEntity be
                    ? be.getCurrentColor()
                    : 0xFFFFFF,
            VietnamDelightModBlocks.CERAMIC_PLATE.get()
        );
        event.getBlockColors().register(
            (state, world, pos, tintIndex) ->
                world != null && pos != null && world.getBlockEntity(pos) instanceof CeramicClayPotBlockEntity be
                    ? be.getCurrentColor()
                    : 0xFFFFFF,
            VietnamDelightModBlocks.CERAMIC_CLAY_POT.get()
        );
        event.getBlockColors().register(
            (state, world, pos, tintIndex) ->
                world != null && pos != null && world.getBlockEntity(pos) instanceof CeramicClayLidBlockEntity be
                    ? be.getCurrentColor()
                    : 0xFFFFFF,
            VietnamDelightModBlocks.CERAMIC_CLAY_LID.get()
        );
        event.getBlockColors().register(
            (state, world, pos, tintIndex) ->
                world != null && pos != null && world.getBlockEntity(pos) instanceof CeramicClayVaseBlockEntity be
                    ? be.getCurrentColor()
                    : 0xFFFFFF,
            VietnamDelightModBlocks.CERAMIC_CLAY_VASE.get()
        );
        event.getBlockColors().register(
            (state, world, pos, tintIndex) ->
                world != null && pos != null && world.getBlockEntity(pos) instanceof net.huwng.vietnamdelight.block.entity.IColorLayerBE be
                    ? be.getCurrentColor()
                    : 0xFFFFFF,
            VietnamDelightModBlocks.CERAMIC_CLAY_WALL.get(),
            VietnamDelightModBlocks.POLISHED_CERAMIC_CLAY.get(),
            VietnamDelightModBlocks.POLISHED_CERAMIC_CLAY_STAIRS.get(),
            VietnamDelightModBlocks.POLISHED_CERAMIC_CLAY_SLAB.get(),
            VietnamDelightModBlocks.POLISHED_CERAMIC_CLAY_WALL.get(),
            VietnamDelightModBlocks.CERAMIC_TILES_WALL.get()
        );
        event.getBlockColors().register(
            (state, world, pos, tintIndex) -> {
                if (tintIndex == 0) {
                    net.huwng.vietnamdelight.block.BougainvilleaTrunkBlock.LeafType leaf = state.getValue(net.huwng.vietnamdelight.block.BougainvilleaTrunkBlock.LEAF);
                    if (leaf == net.huwng.vietnamdelight.block.BougainvilleaTrunkBlock.LeafType.BIRCH) {
                        return net.minecraft.world.level.FoliageColor.getBirchColor();
                    } else if (leaf == net.huwng.vietnamdelight.block.BougainvilleaTrunkBlock.LeafType.SPRUCE) {
                        return net.minecraft.world.level.FoliageColor.getEvergreenColor();
                    } else if (leaf == net.huwng.vietnamdelight.block.BougainvilleaTrunkBlock.LeafType.OAK
                            || leaf == net.huwng.vietnamdelight.block.BougainvilleaTrunkBlock.LeafType.JUNGLE
                            || leaf == net.huwng.vietnamdelight.block.BougainvilleaTrunkBlock.LeafType.ACACIA
                            || leaf == net.huwng.vietnamdelight.block.BougainvilleaTrunkBlock.LeafType.DARK_OAK
                            || leaf == net.huwng.vietnamdelight.block.BougainvilleaTrunkBlock.LeafType.MANGROVE) {
                        return world != null && pos != null
                            ? net.minecraft.client.renderer.BiomeColors.getAverageFoliageColor(world, pos)
                            : net.minecraft.world.level.FoliageColor.getDefaultColor();
                    }
                }
                return 0xFFFFFF;
            },
            VietnamDelightModBlocks.BOUGAINVILLEA_TRUNK.get()
        );
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        ItemColors itemColors = event.getItemColors();

        itemColors.register(
            (stack, tintIndex) ->
                stack.hasTag() && stack.getTag().contains("BlockColor")
                    ? stack.getTag().getInt("BlockColor")
                    : 0xFFFFFF,
            VietnamDelightModItems.CERAMIC_PLATE.get()
        );
        
		itemColors.register(
            (stack, tintIndex) ->
                stack.hasTag() && stack.getTag().contains("BlockColor")
                    ? stack.getTag().getInt("BlockColor")
                    : 0xFFFFFF,
            VietnamDelightModItems.CERAMIC_CLAY_VASE.get()
        );
        
        itemColors.register(
            (stack, tintIndex) -> {
                if (stack.getItem() instanceof PaintbrushItem brush) {
                    return tintIndex == 0 ? brush.getBrushColor(stack) : -1;
                }
                return -1;
            },
            VietnamDelightModItems.PAINTBRUSH.get()
        );
    }

	@SuppressWarnings("unchecked")
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
	    event.registerBlockEntityRenderer(
	        (BlockEntityType<CeramicPlateBlockEntity>)(BlockEntityType<?>) VietnamDelightModBlockEntities.CERAMIC_PLATE.get(),
	        CeramicPlateRenderer::new
	    );
	
	    event.registerBlockEntityRenderer(
	        (BlockEntityType<CeramicClayPotBlockEntity>)(BlockEntityType<?>) VietnamDelightModBlockEntities.CERAMIC_CLAY_POT.get(),
	        CeramicClayPotRenderer::new
	    );
		event.registerBlockEntityRenderer(
	        (BlockEntityType<net.huwng.vietnamdelight.block.entity.BloomingGoldenFlamboyantLeavesBlockEntity>)(BlockEntityType<?>) VietnamDelightModBlockEntities.BLOOMING_GOLDEN_FLAMBOYANT_LEAVES.get(),
	        BloomingGoldenFlamboyantLeavesRenderer::new
	    );
		event.registerBlockEntityRenderer(
	        (BlockEntityType<net.huwng.vietnamdelight.block.entity.FloweringGoldenFlamboyantLeavesBlockEntity>)(BlockEntityType<?>) VietnamDelightModBlockEntities.FLOWERING_GOLDEN_FLAMBOYANT_LEAVES.get(),
	        FloweringGoldenFlamboyantLeavesRenderer::new
	    );
		event.registerBlockEntityRenderer(
	        VietnamDelightModBlockEntities.DURIAN_SIGN.get(),
	        net.minecraft.client.renderer.blockentity.SignRenderer::new
	    );
		event.registerBlockEntityRenderer(
	        VietnamDelightModBlockEntities.DURIAN_HANGING_SIGN.get(),
	        net.minecraft.client.renderer.blockentity.HangingSignRenderer::new
	    );
		event.registerBlockEntityRenderer(
	        VietnamDelightModBlockEntities.FLAMBOYANT_SIGN.get(),
	        net.minecraft.client.renderer.blockentity.SignRenderer::new
	    );
		event.registerBlockEntityRenderer(
	        VietnamDelightModBlockEntities.FLAMBOYANT_HANGING_SIGN.get(),
	        net.minecraft.client.renderer.blockentity.HangingSignRenderer::new
	    );
	}

	@SubscribeEvent
	public static void onClientSetup(net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent event) {
	    event.enqueueWork(() -> {
	        net.minecraft.client.renderer.Sheets.addWoodType(net.huwng.vietnamdelight.init.VietnamDelightWoodTypes.DURIAN);
	        net.minecraft.client.renderer.Sheets.addWoodType(net.huwng.vietnamdelight.init.VietnamDelightWoodTypes.FLAMBOYANT);
	        ItemBlockRenderTypes.setRenderLayer(VietnamDelightModBlocks.DURIAN_DOOR.get(), RenderType.cutout());
	        ItemBlockRenderTypes.setRenderLayer(VietnamDelightModBlocks.DURIAN_TRAPDOOR.get(), RenderType.cutout());
	        ItemBlockRenderTypes.setRenderLayer(VietnamDelightModBlocks.DURIAN_LEAVES.get(), RenderType.cutoutMipped());
	        ItemBlockRenderTypes.setRenderLayer(VietnamDelightModBlocks.DURIAN_SAPLING.get(), RenderType.cutout());
	        ItemBlockRenderTypes.setRenderLayer(VietnamDelightModBlocks.POTTED_DURIAN_SAPLING.get(), RenderType.cutout());
	        ItemBlockRenderTypes.setRenderLayer(VietnamDelightModBlocks.DURIAN_BUD.get(), RenderType.cutout());
	        ItemBlockRenderTypes.setRenderLayer(VietnamDelightModBlocks.DURIAN.get(), RenderType.cutout());
	        ItemBlockRenderTypes.setRenderLayer(VietnamDelightModBlocks.FLAMBOYANT_DOOR.get(), RenderType.cutout());
	        ItemBlockRenderTypes.setRenderLayer(VietnamDelightModBlocks.FLAMBOYANT_TRAPDOOR.get(), RenderType.cutout());
	        ItemBlockRenderTypes.setRenderLayer(VietnamDelightModBlocks.MARIGOLD.get(), RenderType.cutout());
	        ItemBlockRenderTypes.setRenderLayer(VietnamDelightModBlocks.POTTED_MARIGOLD.get(), RenderType.cutout());
	        ItemBlockRenderTypes.setRenderLayer(VietnamDelightModBlocks.MARIGOLD_BUSH.get(), RenderType.cutout());
	        ItemBlockRenderTypes.setRenderLayer(VietnamDelightModBlocks.HIBISCUS_FLOWER.get(), RenderType.cutout());
	        ItemBlockRenderTypes.setRenderLayer(VietnamDelightModBlocks.HIBISCUS_LEAVES.get(), RenderType.cutoutMipped());
	        ItemBlockRenderTypes.setRenderLayer(VietnamDelightModBlocks.FLOWERING_HIBISCUS_LEAVES.get(), RenderType.cutoutMipped());
	        ItemBlockRenderTypes.setRenderLayer(VietnamDelightModBlocks.BOUGAINVILLEA_SAPLING.get(), RenderType.cutout());
	        ItemBlockRenderTypes.setRenderLayer(VietnamDelightModBlocks.POTTED_BOUGAINVILLEA_SAPLING.get(), RenderType.cutout());
	        ItemBlockRenderTypes.setRenderLayer(VietnamDelightModBlocks.BOUGAINVILLEA.get(), RenderType.cutout());
	        ItemBlockRenderTypes.setRenderLayer(VietnamDelightModBlocks.DRIED_BOUGAINVILLEA.get(), RenderType.cutout());
	        ItemBlockRenderTypes.setRenderLayer(VietnamDelightModBlocks.BOUGAINVILLEA_LEAVES.get(), RenderType.cutoutMipped());
	        ItemBlockRenderTypes.setRenderLayer(VietnamDelightModBlocks.FLOWERING_ORANGE_BOUGAINVILLEA_LEAVES.get(), RenderType.cutoutMipped());
	        ItemBlockRenderTypes.setRenderLayer(VietnamDelightModBlocks.FLOWERING_PINK_BOUGAINVILLEA_LEAVES.get(), RenderType.cutoutMipped());
	        ItemBlockRenderTypes.setRenderLayer(VietnamDelightModBlocks.WHITE_BOUGAINVILLEA_LEAVES.get(), RenderType.cutoutMipped());
	        ItemBlockRenderTypes.setRenderLayer(VietnamDelightModBlocks.BOUGAINVILLEA_TRUNK.get(), RenderType.cutoutMipped());
	    });
	}
}

@Mod.EventBusSubscriber(modid = VietnamDelightMod.MODID, value = Dist.CLIENT)
class ClientCameraEvents {
    @SubscribeEvent
    public static void onCameraSetup(ViewportEvent.ComputeCameraAngles event) {
        ScreenShakeHandler.onCameraSetup(event);
    }
}

@Mod.EventBusSubscriber(modid = VietnamDelightMod.MODID, value = Dist.CLIENT)
class ClientPhantasmEvents {
    @SubscribeEvent
    public static void onClientTick(net.minecraftforge.event.TickEvent.ClientTickEvent event) {
        if (event.phase == net.minecraftforge.event.TickEvent.Phase.END) {
            net.huwng.vietnamdelight.client.overlay.PhantasmClientOverlay.clientTick();
        }
    }

    @SubscribeEvent
    public static void onRenderGuiOverlay(net.minecraftforge.client.event.RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay() == net.minecraftforge.client.gui.overlay.VanillaGuiOverlay.VIGNETTE.type()) {
            net.huwng.vietnamdelight.client.overlay.PhantasmClientOverlay.render(event.getGuiGraphics(), event.getWindow().getGuiScaledWidth(), event.getWindow().getGuiScaledHeight());
        }
    }

    @SubscribeEvent
    public static void onComputeFogColor(ViewportEvent.ComputeFogColor event) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.player != null && mc.player.hasEffect(net.huwng.vietnamdelight.init.VietnamDelightModMobEffects.PHANTASM.get())) {
            event.setRed(0.01F);
            event.setGreen(0.005F);
            event.setBlue(0.02F);
        }
    }

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.player != null && mc.player.hasEffect(net.huwng.vietnamdelight.init.VietnamDelightModMobEffects.PHANTASM.get())) {
            event.setNearPlaneDistance(1.0F);
            event.setFarPlaneDistance(22.0F);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPlayerLogOut(net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggingOut event) {
        net.huwng.vietnamdelight.client.overlay.PhantasmClientOverlay.disableShader(net.minecraft.client.Minecraft.getInstance());
    }
}

