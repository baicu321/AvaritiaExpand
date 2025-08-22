package com.cu6.avaritia_expand.event.crystal;

import com.cu6.avaritia_expand.AvaritiaExpand;
import com.cu6.avaritia_expand.item.armor.crystal.CrystalLeggings;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


import java.util.UUID;

@Mod.EventBusSubscriber(modid = AvaritiaExpand.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CrystalLeggingsEvent {

    private static final UUID SPEED_BOOST_UUID = UUID.fromString("f4b5c5a6-8d3e-4c2b-9a1f-0e1d2c3b4a5e");

    private static final double SPEED_BOOST_AMOUNT = 0.3;

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingTickEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (isWearingCrystalLeggings(player)) {
                applySpeedBoost(player);
            } else {
                removeSpeedBoost(player);
            }
        }
    }

    private static boolean isWearingCrystalLeggings(Player player) {
        ItemStack leggings = player.getItemBySlot(EquipmentSlot.LEGS);
        return !leggings.isEmpty() && leggings.getItem() instanceof CrystalLeggings;
    }

    private static void applySpeedBoost(Player player) {

        var speedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttribute == null) {

            return;
        }


        speedAttribute.removeModifier(SPEED_BOOST_UUID);


        AttributeModifier speedBoost = new AttributeModifier(
                SPEED_BOOST_UUID,
                "Crystal leggings speed boost",
                SPEED_BOOST_AMOUNT,
                AttributeModifier.Operation.MULTIPLY_BASE
        );

        speedAttribute.addPermanentModifier(speedBoost);

    }

    private static void removeSpeedBoost(Player player) {
        var speedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttribute == null) return;


        AttributeModifier existing = speedAttribute.getModifier(SPEED_BOOST_UUID);
        if (existing != null) {
            speedAttribute.removeModifier(existing);

        }
    }
}
