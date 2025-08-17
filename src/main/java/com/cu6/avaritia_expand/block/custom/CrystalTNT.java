package com.cu6.avaritia_expand.block.custom;

import com.cu6.avaritia_expand.entity.custom.CrystalTNTEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class CrystalTNT extends Block {
    public static final int FUSE_TIME = 80;

    public CrystalTNT(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (player.getItemInHand(hand).getItem() == net.minecraft.world.item.Items.FLINT_AND_STEEL) {
            ignite(level, pos, player);
            player.getItemInHand(hand).hurtAndBreak(64, player, (p) -> p.broadcastBreakEvent(hand));
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighborPos, boolean isMoving) {
        if (level.hasNeighborSignal(pos)) {
            ignite(level, pos, null);
        }
    }

    private void ignite(Level level, BlockPos pos, Player igniter) {
        if (!level.isClientSide) {
            level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);

            CrystalTNTEntity tntEntity = new CrystalTNTEntity(level,
                    pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, igniter);
            tntEntity.setFuse(FUSE_TIME);


            tntEntity.setExplosionRadius(Math.round(25));


            level.addFreshEntity(tntEntity);
            level.removeBlock(pos, false);
        }
    }
}
