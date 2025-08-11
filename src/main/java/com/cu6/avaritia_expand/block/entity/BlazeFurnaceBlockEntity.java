package com.cu6.avaritia_expand.block.entity;

import com.cu6.avaritia_expand.screen.BlazeFurnaceMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class BlazeFurnaceBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(8){
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (isOutputSlot(slot)) return false;
            if (isInputSlot(slot)) {
                SimpleContainer test = new SimpleContainer(1);
                test.setItem(0, stack);
                return level != null && level.getRecipeManager()
                        .getRecipeFor(RecipeType.SMELTING, test, level).isPresent();
            }
            return false;
        }
    };
    private boolean isInputSlot(int slot) {
        for (int s : INPUT_SLOT) if (s == slot) return true;
        return false;
    }
    private boolean isOutputSlot(int slot) {
        for (int s : OUTPUT_SLOT) if (s == slot) return true;
        return false;
    }
    private LazyOptional<IItemHandler> playerHandler = LazyOptional.of(() -> itemHandler);
    private LazyOptional<IItemHandler> hopperHandler = LazyOptional.of(() -> new BlazeFurnaceBlockEntity.SidedItemHandler(itemHandler));

    public static final int[] INPUT_SLOT = {4, 5, 6, 7};
    public static final int[] OUTPUT_SLOT = {0, 1, 2, 3};

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    protected final ContainerData data;
    private int progress = 0;
    private int max_Progress = 78;

    public BlazeFurnaceBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.BLAZE_FURNACE_BE.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i){
                    case 0 -> BlazeFurnaceBlockEntity.this.progress;
                    case 1 -> BlazeFurnaceBlockEntity.this.max_Progress;
                        default -> 0;
                };
            }

            @Override
            public void set(int i, int i1) {
                switch (i){
                    case 0 -> BlazeFurnaceBlockEntity.this.progress = i1;
                    case 1 -> BlazeFurnaceBlockEntity.this.max_Progress = i1;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (side == null) {
                // 玩家交互：不限制取出
                return playerHandler.cast();
            } else {
                // 漏斗交互：限制输入/输出
                return hopperHandler.cast();
            }
        }
        return super.getCapability(cap, side);
    }
    private static class SidedItemHandler implements IItemHandler {
        private final IItemHandler parent;

        public SidedItemHandler(IItemHandler parent) {
            this.parent = parent;
        }

        @Override
        public int getSlots() {
            return parent.getSlots();
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            return parent.getStackInSlot(slot);
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if (slotIsOutput(slot)) return stack; // 漏斗不能放入输出槽
            return parent.insertItem(slot, stack, simulate);
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slotIsInput(slot)) return ItemStack.EMPTY; // 漏斗不能从输入/燃料取
            return parent.extractItem(slot, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return parent.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return parent.isItemValid(slot, stack);
        }

        private boolean slotIsInput(int slot) {
            for (int s : INPUT_SLOT) if (s == slot) return true;
            return false;
        }

        private boolean slotIsOutput(int slot) {
            for (int s : OUTPUT_SLOT) if (s == slot) return true;
            return false;
        }

    }
    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(()-> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    public void drops(){
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0 ; i < itemHandler.getSlots();i++){
            inventory.setItem(i,itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level,this.worldPosition,inventory);
    }


    @Override
    public Component getDisplayName() {
        return Component.translatable("item.avaritia.blaze_cube");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new BlazeFurnaceMenu(i,inventory,this,this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("inventory",itemHandler.serializeNBT());
        pTag.putInt("blaze_furnace.progress",progress);
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("inventory"));
        progress = pTag.getInt("blaze_furnace.progress");
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if (pLevel.isClientSide()) return;

        boolean changed = false;
        boolean hasValidItem = false;

        // 先检查是否有任何可烧制物品
        for (int slot : INPUT_SLOT) {
            ItemStack stack = itemHandler.getStackInSlot(slot);
            if (!stack.isEmpty() && hasRecipe(stack)) {
                hasValidItem = true;
                break;
            }
        }

        // 如果有可烧制物品，继续处理进度
        if (hasValidItem) {
            increaseCraftingProgress();
            changed = true;

            if (hasProgressFinished()) {
                // 寻找第一个可烧制的物品进行烧制
                for (int slot : INPUT_SLOT) {
                    ItemStack stack = itemHandler.getStackInSlot(slot);
                    if (!stack.isEmpty() && hasRecipe(stack)) {
                        // 烧制当前物品
                        craftSpecificItem(slot);
                        resetProgress();
                        changed = true;
                        break; // 只处理一个物品
                    }
                }
            }
        } else if (progress > 0) {
            // 没有可烧制物品时重置进度
            resetProgress();
            changed = true;
        }

        if (changed) {
            setChanged();
            pLevel.sendBlockUpdated(pPos, pState, pState, 3);
        }
    }

    private void craftSpecificItem(int inputSlot) {
        ItemStack inputStack = itemHandler.getStackInSlot(inputSlot);
        if (inputStack.isEmpty()) return;

        // 查找该物品的配方
        SimpleContainer inventory = new SimpleContainer(1);
        inventory.setItem(0, inputStack);
        Optional<SmeltingRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(RecipeType.SMELTING, inventory, level);

        if (recipe.isPresent()) {
            // 消耗指定槽位的输入物品
            inputStack.shrink(1);

            // 获取配方结果
            ItemStack result = recipe.get().getResultItem(level.registryAccess()).copy();

            // 输出到可用槽位
            int outputSlot = findAvailableOutputSlot(result);
            if (outputSlot >= 0) {
                ItemStack currentOutput = itemHandler.getStackInSlot(outputSlot);
                if (currentOutput.isEmpty()) {
                    itemHandler.setStackInSlot(outputSlot, result);
                } else if (ItemStack.isSameItemSameTags(currentOutput, result)) {
                    currentOutput.grow(result.getCount());
                }
            }
        }
    }

    private void resetProgress() {
        progress = 0;
    }

    private void craftItem() {
        // 查找有效的配方
        Optional<SmeltingRecipe> recipe = getCurrentRecipe();

        if (recipe.isPresent()) {
            // 消耗输入物品 - 找到第一个有物品的输入槽
            for (int slot : INPUT_SLOT) {
                ItemStack inputStack = itemHandler.getStackInSlot(slot);
                if (!inputStack.isEmpty()) {
                    inputStack.shrink(1);
                    break; // 每次只消耗一个物品
                }
            }

            // 获取配方结果
            ItemStack result = recipe.get().getResultItem(level.registryAccess()).copy();

            // 修改：固定输出到第一个可用槽位
            int outputSlot = findAvailableOutputSlot(result);

            if (outputSlot >= 0) {
                ItemStack currentOutput = itemHandler.getStackInSlot(outputSlot);

                if (currentOutput.isEmpty()) {
                    itemHandler.setStackInSlot(outputSlot, result);
                } else if (ItemStack.isSameItemSameTags(currentOutput, result)) {
                    currentOutput.grow(result.getCount());
                }
            }
        }
    }

    private int findAvailableOutputSlot(ItemStack result) {
        // 1. 优先查找已有相同物品的槽位
        for (int slot : OUTPUT_SLOT) {
            ItemStack stack = itemHandler.getStackInSlot(slot);
            if (!stack.isEmpty() &&
                    ItemStack.isSameItemSameTags(stack, result) &&
                    stack.getCount() + result.getCount() <= stack.getMaxStackSize()) {
                return slot;
            }
        }

        // 2. 查找空槽位
        for (int slot : OUTPUT_SLOT) {
            ItemStack stack = itemHandler.getStackInSlot(slot);
            if (stack.isEmpty()) {
                return slot;
            }
        }

        // 3. 没有可用槽位
        return -1;
    }

    private boolean hasProgressFinished() {
        return progress >= max_Progress;
    }

    private void increaseCraftingProgress() {
        progress++;
    }



    private boolean hasRecipe(ItemStack inputStack) {
        SimpleContainer inventory = new SimpleContainer(1);
        inventory.setItem(0, inputStack);

        Optional<SmeltingRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(RecipeType.SMELTING, inventory, level);

        if (recipe.isEmpty()) return false;

        ItemStack result = recipe.get().getResultItem(level.registryAccess());
        return canInsertItemIntoOutputSlot(result.getItem()) &&
                canInsertAmountIntoOutputSlot(result.getCount());
    }

    private Optional<SmeltingRecipe> getCurrentRecipe() {
        SimpleContainer inventory = new SimpleContainer(1); // 只需检查输入物品

        // 检查所有输入槽是否有匹配的配方
        for (int slot : INPUT_SLOT) {
            ItemStack stackInSlot = itemHandler.getStackInSlot(slot);
            if (!stackInSlot.isEmpty()) {
                inventory.setItem(0, stackInSlot);
                return level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, inventory, level);
            }
        }
        return Optional.empty();
    }
    private boolean canInsertItemIntoOutputSlot(Item item) {
        for (int slot : OUTPUT_SLOT) {
            ItemStack stack = itemHandler.getStackInSlot(slot);
            if (stack.isEmpty() || (stack.is(item) && stack.getCount() < stack.getMaxStackSize())) {
                return true;
            }
        }
        return false;
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        for (int slot : OUTPUT_SLOT) {
            ItemStack stack = itemHandler.getStackInSlot(slot);
            if (stack.isEmpty() || (stack.getCount() + count <= stack.getMaxStackSize())) {
                return true;
            }
        }
        return false;
    }
}

