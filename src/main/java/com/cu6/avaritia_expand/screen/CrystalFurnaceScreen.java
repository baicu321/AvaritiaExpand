package com.cu6.avaritia_expand.screen;

import com.cu6.avaritia_expand.AvaritiaExpand;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CrystalFurnaceScreen extends AbstractContainerScreen<CrystalFurnaceMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(AvaritiaExpand.MOD_ID,"textures/gui/crystal_furnace_gui.png");
    public CrystalFurnaceScreen(CrystalFurnaceMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, Component.translatable("title.avaritia_expand.crystal_furnace"));
    }

    @Override
    protected void init() {
        super.init();
        this.inventoryLabelX = 12;
        this.inventoryLabelY = 101;
        this.titleLabelX = 6;
        this.titleLabelY = -16;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F,1.0F,1.0F,1.0F);
        RenderSystem.setShaderTexture(0,TEXTURE);
        int x = (width - imageWidth)/2;
        int y = (height - imageHeight)/2;
        guiGraphics.blit(TEXTURE,x,y-22,0,0,176,240);

        renderProgressArrow(guiGraphics,x,y);
    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if (menu.isCrafting()){
            guiGraphics.blit(TEXTURE,x+64,y+35,176,0,menu.getScaledProgress(),10);
        }
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pGuiGraphics,pMouseX,pMouseY);
    }
}
