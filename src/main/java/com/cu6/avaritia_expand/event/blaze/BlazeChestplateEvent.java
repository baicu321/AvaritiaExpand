package com.cu6.avaritia_expand.event.blaze;


import com.cu6.avaritia_expand.AvaritiaExpand;
import com.cu6.avaritia_expand.item.armor.blaze.BlazeChestplate;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = AvaritiaExpand.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BlazeChestplateEvent {
    // Recursion guard to prevent infinite damage reflection loops
    private static final ThreadLocal<Set<UUID>> processingEntities = ThreadLocal.withInitial(HashSet::new);

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event){
        if (event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();
            if (isWearingBlazeChestplate(player)) {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,40,0,false,false,false));
            }
        }
    }
@SubscribeEvent
    public static void onHurt(LivingHurtEvent event){
        if (event.getEntity() instanceof Player player) {
            if (isWearingBlazeChestplate(player)) {
                DamageSource source = event.getSource();
                LivingEntity attacker = source.getDirectEntity() instanceof LivingEntity ?
                        (LivingEntity) source.getDirectEntity() : null;

                if (attacker != null && attacker != player) {
                    UUID attackerId = attacker.getUUID();
                    Set<UUID> processing = processingEntities.get();

                    // Prevent recursion: skip if this attacker is already being processed
                    if (processing.contains(attackerId)) {
                        return;
                    }

                    processing.add(attackerId);
                    try {
                        float reflectedDamage = event.getAmount() * 0.2f;
                        DamageSource reflectedSource = player.level().damageSources().playerAttack(player);
                        attacker.hurt(reflectedSource, reflectedDamage);
                    } finally {
                        processing.remove(attackerId);
                    }
                }
            }
        }
    }


    private static boolean isWearingBlazeChestplate(Player player) {
        ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
        return !chestplate.isEmpty() && chestplate.getItem() instanceof BlazeChestplate;
    }

}
