package com.cu6.avaritia_expand.item.tool;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlazeBowItem extends BowItem {
    public BlazeBowItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean isFoil(@NotNull ItemStack pStack) {
        return false;
    }

    @Override
    public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity pEntityLiving, int pTimeLeft) {
        if (pEntityLiving instanceof Player player) {
            boolean hasInfinity = player.getAbilities().instabuild ||
                    EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, pStack) > 0;
            ItemStack ammo = player.getProjectile(pStack);

            int chargeTime = this.getUseDuration(pStack) - pTimeLeft;
            chargeTime = ForgeEventFactory.onArrowLoose(pStack, pLevel, player, chargeTime, !ammo.isEmpty() || hasInfinity);
            if (chargeTime < 0) {
                return;
            }

            if (!ammo.isEmpty() || hasInfinity) {
                if (ammo.isEmpty()) {
                    ammo = new ItemStack(Items.ARROW);
                }

                float power = getPowerForTime(chargeTime);
                if (power >= 0.1) {
                    boolean isInfinite = player.getAbilities().instabuild || hasInfinity;
                    int multishotLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MULTISHOT, pStack);
                    int projectileCount = multishotLevel > 0 ? 3 : 1;


                    int powerLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, pStack);
                    float damageBoost = (float) powerLevel * 0.8F + (power * 3.0F);
                    final float finalDamage = 20.0F + damageBoost;


                    final int maxLifeTicks = 20 + (int) (power * 40);

                    if (!pLevel.isClientSide) {

                        Vec3 shootPos = getAdjustedShootPosition(player);

                        for (int i = 0; i < projectileCount; i++) {

                            SmallFireball customFireball = new SmallFireball(pLevel, player, 0, 0, 0) {

                                private int lifeTicks = 0;

                                @Override
                                public void tick() {
                                    super.tick();

                                    lifeTicks++;
                                    if (lifeTicks >= maxLifeTicks && !this.level().isClientSide) {
                                        this.discard();
                                    }
                                }

                                @Override
                                protected void onHitEntity(EntityHitResult pResult) {
                                    if (!this.level().isClientSide) {
                                        Entity entity = pResult.getEntity();
                                        Entity owner = this.getOwner();
                                        int remainingFire = entity.getRemainingFireTicks();
                                        entity.setSecondsOnFire(5);
                                        if (!entity.hurt(this.damageSources().fireball(this, owner), finalDamage)) {
                                            entity.setRemainingFireTicks(remainingFire);
                                        } else if (owner instanceof LivingEntity) {
                                            this.doEnchantDamageEffects((LivingEntity) owner, entity);
                                        }
                                    }
                                }
                            };


                            customFireball.setPos(shootPos.x, shootPos.y, shootPos.z);


                            float speed = 1.5F * (1.0F + power * 2.0F);
                            float inaccuracy = 0.5F - (power * 0.4F);


                            float yawOffset = 0.0F;
                            if (multishotLevel > 0) {
                                yawOffset = (i == 0) ? -10.0F : (i == 1) ? 10.0F : 0.0F;
                            }


                            customFireball.shootFromRotation(
                                    player,
                                    player.getXRot(),
                                    player.getYRot() + yawOffset,
                                    0.0F,
                                    speed,
                                    inaccuracy
                            );

                            pLevel.addFreshEntity(customFireball);
                        }


                        pStack.hurtAndBreak(1, player, (user) -> user.broadcastBreakEvent(player.getUsedItemHand()));
                    }


                    pLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS,
                            1.0F, 0.8F + (power * 0.4F));

                    if (!isInfinite && !player.getAbilities().instabuild) {
                        ammo.shrink(1);
                        if (ammo.isEmpty()) {
                            player.getInventory().removeItem(ammo);
                        }
                    }

                    player.awardStat(Stats.ITEM_USED.get(this));
                }
            }
        }
    }

    private Vec3 getAdjustedShootPosition(Player player) {
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookDir = player.getLookAngle().normalize();
        return eyePos.add(lookDir.x * 0.5, 0.1, lookDir.z * 0.5);
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level level, Player player) {
        stack.enchant(Enchantments.FLAMING_ARROWS, 10);
        super.onCraftedBy(stack, level, player);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("tooltip.avaritia_expand.blaze_bow"));
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}