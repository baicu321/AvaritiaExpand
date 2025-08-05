package com.cu6.avaritia_expand;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

import java.nio.file.Path;

@Mod.EventBusSubscriber
public class ModConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;


    public static final ForgeConfigSpec.ConfigValue<Float> EXPLOSION_RADIUS;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ALLOW_BREAK_BEDROCK;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ALLOW_BREAK_OBSIDIAN;

    static {
        BUILDER.push("Infinity TNT Configuration");

        // 爆炸半径配置（默认100.0）
        EXPLOSION_RADIUS = BUILDER
                .translation("infinity_tnt_explosion_radius")
                .define("explosion_radius", 100.0F);

        // 是否允许破坏基岩（默认不允许）
        ALLOW_BREAK_BEDROCK = BUILDER
                .translation("can_break_bedrock")
                .define("allow_break_bedrock", false);

        // 是否允许破坏黑曜石（默认不允许）
        ALLOW_BREAK_OBSIDIAN = BUILDER
                .translation("can_break_obsidian")
                .define("allow_break_obsidian", false);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }


    public static void loadConfig(ForgeConfigSpec spec, Path path) {
        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();
        configData.load();
        spec.setConfig(configData);
    }
}