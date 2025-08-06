package com.cu6.avaritia_expand.entity.custom;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class InfinityXP extends ThrowableItemProjectile {
    private static final float DAMAGE_AMOUNT = 1.0F;

    public InfinityXP(EntityType<? extends InfinityXP> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public InfinityXP(Level level, LivingEntity shooter){
        super(EntityType.EXPERIENCE_BOTTLE,shooter,level);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.EXPERIENCE_BOTTLE;
    }

    @Override
    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        if (!this.level().isClientSide) {

            this.level().levelEvent(2002, this.blockPosition(), 0);


            int experience = this.random.nextInt(51) + 50;
            while (experience > 0) {
                int expToSpawn = Math.min(experience, 127);
                this.level().addFreshEntity(new ExperienceOrb(this.level(), this.getX(), this.getY(), this.getZ(), expToSpawn));
                experience -= expToSpawn;
            }


            if (pResult instanceof EntityHitResult entityHit) {
                Entity target = entityHit.getEntity();
                LivingEntity shooter = this.getOwner() instanceof LivingEntity ? (LivingEntity) this.getOwner() : null;


                if (shooter != null && target == shooter) {
                    this.discard();
                    return;
                }


                DamageSources damageSources = this.level().damageSources();
                DamageSource thrownDamage = damageSources.thrown(this, shooter);


                if (target instanceof LivingEntity livingTarget) {
                    livingTarget.hurt(thrownDamage, DAMAGE_AMOUNT);
                }
            }

            this.discard();
        }
    }
    @Override
    public void handleEntityEvent(byte pId) {
        if (pId == 3) {
            for (int i = 0; i < 8; ++i) {
                this.level().addParticle(ParticleTypes.SPLASH, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
            }
        }
    }
}
