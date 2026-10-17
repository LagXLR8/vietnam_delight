package net.huwng.vietnamdelight;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModFeatures {
    // Registry cho tất cả Feature
    public static final DeferredRegister<Feature<?>> FEATURES =
        DeferredRegister.create(Registries.FEATURE, VietnamDelightMod.MODID);

    // Feature cây lệch góc (ví dụ dùng cho cây Ginger)
    public static final RegistryObject<Feature<TreeConfiguration>> OFFSET_TREE =
        FEATURES.register("offset_tree", OffsetTreeFeature::new);

    // Hàm khởi tạo gọi từ VietnamDelightMod.java
    public static void register(IEventBus eventBus) {
        FEATURES.register(eventBus);
    }
}
