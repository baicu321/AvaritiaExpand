package com.cu6.avaritia_expand.entity.custom;

import com.cu6.avaritia_expand.ModConfig;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

public class InfinityTNTEntity extends Entity implements TraceableEntity {
    private static final EntityDataAccessor<Integer> DATA_FUSE_ID;
    public static final int DEFAULT_FUSE_TIME = 1000;
    private int explosionRadius;
    @Nullable
    private LivingEntity owner;

    public InfinityTNTEntity(EntityType<? extends InfinityTNTEntity> type, Level level) {
        super(type, level);
        this.explosionRadius = Math.round(ModConfig.InfinityTNTExplosionRadius.get());
    }

    public InfinityTNTEntity(Level pLevel, double pX, double pY, double pZ, @Nullable LivingEntity pOwner) {
        this(ModEntities.INFINITY_TNT_ENTITY.get(), pLevel);
        this.setPos(pX, pY, pZ);
        double $$5 = pLevel.random.nextDouble() * (double)((float)Math.PI * 2F);
        this.setDeltaMovement(-Math.sin($$5) * 0.02, (double)0.2F, -Math.cos($$5) * 0.02);
        this.setFuse(DEFAULT_FUSE_TIME);
        this.xo = pX;
        this.yo = pY;
        this.zo = pZ;
        this.owner = pOwner;

        this.explosionRadius = Math.round(ModConfig.InfinityTNTExplosionRadius.get());
    }


    protected void defineSynchedData() {
        this.entityData.define(DATA_FUSE_ID, DEFAULT_FUSE_TIME);
    }
    public void setExplosionRadius(int radius) {
        this.explosionRadius = radius;
    }

    public int getExplosionRadius() {
        return this.explosionRadius;
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

        int fuse = this.getFuse() - 1;
        this.setFuse(fuse);

        if (fuse <= DEFAULT_FUSE_TIME / 2) {
            int deathTime = DEFAULT_FUSE_TIME / 2 - fuse;
        }

        if (fuse <= 0) {
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

            float radius = (float) this.explosionRadius;


            for (int x = -(int) radius; x <= radius; x++) {
                for (int z = -(int) radius; z <= radius; z++) {

                    if ((x*x + z*z) <= radius * radius) {

                        for (int yOffset = ModConfig.InfinityTNTExplosionLength.get(); yOffset <= 10; yOffset++) {
                            BlockPos pos = center.offset(x, yOffset, z);
                            BlockState state = level.getBlockState(pos);
                            Block block = state.getBlock();


                            boolean canBreak = true;


                            if (block == Blocks.BEDROCK && !ModConfig.InfinityTNTCanBreakBedRock.get()) {
                                canBreak = false;
                            }


                            if (block == Blocks.OBSIDIAN && !ModConfig.InfinityTNTCanBreakObsidian.get()) {
                                canBreak = false;
                            }


                            if (canBreak) {
                                level.destroyBlock(pos, false, this);
                            }
                        }
                    }


                    double centerX = this.getX();
                    double centerY = this.getY();
                    double centerZ = this.getZ();

                    List<Entity> entities = level.getEntities(
                            this,
                            new AABB(
                                    centerX - radius, centerY - radius, centerZ - radius,
                                    centerX + radius, centerY + radius, centerZ + radius
                            ),
                            Entity::isAlive
                    );

                    float baseDamage = 999999999.0F;
                    DamageSource explosionDamage = level.damageSources().explosion(this, this.getOwner());

                    for (Entity entity : entities) {
                        double distance = entity.distanceToSqr(this);
                        float damage = (float) (baseDamage * (1.0F - Math.min(distance, radius * radius) / (radius * radius)));

                        if (damage > 0) {
                            if (entity instanceof Player) {
                                entity.hurt(explosionDamage,0);
                            }
                            else {
                                entity.hurt(explosionDamage, damage);
                            }
                        }

                    }
                }
            }
            this.discard();
        }
    }

    protected void explode() {
        createCircleExplosion();
    }



    protected void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putShort("Fuse", (short)this.getFuse());
        pCompound.putInt("ExplosionRadius", this.explosionRadius);
    }

    protected void readAdditionalSaveData(CompoundTag pCompound) {
        this.setFuse(pCompound.getShort("Fuse"));
        this.explosionRadius = pCompound.getInt("ExplosionRadius");
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
        return this.entityData.get(DATA_FUSE_ID);
    }

    static {
        DATA_FUSE_ID = SynchedEntityData.defineId(InfinityTNTEntity.class, EntityDataSerializers.INT);
    }
}
