package com.cu6.avaritia_expand.screen;

import com.cu6.avaritia_expand.block.ModBlocks;
import com.cu6.avaritia_expand.block.entity.BlazeFurnaceBlockEntity;
import net.minecraft.client.gui.components.ImageWidget;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;

public class BlazeFurnaceMenu extends AbstractContainerMenu {
    public final BlazeFurnaceBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public BlazeFurnaceMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData){
        this(pContainerId,inv,inv.player.level().getBlockEntity(extraData.readBlockPos()),new SimpleContainerData(8));
    }

    public BlazeFurnaceMenu(int pContainerId, Inventory inv, BlockEntity entity,ContainerData data){
        super(ModMenuTypes.BLAZE_FURNACE_MENU.get(),pContainerId);
        checkContainerSize(inv,8);
        blockEntity = ((BlazeFurnaceBlockEntity) entity);
        this.level = inv.player.level();
        this.data = data;
        addPlayerInventory(inv);
        addPlayerHotbar(inv);


        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(iItemHandler -> {

            for (int i = 0; i < 4; i++) {
                int slotX = 114 + (i % 2) * 18;
                int slotY = 22 + (i / 2) * 18;
                this.addSlot(new SlotItemHandler(iItemHandler, i, slotX, slotY) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return false;
                    }
                });
            }
            this.addSlot(new SlotItemHandler(iItemHandler,4,33,22));
            this.addSlot(new SlotItemHandler(iItemHandler,5,51,22));
            this.addSlot(new SlotItemHandler(iItemHandler,6,33,40));
            this.addSlot(new SlotItemHandler(iItemHandler,7,51,40));


        });

        addDataSlots(data);
    }

    public boolean isCrafing() {
        return data.get(0) > 0;
    }

    public int getScaledProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        int progressArrowSize = 23;

        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;


    private static final int TE_INVENTORY_SLOT_COUNT = 8;
    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + pIndex);
            return ItemStack.EMPTY;
        }
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level,blockEntity.getBlockPos()),
                player, ModBlocks.BLAZE_FURNACE.get());
    }
    private void addPlayerInventory(Inventory playerInventory){
        for (int i = 0 ; i < 3;++i){
            for (int l = 0; l < 9 ; ++l){
                this.addSlot(new Slot(playerInventory,l + i * 9 + 9,8 + l * 18,85 + i * 18));
            }
        }
    }
    private void addPlayerHotbar(Inventory playerInventory){
        for (int i = 0 ; i < 9; ++i){
            this.addSlot(new Slot(playerInventory,i,8 + i * 18,143));
        }
    }
}
