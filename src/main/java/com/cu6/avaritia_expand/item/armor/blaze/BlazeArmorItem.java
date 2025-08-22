package com.cu6.avaritia_expand.item.armor.blaze;

import com.cu6.avaritia_expand.item.ModArmorMaterials;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Map;

public class BlazeArmorItem extends ArmorItem {
    public static final Map<ArmorMaterial, MobEffectInstance> MATERIAL_TO_EFFECT_MAP =
            (new ImmutableMap.Builder<ArmorMaterial,MobEffectInstance>())
                    .put(ModArmorMaterials.BLAZE_CUBE,
                            new MobEffectInstance(MobEffects.FIRE_RESISTANCE,20,0,false,false,false)).build();
    public BlazeArmorItem(ArmorMaterial pMaterial, Type pType, Properties pProperties) {
        super(pMaterial, pType, pProperties);
    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        if (!level.isClientSide()){
            if (hasFullSuitOfArmorOn(player)){
                evaluateArmorEffects(player);
                if (player.isInLava()) {
                    if (player.hasEffect(MobEffects.REGENERATION)) {
                    }else {
                        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 1, false, false, false));
                    }
                }
            }
        }
    }

    private void evaluateArmorEffects(Player player){
        for (Map.Entry<ArmorMaterial,MobEffectInstance> entry : MATERIAL_TO_EFFECT_MAP.entrySet()){
            ArmorMaterial mapArmorMaterial = entry.getKey();
            MobEffectInstance mapStatusEffect = entry.getValue();

            if (hasCorrectArmorOn(mapArmorMaterial,player)){
                addStatusEffectForMaterial(player,mapArmorMaterial,mapStatusEffect);
            }
        }
    }

    private void addStatusEffectForMaterial(Player player, ArmorMaterial mapArmorMaterial, MobEffectInstance mapStatusEffect) {
        boolean hasPlayerEffect = player.hasEffect(mapStatusEffect.getEffect());

        if (hasCorrectArmorOn(mapArmorMaterial,player) && !hasPlayerEffect){
            player.addEffect(new MobEffectInstance(
                    mapStatusEffect.getEffect(),
                    mapStatusEffect.getDuration(),
                    mapStatusEffect.getAmplifier(),
                    false,
                    false,
                    false
                    ));
        }
    }

    private boolean hasCorrectArmorOn(ArmorMaterial material, Player player) {
        ArmorItem boots = ((ArmorItem) player.getInventory().getArmor(0).getItem());
        ArmorItem leggings = ((ArmorItem) player.getInventory().getArmor(1).getItem());
        ArmorItem chestplate = ((ArmorItem) player.getInventory().getArmor(2).getItem());
        ArmorItem helmet = ((ArmorItem) player.getInventory().getArmor(3).getItem());

        return helmet.getMaterial() == material && chestplate.getMaterial() == material && leggings.getMaterial() == material && boots.getMaterial() == material;
    }
    // 移除所有盔甲效果
    private void removeAllArmorEffects(Player player) {
        for (Map.Entry<ArmorMaterial, MobEffectInstance> entry : MATERIAL_TO_EFFECT_MAP.entrySet()) {
            MobEffect effect = entry.getValue().getEffect();
            if (player.hasEffect(effect)) {
                player.removeEffect(effect);
            }
        }
    }
    private boolean hasFullSuitOfArmorOn(Player player){
        ItemStack boots = player.getInventory().getArmor(0);
        ItemStack leggings = player.getInventory().getArmor(1);
        ItemStack  chestplate= player.getInventory().getArmor(2);
        ItemStack helmet = player.getInventory().getArmor(3);

        return !helmet.isEmpty() && !chestplate.isEmpty()
                && ! leggings.isEmpty() && !boots.isEmpty();
    }
}
