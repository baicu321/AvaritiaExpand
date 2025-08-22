package com.cu6.avaritia_expand.event.crystal;

import com.cu6.avaritia_expand.AvaritiaExpand;
import com.cu6.avaritia_expand.item.ModItems;
import com.cu6.avaritia_expand.item.armor.crystal.CrystalChestplate;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = AvaritiaExpand.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CrystalChestplateEvent {

    private static final UUID LUCK_BOOST_UUID = UUID.fromString("a1b2c3d4-e5f6-4a5b-9c8d-7e6f5a4b3c2d");
    private static final double LUCK_BOOST_AMOUNT = 2.0;


    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (isWearingCrystalChestplate(player)) {

                clearNegativeEffects(player);


                applyLuckBoost(player);
            } else {

                removeLuckBoost(player);
            }
        }
    }


private static void clearNegativeEffects(Player player) {

    List<MobEffectInstance> effects = new ArrayList<>(player.getActiveEffects());
    for (MobEffectInstance effect : effects) {
        MobEffect potion = effect.getEffect();
        if (!potion.isBeneficial()) {
            player.removeEffect(potion);
        }
    }
}

    private static boolean isWearingCrystalChestplate(Player player) {
        ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
        return !chestplate.isEmpty() && chestplate.getItem() instanceof CrystalChestplate;
    }

    private static void applyLuckBoost(Player player) {
        var luckAttribute = player.getAttribute(Attributes.LUCK);
        if (luckAttribute == null) return;


        luckAttribute.removeModifier(LUCK_BOOST_UUID);


        AttributeModifier luckBoost = new AttributeModifier(
                LUCK_BOOST_UUID,
                "Crystal chestplate luck boost",
                LUCK_BOOST_AMOUNT,
                AttributeModifier.Operation.ADDITION
        );

        luckAttribute.addPermanentModifier(luckBoost);
    }

    private static void removeLuckBoost(Player player) {
        var luckAttribute = player.getAttribute(Attributes.LUCK);
        if (luckAttribute == null) return;

        AttributeModifier existing = luckAttribute.getModifier(LUCK_BOOST_UUID);
        if (existing != null) {
            luckAttribute.removeModifier(existing);
        }
    }
}