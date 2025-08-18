package com.cu6.avaritia_expand.item.tool;

import com.cu6.avaritia_expand.entity.custom.InfinityFishingHook;
import committee.nova.mods.avaritia.common.entity.ImmortalItemEntity;
import committee.nova.mods.avaritia.init.registry.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.Nullable;

public class InfinityFishingRod extends Item {
    public InfinityFishingRod(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);


        if (pPlayer.isCrouching()) {

            if (!pLevel.isClientSide && pPlayer instanceof ServerPlayer serverPlayer) {
                CompoundTag nbt = itemstack.getOrCreateTag();
                boolean isLootMode = nbt.getBoolean("loot");
                isLootMode = !isLootMode;
                nbt.putBoolean("loot", isLootMode);


                serverPlayer.sendSystemMessage(
                        Component.translatable(isLootMode ?
                                "tooltip.infinity_rod.loot_mode" :
                                "tooltip.infinity_rod.normal_mode"),
                        true
                );
            }
            pPlayer.swing(pHand);
            return InteractionResultHolder.success(itemstack);
        }


        if (pPlayer.fishing != null) {
            if (!pLevel.isClientSide) {
                int i = pPlayer.fishing.retrieve(itemstack);
                itemstack.hurtAndBreak(i, pPlayer, (p_41288_) -> p_41288_.broadcastBreakEvent(pHand));
            }
            pLevel.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(),
                    SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.NEUTRAL,
                    1.0F, 0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F));
            pPlayer.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
        } else {
            pLevel.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(),
                    SoundEvents.FISHING_BOBBER_THROW, SoundSource.NEUTRAL,
                    0.5F, 0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F));
            if (!pLevel.isClientSide) {
                int k = net.minecraft.world.item.enchantment.EnchantmentHelper.getFishingSpeedBonus(itemstack);
                int j = net.minecraft.world.item.enchantment.EnchantmentHelper.getFishingLuckBonus(itemstack);
                pLevel.addFreshEntity(new InfinityFishingHook(pPlayer, pLevel, j, k));
            }
            pPlayer.awardStat(Stats.ITEM_USED.get(this));
            pPlayer.gameEvent(GameEvent.ITEM_INTERACT_START);
        }

        return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return toolAction == ToolActions.FISHING_ROD_CAST;
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    public @Nullable Entity createEntity(Level level, Entity location, ItemStack stack) {
        return ImmortalItemEntity.create(ModEntities.IMMORTAL.get(), level, location.getX(), location.getY(), location.getZ(), stack);
    }


}
