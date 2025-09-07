package com.cu6.avaritia_expand.block.entity;

import com.cu6.avaritia_expand.screen.NeutronDecomposeMenu;
import committee.nova.mods.avaritia.init.handler.SingularityRegistryHandler;
import committee.nova.mods.avaritia.util.SingularityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class NeutronDecomposeBlockEntity extends BlockEntity implements MenuProvider {
    private static final int INPUT_SLOT = 0;

    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return slot == INPUT_SLOT && isSingularity(stack);
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            if (slot == INPUT_SLOT && !level.isClientSide) {
                processSingularity();
            }
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    protected final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> NeutronDecomposeBlockEntity.this.progress;
                case 1 -> NeutronDecomposeBlockEntity.this.maxProgress;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> NeutronDecomposeBlockEntity.this.progress = value;
                case 1 -> NeutronDecomposeBlockEntity.this.maxProgress = value;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    private int progress = 0;
    private int maxProgress = 78;

    public NeutronDecomposeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.NEUTRON_DECOMPOSE_BE.get(), pos, state);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap);
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.avaritia_expand.neutron_decompose");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new NeutronDecomposeMenu(containerId, playerInventory, this, data);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("inventory", itemHandler.serializeNBT());
        tag.putInt("neutron_decompose.progress", progress);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        progress = tag.getInt("neutron_decompose.progress");
    }

    private boolean isSingularity(ItemStack stack) {
        if (stack.isEmpty()) return false;


        if (ForgeRegistries.ITEMS.getKey(stack.getItem()) != null) {
            ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
            if (itemId.toString().equals("avaritia:eternal_singularity")) {
                return true;
            }
        }

        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if (tag.contains("Id")) {
                String id = tag.getString("Id");
                try {
                    ResourceLocation res = new ResourceLocation(id);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
        }
        return false;
    }


    private void processSingularity() {
        ItemStack inputStack = itemHandler.getStackInSlot(INPUT_SLOT);
        if (inputStack.isEmpty() || !isSingularity(inputStack)) return;


        Player player = level.getNearestPlayer(
                worldPosition.getX() + 0.5,
                worldPosition.getY() + 0.5,
                worldPosition.getZ() + 0.5,
                5.0,
                false
        );

        if (player == null) return;


        ItemStack consumed = inputStack.split(1);
        if (inputStack.isEmpty()) {
            itemHandler.setStackInSlot(INPUT_SLOT, ItemStack.EMPTY);
        }


        List<ItemStack> results = getDecompositionResults(consumed);


        for (ItemStack result : results) {
            if (!result.isEmpty()) {

                if (!player.getInventory().add(result)) {

                    player.drop(result, false);
                }
            }
        }

        setChanged();
    }


    private List<ItemStack> getDecompositionResults(ItemStack singularity) {
        List<ItemStack> results = new ArrayList<>();

        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(singularity.getItem());
        if (itemId != null && itemId.toString().equals("avaritia:eternal_singularity")) {

            for (var singularityEntry : SingularityRegistryHandler.getInstance().getSingularities()) {
                if (singularityEntry.isEnabled()) {
                    ItemStack singularityStack = SingularityUtils.getItemForSingularity(singularityEntry);
                    if (!singularityStack.isEmpty()) {
                        results.add(singularityStack);
                    }
                }
            }
            return results;
        }


        if (singularity.hasTag()) {
            CompoundTag tag = singularity.getTag();
            String id = tag.getString("Id");

            var singularityEntry = SingularityRegistryHandler.getInstance().getSingularityById(new ResourceLocation(id));
            if (singularityEntry != null) {
                Item resultItem = singularityEntry.getIngredient().getItems()[0].getItem(); // 获取配方中的第一个物品
                results.add(new ItemStack(resultItem, 1000));
                return results;
            }
        }

        results.add(new ItemStack(net.minecraft.world.item.Items.COBBLESTONE, 64));
        return results;
    }

    public void tick(Level level, BlockPos pos, BlockState state) {

    }
}
