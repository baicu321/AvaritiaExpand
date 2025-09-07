package com.cu6.avaritia_expand.item;

import com.cu6.avaritia_expand.AvaritiaExpand;
import com.cu6.avaritia_expand.block.ModBlocks;
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
            ()-> CreativeModeTab.builder().icon(()->new ItemStack(ModItems.INFINITY_EXPERIENCE_BOTTLE.get()))
                    .title(Component.translatable("creativetab.expand_tab"))
                    .displayItems((itemDisplayParameters, output) -> {
                        //item
                        output.accept(ModItems.BLAZE_HELMET.get());
                        output.accept(ModItems.BLAZE_CHESTPLATE.get());
                        output.accept(ModItems.BLAZE_LEGGINGS.get());
                        output.accept(ModItems.BLAZE_BOOTS.get());
                        output.accept(ModItems.BLAZE_SHIELD.get());
                        output.accept(ModItems.BLAZE_BOW.get());
                        output.accept(ModItems.BLAZE_NUGGET.get());
                        output.accept(ModItems.CRYSTAL_HELMET.get());
                        output.accept(ModItems.CRYSTAL_CHESTPLATE.get());
                        output.accept(ModItems.CRYSTAL_LEGGINGS.get());
                        output.accept(ModItems.CRYSTAL_BOOTS.get());
                        output.accept(ModItems.CRYSTAL_SHIELD.get());
                        output.accept(ModItems.CRYSTAL_BOW.get());
                        output.accept(ModItems.CRYSTAL_NUGGET.get());
                        output.accept(ModItems.INFINITY_FISHINGROD.get());
                        output.accept(ModItems.INFINITY_EXPERIENCE_BOTTLE.get());
                        output.accept(ModItems.NEUTRON_SHEARS.get());
                        output.accept(ModItems.ENDEST_WATCH.get());
                        output.accept(ModItems.CLONE_CORE.get());
                        //block
                        output.accept(ModBlocks.BLAZE_FURNACE.get());
                        output.accept(ModBlocks.CRYSTAL_FURNACE.get());
                        output.accept(ModBlocks.NEUTRON_DECOMPOSE.get());
                        output.accept(ModBlocks.CRYSTAL_TNT.get());
                        output.accept(ModBlocks.INFINITY_TNT.get());
                    })
                    .build());

    public static void register(IEventBus eventBus){
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
