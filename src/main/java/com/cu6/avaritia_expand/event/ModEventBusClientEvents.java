package com.cu6.avaritia_expand.event;


import com.cu6.avaritia_expand.AvaritiaExpand;
import com.cu6.avaritia_expand.entity.client.InfinityTNTModel;
import com.cu6.avaritia_expand.entity.client.ModModelLayers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AvaritiaExpand.MOD_ID,bus = Mod.EventBusSubscriber.Bus.MOD,value = Dist.CLIENT)
public class ModEventBusClientEvents {
@SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event){
        event.registerLayerDefinition(ModModelLayers.INFINITY_TNT_LAYER, InfinityTNTModel::createBodyLayer);

    }

}
