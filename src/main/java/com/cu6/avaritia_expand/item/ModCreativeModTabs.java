package com.cu6.avaritia_expand.item;

import com.cu6.avaritia_expand.AvaritiaExpand;
import com.cu6.avaritia_expand.block.ModBlocks;
import committee.nova.mods.avaritia.init.registry.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AvaritiaExpand.MOD_ID);

    public static final RegistryObject<CreativeModeTab> EXPAND_TAB = CREATIVE_MODE_TABS.register("avaritia_expand_tab",
            ()-> CreativeModeTab.builder().icon(()->new ItemStack(ModItems.infinity_nugget.get()))
                    .title(Component.translatable("creativetab.expand_tab"))
                    .displayItems((itemDisplayParameters, output) -> {
                        //item
                        output.accept(com.cu6.avaritia_expand.item.ModItems.BLAZE_HELMET.get());
                        output.accept(com.cu6.avaritia_expand.item.ModItems.BLAZE_CHESTPLATE.get());
                        output.accept(com.cu6.avaritia_expand.item.ModItems.BLAZE_LEGGINGS.get());
                        output.accept(com.cu6.avaritia_expand.item.ModItems.BLAZE_BOOTS.get());
                        output.accept(com.cu6.avaritia_expand.item.ModItems.BLAZE_SHIELD.get());
                        output.accept(com.cu6.avaritia_expand.item.ModItems.BLAZE_BOW.get());
                        output.accept(com.cu6.avaritia_expand.item.ModItems.CRYSTAL_HELMET.get());
                        output.accept(com.cu6.avaritia_expand.item.ModItems.CRYSTAL_CHESTPLATE.get());
                        output.accept(com.cu6.avaritia_expand.item.ModItems.CRYSTAL_LEGGINGS.get());
                        output.accept(com.cu6.avaritia_expand.item.ModItems.CRYSTAL_BOOTS.get());
                        output.accept(com.cu6.avaritia_expand.item.ModItems.CRYSTAL_SHIELD.get());
                        output.accept(com.cu6.avaritia_expand.item.ModItems.CRYSTAL_BOW.get());
                        output.accept(com.cu6.avaritia_expand.item.ModItems.INFINITY_FISHINGROD.get());
                        output.accept(com.cu6.avaritia_expand.item.ModItems.INFINITY_EXPERIENCE_BOTTLE.get());
                        output.accept(com.cu6.avaritia_expand.item.ModItems.NEUTRON_SHEARS.get());
                        //block
                        output.accept(ModBlocks.BLAZE_FURNACE.get());
                        output.accept(ModBlocks.CRYSTAL_FURNACE.get());
                        output.accept(ModBlocks.INFINITY_TNT.get());
                    })
                    .build());

    public static void register(IEventBus eventBus){
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
