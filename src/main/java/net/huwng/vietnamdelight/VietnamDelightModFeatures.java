package net.huwng.vietnamdelight;

import net.huwng.vietnamdelight.VietnamDelightMod;
import net.huwng.vietnamdelight.world.feature.BananaTreeFeature;
import net.huwng.vietnamdelight.world.feature.BanyanTreeFeature;
import net.huwng.vietnamdelight.world.feature.FlamboyantTreeFeature;
import net.huwng.vietnamdelight.world.feature.GoldenFlamboyantTreeFeature;
import net.huwng.vietnamdelight.world.feature.NipaPalmFeature;
import net.huwng.vietnamdelight.world.feature.TamarindTreeFeature;
import net.huwng.vietnamdelight.world.feature.WaterPrimroseFeature;
import net.huwng.vietnamdelight.world.feature.WaterHyacinthFieldFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class VietnamDelightModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(ForgeRegistries.FEATURES, VietnamDelightMod.MODID);

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> BANANA_TREE =
            FEATURES.register("banana_tree", () -> new BananaTreeFeature(NoneFeatureConfiguration.CODEC));

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> BANYAN_TREE =
            FEATURES.register("banyan_tree", () -> new BanyanTreeFeature());

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> FLAMBOYANT_TREE =
            FEATURES.register("flamboyant_tree", () -> new FlamboyantTreeFeature());

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> GOLDEN_FLAMBOYANT_TREE =
            FEATURES.register("golden_flamboyant_tree", () -> new GoldenFlamboyantTreeFeature());

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> NIPA_PALM =
            FEATURES.register("nipa_palm", () -> new NipaPalmFeature());

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> TAMARIND_TREE =
            FEATURES.register("tamarind_tree", () -> new TamarindTreeFeature());

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> WATER_PRIMROSE =
            FEATURES.register("water_primrose", () -> new WaterPrimroseFeature());

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> WATER_HYACINTH_FIELD =
            FEATURES.register("water_hyacinth_field", () -> new WaterHyacinthFieldFeature());

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> DURIAN_TREE =
            FEATURES.register("durian_tree", () -> new net.huwng.vietnamdelight.world.feature.DurianTreeFeature());

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> TALL_DURIAN_TREE =
            FEATURES.register("tall_durian_tree", () -> new net.huwng.vietnamdelight.world.feature.TallDurianTreeFeature());

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> HIBISCUS_BUSH =
            FEATURES.register("hibiscus_bush", () -> new net.huwng.vietnamdelight.world.feature.HibiscusBushFeature());

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> BOUGAINVILLEA_TREE =
            FEATURES.register("bougainvillea_tree", () -> new net.huwng.vietnamdelight.world.feature.BougainvilleaTreeFeature());
}