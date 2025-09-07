package com.cu6.avaritia_expand.screen;

import com.cu6.avaritia_expand.block.ModBlocks;
import com.cu6.avaritia_expand.block.entity.CrystalFurnaceBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class CrystalFurnaceMenu extends AbstractContainerMenu {
    public final CrystalFurnaceBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    // 客户端构造函数
    public CrystalFurnaceMenu(int containerId, Inventory inventory, FriendlyByteBuf extraData) {
        this(containerId, inventory, inventory.player.level().getBlockEntity(extraData.readBlockPos()),
                new SimpleContainerData(4));
    }

    // 服务器端构造函数
    public CrystalFurnaceMenu(int containerId, Inventory inventory, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.CRYSTAL_FURNACE_MENU.get(), containerId);
        checkContainerSize(inventory, 21); // 总槽位21个（0-20）
        this.blockEntity = (CrystalFurnaceBlockEntity) entity;
        this.level = inventory.player.level();
        this.data = data;

        // 添加熔炉槽位
        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            // 输出槽（0-15）：禁止手动放入物品
            for (int slot : CrystalFurnaceBlockEntity.OUTPUT_SLOT) {
                this.addSlot(new SlotItemHandler(handler, slot,
                        92 + (slot % 4) * 18, -2 + (slot / 4) * 18) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return false; // 输出槽不可放入物品
                    }
                });
            }

            // 输入槽（16-19）：允许放入可熔炼物品
            for (int slot : CrystalFurnaceBlockEntity.INPUT_SLOT) {
                int xPos = 19 + ((slot - 16) % 2) * 18;
                int yPos = 23 + ((slot - 16) / 2) * 18;
                this.addSlot(new SlotItemHandler(handler, slot, xPos, yPos) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        // 检查物品是否可熔炼（有对应的熔炉配方）
                        SimpleContainer testContainer = new SimpleContainer(1);
                        testContainer.setItem(0, stack);
                        return level.getRecipeManager().getRecipeFor(
                                net.minecraft.world.item.crafting.RecipeType.SMELTING,
                                testContainer, level).isPresent();
                    }
                });
            }

            // 燃料槽（20）：只允许放入燃料
            int fuelSlot = CrystalFurnaceBlockEntity.FUEL_SLOT[0];
            this.addSlot(new SlotItemHandler(handler, fuelSlot, 28, 61) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    // 使用方块实体中定义的燃料判断方法
                    return CrystalFurnaceBlockEntity.isFuel(stack);
                }
            });
        });

        // 添加玩家背包和快捷栏
        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);

        // 添加数据同步
        addDataSlots(data);
    }

    // 获取熔炼进度（用于GUI渲染）
    public int getScaledProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        int progressArrowSize = 23; // 进度条像素长度
        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }

    // 获取燃烧进度（用于燃料燃烧动画）
    public int getScaledBurnTime() {
        int burnTime = this.data.get(2);
        int totalBurnTime = this.data.get(3);
        int burnIconSize = 14; // 燃烧图标像素长度
        return totalBurnTime != 0 ? burnTime * burnIconSize / totalBurnTime : 0;
    }

    // 物品快捷移动逻辑
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // 区分：玩家背包槽位 → 熔炉槽位
        if (index < 36) { // 0-35是玩家背包+快捷栏
            // 燃料物品优先放入燃料槽
            if (CrystalFurnaceBlockEntity.isFuel(sourceStack)) {
                if (!moveItemStackTo(sourceStack, 36 + 20, 36 + 21, false)) {
                    return ItemStack.EMPTY;
                }
            }
            // 可熔炼物品放入输入槽（16-19）
            else {
                SimpleContainer testContainer = new SimpleContainer(1);
                testContainer.setItem(0, sourceStack);
                boolean isSmeltable = level.getRecipeManager().getRecipeFor(
                        net.minecraft.world.item.crafting.RecipeType.SMELTING,
                        testContainer, level).isPresent();

                if (isSmeltable) {
                    if (!moveItemStackTo(sourceStack, 36 + 16, 36 + 20, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                // 其他物品无法放入熔炉，返回空
                else {
                    return ItemStack.EMPTY;
                }
            }
        }
        // 熔炉槽位 → 玩家背包
        else if (index < 36 + 21) { // 36-56是熔炉槽位
            // 检查是否为输出槽（0-15）
            if (index >= 36 && index < 36 + 16) {
                // 输出槽的物品移回玩家背包，优先放入快捷栏
                if (!moveItemStackTo(sourceStack, 0, 36, false)) {
                    return ItemStack.EMPTY;
                }
            }
            // 输入槽（16-19）和燃料槽（20）的物品移回玩家背包
            else {
                if (!moveItemStackTo(sourceStack, 0, 36, false)) {
                    return ItemStack.EMPTY;
                }
            }
        } else {
            return ItemStack.EMPTY;
        }

        // 更新槽位状态
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }

        sourceSlot.onTake(player, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, ModBlocks.CRYSTAL_FURNACE.get());
    }

    // 添加玩家背包槽位
    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9,
                        8 + l * 18, 113 + i * 18));
            }
        }
    }

    // 添加玩家快捷栏槽位
    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i,
                    8 + i * 18, 171));
        }
    }
    public boolean isCrafting() {
        return data.get(2) > 0; // 检查燃烧时间是否大于0（data[2]是燃烧时间）
    }
}