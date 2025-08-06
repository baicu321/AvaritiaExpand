package com.cu6.avaritia_expand.item;

import com.cu6.avaritia_expand.AvaritiaExpand;
import com.cu6.avaritia_expand.item.armor.*;
import com.cu6.avaritia_expand.item.shield.BlazeShieldItem;
import com.cu6.avaritia_expand.item.shield.CrystalShieldItem;
import com.cu6.avaritia_expand.item.tool.InfinityCrossBow;
import com.cu6.avaritia_expand.item.tool.InfinityExperienceBottle;
import com.cu6.avaritia_expand.item.tool.InfinityFishingRod;
import committee.nova.mods.avaritia.init.registry.ModRarities;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, AvaritiaExpand.MOD_ID);

//crystal armor
    public static final RegistryObject<Item> CRYSTAL_HELMET = ITEMS.register("crystal_helmet",
            ()-> new CrystalHelmet(ModArmorMaterials.CRYSTAL_MATRIX, ArmorItem.Type.HELMET,new Item.Properties().rarity(ModRarities.EPIC)));
    public static final RegistryObject<Item> CRYSTAL_CHESTPLATE = ITEMS.register("crystal_chestplate",
            ()-> new CrystalChestplate(ModArmorMaterials.CRYSTAL_MATRIX, ArmorItem.Type.CHESTPLATE,new Item.Properties().rarity(ModRarities.EPIC)));
    public static final RegistryObject<Item> CRYSTAL_LEGGINGS = ITEMS.register("crystal_leggings",
            ()-> new CrystalLeggings(ModArmorMaterials.CRYSTAL_MATRIX, ArmorItem.Type.LEGGINGS,new Item.Properties().rarity(ModRarities.EPIC)));
    public static final RegistryObject<Item> CRYSTAL_BOOTS = ITEMS.register("crystal_boots",
            ()-> new CrystalBoots(ModArmorMaterials.CRYSTAL_MATRIX, ArmorItem.Type.BOOTS,new Item.Properties().rarity(ModRarities.EPIC)));
//blaze_cube armor
    public static final RegistryObject<Item> BLAZE_HELMET = ITEMS.register("blaze_helmet",
            ()-> new BlazeArmorItem(ModArmorMaterials.BLAZE_CUBE, BlazeArmorItem.Type.HELMET,new Item.Properties().rarity(ModRarities.EPIC)));
    public static final RegistryObject<Item> BLAZE_CHESTPLATE = ITEMS.register("blaze_chestplate",
            ()-> new BlazeArmorItem(ModArmorMaterials.BLAZE_CUBE, BlazeArmorItem.Type.CHESTPLATE,new Item.Properties().rarity(ModRarities.EPIC)));
    public static final RegistryObject<Item> BLAZE_LEGGINGS = ITEMS.register("blaze_leggings",
            ()-> new BlazeArmorItem(ModArmorMaterials.BLAZE_CUBE, BlazeArmorItem.Type.LEGGINGS,new Item.Properties().rarity(ModRarities.EPIC)));
    public static final RegistryObject<Item> BLAZE_BOOTS = ITEMS.register("blaze_boots",
            ()-> new BlazeArmorItem(ModArmorMaterials.BLAZE_CUBE, BlazeArmorItem.Type.BOOTS,new Item.Properties().rarity(ModRarities.EPIC)));
//blaze_cube tools
    public static final RegistryObject<Item> BLAZE_SHIELD = ITEMS.register("blaze_shield",
        ()-> new BlazeShieldItem(new Item.Properties().rarity(ModRarities.EPIC).durability(7777)));
//crystal tools
public static final RegistryObject<Item> CRYSTAL_SHIELD = ITEMS.register("crystal_shield",
        ()-> new CrystalShieldItem(new Item.Properties().rarity(ModRarities.EPIC).durability(8888)));
//Infinity_tools
    public static final RegistryObject<Item> INFINITY_FISHINGROD = ITEMS.register("infinity_fishing_rod",
        ()-> new InfinityFishingRod(new Item.Properties().rarity(ModRarities.COSMIC).durability(9999)));
    public static final RegistryObject<Item> INFINITY_CROSSBOW = ITEMS.register("infinity_crossbow",
            ()-> new InfinityCrossBow(new Item.Properties().rarity(ModRarities.COSMIC).durability(9999)));
//Infinity_Items
    public static final RegistryObject<Item> INFINITY_EXPERIENCE_BOTTLE = ITEMS.register("infinity_experience_bottle",
        ()-> new InfinityExperienceBottle(new Item.Properties().rarity(ModRarities.COSMIC).stacksTo(1)));







    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
