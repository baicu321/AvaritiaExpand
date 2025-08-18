package com.cu6.avaritia_expand;

import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
@Mod.EventBusSubscriber
public class ModConfig {
    public static final ForgeConfigSpec COMMON;
    public static final ForgeConfigSpec.IntValue InfinityTNTExplosionRadius;
    public static final ForgeConfigSpec.IntValue InfinityTNTExplosionLength;
    public static final ForgeConfigSpec.BooleanValue InfinityTNTCanBreakBedRock;
    public static final ForgeConfigSpec.BooleanValue InfinityTNTCanBreakObsidian;
public ModConfig() {}
    public static void register() {
        ModLoadingContext.get().registerConfig(Type.COMMON, COMMON);
    }


    private static ForgeConfigSpec.BooleanValue buildBoolean(ForgeConfigSpec.Builder builder, String name, boolean defaultValue, String comment) {
        return builder.comment(comment).translation(name).define(name, defaultValue);
    }

    private static ForgeConfigSpec.IntValue buildInt(ForgeConfigSpec.Builder builder, String name, int defaultValue, int min, int max, String comment) {
        return builder.comment(comment).translation(name).defineInRange(name, defaultValue, min, max);
    }

    private static ForgeConfigSpec.DoubleValue buildDouble(ForgeConfigSpec.Builder builder, String name, double defaultValue, double min, double max, String comment) {
        return builder.comment(comment).translation(name).defineInRange(name, defaultValue, min, max);
    }

    private static ForgeConfigSpec.LongValue buildLong(ForgeConfigSpec.Builder builder, String name, long defaultValue, long min, long max, String comment) {
        return builder.comment(comment).translation(name).defineInRange(name, defaultValue, min, max);
    }

    static {
        ForgeConfigSpec.Builder common = new ForgeConfigSpec.Builder();
        common.comment("Avaritia Expand Common Config");
        common.push("block");
        InfinityTNTExplosionRadius = buildInt(common, "InfinityTNT Radius",
                100,0,1000,
                "The explosion radius of InfinityTNT");
        InfinityTNTExplosionLength = buildInt(common, "InfinityTNT Length",
                -40,-500,0,
                "The explosion length of InfinityTNT");
        InfinityTNTCanBreakBedRock = buildBoolean(common, "InfinityTNT Break BedRock",
                true,
                "Can InfinityTNT break bedrock?");
        InfinityTNTCanBreakObsidian = buildBoolean(common, "InfinityTNT Break Obsidian",
                true,
                "Can InfinityTNT break obsidian?");
        common.pop();
        COMMON = common.build();
    }

}