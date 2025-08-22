package com.cu6.avaritia_expand.event.crystal;

import com.cu6.avaritia_expand.item.armor.crystal.CrystalBoots;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.cu6.avaritia_expand.AvaritiaExpand;

@Mod.EventBusSubscriber(modid = AvaritiaExpand.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CrystalBootsEvent {


    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        LivingEntity entity = event.getEntity();


        if (entity instanceof Player) {
            Player player = (Player) entity;


            ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
            if (boots.getItem() instanceof CrystalBoots) {

                event.setCanceled(true);



            }
        }
    }
}
