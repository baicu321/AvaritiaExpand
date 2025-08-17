package com.cu6.avaritia_expand.screen;

import com.cu6.avaritia_expand.AvaritiaExpand;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class BlazeFurnaceScreen extends AbstractContainerScreen<BlazeFurnaceMenu> {
    private static final ResourceLocation TEXTURE =
                new ResourceLocation(AvaritiaExpand.MOD_ID,"textures/gui/blaze_furnace_gui.png");
    public static final int SLOT_SIZE = 18;
    private int leftPos(int guiWidth) {
        return (this.width - guiWidth) / 2;
    }

    private int topPos(int guiHeight) {
        return (this.height - guiHeight) / 2;
    }
    public BlazeFurnaceScreen(BlazeFurnaceMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, Component.translatable("title.avaritia_expand.blaze_furnace"));
    }

    @Override
    protected void init() {
        super.init();
        this.inventoryLabelY = 100000000;
        this.titleLabelX = 7;
        this.titleLabelY = 73;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F,1.0F,1.0F,1.0F);
        RenderSystem.setShaderTexture(0,TEXTURE);
        int x = (width - imageWidth)/2;
        int y = (height - imageHeight)/2;
        guiGraphics.blit(TEXTURE,x,y,256,256,imageWidth,imageHeight);

        renderProgressArrow(guiGraphics,x,y);
    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if (menu.isCrafing()){
            guiGraphics.blit(TEXTURE,x+77,y+37,176,0,menu.getScaledProgress(),16);
        }
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pGuiGraphics,pMouseX,pMouseY);
    }
}
