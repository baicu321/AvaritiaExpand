package com.cu6.avaritia_expand.event.blaze;

import com.cu6.avaritia_expand.item.shield.BlazeShieldItem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.util.Mth;

import java.util.WeakHashMap;
import java.util.Map;

@Mod.EventBusSubscriber
public class BlazeShieldEvent {
    // 冷却时间（20 ticks = 1秒）
    private static final int COOLDOWN_TICKS = 20;
    // 存储玩家最后发射烈焰弹的时间
    private static final Map<Player, Long> lastFired = new WeakHashMap<>();
    // 四个火球的角度偏移（度）
    private static final float[] ANGLE_OFFSETS = {-15, -5, 5, 15};

    @SubscribeEvent
    public static void onShieldBlock(LivingAttackEvent event) {
        LivingEntity target = event.getEntity();
        // 只处理玩家
        if (!(target instanceof Player player)) {
            return;
        }

        Level level = player.level();
        // 检查玩家是否正在格挡且使用的是我们的盾牌
        if (isBlockingWithBlazeShield(player)) {
            // 检查冷却时间
            long currentTime = level.getGameTime();
            long lastTime = lastFired.getOrDefault(player, 0L);

            if (currentTime - lastTime >= COOLDOWN_TICKS) {
                // 发射四个烈焰弹
                fireFourLargeFireballs(player);
                lastFired.put(player, currentTime);
            }
        }
    }

    // 检查玩家是否使用BlazeShield进行格挡
    private static boolean isBlockingWithBlazeShield(Player player) {
        // 玩家必须正在使用物品（格挡动作）
        if (!player.isUsingItem()) {
            return false;
        }

        // 检查主手和副手是否是我们的盾牌
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();

        return (mainHand.getItem() instanceof BlazeShieldItem) ||
                (offHand.getItem() instanceof BlazeShieldItem);
    }

    // 发射四个原版烈焰弹（LargeFireball）
    private static void fireFourLargeFireballs(Player player) {
        Level level = player.level();
        // 只在服务端生成实体
        if (level.isClientSide) {
            return;
        }

        // 获取玩家当前的旋转角度
        float yaw = player.getYRot();
        float pitch = player.getXRot();

        // 为每个角度偏移发射一个火球
        for (float angleOffset : ANGLE_OFFSETS) {
            // 计算偏移后的角度（转换为弧度）
            float adjustedYaw = yaw + angleOffset;
            double radiansY = Math.toRadians(adjustedYaw);
            double radiansP = Math.toRadians(pitch);

            // 计算偏移后的方向向量
            double x = -Mth.sin((float)radiansY) * Mth.cos((float)radiansP);
            double y = -Mth.sin((float)radiansP);
            double z = Mth.cos((float)radiansY) * Mth.cos((float)radiansP);

            // 创建原版烈焰弹实体
            LargeFireball fireball = new LargeFireball(
                    EntityType.FIREBALL,
                    level
            );

            // 设置烈焰弹位置（玩家前方）
            fireball.setPos(
                    player.getX() + x * 0.5,  // 稍微向前偏移，避免碰撞玩家
                    player.getEyeY() - 0.5,
                    player.getZ() + z * 0.5
            );

            // 设置所有者为玩家
            fireball.setOwner(player);

            // 设置烈焰弹速度（沿计算后的方向）
            float speed = 1.5F;
            fireball.setDeltaMovement(
                    x * speed,
                    y * speed,
                    z * speed
            );

            // 将烈焰弹添加到世界
            level.addFreshEntity(fireball);
        }
    }
}
