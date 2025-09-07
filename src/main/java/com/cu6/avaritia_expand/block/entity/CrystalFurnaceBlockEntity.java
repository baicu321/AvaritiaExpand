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
    private final ItemStackHandler itemHandler = new ItemStackHandler(21) {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (isOutputSlot(slot)) return false;
            if (isInputSlot(slot)) {
                SimpleContainer test = new SimpleContainer(1);
                test.setItem(0, stack);
                return level != null && level.getRecipeManager()
                        .getRecipeFor(RecipeType.SMELTING, test, level).isPresent();
            }
            if (isFuelSlot(slot)) {
                return isFuel(stack);
            }
            return false;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 64;
        }
    };
    private LazyOptional<IItemHandler> playerHandler = LazyOptional.of(() -> itemHandler);
    private LazyOptional<IItemHandler> hopperHandler = LazyOptional.of(() -> new SidedItemHandler(itemHandler));

    private boolean isInputSlot(int slot) {
        for (int s : INPUT_SLOT) if (s == slot) return true;
        return false;
    }
    private boolean isOutputSlot(int slot) {
        for (int s : OUTPUT_SLOT) if (s == slot) return true;
        return false;
    }
    private boolean isFuelSlot(int slot) {
        for (int s : FUEL_SLOT) if (s == slot) return true;
        return false;
    }
    public static final int[] INPUT_SLOT = {16, 17, 18, 19};
    public static final int[] OUTPUT_SLOT = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
    public static final int[] FUEL_SLOT = {20};

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 30;

    private int burnTime = 0;
    private int totalBurnTime = 0;

    public CrystalFurnaceBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.CRYSTAL_FURNACE_CE.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i){
                    case 0 -> CrystalFurnaceBlockEntity.this.progress;
                    case 1 -> CrystalFurnaceBlockEntity.this.maxProgress;
                    case 2 -> CrystalFurnaceBlockEntity.this.burnTime;
                    case 3 -> CrystalFurnaceBlockEntity.this.totalBurnTime;
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
                return 4;
            }
        };
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (side == null) {
                return playerHandler.cast();
            } else {
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
            if (isOutputSlot(slot)) return stack;
            return parent.insertItem(slot, stack, simulate);
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (isInputSlot(slot) || isFuelSlot(slot)) return ItemStack.EMPTY;
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

        private boolean isInputSlot(int slot) {
            for (int s : INPUT_SLOT) if (s == slot) return true;
            return false;
        }
        private boolean isOutputSlot(int slot) {
            for (int s : OUTPUT_SLOT) if (s == slot) return true;
            return false;
        }
        private boolean isFuelSlot(int slot) {
            for (int s : FUEL_SLOT) if (s == slot) return true;
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
        return new CrystalFurnaceMenu(i,inventory,this,this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("inventory",itemHandler.serializeNBT());
        pTag.putInt("crystal_furnace.progress",progress);
        pTag.putInt("crystal_furnace.burnTime", burnTime);
        pTag.putInt("crystal_furnace.totalBurnTime", totalBurnTime);
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("inventory"));
        progress = pTag.getInt("crystal_furnace.progress");
        burnTime = pTag.getInt("crystal_furnace.burnTime");
        totalBurnTime = pTag.getInt("crystal_furnace.totalBurnTime");
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if (pLevel.isClientSide()) return;

        boolean changed = false;
        boolean hasValidItem = false;
        boolean hasFuel = false;

        for (int slot : INPUT_SLOT) {
            ItemStack stack = itemHandler.getStackInSlot(slot);
            if (!stack.isEmpty() && hasRecipe(stack)) {
                hasValidItem = true;
                break;
            }
        }

        ItemStack fuelStack = itemHandler.getStackInSlot(FUEL_SLOT[0]);
        if (!fuelStack.isEmpty() && getBurnTime(fuelStack) > 0) {
            hasFuel = true;
        }


        if (isBurning()) {
            burnTime--;
            changed = true;
        } else if (hasValidItem && hasFuel) {
            consumeFuel();
            changed = true;
        }

        if (isBurning() && hasValidItem) {
            increaseCraftingProgress();
            changed = true;

            if (hasProgressFinished()) {
                for (int slot : INPUT_SLOT) {
                    ItemStack stack = itemHandler.getStackInSlot(slot);
                    if (!stack.isEmpty() && hasRecipe(stack)) {
                        SimpleContainer inventory = new SimpleContainer(1);
                        inventory.setItem(0, stack);
                        Optional<SmeltingRecipe> recipe = level.getRecipeManager()
                                .getRecipeFor(RecipeType.SMELTING, inventory, level);

                        if (recipe.isPresent()) {
                            ItemStack result = recipe.get().getResultItem(level.registryAccess()).copy();
                            result.setCount(result.getCount() * 3);
                            if (findAvailableOutputSlot(result) >= 0) {
                                craftSpecificItem(slot);
                                resetProgress();
                                changed = true;
                                break;
                            }
                        }
                    }
                }
            }
        } else if (progress > 0) {
            resetProgress();
            changed = true;
        }

        if (changed) {
            setChanged();
            pLevel.sendBlockUpdated(pPos, pState, pState, 3);
        }
    }

    private void consumeFuel() {
        ItemStack fuelStack = itemHandler.getStackInSlot(FUEL_SLOT[0]);
        if (fuelStack.isEmpty()) return;

        totalBurnTime = getBurnTime(fuelStack);
        burnTime = totalBurnTime;

        fuelStack.shrink(1);
    }

    private boolean isBurning() {
        return burnTime > 0;
    }

    private int getBurnTime(ItemStack stack) {
        return ForgeHooks.getBurnTime(stack, RecipeType.SMELTING);
    }

    public static boolean isFuel(ItemStack stack) {
        return ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0;
    }

    private void craftSpecificItem(int inputSlot) {
        ItemStack inputStack = itemHandler.getStackInSlot(inputSlot);
        if (inputStack.isEmpty()) return;

        SimpleContainer inventory = new SimpleContainer(1);
        inventory.setItem(0, inputStack);
        Optional<SmeltingRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(RecipeType.SMELTING, inventory, level);

        if (recipe.isPresent()) {
            inputStack.shrink(1);

            ItemStack result = recipe.get().getResultItem(level.registryAccess()).copy();
            result.setCount(result.getCount() * 4);

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

    private int findAvailableOutputSlot(ItemStack result) {
        for (int slot : OUTPUT_SLOT) {
            ItemStack stack = itemHandler.getStackInSlot(slot);
            if (!stack.isEmpty() &&
                    ItemStack.isSameItemSameTags(stack, result) &&
                    stack.getCount() + result.getCount() <= stack.getMaxStackSize()) {
                return slot;
            }
        }

        for (int slot : OUTPUT_SLOT) {
            ItemStack stack = itemHandler.getStackInSlot(slot);
            if (stack.isEmpty()) {
                return slot;
            }
        }

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
        int resultCount = result.getCount() * 3;
        return canInsertItemIntoOutputSlot(result.getItem()) &&
                canInsertAmountIntoOutputSlot(resultCount);
    }

    private Optional<SmeltingRecipe> getCurrentRecipe() {
        SimpleContainer inventory = new SimpleContainer(1);

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
            if (stack.isEmpty()) {
                return true;
            }
            if (stack.is(item) && stack.getCount() < stack.getMaxStackSize()) {
                return true;
            }
        }
        return false;
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        for (int slot : OUTPUT_SLOT) {
            ItemStack stack = itemHandler.getStackInSlot(slot);
            if (stack.isEmpty()) {
                return true;
            }
            if (stack.getCount() + count <= stack.getMaxStackSize()) {
                return true;
            }
        }
        return false;
    }
}
