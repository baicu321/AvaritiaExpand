package com.cu6.avaritia_expand.item.shield;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

import javax.annotation.Nullable;
import java.util.List;

public class BlazeShieldItem extends ShieldItem {
    public static final int EFFECTIVE_BLOCK_DELAY = 5;
    public static final float MINIMUM_DURABILITY_DAMAGE = 3.0F;
    public static final String TAG_BASE_COLOR = "Base";

    public BlazeShieldItem(Item.Properties pProperties) {
        super(pProperties);
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSE_ITEM_BEHAVIOR);
    }

    public String getDescriptionId(ItemStack pStack) {
        return BlockItem.getBlockEntityData(pStack) != null ? this.getDescriptionId() + "." + getColor(pStack).getName() : super.getDescriptionId(pStack);
    }

    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        BannerItem.appendHoverTextFromBannerBlockEntityTag(pStack, pTooltip);
    }

    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.BLOCK;
    }

    public int getUseDuration(ItemStack pStack) {
        return 72000;
    }

    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        pPlayer.startUsingItem(pHand);
        return InteractionResultHolder.consume(itemstack);
    }

    public boolean isValidRepairItem(ItemStack pToRepair, ItemStack pRepair) {
        return pRepair.is(ItemTags.PLANKS) || super.isValidRepairItem(pToRepair, pRepair);
    }

    public static DyeColor getColor(ItemStack pStack) {
        CompoundTag compoundtag = BlockItem.getBlockEntityData(pStack);
        return compoundtag != null ? DyeColor.byId(compoundtag.getInt("Base")) : DyeColor.WHITE;
    }

    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return ToolActions.DEFAULT_SHIELD_ACTIONS.contains(toolAction);
    }

    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.OFFHAND;
    }
}
