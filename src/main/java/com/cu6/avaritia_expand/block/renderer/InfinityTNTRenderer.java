package com.cu6.avaritia_expand.block.renderer;

import com.cu6.avaritia_expand.AvaritiaExpand;
import com.cu6.avaritia_expand.block.ModBlocks;
import com.cu6.avaritia_expand.entity.block.InfinityTNTEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.TntMinecartRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class InfinityTNTRenderer extends EntityRenderer<InfinityTNTEntity> {
    private final BlockRenderDispatcher blockRenderer;
    private BlockState infinityTntState;

    public InfinityTNTRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.shadowRadius = 0.5F;
        this.blockRenderer = pContext.getBlockRenderDispatcher();
        this.infinityTntState = null;
    }

    @Override
    public void render(InfinityTNTEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        // 延迟加载方块状态
        if (infinityTntState == null) {
            try {
                infinityTntState = ModBlocks.INFINITY_TNT.get().defaultBlockState();
                        ModBlocks.INFINITY_TNT.getId();
            } catch (Exception e) {
                infinityTntState = Blocks.TNT.defaultBlockState();
            }
        }
        pPoseStack.pushPose();
        pPoseStack.translate(0.0F, 0.5F, 0.0F);
        int $$6 = pEntity.getFuse();
        if ((float)$$6 - pPartialTick + 1.0F < 10.0F) {
            float $$7 = 1.0F - ((float)$$6 - pPartialTick + 1.0F) / 10.0F;
            $$7 = Mth.clamp($$7, 0.0F, 1.0F);
            $$7 *= $$7;
            $$7 *= $$7;
            float $$8 = 1.0F + $$7 * 0.3F;
            pPoseStack.scale($$8, $$8, $$8);
        }

        pPoseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
        pPoseStack.translate(-0.5F, -0.5F, 0.5F);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        BlockState state = ModBlocks.INFINITY_TNT.get().defaultBlockState();
        this.blockRenderer.renderSingleBlock(
                state,
                pPoseStack,
                pBuffer,
                pPackedLight,
                OverlayTexture.NO_OVERLAY
        );
//        TntMinecartRenderer.renderWhiteSolidBlock(this.blockRenderer, ModBlocks.INFINITY_TNT.get().defaultBlockState(), pPoseStack, pBuffer, pPackedLight, $$6 / 5 % 2 == 0);
        pPoseStack.popPose();
//        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(InfinityTNTEntity infinityTNTEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
