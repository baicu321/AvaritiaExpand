package com.cu6.avaritia_expand.screen;

import com.cu6.avaritia_expand.AvaritiaExpand;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, AvaritiaExpand.MOD_ID);

    private static <T extends AbstractContainerMenu>RegistryObject<MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory){
        return MENUS.register(name,()-> IForgeMenuType.create(factory));
    }


    public static final RegistryObject<MenuType<BlazeFurnaceMenu>> BLAZE_FURNACE_MENU =
            registerMenuType("blaze_furnace_menu",BlazeFurnaceMenu::new);
    public static final RegistryObject<MenuType<CrystalFurnaceMenu>> CRYSTAL_FURNACE_MENU =
            registerMenuType("crystal_furnace_menu",CrystalFurnaceMenu::new);
    public static void register(IEventBus eventBus){
        MENUS.register(eventBus);
    }
}
