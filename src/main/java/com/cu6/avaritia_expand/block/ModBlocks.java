package com.cu6.avaritia_expand.block;

import com.cu6.avaritia_expand.AvaritiaExpand;
import com.cu6.avaritia_expand.block.custom.*;
import com.cu6.avaritia_expand.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, AvaritiaExpand.MOD_ID);


    public static final RegistryObject<Block> BLAZE_FURNACE = registerBlock("blaze_furnace",
            ()-> new BlazeFurnaceBlock(BlockBehaviour.Properties.copy(Blocks.STONE).lightLevel(value -> 11).noOcclusion()));
    public static final RegistryObject<Block> CRYSTAL_FURNACE = registerBlock("crystal_furnace",
            ()-> new CrystalFurnaceBlock(BlockBehaviour.Properties.copy(Blocks.STONE).noOcclusion()));
    public static final RegistryObject<Block> INFINITY_TNT = registerBlock("infinity_tnt",
            ()-> new InfinityTNT(BlockBehaviour.Properties.copy(Blocks.TNT).lightLevel(value -> 15).noOcclusion()));
    public static final RegistryObject<Block> CRYSTAL_TNT = registerBlock("crystal_tnt",
            ()-> new CrystalTNT(BlockBehaviour.Properties.copy(Blocks.TNT).lightLevel(value -> 13).noOcclusion()));
    public static final RegistryObject<Block> NEUTRON_DECOMPOSE = registerBlock("neutron_decompose",
            ()-> new NeutronDecomposeBlock(BlockBehaviour.Properties.copy(Blocks.STONE).noOcclusion()));


    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block){
        RegistryObject<T> toReturn = BLOCKS.register(name,block);
        registerBlockItem(name,toReturn);
        return toReturn;
    }

    private static <T extends Block>RegistryObject<Item> registerBlockItem(String name,RegistryObject<T> block){
        return ModItems.ITEMS.register(name,()-> new BlockItem(block.get(),new Item.Properties()));
    }


    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }


}
