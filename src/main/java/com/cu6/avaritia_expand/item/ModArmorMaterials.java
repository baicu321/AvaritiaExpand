package com.cu6.avaritia_expand.item;

import com.cu6.avaritia_expand.AvaritiaExpand;
import committee.nova.mods.avaritia.init.registry.ModBlocks;
import committee.nova.mods.avaritia.init.registry.ModItems;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;
public enum ModArmorMaterials implements ArmorMaterial {
    CRYSTAL_MATRIX("crystal_matrix",8888,new int[]{58,60,58,56},25,
            SoundEvents.ARMOR_EQUIP_GOLD,5f,0.5f,()->Ingredient.of(ModBlocks.crystal_matrix.get())),
    BLAZE_CUBE("blaze_cube",7777,new int[]{32,35,32,30},25,
            SoundEvents.ARMOR_EQUIP_GOLD,3f,0.1f,()->Ingredient.of(ModItems.blaze_cube.get()));

    public  final String name;
    private final int durabilityMultiplier;
    private final int[] protectionAmounts;
    private final int enchantmenValue;
    private final SoundEvent equipSound;
    private final float toughness;
    private final float knockbackResistance;
    private final Supplier<Ingredient> repairIngredient;

    private static final int[] BASE_DURABILITY = {1,1,1,1};

    ModArmorMaterials(String name, int durabilityMultiplier, int[] protectionAmounts, int enchantmenValue, SoundEvent equipSound, float toughness, float knockbackResistance, Supplier<Ingredient> repairIngredient) {
        this.name = name;
        this.durabilityMultiplier = durabilityMultiplier;
        this.protectionAmounts = protectionAmounts;
        this.enchantmenValue = enchantmenValue;
        this.equipSound = equipSound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.repairIngredient = repairIngredient;
    }

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return BASE_DURABILITY[type.ordinal()] * this.durabilityMultiplier;
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return this.protectionAmounts[type.ordinal()];
    }

    @Override
    public int getEnchantmentValue() {
        return enchantmenValue;
    }

    @Override
    public SoundEvent getEquipSound() {
        return this.equipSound;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }

    @Override
    public String getName() {
        return AvaritiaExpand.MOD_ID + ":" + this.name;
    }

    @Override
    public float getToughness() {
        return this.toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }
}
