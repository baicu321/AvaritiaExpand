package com.cu6.avaritia_expand.item.tool;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.IForgeShearable;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class NeutronShears extends ShearsItem {
    public NeutronShears(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (target instanceof EnderMan enderman) {
            Level level = player.level();
            if (!level.isClientSide) {
                var nbt = enderman.getPersistentData();

                long gameTime = level.getGameTime();
                long nextUse = nbt.getLong("EnderShearsCooldown");
                if (gameTime < nextUse) {
                    return InteractionResult.FAIL;
                }
                nbt.putLong("EnderShearsCooldown", gameTime + 24000);
                enderman.playSound(SoundEvents.ENDERMAN_SCREAM);
                enderman.spawnAtLocation(Items.ENDER_PEARL);
                enderman.teleportRelative(0.0D, 1.0D, 0.0D);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.interactLivingEntity(stack, player, target, hand);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("tooltip.avaritia_expand.neutron_shears"));
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}
