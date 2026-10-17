package net.huwng.vietnamdelight.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.level.block.Block;

import net.huwng.vietnamdelight.block.CicadasBlock;
import net.huwng.vietnamdelight.block.YoungLotusStemCrateBlock;
import net.huwng.vietnamdelight.block.YellowLotusFlowerBlock;
import net.huwng.vietnamdelight.block.WildWhiteLotusFlowerBlock;
import net.huwng.vietnamdelight.block.WildWhiteLotusBudBlock;
import net.huwng.vietnamdelight.block.WildTurmericBlock;
import net.huwng.vietnamdelight.block.WildShallotBlock;
import net.huwng.vietnamdelight.block.WildScallionBlock;
import net.huwng.vietnamdelight.block.WildPurpleLotusFlowerBlock;
import net.huwng.vietnamdelight.block.WildPurpleLotusBudBlock;
import net.huwng.vietnamdelight.block.WildPandanBlock;
import net.huwng.vietnamdelight.block.WildMungBeanBlock;
import net.huwng.vietnamdelight.block.WildLotusSeedPodBlock;
import net.huwng.vietnamdelight.block.WildLotusSeedPod2Block;
import net.huwng.vietnamdelight.block.WildLotusLilypad3Block;
import net.huwng.vietnamdelight.block.WildLotusLilypad2Block;
import net.huwng.vietnamdelight.block.WildLotusLilypad1Block;
import net.huwng.vietnamdelight.block.WildLotusLilypad0Block;
import net.huwng.vietnamdelight.block.WildLotusLeafBlock;
import net.huwng.vietnamdelight.block.WildLotusFlowerBlock;
import net.huwng.vietnamdelight.block.WildLotusBudBlock;
import net.huwng.vietnamdelight.block.WildLototBlock;
import net.huwng.vietnamdelight.block.WildLimeTreeBlock;
import net.huwng.vietnamdelight.block.WildLemongrassBlock;
import net.huwng.vietnamdelight.block.WildKumquatBushBlock;
import net.huwng.vietnamdelight.block.WildGreenPeppercornBlock;
import net.huwng.vietnamdelight.block.WildGingersBlock;
import net.huwng.vietnamdelight.block.WildGarlicChivesFlowerBlock;
import net.huwng.vietnamdelight.block.WildGarlicChivesBudBlock;
import net.huwng.vietnamdelight.block.WildGarlicChivesBlock;
import net.huwng.vietnamdelight.block.WildGarlicBlock;
import net.huwng.vietnamdelight.block.WildBlackPeppercornBlock;
import net.huwng.vietnamdelight.block.WildBirdEyeChilisBlock;
import net.huwng.vietnamdelight.block.WhitePeppercornCropBlock;
import net.huwng.vietnamdelight.block.WhitePeppercornBagBlock;
import net.huwng.vietnamdelight.block.WhiteLotusFlowerBlock;
import net.huwng.vietnamdelight.block.WaxedGreenBananaBundleBlock;
import net.huwng.vietnamdelight.block.WaterPrimroseYellowFlowerBlock;
import net.huwng.vietnamdelight.block.WaterPrimroseFlowerBlock;
import net.huwng.vietnamdelight.block.WaterPrimroseBlock;
import net.huwng.vietnamdelight.block.WaterLettuceBlock;
import net.huwng.vietnamdelight.block.WaterHyacinthStemBlock;
import net.huwng.vietnamdelight.block.WaterHyacinthLayerBlock;
import net.huwng.vietnamdelight.block.WaterHyacinthFlowerStemBlock;
import net.huwng.vietnamdelight.block.WaterHyacinthFlowerBlock;
import net.huwng.vietnamdelight.block.WaterHyacinthBlock;
import net.huwng.vietnamdelight.block.WallBananaFrondBlock;
import net.huwng.vietnamdelight.block.VietNamFlagBlock;
import net.huwng.vietnamdelight.block.TurmericCropBlock;
import net.huwng.vietnamdelight.block.TurmericCrateBlock;
import net.huwng.vietnamdelight.block.TamarindSaplingBlock;
import net.huwng.vietnamdelight.block.TamarindLeavesBlock;
import net.huwng.vietnamdelight.block.TamarindFlowersBlock;
import net.huwng.vietnamdelight.block.StarLanternBlock;
import net.huwng.vietnamdelight.block.ShallotCropBlock;
import net.huwng.vietnamdelight.block.ShallotCrateBlock;
import net.huwng.vietnamdelight.block.ScallionCropBlock;
import net.huwng.vietnamdelight.block.ScallionCrateBlock;
import net.huwng.vietnamdelight.block.SaltLanternBlock;
import net.huwng.vietnamdelight.block.SaltClusterBlock;
import net.huwng.vietnamdelight.block.SaltBlockBlock;
import net.huwng.vietnamdelight.block.RopeNodesBlock;
import net.huwng.vietnamdelight.block.PurpleLotusFlowerBlock;
import net.huwng.vietnamdelight.block.PottedKumquatBushBlock;
import net.huwng.vietnamdelight.block.PaperLanternBlock;
import net.huwng.vietnamdelight.block.PandanLeafBundleBlock;
import net.huwng.vietnamdelight.block.PandanCropBlock;
import net.huwng.vietnamdelight.block.OvergrowGingerBlock;
import net.huwng.vietnamdelight.block.NipaRootsBlock;
import net.huwng.vietnamdelight.block.NipaRootBlock;
import net.huwng.vietnamdelight.block.NipaPropaguleBlock;
import net.huwng.vietnamdelight.block.NipaPostBlock;
import net.huwng.vietnamdelight.block.NipaFruitCropBlock;
import net.huwng.vietnamdelight.block.NipaFruitBlock;
import net.huwng.vietnamdelight.block.NipaFrondSidesBlock;
import net.huwng.vietnamdelight.block.NipaFrondPostBlock;
import net.huwng.vietnamdelight.block.NipaFrondBlock;
import net.huwng.vietnamdelight.block.MungBeansCropBlock;
import net.huwng.vietnamdelight.block.MungBeansBagBlock;
import net.huwng.vietnamdelight.block.MuddyNipaRootsBlock;
import net.huwng.vietnamdelight.block.LotusStemBlock;
import net.huwng.vietnamdelight.block.LotusSeedsBagBlock;
import net.huwng.vietnamdelight.block.LotusRootBlock;
import net.huwng.vietnamdelight.block.LotusRhizomeCrateBlock;
import net.huwng.vietnamdelight.block.LotusLilypadBlock;
import net.huwng.vietnamdelight.block.LotusFlowerBlock;
import net.huwng.vietnamdelight.block.LototCropBlock;
import net.huwng.vietnamdelight.block.LolotLeafCrateBlock;
import net.huwng.vietnamdelight.block.LimeTreeBlock;
import net.huwng.vietnamdelight.block.LimeCropBlock;
import net.huwng.vietnamdelight.block.LimeCrateBlock;
import net.huwng.vietnamdelight.block.LemongrassCropBlock;
import net.huwng.vietnamdelight.block.LemongrassCrateBlock;
import net.huwng.vietnamdelight.block.KumquatCropBlock;
import net.huwng.vietnamdelight.block.KumquatCrateBlock;
import net.huwng.vietnamdelight.block.KumquatBushBlock;
import net.huwng.vietnamdelight.block.JunimoBlock;
import net.huwng.vietnamdelight.block.GreenPeppercornCropBlock;
import net.huwng.vietnamdelight.block.GreenPeppercornBagBlock;
import net.huwng.vietnamdelight.block.GreenKumquatCrateBlock;
import net.huwng.vietnamdelight.block.GreenBananaBundleBlock;
import net.huwng.vietnamdelight.block.GingersCropBlock;
import net.huwng.vietnamdelight.block.GingerCrateBlock;
import net.huwng.vietnamdelight.block.GarlicCropBlock;
import net.huwng.vietnamdelight.block.GarlicCrateBlock;
import net.huwng.vietnamdelight.block.GarlicChivesCropBlock;
import net.huwng.vietnamdelight.block.GarlicChivesCrateBlock;
import net.huwng.vietnamdelight.block.FloweringTamarindLeavesBlock;
import net.huwng.vietnamdelight.block.FloweringBanyanLeavesBlock;
import net.huwng.vietnamdelight.block.HintBlock;
import net.huwng.vietnamdelight.block.FlamboyantLeavesBlock;
import net.huwng.vietnamdelight.block.FloweringFlamboyantLeavesBlock;
import net.huwng.vietnamdelight.block.BloomingFlamboyantLeavesBlock;
import net.huwng.vietnamdelight.block.FlamboyantSaplingBlock;
import net.huwng.vietnamdelight.block.FlamboyantFlowerBlock;
import net.huwng.vietnamdelight.block.FlamboyantPetalsBlock;
import net.huwng.vietnamdelight.block.FloweringGoldenFlamboyantLeavesBlock;
import net.huwng.vietnamdelight.block.BloomingGoldenFlamboyantLeavesBlock;
import net.huwng.vietnamdelight.block.GoldenFlamboyantSaplingBlock;
import net.huwng.vietnamdelight.block.GoldenFlamboyantFlowerBlock;
import net.huwng.vietnamdelight.block.GoldenFlamboyantPetalsBlock;
import net.huwng.vietnamdelight.block.FishTrapBlock;
import net.huwng.vietnamdelight.block.FelspatStairsBlock;
import net.huwng.vietnamdelight.block.FelspatSlabBlock;
import net.huwng.vietnamdelight.block.FelspatBricksBlock;
import net.huwng.vietnamdelight.block.FelspatBrickStairsBlock;
import net.huwng.vietnamdelight.block.FelspatBrickSlabBlock;
import net.huwng.vietnamdelight.block.FelspatBlockBlock;
import net.huwng.vietnamdelight.block.DuckweedBlock;
import net.huwng.vietnamdelight.block.ChucmungBlock;
import net.huwng.vietnamdelight.block.ChilisCropBlock;
import net.huwng.vietnamdelight.block.CeramicTilesStairsBlock;
import net.huwng.vietnamdelight.block.CeramicTilesSlabBlock;
import net.huwng.vietnamdelight.block.CeramicTilesBlock;
import net.huwng.vietnamdelight.block.CeramicPlateBlock;
import net.huwng.vietnamdelight.block.CeramicClayVaseBlock;
import net.huwng.vietnamdelight.block.CeramicClayStairsBlock;
import net.huwng.vietnamdelight.block.CeramicClaySlabBlock;
import net.huwng.vietnamdelight.block.CeramicClayPotBlock;
import net.huwng.vietnamdelight.block.CeramicClayLidBlock;
import net.huwng.vietnamdelight.block.CeramicClayBlockBlock;
import net.huwng.vietnamdelight.block.BruisedGreenBananaBlock;
import net.huwng.vietnamdelight.block.BruisedBananaBlock;
import net.huwng.vietnamdelight.block.BlackPeppercornCropBlock;
import net.huwng.vietnamdelight.block.BlackPeppercornBagBlock;
import net.huwng.vietnamdelight.block.BirdEyeChilisCrateBlock;
import net.huwng.vietnamdelight.block.BeanSproutsBagBlock;
import net.huwng.vietnamdelight.block.BanyanVineBlock;
import net.huwng.vietnamdelight.block.BanyanSaplingBlock;
import net.huwng.vietnamdelight.block.BanyanRootsPlantBlock;
import net.huwng.vietnamdelight.block.BanyanRootsBlock;
import net.huwng.vietnamdelight.block.BanyanLeavesBlock;
import net.huwng.vietnamdelight.block.BanyanFlowerBlock;
import net.huwng.vietnamdelight.block.BananaSuckerBlock;
import net.huwng.vietnamdelight.block.BananaSproutBlock;
import net.huwng.vietnamdelight.block.BananaRootBlock;
import net.huwng.vietnamdelight.block.BananaPseudostemBlock;
import net.huwng.vietnamdelight.block.BananaFrondBlock;
import net.huwng.vietnamdelight.block.BananaFlowerBlock;
import net.huwng.vietnamdelight.block.BananaBundleCropBlock;
import net.huwng.vietnamdelight.block.BananaBundleBlock;
import net.huwng.vietnamdelight.block.AppleSnailsBlock;
import net.huwng.vietnamdelight.VietnamDelightMod;

public class VietnamDelightModBlocks {
	public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, VietnamDelightMod.MODID);
	public static final RegistryObject<Block> GINGERS_CROP = REGISTRY.register("gingers_crop", () -> new GingersCropBlock());
	public static final RegistryObject<Block> CHILIS_CROP = REGISTRY.register("chilis_crop", () -> new ChilisCropBlock());
	public static final RegistryObject<Block> OVERGROW_GINGER = REGISTRY.register("overgrow_ginger", () -> new OvergrowGingerBlock());
	public static final RegistryObject<Block> PANDAN_CROP = REGISTRY.register("pandan_crop", () -> new PandanCropBlock());
	public static final RegistryObject<Block> LOTUS_STEM = REGISTRY.register("lotus_stem", () -> new LotusStemBlock());
	public static final RegistryObject<Block> LOTUS_LILYPAD = REGISTRY.register("lotus_lilypad", () -> new LotusLilypadBlock());
	public static final RegistryObject<Block> LOTUS_FLOWER = REGISTRY.register("lotus_flower", () -> new LotusFlowerBlock());
	public static final RegistryObject<Block> LOTUS_ROOT = REGISTRY.register("lotus_root", () -> new LotusRootBlock());
	public static final RegistryObject<Block> WHITE_LOTUS_FLOWER = REGISTRY.register("white_lotus_flower", () -> new WhiteLotusFlowerBlock());
	public static final RegistryObject<Block> PURPLE_LOTUS_FLOWER = REGISTRY.register("purple_lotus_flower", () -> new PurpleLotusFlowerBlock());
	public static final RegistryObject<Block> YELLOW_LOTUS_FLOWER = REGISTRY.register("yellow_lotus_flower", () -> new YellowLotusFlowerBlock());
	public static final RegistryObject<Block> YOUNG_LOTUS_STEM_CRATE = REGISTRY.register("young_lotus_stem_crate", () -> new YoungLotusStemCrateBlock());
	public static final RegistryObject<Block> LOTUS_RHIZOME_CRATE = REGISTRY.register("lotus_rhizome_crate", () -> new LotusRhizomeCrateBlock());
	public static final RegistryObject<Block> LEMONGRASS_CROP = REGISTRY.register("lemongrass_crop", () -> new LemongrassCropBlock());
	public static final RegistryObject<Block> WILD_LEMONGRASS = REGISTRY.register("wild_lemongrass", () -> new WildLemongrassBlock());
	public static final RegistryObject<Block> WILD_PANDAN = REGISTRY.register("wild_pandan", () -> new WildPandanBlock());
	public static final RegistryObject<Block> WILD_BIRD_EYE_CHILIS = REGISTRY.register("wild_bird_eye_chilis", () -> new WildBirdEyeChilisBlock());
	public static final RegistryObject<Block> WILD_GINGERS = REGISTRY.register("wild_gingers", () -> new WildGingersBlock());
	public static final RegistryObject<Block> GARLIC_CROP = REGISTRY.register("garlic_crop", () -> new GarlicCropBlock());
	public static final RegistryObject<Block> WILD_GARLIC = REGISTRY.register("wild_garlic", () -> new WildGarlicBlock());
	public static final RegistryObject<Block> SHALLOT_CROP = REGISTRY.register("shallot_crop", () -> new ShallotCropBlock());
	public static final RegistryObject<Block> WILD_SHALLOT = REGISTRY.register("wild_shallot", () -> new WildShallotBlock());
	public static final RegistryObject<Block> TURMERIC_CROP = REGISTRY.register("turmeric_crop", () -> new TurmericCropBlock());
	public static final RegistryObject<Block> WILD_TURMERIC = REGISTRY.register("wild_turmeric", () -> new WildTurmericBlock());
	public static final RegistryObject<Block> SCALLION_CROP = REGISTRY.register("scallion_crop", () -> new ScallionCropBlock());
	public static final RegistryObject<Block> WILD_SCALLION = REGISTRY.register("wild_scallion", () -> new WildScallionBlock());
	public static final RegistryObject<Block> GINGER_CRATE = REGISTRY.register("ginger_crate", () -> new GingerCrateBlock());
	public static final RegistryObject<Block> BIRD_EYE_CHILIS_CRATE = REGISTRY.register("bird_eye_chilis_crate", () -> new BirdEyeChilisCrateBlock());
	public static final RegistryObject<Block> LEMONGRASS_CRATE = REGISTRY.register("lemongrass_crate", () -> new LemongrassCrateBlock());
	public static final RegistryObject<Block> GARLIC_CRATE = REGISTRY.register("garlic_crate", () -> new GarlicCrateBlock());
	public static final RegistryObject<Block> SHALLOT_CRATE = REGISTRY.register("shallot_crate", () -> new ShallotCrateBlock());
	public static final RegistryObject<Block> TURMERIC_CRATE = REGISTRY.register("turmeric_crate", () -> new TurmericCrateBlock());
	public static final RegistryObject<Block> SCALLION_CRATE = REGISTRY.register("scallion_crate", () -> new ScallionCrateBlock());
	public static final RegistryObject<Block> LOTOT_CROP = REGISTRY.register("lotot_crop", () -> new LototCropBlock());
	public static final RegistryObject<Block> WILD_LOTOT = REGISTRY.register("wild_lotot", () -> new WildLototBlock());
	public static final RegistryObject<Block> LOLOT_LEAF_CRATE = REGISTRY.register("lolot_leaf_crate", () -> new LolotLeafCrateBlock());
	public static final RegistryObject<Block> KUMQUAT_CROP = REGISTRY.register("kumquat_crop", () -> new KumquatCropBlock());
	public static final RegistryObject<Block> KUMQUAT_BUSH = REGISTRY.register("kumquat_bush", () -> new KumquatBushBlock());
	public static final RegistryObject<Block> POTTED_KUMQUAT_BUSH = REGISTRY.register("potted_kumquat_bush", () -> new PottedKumquatBushBlock());
	public static final RegistryObject<Block> WILD_KUMQUAT_BUSH = REGISTRY.register("wild_kumquat_bush", () -> new WildKumquatBushBlock());
	public static final RegistryObject<Block> GREEN_KUMQUAT_CRATE = REGISTRY.register("green_kumquat_crate", () -> new GreenKumquatCrateBlock());
	public static final RegistryObject<Block> KUMQUAT_CRATE = REGISTRY.register("kumquat_crate", () -> new KumquatCrateBlock());
	public static final RegistryObject<Block> BLACK_PEPPERCORN_CROP = REGISTRY.register("black_peppercorn_crop", () -> new BlackPeppercornCropBlock());
	public static final RegistryObject<Block> GREEN_PEPPERCORN_CROP = REGISTRY.register("green_peppercorn_crop", () -> new GreenPeppercornCropBlock());
	public static final RegistryObject<Block> WHITE_PEPPERCORN_CROP = REGISTRY.register("white_peppercorn_crop", () -> new WhitePeppercornCropBlock());
	public static final RegistryObject<Block> BLACK_PEPPERCORN_BAG = REGISTRY.register("black_peppercorn_bag", () -> new BlackPeppercornBagBlock());
	public static final RegistryObject<Block> GREEN_PEPPERCORN_BAG = REGISTRY.register("green_peppercorn_bag", () -> new GreenPeppercornBagBlock());
	public static final RegistryObject<Block> WHITE_PEPPERCORN_BAG = REGISTRY.register("white_peppercorn_bag", () -> new WhitePeppercornBagBlock());
	public static final RegistryObject<Block> WILD_LOTUS_LILYPAD_0 = REGISTRY.register("wild_lotus_lilypad_0", () -> new WildLotusLilypad0Block());
	public static final RegistryObject<Block> WILD_LOTUS_LILYPAD_1 = REGISTRY.register("wild_lotus_lilypad_1", () -> new WildLotusLilypad1Block());
	public static final RegistryObject<Block> WILD_LOTUS_LILYPAD_2 = REGISTRY.register("wild_lotus_lilypad_2", () -> new WildLotusLilypad2Block());
	public static final RegistryObject<Block> WILD_LOTUS_LILYPAD_3 = REGISTRY.register("wild_lotus_lilypad_3", () -> new WildLotusLilypad3Block());
	public static final RegistryObject<Block> WILD_LOTUS_LEAF = REGISTRY.register("wild_lotus_leaf", () -> new WildLotusLeafBlock());
	public static final RegistryObject<Block> WILD_LOTUS_BUD = REGISTRY.register("wild_lotus_bud", () -> new WildLotusBudBlock());
	public static final RegistryObject<Block> WILD_PURPLE_LOTUS_BUD = REGISTRY.register("wild_purple_lotus_bud", () -> new WildPurpleLotusBudBlock());
	public static final RegistryObject<Block> WILD_WHITE_LOTUS_BUD = REGISTRY.register("wild_white_lotus_bud", () -> new WildWhiteLotusBudBlock());
	public static final RegistryObject<Block> WILD_LOTUS_FLOWER = REGISTRY.register("wild_lotus_flower", () -> new WildLotusFlowerBlock());
	public static final RegistryObject<Block> WILD_PURPLE_LOTUS_FLOWER = REGISTRY.register("wild_purple_lotus_flower", () -> new WildPurpleLotusFlowerBlock());
	public static final RegistryObject<Block> WILD_WHITE_LOTUS_FLOWER = REGISTRY.register("wild_white_lotus_flower", () -> new WildWhiteLotusFlowerBlock());
	public static final RegistryObject<Block> WILD_LOTUS_SEED_POD = REGISTRY.register("wild_lotus_seed_pod", () -> new WildLotusSeedPodBlock());
	public static final RegistryObject<Block> WILD_LOTUS_SEED_POD_2 = REGISTRY.register("wild_lotus_seed_pod_2", () -> new WildLotusSeedPod2Block());
	public static final RegistryObject<Block> GARLIC_CHIVES_CROP = REGISTRY.register("garlic_chives_crop", () -> new GarlicChivesCropBlock());
	public static final RegistryObject<Block> WILD_GARLIC_CHIVES = REGISTRY.register("wild_garlic_chives", () -> new WildGarlicChivesBlock());
	public static final RegistryObject<Block> WILD_GARLIC_CHIVES_BUD = REGISTRY.register("wild_garlic_chives_bud", () -> new WildGarlicChivesBudBlock());
	public static final RegistryObject<Block> WILD_GARLIC_CHIVES_FLOWER = REGISTRY.register("wild_garlic_chives_flower", () -> new WildGarlicChivesFlowerBlock());
	public static final RegistryObject<Block> GARLIC_CHIVES_CRATE = REGISTRY.register("garlic_chives_crate", () -> new GarlicChivesCrateBlock());
	public static final RegistryObject<Block> WILD_BLACK_PEPPERCORN = REGISTRY.register("wild_black_peppercorn", () -> new WildBlackPeppercornBlock());
	public static final RegistryObject<Block> WILD_GREEN_PEPPERCORN = REGISTRY.register("wild_green_peppercorn", () -> new WildGreenPeppercornBlock());
	public static final RegistryObject<Block> LIME_CROP = REGISTRY.register("lime_crop", () -> new LimeCropBlock());
	public static final RegistryObject<Block> LIME_TREE = REGISTRY.register("lime_tree", () -> new LimeTreeBlock());
	public static final RegistryObject<Block> WILD_LIME_TREE = REGISTRY.register("wild_lime_tree", () -> new WildLimeTreeBlock());
	public static final RegistryObject<Block> LIME_CRATE = REGISTRY.register("lime_crate", () -> new LimeCrateBlock());
	public static final RegistryObject<Block> BANANA_PSEUDOSTEM = REGISTRY.register("banana_pseudostem", () -> new BananaPseudostemBlock());
	public static final RegistryObject<Block> BANANA_FROND = REGISTRY.register("banana_frond", () -> new BananaFrondBlock());
	public static final RegistryObject<Block> WALL_BANANA_FROND = REGISTRY.register("wall_banana_frond", () -> new WallBananaFrondBlock());
	public static final RegistryObject<Block> BANANA_FLOWER = REGISTRY.register("banana_flower", () -> new BananaFlowerBlock());
	public static final RegistryObject<Block> BANANA_BUNDLE = REGISTRY.register("banana_bundle", () -> new BananaBundleBlock());
	public static final RegistryObject<Block> BANANA_BUNDLE_CROP = REGISTRY.register("banana_bundle_crop", () -> new BananaBundleCropBlock());
	public static final RegistryObject<Block> GREEN_BANANA_BUNDLE = REGISTRY.register("green_banana_bundle", () -> new GreenBananaBundleBlock());
	public static final RegistryObject<Block> WAXED_GREEN_BANANA_BUNDLE = REGISTRY.register("waxed_green_banana_bundle", () -> new WaxedGreenBananaBundleBlock());
	public static final RegistryObject<Block> BANANA_ROOT = REGISTRY.register("banana_root", () -> new BananaRootBlock());
	public static final RegistryObject<Block> BANANA_SUCKER = REGISTRY.register("banana_sucker", () -> new BananaSuckerBlock());
	public static final RegistryObject<Block> BRUISED_BANANA = REGISTRY.register("bruised_banana", () -> new BruisedBananaBlock());
	public static final RegistryObject<Block> BRUISED_GREEN_BANANA = REGISTRY.register("bruised_green_banana", () -> new BruisedGreenBananaBlock());
	public static final RegistryObject<Block> BANANA_SPROUT = REGISTRY.register("banana_sprout", () -> new BananaSproutBlock());
	public static final RegistryObject<Block> JUNIMO = REGISTRY.register("junimo", () -> new JunimoBlock());
	public static final RegistryObject<Block> VIET_NAM_FLAG = REGISTRY.register("viet_nam_flag", () -> new VietNamFlagBlock());
	public static final RegistryObject<Block> FELSPAT_BLOCK = REGISTRY.register("felspat_block", () -> new FelspatBlockBlock());
	public static final RegistryObject<Block> FELSPAT_STAIRS = REGISTRY.register("felspat_stairs", () -> new FelspatStairsBlock());
	public static final RegistryObject<Block> FELSPAT_SLAB = REGISTRY.register("felspat_slab", () -> new FelspatSlabBlock());
	public static final RegistryObject<Block> FELSPAT_BRICKS = REGISTRY.register("felspat_bricks", () -> new FelspatBricksBlock());
	public static final RegistryObject<Block> FELSPAT_BRICK_STAIRS = REGISTRY.register("felspat_brick_stairs", () -> new FelspatBrickStairsBlock());
	public static final RegistryObject<Block> FELSPAT_BRICK_SLAB = REGISTRY.register("felspat_brick_slab", () -> new FelspatBrickSlabBlock());
	public static final RegistryObject<Block> CERAMIC_CLAY_BLOCK = REGISTRY.register("ceramic_clay_block", () -> new CeramicClayBlockBlock());
	public static final RegistryObject<Block> CERAMIC_CLAY_STAIRS = REGISTRY.register("ceramic_clay_stairs", () -> new CeramicClayStairsBlock());
	public static final RegistryObject<Block> CERAMIC_CLAY_SLAB = REGISTRY.register("ceramic_clay_slab", () -> new CeramicClaySlabBlock());
	public static final RegistryObject<Block> CERAMIC_TILES = REGISTRY.register("ceramic_tiles", () -> new CeramicTilesBlock());
	public static final RegistryObject<Block> CERAMIC_TILES_STAIRS = REGISTRY.register("ceramic_tiles_stairs", () -> new CeramicTilesStairsBlock());
	public static final RegistryObject<Block> CERAMIC_TILES_SLAB = REGISTRY.register("ceramic_tiles_slab", () -> new CeramicTilesSlabBlock());
	public static final RegistryObject<Block> CERAMIC_PLATE = REGISTRY.register("ceramic_plate", () -> new CeramicPlateBlock());
	public static final RegistryObject<Block> SALT_BLOCK = REGISTRY.register("salt_block", () -> new SaltBlockBlock());
	public static final RegistryObject<Block> SALT_CLUSTER = REGISTRY.register("salt_cluster", () -> new SaltClusterBlock());
	public static final RegistryObject<Block> SALT_LANTERN = REGISTRY.register("salt_lantern", () -> new SaltLanternBlock());
	public static final RegistryObject<Block> CERAMIC_CLAY_POT = REGISTRY.register("ceramic_clay_pot", () -> new CeramicClayPotBlock());
	public static final RegistryObject<Block> CERAMIC_CLAY_LID = REGISTRY.register("ceramic_clay_lid", () -> new CeramicClayLidBlock());
	public static final RegistryObject<Block> CERAMIC_CLAY_VASE = REGISTRY.register("ceramic_clay_vase", () -> new CeramicClayVaseBlock());
	public static final RegistryObject<Block> BANYAN_LEAVES = REGISTRY.register("banyan_leaves", () -> new BanyanLeavesBlock());
	public static final RegistryObject<Block> ROPE_NODES = REGISTRY.register("rope_nodes", () -> new RopeNodesBlock());
	public static final RegistryObject<Block> PAPER_LANTERN = REGISTRY.register("paper_lantern", () -> new PaperLanternBlock());
	public static final RegistryObject<Block> STAR_LANTERN = REGISTRY.register("star_lantern", () -> new StarLanternBlock());
	public static final RegistryObject<Block> BANYAN_VINE = REGISTRY.register("banyan_vine", () -> new BanyanVineBlock());
	public static final RegistryObject<Block> BANYAN_ROOTS = REGISTRY.register("banyan_roots", () -> new BanyanRootsBlock());
	public static final RegistryObject<Block> BANYAN_ROOTS_PLANT = REGISTRY.register("banyan_roots_plant", () -> new BanyanRootsPlantBlock());
	public static final RegistryObject<Block> FLOWERING_BANYAN_LEAVES = REGISTRY.register("flowering_banyan_leaves", () -> new FloweringBanyanLeavesBlock());
	public static final RegistryObject<Block> BANYAN_SAPLING = REGISTRY.register("banyan_sapling", () -> new BanyanSaplingBlock());
	public static final RegistryObject<Block> BANYAN_FLOWER = REGISTRY.register("banyan_flower", () -> new BanyanFlowerBlock());
	public static final RegistryObject<Block> WILD_MUNG_BEAN = REGISTRY.register("wild_mung_bean", () -> new WildMungBeanBlock());
	public static final RegistryObject<Block> MUNG_BEANS_CROP = REGISTRY.register("mung_beans_crop", () -> new MungBeansCropBlock());
	public static final RegistryObject<Block> MUNG_BEANS_BAG = REGISTRY.register("mung_beans_bag", () -> new MungBeansBagBlock());
	public static final RegistryObject<Block> LOTUS_SEEDS_BAG = REGISTRY.register("lotus_seeds_bag", () -> new LotusSeedsBagBlock());
	public static final RegistryObject<Block> BEAN_SPROUTS_BAG = REGISTRY.register("bean_sprouts_bag", () -> new BeanSproutsBagBlock());
	public static final RegistryObject<Block> NIPA_FROND = REGISTRY.register("nipa_frond", () -> new NipaFrondBlock());
	public static final RegistryObject<Block> NIPA_ROOT = REGISTRY.register("nipa_root", () -> new NipaRootBlock());
	public static final RegistryObject<Block> NIPA_ROOTS = REGISTRY.register("nipa_roots", () -> new NipaRootsBlock());
	public static final RegistryObject<Block> NIPA_POST = REGISTRY.register("nipa_post", () -> new NipaPostBlock());
	public static final RegistryObject<Block> NIPA_FROND_POST = REGISTRY.register("nipa_frond_post", () -> new NipaFrondPostBlock());
	public static final RegistryObject<Block> NIPA_FROND_SIDES = REGISTRY.register("nipa_frond_sides", () -> new NipaFrondSidesBlock());
	public static final RegistryObject<Block> NIPA_FRUIT = REGISTRY.register("nipa_fruit", () -> new NipaFruitBlock());
	public static final RegistryObject<Block> NIPA_FRUIT_CROP = REGISTRY.register("nipa_fruit_crop", () -> new NipaFruitCropBlock());
	public static final RegistryObject<Block> MUDDY_NIPA_ROOTS = REGISTRY.register("muddy_nipa_roots", () -> new MuddyNipaRootsBlock());
	public static final RegistryObject<Block> NIPA_PROPAGULE = REGISTRY.register("nipa_propagule", () -> new NipaPropaguleBlock());
	public static final RegistryObject<Block> CHUCMUNG = REGISTRY.register("chucmung", () -> new ChucmungBlock());
	public static final RegistryObject<Block> TAMARIND_LEAVES = REGISTRY.register("tamarind_leaves", () -> new TamarindLeavesBlock());
	public static final RegistryObject<Block> TAMARIND_FLOWERS = REGISTRY.register("tamarind_flowers", () -> new TamarindFlowersBlock());
	public static final RegistryObject<Block> FLOWERING_TAMARIND_LEAVES = REGISTRY.register("flowering_tamarind_leaves", () -> new FloweringTamarindLeavesBlock());
	public static final RegistryObject<Block> TAMARIND_SAPLING = REGISTRY.register("tamarind_sapling", () -> new TamarindSaplingBlock());
	public static final RegistryObject<Block> FISH_TRAP = REGISTRY.register("fish_trap", () -> new FishTrapBlock());
	public static final RegistryObject<Block> APPLE_SNAILS = REGISTRY.register("apple_snails", () -> new AppleSnailsBlock());
	public static final RegistryObject<Block> WATER_LETTUCE = REGISTRY.register("water_lettuce", () -> new WaterLettuceBlock());
	public static final RegistryObject<Block> DUCKWEED = REGISTRY.register("duckweed", () -> new DuckweedBlock());
	public static final RegistryObject<Block> WATER_PRIMROSE = REGISTRY.register("water_primrose", () -> new WaterPrimroseBlock());
	public static final RegistryObject<Block> WATER_PRIMROSE_FLOWER = REGISTRY.register("water_primrose_flower", () -> new WaterPrimroseFlowerBlock());
	public static final RegistryObject<Block> WATER_PRIMROSE_YELLOW_FLOWER = REGISTRY.register("water_primrose_yellow_flower", () -> new WaterPrimroseYellowFlowerBlock());
	public static final RegistryObject<Block> WATER_HYACINTH = REGISTRY.register("water_hyacinth", () -> new WaterHyacinthBlock());
	public static final RegistryObject<Block> WATER_HYACINTH_FLOWER = REGISTRY.register("water_hyacinth_flower", () -> new WaterHyacinthFlowerBlock());
	public static final RegistryObject<Block> WATER_HYACINTH_FLOWER_STEM = REGISTRY.register("water_hyacinth_flower_stem", () -> new WaterHyacinthFlowerStemBlock());
	public static final RegistryObject<Block> WATER_HYACINTH_STEM = REGISTRY.register("water_hyacinth_stem", () -> new WaterHyacinthStemBlock());
	public static final RegistryObject<Block> WATER_HYACINTH_LAYER = REGISTRY.register("water_hyacinth_layer", () -> new WaterHyacinthLayerBlock());
	public static final RegistryObject<Block> PANDAN_LEAF_BUNDLE = REGISTRY.register("pandan_leaf_bundle", () -> new PandanLeafBundleBlock());
	// Start of user code block custom blocks
	public static final RegistryObject<Block> HINT = REGISTRY.register("hint", () -> new HintBlock());
	public static final RegistryObject<Block> FLAMBOYANT_LEAVES = REGISTRY.register("flamboyant_leaves", () -> new FlamboyantLeavesBlock());
	public static final RegistryObject<Block> FLOWERING_FLAMBOYANT_LEAVES = REGISTRY.register("flowering_flamboyant_leaves", () -> new FloweringFlamboyantLeavesBlock());
	public static final RegistryObject<Block> BLOOMING_FLAMBOYANT_LEAVES = REGISTRY.register("blooming_flamboyant_leaves", () -> new BloomingFlamboyantLeavesBlock());
	public static final RegistryObject<Block> FLAMBOYANT_SAPLING = REGISTRY.register("flamboyant_sapling", () -> new FlamboyantSaplingBlock());
	public static final RegistryObject<Block> FLAMBOYANT_FLOWER = REGISTRY.register("flamboyant_flower", () -> new FlamboyantFlowerBlock());
	public static final RegistryObject<Block> FLAMBOYANT_PETALS = REGISTRY.register("flamboyant_petals", () -> new FlamboyantPetalsBlock());
	public static final RegistryObject<Block> FLOWERING_GOLDEN_FLAMBOYANT_LEAVES = REGISTRY.register("flowering_golden_flamboyant_leaves", () -> new FloweringGoldenFlamboyantLeavesBlock());
	public static final RegistryObject<Block> BLOOMING_GOLDEN_FLAMBOYANT_LEAVES = REGISTRY.register("blooming_golden_flamboyant_leaves", () -> new BloomingGoldenFlamboyantLeavesBlock());
	public static final RegistryObject<Block> GOLDEN_FLAMBOYANT_SAPLING = REGISTRY.register("golden_flamboyant_sapling", () -> new GoldenFlamboyantSaplingBlock());
	public static final RegistryObject<Block> GOLDEN_FLAMBOYANT_FLOWER = REGISTRY.register("golden_flamboyant_flower", () -> new GoldenFlamboyantFlowerBlock());
	public static final RegistryObject<Block> GOLDEN_FLAMBOYANT_PETALS = REGISTRY.register("golden_flamboyant_petals", () -> new GoldenFlamboyantPetalsBlock());
	public static final RegistryObject<Block> CICADAS = REGISTRY.register("cicadas", () -> new CicadasBlock());
	public static final RegistryObject<Block> DURIAN_LOG = REGISTRY.register("durian_log",
			() -> new net.minecraft.world.level.block.RotatedPillarBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
					.mapColor(net.minecraft.world.level.material.MapColor.COLOR_BROWN)
					.instrument(net.minecraft.world.level.block.state.properties.NoteBlockInstrument.BASS)
					.strength(2.0F)
					.sound(net.minecraft.world.level.block.SoundType.WOOD)
					.ignitedByLava()));
	public static final RegistryObject<Block> DURIAN_WOOD = REGISTRY.register("durian_wood",
			() -> new net.huwng.vietnamdelight.block.DurianWoodBlock());
	public static final RegistryObject<Block> STRIPPED_DURIAN_LOG = REGISTRY.register("stripped_durian_log",
			() -> new net.minecraft.world.level.block.RotatedPillarBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
					.mapColor(net.minecraft.world.level.material.MapColor.COLOR_YELLOW)
					.instrument(net.minecraft.world.level.block.state.properties.NoteBlockInstrument.BASS)
					.strength(2.0F)
					.sound(net.minecraft.world.level.block.SoundType.WOOD)
					.ignitedByLava()));
	public static final RegistryObject<Block> STRIPPED_DURIAN_WOOD = REGISTRY.register("stripped_durian_wood",
			() -> new net.minecraft.world.level.block.RotatedPillarBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
					.mapColor(net.minecraft.world.level.material.MapColor.COLOR_YELLOW)
					.instrument(net.minecraft.world.level.block.state.properties.NoteBlockInstrument.BASS)
					.strength(2.0F)
					.sound(net.minecraft.world.level.block.SoundType.WOOD)
					.ignitedByLava()));
	public static final RegistryObject<Block> DURIAN_PLANKS = REGISTRY.register("durian_planks",
			() -> new net.minecraft.world.level.block.Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
					.mapColor(net.minecraft.world.level.material.MapColor.COLOR_YELLOW)
					.instrument(net.minecraft.world.level.block.state.properties.NoteBlockInstrument.BASS)
					.strength(2.0F, 3.0F)
					.sound(net.minecraft.world.level.block.SoundType.WOOD)
					.ignitedByLava()));
	public static final RegistryObject<Block> DURIAN_STAIRS = REGISTRY.register("durian_stairs",
			() -> new net.minecraft.world.level.block.StairBlock(() -> DURIAN_PLANKS.get().defaultBlockState(),
					net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(DURIAN_PLANKS.get())));
	public static final RegistryObject<Block> DURIAN_SLAB = REGISTRY.register("durian_slab",
			() -> new net.minecraft.world.level.block.SlabBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(DURIAN_PLANKS.get())));
	public static final RegistryObject<Block> DURIAN_FENCE = REGISTRY.register("durian_fence",
			() -> new net.minecraft.world.level.block.FenceBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(DURIAN_PLANKS.get())));
	public static final RegistryObject<Block> DURIAN_FENCE_GATE = REGISTRY.register("durian_fence_gate",
			() -> new net.minecraft.world.level.block.FenceGateBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(DURIAN_PLANKS.get()), VietnamDelightWoodTypes.DURIAN));
	public static final RegistryObject<Block> DURIAN_DOOR = REGISTRY.register("durian_door",
			() -> new net.minecraft.world.level.block.DoorBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
					.mapColor(net.minecraft.world.level.material.MapColor.COLOR_YELLOW)
					.instrument(net.minecraft.world.level.block.state.properties.NoteBlockInstrument.BASS)
					.strength(3.0F)
					.sound(net.minecraft.world.level.block.SoundType.WOOD)
					.noOcclusion()
					.ignitedByLava(),
					VietnamDelightWoodTypes.DURIAN_BLOCK_SET_TYPE));
	public static final RegistryObject<Block> DURIAN_TRAPDOOR = REGISTRY.register("durian_trapdoor",
			() -> new net.minecraft.world.level.block.TrapDoorBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
					.mapColor(net.minecraft.world.level.material.MapColor.COLOR_YELLOW)
					.instrument(net.minecraft.world.level.block.state.properties.NoteBlockInstrument.BASS)
					.strength(3.0F)
					.sound(net.minecraft.world.level.block.SoundType.WOOD)
					.noOcclusion()
					.isValidSpawn((bs, br, bp, ent) -> false)
					.ignitedByLava(),
					VietnamDelightWoodTypes.DURIAN_BLOCK_SET_TYPE));
	public static final RegistryObject<Block> DURIAN_PRESSURE_PLATE = REGISTRY.register("durian_pressure_plate",
			() -> new net.minecraft.world.level.block.PressurePlateBlock(net.minecraft.world.level.block.PressurePlateBlock.Sensitivity.EVERYTHING,
					net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
							.mapColor(net.minecraft.world.level.material.MapColor.COLOR_YELLOW)
							.forceSolidOn()
							.instrument(net.minecraft.world.level.block.state.properties.NoteBlockInstrument.BASS)
							.noCollission()
							.strength(0.5F)
							.sound(net.minecraft.world.level.block.SoundType.WOOD)
							.ignitedByLava(),
					VietnamDelightWoodTypes.DURIAN_BLOCK_SET_TYPE));
	public static final RegistryObject<Block> DURIAN_BUTTON = REGISTRY.register("durian_button",
			() -> new net.minecraft.world.level.block.ButtonBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
					.noCollission()
					.strength(0.5F)
					.sound(net.minecraft.world.level.block.SoundType.WOOD)
					.ignitedByLava(),
					VietnamDelightWoodTypes.DURIAN_BLOCK_SET_TYPE, 30, true));
	public static final RegistryObject<Block> DURIAN_SIGN = REGISTRY.register("durian_sign",
			() -> new net.huwng.vietnamdelight.block.DurianStandingSignBlock());
	public static final RegistryObject<Block> DURIAN_WALL_SIGN = REGISTRY.register("durian_wall_sign",
			() -> new net.huwng.vietnamdelight.block.DurianWallSignBlock());
	public static final RegistryObject<Block> DURIAN_HANGING_SIGN = REGISTRY.register("durian_hanging_sign",
			() -> new net.huwng.vietnamdelight.block.DurianCeilingHangingSignBlock());
	public static final RegistryObject<Block> DURIAN_WALL_HANGING_SIGN = REGISTRY.register("durian_wall_hanging_sign",
			() -> new net.huwng.vietnamdelight.block.DurianWallHangingSignBlock());
	public static final RegistryObject<Block> DURIAN_LEAVES = REGISTRY.register("durian_leaves",
			() -> new net.huwng.vietnamdelight.block.DurianLeavesBlock());
	public static final RegistryObject<Block> DURIAN_SAPLING = REGISTRY.register("durian_sapling",
			() -> new net.huwng.vietnamdelight.block.DurianSaplingBlock());
	public static final RegistryObject<Block> POTTED_DURIAN_SAPLING = REGISTRY.register("potted_durian_sapling",
			() -> new net.minecraft.world.level.block.FlowerPotBlock(() -> (net.minecraft.world.level.block.FlowerPotBlock) net.minecraft.world.level.block.Blocks.FLOWER_POT,
					DURIAN_SAPLING, net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.POTTED_OAK_SAPLING)));
	public static final RegistryObject<Block> DURIAN_BUD = REGISTRY.register("durian_bud",
			() -> new net.huwng.vietnamdelight.block.DurianBudBlock());
	public static final RegistryObject<Block> DURIAN = REGISTRY.register("durian",
			() -> new net.huwng.vietnamdelight.block.DurianBlock());
	public static final RegistryObject<Block> FLAMBOYANT_LOG = REGISTRY.register("flamboyant_log",
			() -> new net.minecraft.world.level.block.RotatedPillarBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
					.mapColor(net.minecraft.world.level.material.MapColor.COLOR_BROWN)
					.instrument(net.minecraft.world.level.block.state.properties.NoteBlockInstrument.BASS)
					.strength(2.0F)
					.sound(net.minecraft.world.level.block.SoundType.WOOD)
					.ignitedByLava()));
	public static final RegistryObject<Block> FLAMBOYANT_WOOD = REGISTRY.register("flamboyant_wood",
			() -> new net.minecraft.world.level.block.RotatedPillarBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
					.mapColor(net.minecraft.world.level.material.MapColor.COLOR_BROWN)
					.instrument(net.minecraft.world.level.block.state.properties.NoteBlockInstrument.BASS)
					.strength(2.0F)
					.sound(net.minecraft.world.level.block.SoundType.WOOD)
					.ignitedByLava()));
	public static final RegistryObject<Block> STRIPPED_FLAMBOYANT_LOG = REGISTRY.register("stripped_flamboyant_log",
			() -> new net.minecraft.world.level.block.RotatedPillarBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
					.mapColor(net.minecraft.world.level.material.MapColor.COLOR_RED)
					.instrument(net.minecraft.world.level.block.state.properties.NoteBlockInstrument.BASS)
					.strength(2.0F)
					.sound(net.minecraft.world.level.block.SoundType.WOOD)
					.ignitedByLava()));
	public static final RegistryObject<Block> STRIPPED_FLAMBOYANT_WOOD = REGISTRY.register("stripped_flamboyant_wood",
			() -> new net.minecraft.world.level.block.RotatedPillarBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
					.mapColor(net.minecraft.world.level.material.MapColor.COLOR_RED)
					.instrument(net.minecraft.world.level.block.state.properties.NoteBlockInstrument.BASS)
					.strength(2.0F)
					.sound(net.minecraft.world.level.block.SoundType.WOOD)
					.ignitedByLava()));
	public static final RegistryObject<Block> FLAMBOYANT_PLANKS = REGISTRY.register("flamboyant_planks",
			() -> new net.minecraft.world.level.block.Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
					.mapColor(net.minecraft.world.level.material.MapColor.COLOR_RED)
					.instrument(net.minecraft.world.level.block.state.properties.NoteBlockInstrument.BASS)
					.strength(2.0F, 3.0F)
					.sound(net.minecraft.world.level.block.SoundType.WOOD)
					.ignitedByLava()));
	public static final RegistryObject<Block> FLAMBOYANT_STAIRS = REGISTRY.register("flamboyant_stairs",
			() -> new net.minecraft.world.level.block.StairBlock(() -> FLAMBOYANT_PLANKS.get().defaultBlockState(),
					net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(FLAMBOYANT_PLANKS.get())));
	public static final RegistryObject<Block> FLAMBOYANT_SLAB = REGISTRY.register("flamboyant_slab",
			() -> new net.minecraft.world.level.block.SlabBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(FLAMBOYANT_PLANKS.get())));
	public static final RegistryObject<Block> FLAMBOYANT_FENCE = REGISTRY.register("flamboyant_fence",
			() -> new net.minecraft.world.level.block.FenceBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(FLAMBOYANT_PLANKS.get())));
	public static final RegistryObject<Block> FLAMBOYANT_FENCE_GATE = REGISTRY.register("flamboyant_fence_gate",
			() -> new net.minecraft.world.level.block.FenceGateBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(FLAMBOYANT_PLANKS.get()), VietnamDelightWoodTypes.FLAMBOYANT));
	public static final RegistryObject<Block> FLAMBOYANT_DOOR = REGISTRY.register("flamboyant_door",
			() -> new net.minecraft.world.level.block.DoorBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
					.mapColor(net.minecraft.world.level.material.MapColor.COLOR_RED)
					.instrument(net.minecraft.world.level.block.state.properties.NoteBlockInstrument.BASS)
					.strength(3.0F)
					.sound(net.minecraft.world.level.block.SoundType.WOOD)
					.noOcclusion()
					.ignitedByLava(),
					VietnamDelightWoodTypes.FLAMBOYANT_BLOCK_SET_TYPE));
	public static final RegistryObject<Block> FLAMBOYANT_TRAPDOOR = REGISTRY.register("flamboyant_trapdoor",
			() -> new net.minecraft.world.level.block.TrapDoorBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
					.mapColor(net.minecraft.world.level.material.MapColor.COLOR_RED)
					.instrument(net.minecraft.world.level.block.state.properties.NoteBlockInstrument.BASS)
					.strength(3.0F)
					.sound(net.minecraft.world.level.block.SoundType.WOOD)
					.noOcclusion()
					.isValidSpawn((bs, br, bp, ent) -> false)
					.ignitedByLava(),
					VietnamDelightWoodTypes.FLAMBOYANT_BLOCK_SET_TYPE));
	public static final RegistryObject<Block> FLAMBOYANT_PRESSURE_PLATE = REGISTRY.register("flamboyant_pressure_plate",
			() -> new net.minecraft.world.level.block.PressurePlateBlock(net.minecraft.world.level.block.PressurePlateBlock.Sensitivity.EVERYTHING,
					net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
							.mapColor(net.minecraft.world.level.material.MapColor.COLOR_RED)
							.forceSolidOn()
							.instrument(net.minecraft.world.level.block.state.properties.NoteBlockInstrument.BASS)
							.noCollission()
							.strength(0.5F)
							.sound(net.minecraft.world.level.block.SoundType.WOOD)
							.ignitedByLava(),
					VietnamDelightWoodTypes.FLAMBOYANT_BLOCK_SET_TYPE));
	public static final RegistryObject<Block> FLAMBOYANT_BUTTON = REGISTRY.register("flamboyant_button",
			() -> new net.minecraft.world.level.block.ButtonBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
					.noCollission()
					.strength(0.5F)
					.sound(net.minecraft.world.level.block.SoundType.WOOD)
					.ignitedByLava(),
					VietnamDelightWoodTypes.FLAMBOYANT_BLOCK_SET_TYPE, 30, true));
	public static final RegistryObject<Block> FLAMBOYANT_SIGN = REGISTRY.register("flamboyant_sign",
			() -> new net.huwng.vietnamdelight.block.FlamboyantStandingSignBlock());
	public static final RegistryObject<Block> FLAMBOYANT_WALL_SIGN = REGISTRY.register("flamboyant_wall_sign",
			() -> new net.huwng.vietnamdelight.block.FlamboyantWallSignBlock());
	public static final RegistryObject<Block> FLAMBOYANT_HANGING_SIGN = REGISTRY.register("flamboyant_hanging_sign",
			() -> new net.huwng.vietnamdelight.block.FlamboyantCeilingHangingSignBlock());
	public static final RegistryObject<Block> FLAMBOYANT_WALL_HANGING_SIGN = REGISTRY.register("flamboyant_wall_hanging_sign",
			() -> new net.huwng.vietnamdelight.block.FlamboyantWallHangingSignBlock());
	public static final RegistryObject<Block> FELSPAT_WALL = REGISTRY.register("felspat_wall",
			() -> new net.minecraft.world.level.block.WallBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(FELSPAT_BLOCK.get())));
	public static final RegistryObject<Block> POLISHED_FELSPAT = REGISTRY.register("polished_felspat",
			() -> new net.minecraft.world.level.block.Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(FELSPAT_BLOCK.get())));
	public static final RegistryObject<Block> POLISHED_FELSPAT_STAIRS = REGISTRY.register("polished_felspat_stairs",
			() -> new net.minecraft.world.level.block.StairBlock(() -> POLISHED_FELSPAT.get().defaultBlockState(),
					net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(FELSPAT_BLOCK.get())));
	public static final RegistryObject<Block> POLISHED_FELSPAT_SLAB = REGISTRY.register("polished_felspat_slab",
			() -> new net.minecraft.world.level.block.SlabBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(FELSPAT_BLOCK.get())));
	public static final RegistryObject<Block> POLISHED_FELSPAT_WALL = REGISTRY.register("polished_felspat_wall",
			() -> new net.minecraft.world.level.block.WallBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(FELSPAT_BLOCK.get())));
	public static final RegistryObject<Block> FELSPAT_BRICK_WALL = REGISTRY.register("felspat_brick_wall",
			() -> new net.minecraft.world.level.block.WallBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(FELSPAT_BRICKS.get())));
	public static final RegistryObject<Block> CERAMIC_CLAY_WALL = REGISTRY.register("ceramic_clay_wall",
			() -> new net.huwng.vietnamdelight.block.CeramicClayWallBlock());
	public static final RegistryObject<Block> POLISHED_CERAMIC_CLAY = REGISTRY.register("polished_ceramic_clay",
			() -> new net.huwng.vietnamdelight.block.PolishedCeramicClayBlock());
	public static final RegistryObject<Block> POLISHED_CERAMIC_CLAY_STAIRS = REGISTRY.register("polished_ceramic_clay_stairs",
			() -> new net.huwng.vietnamdelight.block.PolishedCeramicClayStairsBlock());
	public static final RegistryObject<Block> POLISHED_CERAMIC_CLAY_SLAB = REGISTRY.register("polished_ceramic_clay_slab",
			() -> new net.huwng.vietnamdelight.block.PolishedCeramicClaySlabBlock());
	public static final RegistryObject<Block> POLISHED_CERAMIC_CLAY_WALL = REGISTRY.register("polished_ceramic_clay_wall",
			() -> new net.huwng.vietnamdelight.block.PolishedCeramicClayWallBlock());
	public static final RegistryObject<Block> CERAMIC_TILES_WALL = REGISTRY.register("ceramic_tiles_wall",
			() -> new net.huwng.vietnamdelight.block.CeramicTilesWallBlock());
	public static final RegistryObject<Block> CANDLE_HOLDER = REGISTRY.register("candle_holder",
			() -> new net.huwng.vietnamdelight.block.CandleHolderBlock());
	public static final RegistryObject<Block> PURIFYING_CANDLE = REGISTRY.register("purifying_candle",
			() -> new net.huwng.vietnamdelight.block.PurifyingCandleBlock());
	public static final RegistryObject<Block> PURIFYING_CANDLE_HOLDER = REGISTRY.register("purifying_candle_holder",
			() -> new net.huwng.vietnamdelight.block.PurifyingCandleHolderBlock());
	public static final RegistryObject<Block> RAGE_CANDLE = REGISTRY.register("rage_candle",
			() -> new net.huwng.vietnamdelight.block.RageCandleBlock());
	public static final RegistryObject<Block> RAGE_CANDLE_HOLDER = REGISTRY.register("rage_candle_holder",
			() -> new net.huwng.vietnamdelight.block.RageCandleHolderBlock());
	public static final RegistryObject<Block> ARID_CANDLE = REGISTRY.register("arid_candle",
			() -> new net.huwng.vietnamdelight.block.AridCandleBlock());
	public static final RegistryObject<Block> ARID_CANDLE_HOLDER = REGISTRY.register("arid_candle_holder",
			() -> new net.huwng.vietnamdelight.block.AridCandleHolderBlock());
	public static final RegistryObject<Block> SUNBURST_CANDLE = REGISTRY.register("sunburst_candle",
			() -> new net.huwng.vietnamdelight.block.SunburstCandleBlock());
	public static final RegistryObject<Block> SUNBURST_CANDLE_HOLDER = REGISTRY.register("sunburst_candle_holder",
			() -> new net.huwng.vietnamdelight.block.SunburstCandleHolderBlock());
	public static final RegistryObject<Block> PHANTASM_CANDLE = REGISTRY.register("phantasm_candle",
			() -> new net.huwng.vietnamdelight.block.PhantasmCandleBlock());
	public static final RegistryObject<Block> PHANTASM_CANDLE_HOLDER = REGISTRY.register("phantasm_candle_holder",
			() -> new net.huwng.vietnamdelight.block.PhantasmCandleHolderBlock());
	public static final RegistryObject<Block> MARIGOLD = REGISTRY.register("marigold",
			() -> new net.huwng.vietnamdelight.block.MarigoldBlock());
	public static final RegistryObject<Block> POTTED_MARIGOLD = REGISTRY.register("potted_marigold",
			() -> new net.minecraft.world.level.block.FlowerPotBlock(() -> (net.minecraft.world.level.block.FlowerPotBlock) net.minecraft.world.level.block.Blocks.FLOWER_POT,
					MARIGOLD, net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.POTTED_POPPY)));
	public static final RegistryObject<Block> MARIGOLD_BUSH = REGISTRY.register("marigold_bush",
			() -> new net.huwng.vietnamdelight.block.MarigoldBushBlock());
	public static final RegistryObject<Block> HIBISCUS_FLOWER = REGISTRY.register("hibiscus_flower",
			() -> new net.huwng.vietnamdelight.block.HibiscusFlowerBlock());
	public static final RegistryObject<Block> HIBISCUS_LEAVES = REGISTRY.register("hibiscus_leaves",
			() -> new net.huwng.vietnamdelight.block.HibiscusLeavesBlock());
	public static final RegistryObject<Block> FLOWERING_HIBISCUS_LEAVES = REGISTRY.register("flowering_hibiscus_leaves",
			() -> new net.huwng.vietnamdelight.block.FloweringHibiscusLeavesBlock());
	public static final RegistryObject<Block> BOUGAINVILLEA_SAPLING = REGISTRY.register("bougainvillea_sapling",
			() -> new net.huwng.vietnamdelight.block.BougainvilleaSaplingBlock());
	public static final RegistryObject<Block> POTTED_BOUGAINVILLEA_SAPLING = REGISTRY.register("potted_bougainvillea_sapling",
			() -> new net.minecraft.world.level.block.FlowerPotBlock(() -> (net.minecraft.world.level.block.FlowerPotBlock) net.minecraft.world.level.block.Blocks.FLOWER_POT,
					BOUGAINVILLEA_SAPLING, net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.POTTED_OAK_SAPLING)));
	public static final RegistryObject<Block> BOUGAINVILLEA = REGISTRY.register("bougainvillea",
			() -> new net.huwng.vietnamdelight.block.BougainvilleaBlock());
	public static final RegistryObject<Block> DRIED_BOUGAINVILLEA = REGISTRY.register("dried_bougainvillea",
			() -> new net.huwng.vietnamdelight.block.DriedBougainvilleaBlock());
	public static final RegistryObject<Block> BOUGAINVILLEA_LEAVES = REGISTRY.register("bougainvillea_leaves",
			() -> new net.huwng.vietnamdelight.block.BougainvilleaLeavesBlock(net.huwng.vietnamdelight.block.BougainvilleaLeavesBlock.Variant.REGULAR));
	public static final RegistryObject<Block> FLOWERING_ORANGE_BOUGAINVILLEA_LEAVES = REGISTRY.register("flowering_orange_bougainvillea_leaves",
			() -> new net.huwng.vietnamdelight.block.BougainvilleaLeavesBlock(net.huwng.vietnamdelight.block.BougainvilleaLeavesBlock.Variant.ORANGE));
	public static final RegistryObject<Block> FLOWERING_PINK_BOUGAINVILLEA_LEAVES = REGISTRY.register("flowering_pink_bougainvillea_leaves",
			() -> new net.huwng.vietnamdelight.block.BougainvilleaLeavesBlock(net.huwng.vietnamdelight.block.BougainvilleaLeavesBlock.Variant.PINK));
	public static final RegistryObject<Block> WHITE_BOUGAINVILLEA_LEAVES = REGISTRY.register("white_bougainvillea_leaves",
			() -> new net.huwng.vietnamdelight.block.BougainvilleaLeavesBlock(net.huwng.vietnamdelight.block.BougainvilleaLeavesBlock.Variant.WHITE));
	public static final RegistryObject<Block> BOUGAINVILLEA_TRUNK = REGISTRY.register("bougainvillea_trunk",
			() -> new net.huwng.vietnamdelight.block.BougainvilleaTrunkBlock());
	// End of user code block custom blocks
	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class BlocksClientSideHandler {
		@SubscribeEvent
		public static void blockColorLoad(RegisterColorHandlersEvent.Block event) {
			BanyanLeavesBlock.blockColorLoad(event);
			BanyanVineBlock.blockColorLoad(event);
			FloweringBanyanLeavesBlock.blockColorLoad(event);
			TamarindLeavesBlock.blockColorLoad(event);
			FloweringTamarindLeavesBlock.blockColorLoad(event);
		}

		@SubscribeEvent
		public static void itemColorLoad(RegisterColorHandlersEvent.Item event) {
			BanyanLeavesBlock.itemColorLoad(event);
			BanyanVineBlock.itemColorLoad(event);
			FloweringBanyanLeavesBlock.itemColorLoad(event);
			TamarindLeavesBlock.itemColorLoad(event);
			FloweringTamarindLeavesBlock.itemColorLoad(event);
		}
	}
}
