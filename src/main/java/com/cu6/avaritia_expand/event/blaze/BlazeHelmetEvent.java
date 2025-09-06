package com.cu6.avaritia_expand.event.blaze;


import com.cu6.avaritia_expand.AvaritiaExpand;
import com.cu6.avaritia_expand.item.armor.blaze.BlazeHelmet;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AvaritiaExpand.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BlazeHelmetEvent {

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event){
        if (event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();
            if (isWearingBlazeHelmet(player)) {
                player.removeEffect(MobEffects.BLINDNESS);
                player.removeEffect(MobEffects.DARKNESS);
            }
        }
    }

    private static boolean isWearingBlazeHelmet(Player player) {
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        return !helmet.isEmpty() && helmet.getItem() instanceof BlazeHelmet;
    }
}
