package com.cu6.avaritia_expand.item.armor.blaze;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

public class BlazeChestplate extends BlazeArmorItem{
    public BlazeChestplate(ArmorMaterial pMaterial, Type pType, Properties pProperties) {
        super(pMaterial, pType, pProperties);
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return false;
    }

    @Override
    public void onCraftedBy(ItemStack pStack, Level pLevel, Player pPlayer) {
        pStack.enchant(Enchantments.FIRE_PROTECTION,4);
        super.onCraftedBy(pStack, pLevel, pPlayer);
    }
}
