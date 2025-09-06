package com.cu6.avaritia_expand.item;

import com.cu6.avaritia_expand.AvaritiaExpand;
import com.cu6.avaritia_expand.item.armor.blaze.*;
import com.cu6.avaritia_expand.item.armor.crystal.CrystalBoots;
import com.cu6.avaritia_expand.item.armor.crystal.CrystalChestplate;
import com.cu6.avaritia_expand.item.armor.crystal.CrystalHelmet;
import com.cu6.avaritia_expand.item.armor.crystal.CrystalLeggings;
import com.cu6.avaritia_expand.item.shield.BlazeShieldItem;
import com.cu6.avaritia_expand.item.shield.CrystalShieldItem;
import com.cu6.avaritia_expand.item.tool.*;
import committee.nova.mods.avaritia.init.registry.ModRarities;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, AvaritiaExpand.MOD_ID);

//crystal armor
    public static final RegistryObject<Item> CRYSTAL_HELMET = ITEMS.register("crystal_helmet",
            ()-> new CrystalHelmet(ModArmorMaterials.CRYSTAL_MATRIX, ArmorItem.Type.HELMET,new Item.Properties().rarity(ModRarities.EPIC).fireResistant()));
    public static final RegistryObject<Item> CRYSTAL_CHESTPLATE = ITEMS.register("crystal_chestplate",
            ()-> new CrystalChestplate(ModArmorMaterials.CRYSTAL_MATRIX, ArmorItem.Type.CHESTPLATE,new Item.Properties().rarity(ModRarities.EPIC).fireResistant()));
    public static final RegistryObject<Item> CRYSTAL_LEGGINGS = ITEMS.register("crystal_leggings",
            ()-> new CrystalLeggings(ModArmorMaterials.CRYSTAL_MATRIX, ArmorItem.Type.LEGGINGS,new Item.Properties().rarity(ModRarities.EPIC).fireResistant()));
    public static final RegistryObject<Item> CRYSTAL_BOOTS = ITEMS.register("crystal_boots",
            ()-> new CrystalBoots(ModArmorMaterials.CRYSTAL_MATRIX, ArmorItem.Type.BOOTS,new Item.Properties().rarity(ModRarities.EPIC).fireResistant()));
//blaze armor
    public static final RegistryObject<Item> BLAZE_HELMET = ITEMS.register("blaze_helmet",
            ()-> new BlazeHelmet(ModArmorMaterials.BLAZE_CUBE, BlazeArmorItem.Type.HELMET,new Item.Properties().rarity(ModRarities.EPIC).fireResistant()));
    public static final RegistryObject<Item> BLAZE_CHESTPLATE = ITEMS.register("blaze_chestplate",
            ()-> new BlazeChestplate(ModArmorMaterials.BLAZE_CUBE, BlazeArmorItem.Type.CHESTPLATE,new Item.Properties().rarity(ModRarities.EPIC).fireResistant()));
    public static final RegistryObject<Item> BLAZE_LEGGINGS = ITEMS.register("blaze_leggings",
            ()-> new BlazeLeggings(ModArmorMaterials.BLAZE_CUBE, BlazeArmorItem.Type.LEGGINGS,new Item.Properties().rarity(ModRarities.EPIC).fireResistant()));
    public static final RegistryObject<Item> BLAZE_BOOTS = ITEMS.register("blaze_boots",
            ()-> new BlazeBoots(ModArmorMaterials.BLAZE_CUBE, BlazeArmorItem.Type.BOOTS,new Item.Properties().rarity(ModRarities.EPIC).fireResistant()));
//blaze tools
    public static final RegistryObject<Item> BLAZE_SHIELD = ITEMS.register("blaze_shield",
        ()-> new BlazeShieldItem(new Item.Properties().rarity(ModRarities.EPIC).durability(7777).fireResistant()));
    public static final RegistryObject<Item> BLAZE_BOW = ITEMS.register("blaze_bow",
            ()-> new BlazeBowItem(new Item.Properties().rarity(ModRarities.EPIC).durability(7777).fireResistant()));
//crystal tools
public static final RegistryObject<Item> CRYSTAL_SHIELD = ITEMS.register("crystal_shield",
        ()-> new CrystalShieldItem(new Item.Properties().rarity(ModRarities.EPIC).durability(8888).fireResistant()));
    public static final RegistryObject<Item> CRYSTAL_BOW = ITEMS.register("crystal_bow",
            ()-> new CrystalBowItem(new Item.Properties().rarity(ModRarities.EPIC).durability(7777).fireResistant()));
//Infinity_tools
    public static final RegistryObject<Item> INFINITY_FISHINGROD = ITEMS.register("infinity_fishing_rod",
        ()-> new InfinityFishingRod(new Item.Properties().rarity(ModRarities.COSMIC).durability(9999).fireResistant()));
//neutron_tools
    public static final RegistryObject<Item> NEUTRON_SHEARS = ITEMS.register("neutron_shears",
            ()-> new NeutronShears(new Item.Properties().rarity(ModRarities.COSMIC).fireResistant().stacksTo(1)));
//endest_items
    public static final RegistryObject<Item> ENDEST_WATCH = ITEMS.register("endest_watch",
        ()-> new EndestWatch(new Item.Properties().rarity(ModRarities.COSMIC).fireResistant().stacksTo(1)));

//Infinity_Tools
    public static final RegistryObject<Item> INFINITY_EXPERIENCE_BOTTLE = ITEMS.register("infinity_experience_bottle",
        ()-> new InfinityExperienceBottle(new Item.Properties().rarity(ModRarities.COSMIC).stacksTo(1).fireResistant()));

//Blaze_Items
public static final RegistryObject<Item> BLAZE_NUGGET = ITEMS.register("blaze_nugget",
        ()-> new Item(new Item.Properties().rarity(ModRarities.EPIC)));


//Crystal_Items
public static final RegistryObject<Item> CRYSTAL_NUGGET = ITEMS.register("crystal_nugget",
        ()-> new Item(new Item.Properties().rarity(ModRarities.EPIC)));

//neutron_Items
public static final RegistryObject<Item> CLONE_CORE = ITEMS.register("clone_core",
        ()-> new Item(new Item.Properties().rarity(ModRarities.EPIC)));


    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
