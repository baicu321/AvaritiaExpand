package com.cu6.avaritia_expand.entity.custom;

import com.cu6.avaritia_expand.entity.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.network.PlayMessages;

public class InfinityTNTEntity extends PrimedTnt {
    private int explosionRadius = 30;
    private static final float EXPLOSION_RADIUS = 100.0F;

    public InfinityTNTEntity(EntityType<? extends PrimedTnt> type, Level level) {
        super(type, level);
    }
    public InfinityTNTEntity(Level level, double x, double y, double z, LivingEntity igniter) {
        super(level, x, y, z, igniter);
    }

    public InfinityTNTEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(ModEntities.INFINTITY_TNT_ENTITY.get(), level);
    }

    @Override
    protected void explode() {
        // 创建圆形爆炸效果
        createCircleExplosion();
    }

    private void createCircleExplosion() {
        Level level = this.level();
        if (!level.isClientSide) {
            BlockPos center = new BlockPos((int) this.getX(), (int) this.getY(), (int) this.getZ());

            // 计算圆形范围内的所有方块位置
            for (int x = - (int)EXPLOSION_RADIUS; x <= EXPLOSION_RADIUS; x++) {
                for (int z = - (int)EXPLOSION_RADIUS; z <= EXPLOSION_RADIUS; z++) {
                    // 检查是否在圆形范围内 (x² + z² ≤ r²)
                    if ((x*x + z*z) <= EXPLOSION_RADIUS * EXPLOSION_RADIUS) {
                        // 从爆炸中心向下和向上检查方块
                        for (int yOffset = -10; yOffset <= 10; yOffset++) {
                            BlockPos pos = center.offset(x, yOffset, z);

                            // 破坏方块（除了不可破坏的方块）
                            if (level.getBlockState(pos).getBlock() != Blocks.BEDROCK &&
                                    level.getBlockState(pos).getBlock() != Blocks.OBSIDIAN) {

                                // 可以在这里添加方块破坏的逻辑，比如随机掉落等
                                level.destroyBlock(pos, false, this);
                            }
                        }
                    }
                }
            }

            // 移除TNT实体
            this.discard();
        }
    }

    public void setExplosionRadius(int radius) {
        this.explosionRadius = radius;
    }

    public float getBrightness(float partialTicks) {
        return partialTicks;
    }
}
