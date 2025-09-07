package com.cu6.avaritia_expand.entity.client;


import com.cu6.avaritia_expand.block.ModBlocks;
import com.cu6.avaritia_expand.entity.custom.InfinityTNTEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.TntMinecartRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.joml.Matrix4f;

public class InfinityTNTRenderer extends EntityRenderer<InfinityTNTEntity> {

    private final BlockRenderDispatcher blockRenderer;
    private static final ResourceLocation DRAGON_EXPLODING_LOCATION = new ResourceLocation("textures/entity/enderdragon/dragon_exploding.png");
    private static final ResourceLocation DRAGON_LOCATION = new ResourceLocation("textures/entity/enderdragon/dragon.png");
    private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(DRAGON_LOCATION);

    public InfinityTNTRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.shadowRadius = 0.5F;
        this.blockRenderer = pContext.getBlockRenderDispatcher();
    }


    @Override
    public void render(InfinityTNTEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0F, 0.5F, 0.0F);

        int fuse = entity.getFuse();
        if ((float) fuse - partialTicks + 1.0F < 10.0F) {
            float scale = 1.0F - ((float) fuse - partialTicks + 1.0F) / 10.0F;
            scale = Mth.clamp(scale, 0.0F, 1.0F);
            scale *= scale;
            scale *= scale;
            float f1 = 1.0F + scale * 0.3F;
            poseStack.scale(f1, f1, f1);
        }

        poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
        poseStack.translate(-0.5F, -0.5F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        TntMinecartRenderer.renderWhiteSolidBlock(this.blockRenderer, ModBlocks.INFINITY_TNT.get().defaultBlockState(), poseStack, buffer, packedLight, fuse / 5 % 2 == 0);
        poseStack.popPose();

        // 添加末影龙死亡效果渲染
        if (entity.getFuse() <= InfinityTNTEntity.DEFAULT_FUSE_TIME) {
            this.renderDragonDeathEffect(entity, yaw, partialTicks, poseStack, buffer, packedLight);
        }

        super.render(entity, yaw, partialTicks, poseStack, buffer, packedLight);
    }

    private void renderDragonDeathEffect(InfinityTNTEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        float f5 = ((float) (InfinityTNTEntity.DEFAULT_FUSE_TIME - entity.getFuse()) + partialTicks) / (float) InfinityTNTEntity.DEFAULT_FUSE_TIME;
        float f7 = Math.min(f5 > 0.8F ? (f5 - 0.8F) / 0.2F : 0.0F, 1.0F);

        poseStack.pushPose();
        // 将效果定位在实体中心
        poseStack.translate(0.0F, 0.5F, 0.0F);
        // 根据实体的朝向旋转效果
        poseStack.mulPose(Axis.YP.rotationDegrees(-entityYaw));

        RandomSource randomsource = RandomSource.create(432L);
        VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.lightning());

        int rays = (int) ((f5 + f5 * f5) / 2.0F * 60.0F);
        for (int i = 0; i < rays; ++i) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.XP.rotationDegrees(randomsource.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(randomsource.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(randomsource.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(randomsource.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(randomsource.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(randomsource.nextFloat() * 360.0F + f5 * 90.0F));
            float f3 = randomsource.nextFloat() * 20.0F + 5.0F + f7 * 10.0F;
            float f4 = randomsource.nextFloat() * 2.0F + 1.0F + f7 * 2.0F;
            Matrix4f matrix4f = poseStack.last().pose();
            int j = (int) (255.0F * (1.0F - f7));

            vertexconsumer.vertex(matrix4f, 0.0F, 0.0F, 0.0F).color(255, 255, 255, j).endVertex();
            vertexconsumer.vertex(matrix4f, -0.866F * f4, f3, -0.5F * f4).color(255, 0, 255, 0).endVertex();
            vertexconsumer.vertex(matrix4f, 0.866F * f4, f3, -0.5F * f4).color(255, 0, 255, 0).endVertex();
            vertexconsumer.vertex(matrix4f, 0.0F, f3, 1.0F * f4).color(255, 0, 255, 0).endVertex();
            vertexconsumer.vertex(matrix4f, -0.866F * f4, f3, -0.5F * f4).color(255, 0, 255, 0).endVertex();
            poseStack.popPose();
        }

        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(InfinityTNTEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    private static void vertex01(VertexConsumer pConsumer, Matrix4f pMatrix, int pAlpha) {
        pConsumer.vertex(pMatrix, 0.0F, 0.0F, 0.0F).color(255, 255, 255, pAlpha).endVertex();
    }

    private static void vertex2(VertexConsumer pConsumer, Matrix4f pMatrix, float p_114101_, float p_114102_) {
        pConsumer.vertex(pMatrix, -0.866F * p_114102_, p_114101_, -0.5F * p_114102_).color(255, 0, 255, 0).endVertex();
    }

    private static void vertex3(VertexConsumer pConsumer, Matrix4f pMatrix, float p_114110_, float p_114111_) {
        pConsumer.vertex(pMatrix, 0.866F * p_114111_, p_114110_, -0.5F * p_114111_).color(255, 0, 255, 0).endVertex();
    }

    private static void vertex4(VertexConsumer pConsumer, Matrix4f pMatrix, float p_114117_, float p_114118_) {
        pConsumer.vertex(pMatrix, 0.0F, p_114117_, 1.0F * p_114118_).color(255, 0, 255, 0).endVertex();
    }
}
