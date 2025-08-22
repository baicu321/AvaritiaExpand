package com.cu6.avaritia_expand.item.tool;

import com.cu6.avaritia_expand.item.ModItems;
import committee.nova.mods.avaritia.common.entity.ImmortalItemEntity;
import committee.nova.mods.avaritia.init.registry.ModEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EndestWatch extends Item {
    public EndestWatch(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand pUsedHand) {
        if (!level.isClientSide) {
            player.playSound(SoundEvents.GLASS_BREAK);
            Minecraft.getInstance().gameRenderer.displayItemActivation(new ItemStack(ModItems.ENDEST_WATCH.get()));
            if (level instanceof ServerLevel && player.canChangeDimensions()) {
                ResourceKey<Level> $$4 = level.dimension() == Level.END ? Level.OVERWORLD : Level.END;
                ServerLevel $$5 = ((ServerLevel)level).getServer().getLevel($$4);
                player.changeDimension($$5);
                player.getCooldowns().addCooldown(this, 40);
            }
        }
        return super.use(level, player, pUsedHand);
    }

    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    public @Nullable Entity createEntity(Level level, Entity location, ItemStack stack) {
        return ImmortalItemEntity.create(ModEntities.IMMORTAL.get(), level, location.getX(), location.getY(), location.getZ(), stack);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("item.avaritia_expand.endest_watch.tooltip"));
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}
