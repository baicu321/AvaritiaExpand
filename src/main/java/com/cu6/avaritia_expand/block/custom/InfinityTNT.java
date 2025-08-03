package com.cu6.avaritia_expand.block.custom;

import com.cu6.avaritia_expand.entity.custom.InfinityTNTEntity;
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

public class InfinityTNT extends Block {

    // 爆炸半径
    public static final int EXPLOSION_RADIUS = 30;
    // TNT fuse时间（游戏刻，20刻=1秒）
    public static final int FUSE_TIME = 80;

    public InfinityTNT(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        // 检查玩家是否使用打火石
        if (player.getItemInHand(hand).getItem() == net.minecraft.world.item.Items.FLINT_AND_STEEL) {
            ignite(level, pos, player);
            // 消耗打火石耐久
            player.getItemInHand(hand).hurtAndBreak(64, player, (p) -> p.broadcastBreakEvent(hand));
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighborPos, boolean isMoving) {
        // 红石信号激活
        if (level.hasNeighborSignal(pos)) {
            ignite(level, pos, null);
        }
    }

    private void ignite(Level level, BlockPos pos, Player igniter) {
        if (!level.isClientSide) {
            // 播放点燃音效
            level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);

            // 创建并召唤TNT实体
            InfinityTNTEntity tntEntity = new InfinityTNTEntity(level,
                    pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, igniter);
            tntEntity.setFuse(FUSE_TIME);
            tntEntity.setExplosionRadius(EXPLOSION_RADIUS);
            level.addFreshEntity(tntEntity);

            // 移除方块（可选：是否在点燃后移除这个方块）
            level.removeBlock(pos, false);
        }
    }
}
