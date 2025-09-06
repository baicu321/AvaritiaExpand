package com.cu6.avaritia_expand;


import com.cu6.avaritia_expand.block.ModBlocks;
import com.cu6.avaritia_expand.block.entity.ModBlockEntities;
import com.cu6.avaritia_expand.entity.ModEntities;
import com.cu6.avaritia_expand.entity.client.CrystalTNTRenderer;
import com.cu6.avaritia_expand.entity.client.InfinityFishingHookRender;
import com.cu6.avaritia_expand.entity.client.InfinityTNTRenderer;
import com.cu6.avaritia_expand.item.ModCreativeModTabs;
import com.cu6.avaritia_expand.item.ModItemProperties;
import com.cu6.avaritia_expand.item.ModItems;
import com.cu6.avaritia_expand.screen.BlazeFurnaceScreen;
import com.cu6.avaritia_expand.screen.CrystalFurnaceScreen;
import com.cu6.avaritia_expand.screen.ModMenuTypes;
import com.cu6.avaritia_expand.screen.NeutronDecomposeScreen;
import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.EntityRenderers;
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

@Mod(AvaritiaExpand.MOD_ID)
public class AvaritiaExpand {
//666啊,这个入代码全是ai写的
    public static final String MOD_ID = "avaritia_expand";

    private static final Logger LOGGER = LogUtils.getLogger();

    public AvaritiaExpand() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModCreativeModTabs.register(modEventBus);
        ModConfig.register();
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModEntities.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }


    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }


    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }


    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event){
            ModItemProperties.register(event);
            MenuScreens.register(ModMenuTypes.BLAZE_FURNACE_MENU.get(), BlazeFurnaceScreen::new);
            MenuScreens.register(ModMenuTypes.CRYSTAL_FURNACE_MENU.get(), CrystalFurnaceScreen::new);
            MenuScreens.register(ModMenuTypes.NEUTRON_DECOMPOSE_MENU.get(), NeutronDecomposeScreen::new);
            EntityRenderers.register(ModEntities.INFINITY_TNT_ENTITY.get(), InfinityTNTRenderer::new);
            EntityRenderers.register(ModEntities.CRYSTAL_TNT_ENTITY.get(), CrystalTNTRenderer::new);
            EntityRenderers.register(ModEntities.INFINITY_FISHING_HOOK.get(), InfinityFishingHookRender::new);
        }
    }
}

