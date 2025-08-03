package com.cu6.avaritia_expand.entity.custom;

import com.cu6.avaritia_expand.entity.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.PlayMessages;

import javax.annotation.Nullable;
import java.util.List;

public class InfinityTNTEntity extends Entity implements TraceableEntity {
    private static final EntityDataAccessor<Integer> DATA_FUSE_ID;
    private int explosionRadius = 30;
    private static final float EXPLOSION_RADIUS = 100.0F;
    private static final int DEFAULT_FUSE_TIME = 80;
    @javax.annotation.Nullable
    private LivingEntity owner;

    public InfinityTNTEntity(EntityType<InfinityTNTEntity> type, Level level) {
        super(type, level);
        this.blocksBuilding = true;
    }

    public InfinityTNTEntity(Level pLevel, double pX, double pY, double pZ, @javax.annotation.Nullable LivingEntity pOwner) {
        this(ModEntities.INFINTITY_TNT_ENTITY.get(), pLevel);
        this.setPos(pX, pY, pZ);
        double $$5 = pLevel.random.nextDouble() * (double)((float)Math.PI * 2F);
        this.setDeltaMovement(-Math.sin($$5) * 0.02, (double)0.2F, -Math.cos($$5) * 0.02);
        this.setFuse(80);
        this.xo = pX;
        this.yo = pY;
        this.zo = pZ;
        this.owner = pOwner;
    }


    protected void defineSynchedData() {
        this.entityData.define(DATA_FUSE_ID, 80);
    }

    protected Entity.MovementEmission getMovementEmission() {
        return MovementEmission.NONE;
    }

    public boolean isPickable() {
        return !this.isRemoved();
    }

    public void tick() {
        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add((double)0.0F, -0.04, (double)0.0F));
        }

        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(0.98));
        if (this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.7, (double)-0.5F, 0.7));
        }

        int $$0 = this.getFuse() - 1;
        this.setFuse($$0);
        if ($$0 <= 0) {
            this.discard();
            if (!this.level().isClientSide) {
                this.explode();
            }
        } else {
            this.updateInWaterStateAndDoFluidPushing();
            if (this.level().isClientSide) {
                this.level().addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + (double)0.5F, this.getZ(), (double)0.0F, (double)0.0F, (double)0.0F);
            }
        }

    }
    private void createCircleExplosion() {
        Level level = this.level();
        if (!level.isClientSide) {
            BlockPos center = new BlockPos((int) this.getX(), (int) this.getY(), (int) this.getZ());
            float radius = EXPLOSION_RADIUS;
            // 计算圆形范围内的所有方块位置
            for (int x = - (int)EXPLOSION_RADIUS; x <= EXPLOSION_RADIUS; x++) {
                for (int z = - (int)EXPLOSION_RADIUS; z <= EXPLOSION_RADIUS; z++) {
                    // 检查是否在圆形范围内 (x² + z² ≤ r²)
                    if ((x*x + z*z) <= EXPLOSION_RADIUS * EXPLOSION_RADIUS) {
                        // 从爆炸中心向下和向上检查方块
                        for (int yOffset = -40; yOffset <= 10; yOffset++) {
                            BlockPos pos = center.offset(x, yOffset, z);

                            // 破坏方块（除了不可破坏的方块）
                            if (level.getBlockState(pos).getBlock() != Blocks.BEDROCK &&
                                    level.getBlockState(pos).getBlock() != Blocks.OBSIDIAN) {

                                // 可以在这里添加方块破坏的逻辑，比如随机掉落等
                                level.destroyBlock(pos, false, this);
                            }
                        }
                    }
                    double centerX = this.getX();
                    double centerY = this.getY();
                    double centerZ = this.getZ();

                    // 查找爆炸范围内的所有实体实体
                    List<Entity> entities = level.getEntities(
                            this, // 排除自身
                            new AABB(
                                    centerX - radius, centerY - radius, centerZ - radius,
                                    centerX + radius, centerY + radius, centerZ + radius
                            ),
                            Entity::isAlive // 只对存活实体生效
                    );

                    // 定义基础伤害值（可根据需要调整）
                    float baseDamage = 99999999999999999999999F; // 示例：100点伤害（原版TNT约为4点）

                    DamageSource explosionDamage = level.damageSources().explosion(this, this.getOwner());
                    for (Entity entity : entities) {
                        // 计算实体与爆炸中心的距离
                        double distance = entity.distanceToSqr(this);
                        // 距离越近伤害越高，最远伤害衰减至0
                        float damage = (float) (baseDamage * (1.0F - Math.min(distance, radius * radius) / (radius * radius)));

                        // 应用伤害（忽略无敌状态）
                        if (damage > 0) {
                            entity.hurt(explosionDamage, damage);
                        }
                    }
                }
            }

            // 移除TNT实体
            this.discard();
        }
    }
    protected void explode() {
        createCircleExplosion();
    }

    protected void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putShort("Fuse", (short)this.getFuse());
    }

    protected void readAdditionalSaveData(CompoundTag pCompound) {
        this.setFuse(pCompound.getShort("Fuse"));
    }

    @Nullable
    public LivingEntity getOwner() {
        return this.owner;
    }

    protected float getEyeHeight(Pose pPose, EntityDimensions pSize) {
        return 0.15F;
    }

    public void setFuse(int pLife) {
        this.entityData.set(DATA_FUSE_ID, pLife);
    }

    public int getFuse() {
        return (Integer)this.entityData.get(DATA_FUSE_ID);
    }

    static {
        DATA_FUSE_ID = SynchedEntityData.defineId(PrimedTnt.class, EntityDataSerializers.INT);
    }
    public void setExplosionRadius(int radius) {
        this.explosionRadius = radius;
    }
}
