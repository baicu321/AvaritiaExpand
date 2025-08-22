package com.cu6.avaritia_expand.item.tool;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NeutronShears extends ShearsItem {
    public NeutronShears(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        Level level = player.level();
        if (target instanceof EnderMan enderman) {
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
        }
            if (target instanceof Blaze blaze) {
                if (!level.isClientSide) {
                    var nbt = blaze.getPersistentData();

                    long gameTime = level.getGameTime();
                    long nextUse = nbt.getLong("BlazeShearsCooldown");
                    if (gameTime < nextUse) {
                        return InteractionResult.FAIL;
                    }
                    nbt.putLong("BlazeShearsCooldown", gameTime + 24000);
                    blaze.playSound(SoundEvents.BLAZE_HURT);
                    blaze.spawnAtLocation(Items.BLAZE_ROD);
                    blaze.teleportRelative(0.0D, 1.0D, 0.0D);
                }
            }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("tooltip.avaritia_expand.neutron_shears"));
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}
