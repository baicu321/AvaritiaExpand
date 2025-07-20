package com.cu6.avaritia_expand.block.renderer;


import com.cu6.avaritia_expand.AvaritiaExpand;
import com.cu6.avaritia_expand.block.renderer.InfinityTNTRenderer;
import com.cu6.avaritia_expand.entity.ModEntities;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = AvaritiaExpand.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModRenderers {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // ...

        // 确保这行存在并执行
        event.enqueueWork(() -> {
            EntityRenderers.register(
                    ModEntities.INFINITY_TNT_ENTITY.get(),
                    InfinityTNTRenderer::new
            );
        });
    }
}
