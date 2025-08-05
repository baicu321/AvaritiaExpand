package com.cu6.avaritia_expand.block.entity;


import com.cu6.avaritia_expand.screen.CrystalFurnaceMenu;
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
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CrystalFurnaceBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(21);
    public static final int[] INPUT_SLOT = {16, 17, 18, 19};
    public static final int[] OUTPUT_SLOT = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
    public static final int[] FUEL_SLOT = {20}; // 燃料槽：第20格

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 30;

    // 新增：燃料燃烧时间跟踪
    private int burnTime = 0;         // 当前剩余燃烧时间
    private int totalBurnTime = 0;    // 燃料总燃烧时间

    public CrystalFurnaceBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.CRYSTAL_FURNACE_CE.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i){
                    case 0 -> CrystalFurnaceBlockEntity.this.progress;
                    case 1 -> CrystalFurnaceBlockEntity.this.maxProgress;
                    case 2 -> CrystalFurnaceBlockEntity.this.burnTime;    // 同步燃烧时间
                    case 3 -> CrystalFurnaceBlockEntity.this.totalBurnTime; // 同步总燃烧时间
                    default -> 0;
                };
            }

            @Override
            public void set(int i, int i1) {
                switch (i){
                    case 0 -> CrystalFurnaceBlockEntity.this.progress = i1;
                    case 1 -> CrystalFurnaceBlockEntity.this.maxProgress = i1;
                    case 2 -> CrystalFurnaceBlockEntity.this.burnTime = i1;
                    case 3 -> CrystalFurnaceBlockEntity.this.totalBurnTime = i1;
                }
            }

            @Override
            public int getCount() {
                return 4; // 数据数量扩展为4
            }
        };
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER){
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
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
        return new CrystalFurnaceMenu(i,inventory,this,this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("inventory",itemHandler.serializeNBT());
        pTag.putInt("crystal_furnace.progress",progress);
        // 新增：保存燃烧时间
        pTag.putInt("crystal_furnace.burnTime", burnTime);
        pTag.putInt("crystal_furnace.totalBurnTime", totalBurnTime);
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("inventory"));
        progress = pTag.getInt("crystal_furnace.progress");
        // 新增：加载燃烧时间
        burnTime = pTag.getInt("crystal_furnace.burnTime");
        totalBurnTime = pTag.getInt("crystal_furnace.totalBurnTime");
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if (pLevel.isClientSide()) return;

        boolean changed = false;
        boolean hasValidItem = false;
        boolean hasFuel = false;

        // 1. 检查是否有可烧制物品
        for (int slot : INPUT_SLOT) {
            ItemStack stack = itemHandler.getStackInSlot(slot);
            if (!stack.isEmpty() && hasRecipe(stack)) {
                hasValidItem = true;
                break;
            }
        }

        // 2. 检查是否有燃料（新增逻辑）
        ItemStack fuelStack = itemHandler.getStackInSlot(FUEL_SLOT[0]);
        if (!fuelStack.isEmpty() && getBurnTime(fuelStack) > 0) {
            hasFuel = true;
        }

        // 3. 处理燃料燃烧（新增逻辑）
        if (isBurning()) {
            burnTime--; // 燃烧时间减少
            changed = true;
        } else if (hasValidItem && hasFuel) {
            // 燃料耗尽但有可用燃料和物品：消耗燃料并开始燃烧
            consumeFuel();
            changed = true;
        }

        // 4. 只有燃烧中且有可烧物品时才推进进度
        if (isBurning() && hasValidItem) {
            increaseCraftingProgress();
            changed = true;

            if (hasProgressFinished()) {
                // 寻找第一个可烧制的物品进行烧制
                for (int slot : INPUT_SLOT) {
                    ItemStack stack = itemHandler.getStackInSlot(slot);
                    if (!stack.isEmpty() && hasRecipe(stack)) {
                        craftSpecificItem(slot);
                        resetProgress();
                        changed = true;
                        break; // 只处理一个物品
                    }
                }
            }
        } else if (progress > 0) {
            // 没有燃烧或没有物品时重置进度
            resetProgress();
            changed = true;
        }

        if (changed) {
            setChanged();
            pLevel.sendBlockUpdated(pPos, pState, pState, 3);
        }
    }

    // 新增：消耗燃料并设置燃烧时间
    private void consumeFuel() {
        ItemStack fuelStack = itemHandler.getStackInSlot(FUEL_SLOT[0]);
        if (fuelStack.isEmpty()) return;

        // 获取燃料燃烧时间（使用原版熔炉燃料规则）
        totalBurnTime = getBurnTime(fuelStack);
        burnTime = totalBurnTime;

        // 消耗一个燃料
        fuelStack.shrink(1);
    }

    // 新增：判断是否正在燃烧
    private boolean isBurning() {
        return burnTime > 0;
    }

    // 新增：获取燃料燃烧时间（兼容原版燃料）
    private int getBurnTime(ItemStack stack) {
        return ForgeHooks.getBurnTime(stack, RecipeType.SMELTING);
    }

    // 新增：判断物品是否为燃料（供菜单类调用）
    public static boolean isFuel(ItemStack stack) {
        return ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0;
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
            // 消耗指定槽位的输入物品（仍然消耗1个）
            inputStack.shrink(1);

            // 获取配方结果并翻倍数量
            ItemStack result = recipe.get().getResultItem(level.registryAccess()).copy();
            result.setCount(result.getCount() * 2); // 核心修改：数量翻倍

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

            // 输出到第一个可用槽位
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
        return progress >= maxProgress;
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
        SimpleContainer inventory = new SimpleContainer(1);

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
