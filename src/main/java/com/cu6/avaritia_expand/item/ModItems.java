package com.cu6.avaritia_expand.item;

import com.cu6.avaritia_expand.AvaritiaExpand;
import committee.nova.mods.avaritia.common.item.singularity.Singularity;
import committee.nova.mods.avaritia.init.registry.ModRarities;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, AvaritiaExpand.MOD_ID);
//crtsal armor
    public static final RegistryObject<Item> CRYSTAL_HELMET = ITEMS.register("crystal_helmet",
            ()-> new ArmorItem(ModArmorMaterials.CRYSTAL_MATRIX, ArmorItem.Type.HELMET,new Item.Properties().rarity(ModRarities.EPIC)));
    public static final RegistryObject<Item> CRYSTAL_CHESTPLATE = ITEMS.register("crystal_chestplate",
            ()-> new ArmorItem(ModArmorMaterials.CRYSTAL_MATRIX, ArmorItem.Type.CHESTPLATE,new Item.Properties().rarity(ModRarities.EPIC)));
    public static final RegistryObject<Item> CRYSTAL_LEGGINGS = ITEMS.register("crystal_leggings",
            ()-> new ArmorItem(ModArmorMaterials.CRYSTAL_MATRIX, ArmorItem.Type.LEGGINGS,new Item.Properties().rarity(ModRarities.EPIC)));
    public static final RegistryObject<Item> CRYSTAL_BOOTS = ITEMS.register("crystal_boots",
            ()-> new ArmorItem(ModArmorMaterials.CRYSTAL_MATRIX, ArmorItem.Type.BOOTS,new Item.Properties().rarity(ModRarities.EPIC)));
//blaze_cube armor
    public static final RegistryObject<Item> BLAZE_HELMET = ITEMS.register("blaze_helmet",
            ()-> new ModArmorItem(ModArmorMaterials.BLAZE_CUBE,ModArmorItem.Type.HELMET,new Item.Properties().rarity(ModRarities.EPIC)));
    public static final RegistryObject<Item> BLAZE_CHESTPLATE = ITEMS.register("blaze_chestplate",
            ()-> new ModArmorItem(ModArmorMaterials.BLAZE_CUBE,ModArmorItem.Type.CHESTPLATE,new Item.Properties().rarity(ModRarities.EPIC)));
    public static final RegistryObject<Item> BLAZE_LEGGINGS = ITEMS.register("blaze_leggings",
            ()-> new ModArmorItem(ModArmorMaterials.BLAZE_CUBE,ModArmorItem.Type.LEGGINGS,new Item.Properties().rarity(ModRarities.EPIC)));
    public static final RegistryObject<Item> BLAZE_BOOTS = ITEMS.register("blaze_boots",
            ()-> new ModArmorItem(ModArmorMaterials.BLAZE_CUBE,ModArmorItem.Type.BOOTS,new Item.Properties().rarity(ModRarities.EPIC)));
//blaze_cube tools
    public static final RegistryObject<Item> BLAZE_SHIELD = ITEMS.register("blaze_shield",
        ()-> new ModShieldItem(new Item.Properties().rarity(ModRarities.EPIC).durability(7777)));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
