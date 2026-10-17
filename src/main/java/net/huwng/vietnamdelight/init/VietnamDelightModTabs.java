package net.huwng.vietnamdelight.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;

import net.huwng.vietnamdelight.VietnamDelightMod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class VietnamDelightModTabs {
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, VietnamDelightMod.MODID);
	public static final RegistryObject<CreativeModeTab> VIET_NAM_DELIGHT = REGISTRY.register("viet_nam_delight",
			() -> CreativeModeTab.builder().title(Component.translatable("item_group.vietnam_delight.viet_nam_delight")).icon(() -> new ItemStack(VietnamDelightModBlocks.VIET_NAM_FLAG.get())).displayItems((parameters, tabData) -> {
				// ===== Thông tin mod =====
				tabData.accept(VietnamDelightModItems.WARNING.get());
				tabData.accept(VietnamDelightModBlocks.VIET_NAM_FLAG.get().asItem());

				// ===== Dụng cụ =====
				tabData.accept(VietnamDelightModItems.PAINTBRUSH.get());
				tabData.accept(VietnamDelightModItems.LANTERN_ROPE.get());
				tabData.accept(VietnamDelightModItems.OVERGROW_MEAL.get());
				tabData.accept(VietnamDelightModBlocks.PAPER_LANTERN.get().asItem());
				tabData.accept(VietnamDelightModBlocks.SALT_LANTERN.get().asItem());
				tabData.accept(VietnamDelightModBlocks.STAR_LANTERN.get().asItem());
				tabData.accept(VietnamDelightModItems.SKY_LANTERN.get());
				tabData.accept(VietnamDelightModItems.CANDLE_HOLDER.get());
				tabData.accept(VietnamDelightModItems.PURIFYING_CANDLE.get());
				tabData.accept(VietnamDelightModItems.RAGE_CANDLE.get());
				tabData.accept(VietnamDelightModItems.ARID_CANDLE.get());
				tabData.accept(VietnamDelightModItems.SUNBURST_CANDLE.get());
				tabData.accept(VietnamDelightModItems.PHANTASM_CANDLE.get());
				tabData.accept(VietnamDelightModItems.MARIGOLD.get());
				tabData.accept(VietnamDelightModItems.MARIGOLD_BUSH.get());
				tabData.accept(VietnamDelightModBlocks.HIBISCUS_FLOWER.get().asItem());
				tabData.accept(VietnamDelightModBlocks.HIBISCUS_LEAVES.get().asItem());
				tabData.accept(VietnamDelightModBlocks.FLOWERING_HIBISCUS_LEAVES.get().asItem());
				tabData.accept(VietnamDelightModBlocks.BOUGAINVILLEA_SAPLING.get().asItem());
				tabData.accept(VietnamDelightModBlocks.BOUGAINVILLEA.get().asItem());
				tabData.accept(VietnamDelightModBlocks.DRIED_BOUGAINVILLEA.get().asItem());
				tabData.accept(VietnamDelightModBlocks.BOUGAINVILLEA_LEAVES.get().asItem());
				tabData.accept(VietnamDelightModBlocks.FLOWERING_ORANGE_BOUGAINVILLEA_LEAVES.get().asItem());
				tabData.accept(VietnamDelightModBlocks.FLOWERING_PINK_BOUGAINVILLEA_LEAVES.get().asItem());
				tabData.accept(VietnamDelightModBlocks.WHITE_BOUGAINVILLEA_LEAVES.get().asItem());
				tabData.accept(VietnamDelightModBlocks.BOUGAINVILLEA_TRUNK.get().asItem());

				tabData.accept(VietnamDelightModBlocks.GINGER_CRATE.get().asItem());
				tabData.accept(VietnamDelightModBlocks.TURMERIC_CRATE.get().asItem());
				tabData.accept(VietnamDelightModBlocks.BIRD_EYE_CHILIS_CRATE.get().asItem());
				tabData.accept(VietnamDelightModBlocks.LEMONGRASS_CRATE.get().asItem());
				tabData.accept(VietnamDelightModBlocks.SCALLION_CRATE.get().asItem());
				tabData.accept(VietnamDelightModBlocks.GARLIC_CHIVES_CRATE.get().asItem());
				tabData.accept(VietnamDelightModBlocks.GARLIC_CRATE.get().asItem());
				tabData.accept(VietnamDelightModBlocks.SHALLOT_CRATE.get().asItem());
				tabData.accept(VietnamDelightModBlocks.LOLOT_LEAF_CRATE.get().asItem());
				tabData.accept(VietnamDelightModBlocks.GREEN_KUMQUAT_CRATE.get().asItem());
				tabData.accept(VietnamDelightModBlocks.KUMQUAT_CRATE.get().asItem());
				tabData.accept(VietnamDelightModBlocks.LIME_CRATE.get().asItem());
				tabData.accept(VietnamDelightModBlocks.LOTUS_RHIZOME_CRATE.get().asItem());
				tabData.accept(VietnamDelightModBlocks.YOUNG_LOTUS_STEM_CRATE.get().asItem());

				tabData.accept(VietnamDelightModBlocks.PANDAN_LEAF_BUNDLE.get().asItem());

				tabData.accept(VietnamDelightModBlocks.BLACK_PEPPERCORN_BAG.get().asItem());
				tabData.accept(VietnamDelightModBlocks.GREEN_PEPPERCORN_BAG.get().asItem());
				tabData.accept(VietnamDelightModBlocks.WHITE_PEPPERCORN_BAG.get().asItem());
				tabData.accept(VietnamDelightModBlocks.MUNG_BEANS_BAG.get().asItem());
				tabData.accept(VietnamDelightModBlocks.BEAN_SPROUTS_BAG.get().asItem());
				tabData.accept(VietnamDelightModBlocks.LOTUS_SEEDS_BAG.get().asItem());


				tabData.accept(VietnamDelightModBlocks.WILD_GINGERS.get().asItem());
				tabData.accept(VietnamDelightModBlocks.WILD_TURMERIC.get().asItem());
				tabData.accept(VietnamDelightModBlocks.WILD_BIRD_EYE_CHILIS.get().asItem());
				tabData.accept(VietnamDelightModBlocks.WILD_PANDAN.get().asItem());
				tabData.accept(VietnamDelightModBlocks.WILD_LEMONGRASS.get().asItem());
				tabData.accept(VietnamDelightModBlocks.WILD_SCALLION.get().asItem());
				tabData.accept(VietnamDelightModBlocks.WILD_GARLIC_CHIVES.get().asItem());
				tabData.accept(VietnamDelightModBlocks.WILD_GARLIC.get().asItem());
				tabData.accept(VietnamDelightModBlocks.WILD_SHALLOT.get().asItem());
				tabData.accept(VietnamDelightModBlocks.WILD_LOTOT.get().asItem());
				tabData.accept(VietnamDelightModBlocks.WILD_MUNG_BEAN.get().asItem());
				tabData.accept(VietnamDelightModBlocks.WILD_BLACK_PEPPERCORN.get().asItem());
				tabData.accept(VietnamDelightModBlocks.WILD_GREEN_PEPPERCORN.get().asItem());
				tabData.accept(VietnamDelightModBlocks.WILD_KUMQUAT_BUSH.get().asItem());
				tabData.accept(VietnamDelightModBlocks.WILD_LIME_TREE.get().asItem());
				tabData.accept(VietnamDelightModBlocks.LIME_TREE.get().asItem());
				tabData.accept(VietnamDelightModBlocks.KUMQUAT_BUSH.get().asItem());

				// ===== Gừng =====
				tabData.accept(VietnamDelightModBlocks.BANANA_BUNDLE.get().asItem());
				tabData.accept(VietnamDelightModBlocks.GREEN_BANANA_BUNDLE.get().asItem());
				tabData.accept(VietnamDelightModBlocks.WAXED_GREEN_BANANA_BUNDLE.get().asItem());
				tabData.accept(VietnamDelightModBlocks.NIPA_FRUIT.get().asItem());
				tabData.accept(VietnamDelightModBlocks.OVERGROW_GINGER.get().asItem());
				tabData.accept(VietnamDelightModBlocks.LOTUS_ROOT.get().asItem());
				tabData.accept(VietnamDelightModBlocks.BANANA_ROOT.get().asItem());
				tabData.accept(VietnamDelightModBlocks.MUDDY_NIPA_ROOTS.get().asItem());
				tabData.accept(VietnamDelightModBlocks.NIPA_ROOT.get().asItem());
				tabData.accept(VietnamDelightModBlocks.NIPA_ROOTS.get().asItem());
				tabData.accept(VietnamDelightModBlocks.SALT_BLOCK.get().asItem());
				tabData.accept(VietnamDelightModBlocks.SALT_CLUSTER.get().asItem());

				tabData.accept(VietnamDelightModBlocks.LOTUS_LILYPAD.get().asItem());
				tabData.accept(VietnamDelightModBlocks.LOTUS_FLOWER.get().asItem());
				tabData.accept(VietnamDelightModBlocks.WHITE_LOTUS_FLOWER.get().asItem());
				tabData.accept(VietnamDelightModBlocks.PURPLE_LOTUS_FLOWER.get().asItem());
				tabData.accept(VietnamDelightModBlocks.YELLOW_LOTUS_FLOWER.get().asItem());
				tabData.accept(VietnamDelightModBlocks.BANYAN_FLOWER.get().asItem());
				tabData.accept(VietnamDelightModBlocks.FLAMBOYANT_FLOWER.get().asItem());
				tabData.accept(VietnamDelightModBlocks.FLAMBOYANT_PETALS.get().asItem());
				tabData.accept(VietnamDelightModBlocks.GOLDEN_FLAMBOYANT_FLOWER.get().asItem());
				tabData.accept(VietnamDelightModBlocks.GOLDEN_FLAMBOYANT_PETALS.get().asItem());
				tabData.accept(VietnamDelightModBlocks.TAMARIND_FLOWERS.get().asItem());
				tabData.accept(VietnamDelightModItems.MARIGOLD.get());
				tabData.accept(VietnamDelightModItems.MARIGOLD_BUSH.get());
				tabData.accept(VietnamDelightModBlocks.HIBISCUS_FLOWER.get().asItem());
				tabData.accept(VietnamDelightModBlocks.HIBISCUS_LEAVES.get().asItem());
				tabData.accept(VietnamDelightModBlocks.FLOWERING_HIBISCUS_LEAVES.get().asItem());
				tabData.accept(VietnamDelightModBlocks.BOUGAINVILLEA_SAPLING.get().asItem());
				tabData.accept(VietnamDelightModBlocks.BOUGAINVILLEA.get().asItem());
				tabData.accept(VietnamDelightModBlocks.DRIED_BOUGAINVILLEA.get().asItem());
				tabData.accept(VietnamDelightModBlocks.BOUGAINVILLEA_LEAVES.get().asItem());
				tabData.accept(VietnamDelightModBlocks.FLOWERING_ORANGE_BOUGAINVILLEA_LEAVES.get().asItem());
				tabData.accept(VietnamDelightModBlocks.FLOWERING_PINK_BOUGAINVILLEA_LEAVES.get().asItem());
				tabData.accept(VietnamDelightModBlocks.WHITE_BOUGAINVILLEA_LEAVES.get().asItem());
				tabData.accept(VietnamDelightModBlocks.BOUGAINVILLEA_TRUNK.get().asItem());

				tabData.accept(VietnamDelightModBlocks.CICADAS.get().asItem());
				tabData.accept(VietnamDelightModBlocks.BANYAN_SAPLING.get().asItem());
				tabData.accept(VietnamDelightModBlocks.BANYAN_LEAVES.get().asItem());
				tabData.accept(VietnamDelightModBlocks.FLOWERING_BANYAN_LEAVES.get().asItem());
				tabData.accept(VietnamDelightModBlocks.BANYAN_VINE.get().asItem());
				tabData.accept(VietnamDelightModBlocks.BANYAN_ROOTS.get().asItem());

				tabData.accept(VietnamDelightModBlocks.FLAMBOYANT_SAPLING.get().asItem());
				tabData.accept(VietnamDelightModBlocks.FLAMBOYANT_LEAVES.get().asItem());
				tabData.accept(VietnamDelightModBlocks.FLOWERING_FLAMBOYANT_LEAVES.get().asItem());
				tabData.accept(VietnamDelightModBlocks.BLOOMING_FLAMBOYANT_LEAVES.get().asItem());

				tabData.accept(VietnamDelightModBlocks.GOLDEN_FLAMBOYANT_SAPLING.get().asItem());
				tabData.accept(VietnamDelightModBlocks.FLOWERING_GOLDEN_FLAMBOYANT_LEAVES.get().asItem());
				tabData.accept(VietnamDelightModBlocks.BLOOMING_GOLDEN_FLAMBOYANT_LEAVES.get().asItem());

				tabData.accept(VietnamDelightModBlocks.BANANA_SPROUT.get().asItem());
				tabData.accept(VietnamDelightModBlocks.BANANA_PSEUDOSTEM.get().asItem());
				tabData.accept(VietnamDelightModItems.BANANA_BOAT.get());

				tabData.accept(VietnamDelightModItems.BANANA_LEAF.get());
				tabData.accept(VietnamDelightModItems.DRIED_BANANA_LEAF.get());
				tabData.accept(VietnamDelightModItems.NIPA_LEAF.get());

				tabData.accept(VietnamDelightModBlocks.TAMARIND_SAPLING.get().asItem());
				tabData.accept(VietnamDelightModBlocks.TAMARIND_LEAVES.get().asItem());
				tabData.accept(VietnamDelightModBlocks.FLOWERING_TAMARIND_LEAVES.get().asItem());

				tabData.accept(VietnamDelightModItems.DURIAN_SEED.get());
				tabData.accept(VietnamDelightModBlocks.DURIAN_LEAVES.get().asItem());
				tabData.accept(VietnamDelightModBlocks.DURIAN.get().asItem());
				tabData.accept(VietnamDelightModItems.DURIAN_FLESH.get());
				tabData.accept(VietnamDelightModItems.DURIAN_SHELL.get());
				tabData.accept(VietnamDelightModItems.EXPLOSIVE_DURIAN_SHELL.get());

				// ===== Sinh vật & thực vật dưới nước =====
				tabData.accept(VietnamDelightModItems.APPLE_SNAIL.get());
				tabData.accept(VietnamDelightModBlocks.FISH_TRAP.get().asItem());
				tabData.accept(VietnamDelightModBlocks.WATER_LETTUCE.get().asItem());
				tabData.accept(VietnamDelightModBlocks.DUCKWEED.get().asItem());
				tabData.accept(VietnamDelightModBlocks.WATER_PRIMROSE.get().asItem());
				tabData.accept(VietnamDelightModItems.BUNCH_OF_WATER_HYACINTH.get());

				// ===== Khối xây dựng & trang trí =====
				// ===== Gỗ sầu riêng (Durian Wood) =====
				tabData.accept(VietnamDelightModBlocks.DURIAN_LOG.get().asItem());
				tabData.accept(VietnamDelightModBlocks.DURIAN_WOOD.get().asItem());
				tabData.accept(VietnamDelightModBlocks.STRIPPED_DURIAN_LOG.get().asItem());
				tabData.accept(VietnamDelightModBlocks.STRIPPED_DURIAN_WOOD.get().asItem());
				tabData.accept(VietnamDelightModBlocks.DURIAN_PLANKS.get().asItem());
				tabData.accept(VietnamDelightModBlocks.DURIAN_STAIRS.get().asItem());
				tabData.accept(VietnamDelightModBlocks.DURIAN_SLAB.get().asItem());
				tabData.accept(VietnamDelightModBlocks.DURIAN_FENCE.get().asItem());
				tabData.accept(VietnamDelightModBlocks.DURIAN_FENCE_GATE.get().asItem());
				tabData.accept(VietnamDelightModBlocks.DURIAN_DOOR.get().asItem());
				tabData.accept(VietnamDelightModBlocks.DURIAN_TRAPDOOR.get().asItem());
				tabData.accept(VietnamDelightModBlocks.DURIAN_PRESSURE_PLATE.get().asItem());
				tabData.accept(VietnamDelightModBlocks.DURIAN_BUTTON.get().asItem());
				tabData.accept(VietnamDelightModItems.DURIAN_SIGN.get());
				tabData.accept(VietnamDelightModItems.DURIAN_HANGING_SIGN.get());

				// ===== Gỗ phượng (Flamboyant Wood) =====
				tabData.accept(VietnamDelightModBlocks.FLAMBOYANT_LOG.get().asItem());
				tabData.accept(VietnamDelightModBlocks.FLAMBOYANT_WOOD.get().asItem());
				tabData.accept(VietnamDelightModBlocks.STRIPPED_FLAMBOYANT_LOG.get().asItem());
				tabData.accept(VietnamDelightModBlocks.STRIPPED_FLAMBOYANT_WOOD.get().asItem());
				tabData.accept(VietnamDelightModBlocks.FLAMBOYANT_PLANKS.get().asItem());
				tabData.accept(VietnamDelightModBlocks.FLAMBOYANT_STAIRS.get().asItem());
				tabData.accept(VietnamDelightModBlocks.FLAMBOYANT_SLAB.get().asItem());
				tabData.accept(VietnamDelightModBlocks.FLAMBOYANT_FENCE.get().asItem());
				tabData.accept(VietnamDelightModBlocks.FLAMBOYANT_FENCE_GATE.get().asItem());
				tabData.accept(VietnamDelightModBlocks.FLAMBOYANT_DOOR.get().asItem());
				tabData.accept(VietnamDelightModBlocks.FLAMBOYANT_TRAPDOOR.get().asItem());
				tabData.accept(VietnamDelightModBlocks.FLAMBOYANT_PRESSURE_PLATE.get().asItem());
				tabData.accept(VietnamDelightModBlocks.FLAMBOYANT_BUTTON.get().asItem());
				tabData.accept(VietnamDelightModItems.FLAMBOYANT_SIGN.get());
				tabData.accept(VietnamDelightModItems.FLAMBOYANT_HANGING_SIGN.get());

				tabData.accept(VietnamDelightModItems.FELSPAT.get());
				tabData.accept(VietnamDelightModBlocks.FELSPAT_BLOCK.get().asItem());
				tabData.accept(VietnamDelightModBlocks.FELSPAT_STAIRS.get().asItem());
				tabData.accept(VietnamDelightModBlocks.FELSPAT_SLAB.get().asItem());
				tabData.accept(VietnamDelightModBlocks.FELSPAT_WALL.get().asItem());
				tabData.accept(VietnamDelightModBlocks.POLISHED_FELSPAT.get().asItem());
				tabData.accept(VietnamDelightModBlocks.POLISHED_FELSPAT_STAIRS.get().asItem());
				tabData.accept(VietnamDelightModBlocks.POLISHED_FELSPAT_SLAB.get().asItem());
				tabData.accept(VietnamDelightModBlocks.POLISHED_FELSPAT_WALL.get().asItem());
				tabData.accept(VietnamDelightModBlocks.FELSPAT_BRICKS.get().asItem());
				tabData.accept(VietnamDelightModBlocks.FELSPAT_BRICK_STAIRS.get().asItem());
				tabData.accept(VietnamDelightModBlocks.FELSPAT_BRICK_SLAB.get().asItem());
				tabData.accept(VietnamDelightModBlocks.FELSPAT_BRICK_WALL.get().asItem());
				tabData.accept(VietnamDelightModItems.CERAMIC_CLAY.get());
				tabData.accept(VietnamDelightModBlocks.CERAMIC_CLAY_BLOCK.get().asItem());
				tabData.accept(VietnamDelightModBlocks.CERAMIC_CLAY_STAIRS.get().asItem());
				tabData.accept(VietnamDelightModBlocks.CERAMIC_CLAY_SLAB.get().asItem());
				tabData.accept(VietnamDelightModBlocks.CERAMIC_CLAY_WALL.get().asItem());
				tabData.accept(VietnamDelightModBlocks.POLISHED_CERAMIC_CLAY.get().asItem());
				tabData.accept(VietnamDelightModBlocks.POLISHED_CERAMIC_CLAY_STAIRS.get().asItem());
				tabData.accept(VietnamDelightModBlocks.POLISHED_CERAMIC_CLAY_SLAB.get().asItem());
				tabData.accept(VietnamDelightModBlocks.POLISHED_CERAMIC_CLAY_WALL.get().asItem());
				tabData.accept(VietnamDelightModBlocks.CERAMIC_TILES.get().asItem());
				tabData.accept(VietnamDelightModBlocks.CERAMIC_TILES_STAIRS.get().asItem());
				tabData.accept(VietnamDelightModBlocks.CERAMIC_TILES_SLAB.get().asItem());
				tabData.accept(VietnamDelightModBlocks.CERAMIC_TILES_WALL.get().asItem());
				tabData.accept(VietnamDelightModBlocks.CERAMIC_PLATE.get().asItem());
				tabData.accept(VietnamDelightModBlocks.CERAMIC_CLAY_VASE.get().asItem());
				tabData.accept(VietnamDelightModBlocks.CERAMIC_CLAY_POT.get().asItem());
				tabData.accept(VietnamDelightModBlocks.CERAMIC_CLAY_LID.get().asItem());


				tabData.accept(VietnamDelightModItems.GINGER.get());
				tabData.accept(VietnamDelightModItems.TURMERIC.get());
				tabData.accept(VietnamDelightModItems.BIRD_EYE_CHILIS.get());
				tabData.accept(VietnamDelightModItems.PANDAN_LEAF.get());
				tabData.accept(VietnamDelightModItems.LEMONGRASS.get());
				tabData.accept(VietnamDelightModItems.SCALLION.get());
				tabData.accept(VietnamDelightModItems.GARLIC_CHIVES.get());
				tabData.accept(VietnamDelightModItems.GARLIC.get());
				tabData.accept(VietnamDelightModItems.SHALLOT.get());
				tabData.accept(VietnamDelightModItems.LOTOT_LEAF.get());
				tabData.accept(VietnamDelightModItems.LIME.get());
				tabData.accept(VietnamDelightModItems.GREEN_KUMQUAT.get());
				tabData.accept(VietnamDelightModItems.KUMQUAT.get());
				tabData.accept(VietnamDelightModItems.GREEN_BANANA_BUNCH.get());
				tabData.accept(VietnamDelightModItems.BANANA_BUNCH.get());
				tabData.accept(VietnamDelightModItems.BANANA.get());
				tabData.accept(VietnamDelightModItems.NIPA_NUT.get());
				tabData.accept(VietnamDelightModItems.TAMARIND.get());

				tabData.accept(VietnamDelightModItems.SALT.get());
				tabData.accept(VietnamDelightModItems.TURMERIC_POWDER.get());
				tabData.accept(VietnamDelightModItems.MUNG_BEANS.get());
				tabData.accept(VietnamDelightModItems.LOTUS_SEEDS.get());
				tabData.accept(VietnamDelightModItems.PANDAN_SPROUT.get());
				tabData.accept(VietnamDelightModItems.BEAN_SPROUTS.get());
				tabData.accept(VietnamDelightModItems.BIRD_EYE_CHILIS_SEEDS.get());
				tabData.accept(VietnamDelightModItems.LOTOT_SEEDS.get());
				tabData.accept(VietnamDelightModItems.BLACK_PEPPERCORN.get());
				tabData.accept(VietnamDelightModItems.GREEN_PEPPERCORN.get());
				tabData.accept(VietnamDelightModItems.WHITE_PEPPERCORN.get());
				tabData.accept(VietnamDelightModItems.LIME_SEEDS.get());
				tabData.accept(VietnamDelightModItems.KUMQUAT_SEEDS.get());

				tabData.accept(VietnamDelightModItems.SALT_SHAKER.get());
				tabData.accept(VietnamDelightModItems.PEPPER_SHAKER.get());
				tabData.accept(VietnamDelightModItems.TURMERIC_POWDER_SHAKER.get());
				tabData.accept(VietnamDelightModItems.GARLIC_CHILI_SAUCE.get());
				tabData.accept(VietnamDelightModItems.FISH_SAUCE.get());
				tabData.accept(VietnamDelightModItems.MOLASSES.get());

				tabData.accept(VietnamDelightModItems.PICKLED_MUSTARD_GREENS.get());
				tabData.accept(VietnamDelightModItems.SALTED_EGG.get());

				tabData.accept(VietnamDelightModItems.LOTUS_EMBRYO_TEA.get());
				tabData.accept(VietnamDelightModItems.WHITE_LOTUS_FLOWER_TEA.get());
				tabData.accept(VietnamDelightModItems.PURPLE_LOTUS_FLOWER_TEA.get());
				tabData.accept(VietnamDelightModItems.YELLOW_LOTUS_FLOWER_TEA.get());
				tabData.accept(VietnamDelightModItems.BANYAN_FLOWER_TEA.get());

				tabData.accept(VietnamDelightModItems.LIME_SLICE.get());
				tabData.accept(VietnamDelightModItems.LOTUS_RHIZOME_SLICE.get());
				tabData.accept(VietnamDelightModItems.LOTUS_EMBRYO.get());
				tabData.accept(VietnamDelightModItems.YOUNG_LOTUS_STEM.get());
				tabData.accept(VietnamDelightModItems.LOTUS_RHIZOME.get());
				tabData.accept(VietnamDelightModItems.BANANA_RHIZOME.get());
				tabData.accept(VietnamDelightModItems.BANANA_POWDER.get());
				tabData.accept(VietnamDelightModItems.BANANA_STEM.get());
				tabData.accept(VietnamDelightModItems.BANANA_BARK.get());
				tabData.accept(VietnamDelightModItems.BANANA_BLOSSOM.get());
				tabData.accept(VietnamDelightModItems.BANANA_BLOSSOM_SHREDDED.get());
				tabData.accept(VietnamDelightModItems.NIPA_FLESH.get());
				// ===== Món ăn: Cơm & xôi =====
				tabData.accept(VietnamDelightModItems.PLAIN_STICKY_RICE.get());
				tabData.accept(VietnamDelightModItems.COCONUT_STICKY_RICE.get());
				tabData.accept(VietnamDelightModItems.GAC_STICKY_RICE.get());
				tabData.accept(VietnamDelightModItems.GREEN_STICKY_RICE.get());
				tabData.accept(VietnamDelightModItems.MUNG_BEAN_STICKY_RICE.get());
				tabData.accept(VietnamDelightModItems.PURPLE_STICKY_RICE.get());
				tabData.accept(VietnamDelightModItems.FIVE_COLORED_STICKY_RICE.get());
				tabData.accept(VietnamDelightModItems.RICE_WITH_MELON.get());
				tabData.accept(VietnamDelightModItems.COOKED_PANDAN_RICE.get());

				// ===== Món ăn: Canh & súp =====
				tabData.accept(VietnamDelightModItems.SOUR_SOUP.get());
				tabData.accept(VietnamDelightModItems.BANANA_BLOSSOM_SOUR_SOUP.get());
				tabData.accept(VietnamDelightModItems.LOTUS_RHIZOME_PORK_SOUP.get());
				tabData.accept(VietnamDelightModItems.LOTUS_STEM_CHICKEN_PANDAN_SOUP.get());
				tabData.accept(VietnamDelightModItems.LOTUS_RHIZOME_SWEET_SOUP.get());
				tabData.accept(VietnamDelightModItems.LOTUS_SEED_SWEET_SOUP.get());
				tabData.accept(VietnamDelightModItems.DURIAN_SWEET_SOUP.get());
				tabData.accept(VietnamDelightModItems.DURIAN_ICE_CREAM.get());

				// ===== Món ăn: Gỏi & salad =====
				tabData.accept(VietnamDelightModItems.BANANA_BLOSSOM_SALAD.get());
				tabData.accept(VietnamDelightModItems.LOTUS_STEM_SALAD.get());

				// ===== Món ăn: Món mặn chính =====
				tabData.accept(VietnamDelightModItems.CARAMELIZED_BRAISED_PORK.get());
				tabData.accept(VietnamDelightModItems.SWEET_AND_SOUR_PORK_RIBS.get());
				tabData.accept(VietnamDelightModItems.STIRFRIED_BANANA_RHIZOME.get());

				// ===== Món ăn: Món ngọt & bánh =====
				tabData.accept(VietnamDelightModItems.FRIED_BANANA.get());
				tabData.accept(VietnamDelightModItems.BANANA_CAKE.get());
				tabData.accept(VietnamDelightModItems.UNBAKED_MOONCAKE.get());
				tabData.accept(VietnamDelightModItems.BAKED_MOONCAKE.get());
				tabData.accept(VietnamDelightModItems.ASSORTED_MOONCAKE.get());
				tabData.accept(VietnamDelightModItems.LOTUS_SEED_MOONCAKE.get());
				tabData.accept(VietnamDelightModItems.MUNG_BEAN_MOONCAKE.get());
				tabData.accept(VietnamDelightModItems.TARO_MOONCAKE.get());
				tabData.accept(VietnamDelightModItems.SALTED_EGG_MOONCAKE.get());
				tabData.accept(VietnamDelightModItems.PIA_CAKE.get());

				// ===== Món ăn: Trà & thức uống =====

			}).build());

	@SubscribeEvent
	public static void buildTabContentsVanilla(BuildCreativeModeTabContentsEvent tabData) {
		if (tabData.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
			tabData.accept(VietnamDelightModItems.JADE_RABBIT_SPAWN_EGG.get());
		}
		if (tabData.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
			tabData.accept(VietnamDelightModItems.MARIGOLD.get());
			tabData.accept(VietnamDelightModItems.MARIGOLD_BUSH.get());
			tabData.accept(VietnamDelightModBlocks.HIBISCUS_FLOWER.get().asItem());
			tabData.accept(VietnamDelightModBlocks.HIBISCUS_LEAVES.get().asItem());
			tabData.accept(VietnamDelightModBlocks.FLOWERING_HIBISCUS_LEAVES.get().asItem());
			tabData.accept(VietnamDelightModBlocks.BOUGAINVILLEA_SAPLING.get().asItem());
			tabData.accept(VietnamDelightModBlocks.BOUGAINVILLEA.get().asItem());
			tabData.accept(VietnamDelightModBlocks.DRIED_BOUGAINVILLEA.get().asItem());
			tabData.accept(VietnamDelightModBlocks.BOUGAINVILLEA_LEAVES.get().asItem());
			tabData.accept(VietnamDelightModBlocks.FLOWERING_ORANGE_BOUGAINVILLEA_LEAVES.get().asItem());
			tabData.accept(VietnamDelightModBlocks.FLOWERING_PINK_BOUGAINVILLEA_LEAVES.get().asItem());
			tabData.accept(VietnamDelightModBlocks.WHITE_BOUGAINVILLEA_LEAVES.get().asItem());
			tabData.accept(VietnamDelightModBlocks.BOUGAINVILLEA_TRUNK.get().asItem());
		}
	}
}
