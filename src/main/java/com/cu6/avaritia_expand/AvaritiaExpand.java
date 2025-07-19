package com.cu6.avaritia_expand;

import com.cu6.avaritia_expand.block.ModBlocks;
import com.cu6.avaritia_expand.block.entity.ModBlockEntities;
import com.cu6.avaritia_expand.item.ModCreativeModTabs;
import com.cu6.avaritia_expand.item.ModItemProperties;
import com.cu6.avaritia_expand.item.ModItems;
import com.cu6.avaritia_expand.screen.BlazeFurnaceScreen;
import com.cu6.avaritia_expand.screen.ModMenuTypes;
import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(AvaritiaExpand.MOD_ID)
public class AvaritiaExpand {

    public static final String MOD_ID = "avaritia_expand";

    private static final Logger LOGGER = LogUtils.getLogger();

    public AvaritiaExpand(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        ModCreativeModTabs.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::addCreative);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            ModItemProperties.register(event);

            MenuScreens.register(ModMenuTypes.BLAZE_FURNACE_MENU.get(), BlazeFurnaceScreen::new);
        }
    }
}
