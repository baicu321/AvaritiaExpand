package com.cu6.avaritia_expand.event.blaze;


import com.cu6.avaritia_expand.AvaritiaExpand;
import com.cu6.avaritia_expand.item.armor.blaze.BlazeLeggings;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AvaritiaExpand.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BlazeLeggingsEvent {
    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event){
        if (event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();
            if (isWearingBlazeLeggings(player)) {
                player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED,40,1,false,false,false));
            }
        }
    }

    private static boolean isWearingBlazeLeggings(Player player) {
        ItemStack leggings = player.getItemBySlot(EquipmentSlot.LEGS);
        return !leggings.isEmpty() && leggings.getItem() instanceof BlazeLeggings;
    }

}
