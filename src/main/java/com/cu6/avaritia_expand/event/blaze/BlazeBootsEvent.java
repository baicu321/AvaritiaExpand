package com.cu6.avaritia_expand.event.blaze;

import com.cu6.avaritia_expand.AvaritiaExpand;
import com.cu6.avaritia_expand.item.armor.blaze.BlazeBoots;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = AvaritiaExpand.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BlazeBootsEvent {

    // 最低触发推力的摔落高度
    private static final float MIN_PUSH_HEIGHT = 3.0F;
    // 触发粒子效果的最低高度
    private static final float MIN_PARTICLE_HEIGHT = 5.0F;
    // 最大推力强度
    private static final double MAX_PUSH_STRENGTH = 3.0;

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        LivingEntity entity = event.getEntity();

        if (entity instanceof Player player && isWearingBlazeBoots(player)) {

            float originalDamage = event.getDamageMultiplier() * event.getDistance();
            float reducedDamage = originalDamage * 0.7F;
            event.setDamageMultiplier(reducedDamage / event.getDistance());


            float fallHeight = event.getDistance();


            if (fallHeight >= MIN_PUSH_HEIGHT) {

                double pushStrength = calculatePushStrength(fallHeight);
                pushAwayNearbyEntities(player, player.level(), 2.5, pushStrength);
            }


            if (fallHeight >= MIN_PARTICLE_HEIGHT) {
                spawnParticles(player, player.level(), fallHeight);
            }
        }
    }

    private static double calculatePushStrength(float fallHeight) {

        double strength = 0.5 + (fallHeight - MIN_PUSH_HEIGHT) * 0.1;
        return Math.min(strength, MAX_PUSH_STRENGTH);
    }



    private static void pushAwayNearbyEntities(Player player, Level level, double radius, double strength) {
        AABB aabb = new AABB(
                player.getX() - radius, player.getY() - radius, player.getZ() - radius,
                player.getX() + radius, player.getY() + radius, player.getZ() + radius
        );

        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, aabb,
                entity -> entity != player && entity.isAlive() && entity.isPushable());

        for (LivingEntity entity : entities) {
            double dx = entity.getX() - player.getX();
            double dy = entity.getY() - player.getY();
            double dz = entity.getZ() - player.getZ();
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

            if (distance > 0) {
                entity.push(dx / distance * strength, dy / distance * strength, dz / distance * strength);
                entity.hurtMarked = true;
            }
        }
    }


    private static void spawnParticles(Player player, Level level, float fallHeight) {

        if (level.isClientSide) {

            int particleCount = (int) Math.min(10 + (fallHeight - MIN_PARTICLE_HEIGHT) * 2, 30);
            Level world = player.level();
            double x = player.getX();
            double y = player.getY();
            double z = player.getZ();


            for (int i = 0; i < particleCount; i++) {

                double offsetX = (world.random.nextDouble() - 0.5) * 2.0;
                double offsetY = world.random.nextDouble() * 1.5;
                double offsetZ = (world.random.nextDouble() - 0.5) * 2.0;


                world.addParticle(
                        ParticleTypes.FLAME,
                        x + offsetX,
                        y + 0.5 + offsetY,
                        z + offsetZ,
                        0,
                        0.1,
                        0
                );


                world.addParticle(
                        ParticleTypes.LAVA,
                        x + offsetX,
                        y + 0.5 + offsetY,
                        z + offsetZ,
                        0,
                        0.05,
                        0
                );
            }

            world.playSound(
                    player,
                    player.blockPosition(),
                    SoundEvents.ROOTED_DIRT_BREAK,
                    SoundSource.PLAYERS,
                    2F,
                    1.2F
            );
        }
    }

    private static boolean isWearingBlazeBoots(Player player) {
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        return !boots.isEmpty() && boots.getItem() instanceof BlazeBoots;
    }
}
