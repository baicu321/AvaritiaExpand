package com.cu6.avaritia_expand.block.custom;

import com.cu6.avaritia_expand.block.entity.BlazeFurnaceBlockEntity;
import com.cu6.avaritia_expand.block.entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class BlazeFurnaceBlock extends BaseEntityBlock {

    public BlazeFurnaceBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (pState.getBlock() != pNewState.getBlock()){
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof BlazeFurnaceBlockEntity){
                ((BlazeFurnaceBlockEntity) blockEntity).drops();
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }
    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide()){
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if (entity instanceof BlazeFurnaceBlockEntity){
                NetworkHooks.openScreen(((ServerPlayer) pPlayer),(BlazeFurnaceBlockEntity)entity,pPos);
            }else {
                throw new IllegalStateException("");
            }
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new BlazeFurnaceBlockEntity(pPos,pState);
    }

    @Nullable
    @Override
    public  <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if (pLevel.isClientSide()) {
            return null;
        }

        return createTickerHelper(
                pBlockEntityType,
                ModBlockEntities.BLAZE_FURNACE_BE.get(),
                (level, blockPos, blockState, blockEntity) -> {
                    if (blockEntity instanceof BlazeFurnaceBlockEntity) {
                        ((BlazeFurnaceBlockEntity) blockEntity).tick(level, blockPos, blockState);
                    }
                }
        );
    }
}
