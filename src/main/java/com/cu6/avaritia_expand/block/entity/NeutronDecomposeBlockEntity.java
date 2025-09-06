package com.cu6.avaritia_expand.block.entity;

import com.cu6.avaritia_expand.screen.NeutronDecomposeMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
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

import java.util.ArrayList;
import java.util.List;

public class NeutronDecomposeBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(1){
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return isSingularity(stack);
        }
    };

    public static final int INPUT_SLOT = 0;

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    protected final ContainerData data = new ContainerData() {
        @Override
        public int get(int i) {
            return switch (i){
                case 0 -> NeutronDecomposeBlockEntity.this.progress;
                case 1 -> NeutronDecomposeBlockEntity.this.max_Progress;
                default -> 0;
            };
        }

        @Override
        public void set(int i, int i1) {
            switch (i){
                case 0 -> NeutronDecomposeBlockEntity.this.progress = i1;
                case 1 -> NeutronDecomposeBlockEntity.this.max_Progress = i1;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    private int progress = 0;
    private int max_Progress = 78;

    public NeutronDecomposeBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.NEUTRON_DECOMPOSE_BE.get(), pPos, pBlockState);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap);
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
        return Component.translatable("block.avaritia_expand.neutron_decompose");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new NeutronDecomposeMenu(i, inventory, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("inventory", itemHandler.serializeNBT());
        pTag.putInt("neutron_decompose.progress", progress);
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("inventory"));
        progress = pTag.getInt("neutron_decompose.progress");
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if (pLevel.isClientSide()) return;

        boolean changed = false;
        ItemStack stack = itemHandler.getStackInSlot(INPUT_SLOT);

        // 检查是否有奇点在槽位中
        if (!stack.isEmpty() && isSingularity(stack)) {
            increaseCraftingProgress();
            changed = true;

            if (hasProgressFinished()) {
                // 分解奇点
                decomposeSingularity();
                resetProgress();
                changed = true;
            }
        } else if (progress > 0) {
            // 没有奇点时重置进度
            resetProgress();
            changed = true;
        }

        if (changed) {
            setChanged();
            pLevel.sendBlockUpdated(pPos, pState, pState, 3);
        }
    }

    private void decomposeSingularity() {
        ItemStack inputStack = itemHandler.getStackInSlot(INPUT_SLOT);
        if (inputStack.isEmpty() || !isSingularity(inputStack)) return;

        // 获取附近的玩家
        Player player = level.getNearestPlayer(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5, 5.0, false);
        if (player == null) return;

        // 消耗奇点
        inputStack.shrink(1);

        // 获取分解结果
        List<ItemStack> results = getSingularityDecompositionResults(inputStack);

        // 将分解结果放入玩家物品栏
        for (ItemStack result : results) {
            if (!player.getInventory().add(result)) {
                // 如果物品栏满了，将物品掉落在地上
                player.drop(result, false);
            }
        }
    }

    private void resetProgress() {
        progress = 0;
    }

    private boolean hasProgressFinished() {
        return progress >= max_Progress;
    }

    private void increaseCraftingProgress() {
        progress++;
    }

    private boolean isSingularity(ItemStack stack) {
        // 检查物品是否为奇点
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if (tag.contains("Id")) {
                String id = tag.getString("Id");
                return id.contains("singularity");
            }
        }

        // 检查物品注册名是否包含singularity
        return stack.getItem().toString().contains("singularity");
    }

    private List<ItemStack> getSingularityDecompositionResults(ItemStack singularity) {
        List<ItemStack> results = new ArrayList<>();

        // 根据奇点类型返回对应的分解结果
        String id = "";
        if (singularity.hasTag() && singularity.getTag().contains("Id")) {
            id = singularity.getTag().getString("Id");
        } else {
            // 如果没有NBT数据，尝试从物品注册名中提取
            String itemName = singularity.getItem().toString();
            // 例如从 "avaritia:diamond_singularity" 中提取 "diamond"
            if (itemName.contains(":") && itemName.contains("_singularity")) {
                String[] parts = itemName.split(":");
                if (parts.length > 1) {
                    id = "avaritia:" + parts[1].replace("_singularity", "");
                }
            }
        }

        // 根据奇点类型返回分解结果，使用标准的1000个物品数量
        switch (id) {
            case "avaritia:iron":
                results.add(new ItemStack(net.minecraft.world.item.Items.IRON_INGOT, 1000));
                break;
            case "avaritia:gold":
                results.add(new ItemStack(net.minecraft.world.item.Items.GOLD_INGOT, 1000));
                break;
            case "avaritia:diamond":
                results.add(new ItemStack(net.minecraft.world.item.Items.DIAMOND, 1000));
                break;
            case "avaritia:emerald":
                results.add(new ItemStack(net.minecraft.world.item.Items.EMERALD, 1000));
                break;
            case "avaritia:copper":
                results.add(new ItemStack(net.minecraft.world.item.Items.COPPER_INGOT, 1000));
                break;
            case "avaritia:coal":
                results.add(new ItemStack(net.minecraft.world.item.Items.COAL, 1000));
                break;
            case "avaritia:redstone":
                results.add(new ItemStack(net.minecraft.world.item.Items.REDSTONE, 1000));
                break;
            case "avaritia:lapis_lazuli":
                results.add(new ItemStack(net.minecraft.world.item.Items.LAPIS_LAZULI, 1000));
                break;
            case "avaritia:quartz":
                results.add(new ItemStack(net.minecraft.world.item.Items.QUARTZ, 1000));
                break;
            case "avaritia:obsidian":
                results.add(new ItemStack(net.minecraft.world.item.Items.OBSIDIAN, 1000));
                break;
            case "avaritia:amethyst":
                results.add(new ItemStack(net.minecraft.world.item.Items.AMETHYST_SHARD, 1000));
                break;
            case "avaritia:glowstone":
                results.add(new ItemStack(net.minecraft.world.item.Items.GLOWSTONE_DUST, 1000));
                break;
            case "avaritia:clay":
                results.add(new ItemStack(net.minecraft.world.item.Items.CLAY_BALL, 1000));
                break;
            case "avaritia:ender_pearl":
                results.add(new ItemStack(net.minecraft.world.item.Items.ENDER_PEARL, 1000));
                break;
            default:
                // 默认返回一些通用物品
                results.add(new ItemStack(net.minecraft.world.item.Items.COBBLESTONE, 64));
                break;
        }

        return results;
    }
}
