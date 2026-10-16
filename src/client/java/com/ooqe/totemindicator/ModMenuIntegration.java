package com.ooqe.totemindicator;

import com.google.gson.GsonBuilder;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;

public class ModMenuIntegration implements ModMenuApi {
    public boolean enabled = true;
    public boolean armor = true;
    public boolean shield = true;
    public int color = 0xFF0000;
    private static ModMenuIntegration instance;

    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (parent) -> {
            ConfigBuilder Builder = ConfigBuilder.create().setParentScreen(parent).setTitle(Text.literal("Totem Indicator Settings")).setSavingRunnable(ModMenuIntegration::Write);
            ConfigCategory Category = Builder.getOrCreateCategory(Text.literal("Category"));
            Category.addEntry(Builder.entryBuilder().startBooleanToggle(Text.literal("Toggle Mod"), ModMenuIntegration.Get().enabled).setDefaultValue(true).setTooltip(Text.literal("Enable / Disable the mod")).setSaveConsumer((v) -> ModMenuIntegration.Get().enabled = v).build());
            Category.addEntry(Builder.entryBuilder().startBooleanToggle(Text.literal("Detect Armor"), ModMenuIntegration.Get().armor).setDefaultValue(true).setTooltip(Text.literal("Cares if you have an armor piece on / no, shows glow no matter what your wearing if this is off.")).setSaveConsumer((v) -> ModMenuIntegration.Get().armor = v).build());
            Category.addEntry(Builder.entryBuilder().startBooleanToggle(Text.literal("Detect Shield"), ModMenuIntegration.Get().shield).setDefaultValue(true).setTooltip(Text.literal("This and totem get treated the same basically, toggling this off prevents that.")).setSaveConsumer((v) -> ModMenuIntegration.Get().shield = v).build());
            Category.addEntry(Builder.entryBuilder().startColorField(Text.literal("Color"), ModMenuIntegration.Get().color).setDefaultValue(color).setTooltip(Text.literal("Primary glow color that should go to a player.")).setSaveConsumer((v) -> ModMenuIntegration.Get().color = v).build());
            return Builder.build();
        };
    }
    public static void Read() {
        ModMenuIntegration Data = null;
        if (Files.exists(FabricLoader.getInstance().getConfigDir().resolve("totemindicator.json"))) {
            try (Reader reader = Files.newBufferedReader(FabricLoader.getInstance().getConfigDir().resolve("totemindicator.json"))) {
                Data = new GsonBuilder().setPrettyPrinting().create().fromJson(reader, ModMenuIntegration.class);
            } catch (Exception ignored) {
                Data = null;
            }
        }
        ModMenuIntegration.Set(Data);
        Write();
    }

    public static void Write() {
        try {
            Files.createDirectories(FabricLoader.getInstance().getConfigDir().resolve("totemindicator.json").getParent());
            try (Writer writer = Files.newBufferedWriter(FabricLoader.getInstance().getConfigDir().resolve("totemindicator.json"))) {
                new GsonBuilder().setPrettyPrinting().create().toJson(ModMenuIntegration.Get(), writer);
            }
        } catch (Exception ignored) {}
    }

    public static ModMenuIntegration Get() {
        if (instance == null) {
            ModMenuIntegration.Read();
            if (instance == null) {
                instance = new ModMenuIntegration();
            }
        }
        return instance;
    }

    public static void Set(ModMenuIntegration config) {
        instance = config == null ? new ModMenuIntegration() : config;
    }
}