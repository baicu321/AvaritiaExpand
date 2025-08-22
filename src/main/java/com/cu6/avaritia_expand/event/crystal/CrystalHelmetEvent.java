package com.cu6.avaritia_expand.event.crystal;

import com.cu6.avaritia_expand.item.armor.crystal.CrystalHelmet;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.cu6.avaritia_expand.AvaritiaExpand;

@Mod.EventBusSubscriber(modid = AvaritiaExpand.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CrystalHelmetEvent {

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLivingAttack(LivingAttackEvent event) {

        LivingEntity hurtEntity = event.getEntity();
        DamageSource source = event.getSource();


        if (hurtEntity == null) {
            return;
        }


        if (source.getDirectEntity() instanceof AbstractArrow) {

            if (hurtEntity instanceof Player) {
                Player player = (Player) hurtEntity;


                ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
                if (helmet.getItem() instanceof CrystalHelmet) {


                    event.setCanceled(true);


                    AbstractArrow arrow = (AbstractArrow) source.getDirectEntity();
                    deflectArrow(arrow, player);
                }
            }
        }
    }


    private static void deflectArrow(AbstractArrow arrow, LivingEntity target) {
        if (arrow == null || arrow.level().isClientSide()) {
            return;
        }


        arrow.setDeltaMovement(
                arrow.getDeltaMovement().x * -1.5,
                arrow.getDeltaMovement().y * -1.2,
                arrow.getDeltaMovement().z * -1.5
        );


        arrow.setOwner(target);


        arrow.setPierceLevel((byte) 0);
        arrow.setKnockback(1);

        target.level().playSound(null, target.blockPosition(),
                SoundEvents.NOTE_BLOCK_BASEDRUM.get(), SoundSource.PLAYERS, 1.0F, 1.2F);
    }
}
