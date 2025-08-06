package com.cu6.avaritia_expand.event.crystal;

import com.cu6.avaritia_expand.item.shield.CrystalShieldItem;
import committee.nova.mods.avaritia.common.entity.BladeSlashEntity;
import committee.nova.mods.avaritia.init.registry.ModEntities;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

@Mod.EventBusSubscriber
public class CrystalShieldEvent {
    // 冷却时间（20 ticks = 1秒）
    private static final int COOLDOWN_TICKS = 20;
    // 存储玩家最后召唤刀刃的时间
    private static final Map<Player, Long> lastSummoned = new WeakHashMap<>();

    @SubscribeEvent
    public static void onShieldBlock(LivingAttackEvent event) {
        LivingEntity target = event.getEntity();
        // 只处理玩家
        if (!(target instanceof Player player)) {
            return;
        }

        Level level = player.level();

        if (isBlockingWithCrystalShield(player)) {
            // 检查冷却时间
            long currentTime = level.getGameTime();
            long lastTime = lastSummoned.getOrDefault(player, 0L);

            if (currentTime - lastTime >= COOLDOWN_TICKS) {
                // 召唤单个刀刃实体
                summonBladeSlash(player);
                lastSummoned.put(player, currentTime);
            }
        }
    }


    private static boolean isBlockingWithCrystalShield(Player player) {
        if (!player.isUsingItem()) {
            return false;
        }

        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();

        return (mainHand.getItem() instanceof CrystalShieldItem) ||
                (offHand.getItem() instanceof CrystalShieldItem);
    }


    private static void summonBladeSlash(Player player) {
        Level level = player.level();

        if (level.isClientSide) {
            return;
        }

        // 获取玩家的朝向角度
        float yaw = player.getYRot();
        float pitch = player.getXRot();

        // 将角度转换为弧度并计算方向向量
        double radiansY = Math.toRadians(yaw);
        double radiansP = Math.toRadians(pitch);

        double x = -Mth.sin((float)radiansY) * Mth.cos((float)radiansP);
        double y = -Mth.sin((float)radiansP);
        double z = Mth.cos((float)radiansY) * Mth.cos((float)radiansP);

        // 创建刀刃实体
        BladeSlashEntity bladeSlash = new BladeSlashEntity(ModEntities.BLADE_SLASH.get(), level);

        // 设置实体位置（在玩家前方一点）
        bladeSlash.setPos(
                player.getX() + x * 0.5,
                player.getEyeY() - 0.5,
                player.getZ() + z * 0.5
        );

        // 设置实体所有者
        bladeSlash.setOwner(player);

        // 设置实体移动方向和速度
        float speed = 1.5F;
        bladeSlash.setDeltaMovement(
                x * speed,
                y * speed,
                z * speed
        );

        // 将实体添加到世界
        level.addFreshEntity(bladeSlash);
    }
}
