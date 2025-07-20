package com.cu6.avaritia_expand.block.custom;

import com.cu6.avaritia_expand.entity.ModEntities;
import com.cu6.avaritia_expand.entity.block.InfinityTNTEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class InfinityTNTBlock extends TntBlock {
    public InfinityTNTBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void onCaughtFire(BlockState state, Level world, BlockPos pos, @Nullable Direction face, @Nullable LivingEntity igniter) {
        if (!world.isClientSide) {
            InfinityTNTEntity tnt = new InfinityTNTEntity(
                    world,
                    (double)pos.getX() + 0.5D,
                    pos.getY(),
                    (double)pos.getZ() + 0.5D,
                    igniter
            );
            world.addFreshEntity(tnt);
        }
    }
}
