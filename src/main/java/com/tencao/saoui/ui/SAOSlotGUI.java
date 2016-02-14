package com.tencao.saoui.ui;

import com.tencao.saoui.util.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class SAOSlotGUI extends SAOButtonGUI {

    private static final String UNKNOWN = "???";
    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");

    private Slot buttonSlot;
    protected static RenderItem itemRender = new RenderItem();

    public SAOSlotGUI(SAOParentGUI gui, int xPos, int yPos, int w, int h, Slot slot) {
        super(gui, SAOID.SLOT, xPos, yPos, w, h);
        buttonSlot = slot;
        super.caption = this.getCaption();
        super.icon = this.getIcon();
    }

    public SAOSlotGUI(SAOParentGUI gui, int xPos, int yPos, int w, Slot slot) {
        this(gui, xPos, yPos, w, 20, slot);
    }

    public SAOSlotGUI(SAOParentGUI gui, int xPos, int yPos, Slot slot) {
        this(gui, xPos, yPos, 150, slot);
    }

    static SAOIcon getIcon(ItemStack stack) {
        if (stack != null) {
            if (SAOInventory.WEAPONS.isFine(stack, false)) return SAOIcon.EQUIPMENT;
            else if (SAOInventory.EQUIPMENT.isFine(stack, false)) return SAOIcon.ARMOR;
            else if (SAOInventory.ACCESSORY.isFine(stack, false)) return SAOIcon.ACCESSORY;
            else return SAOIcon.ITEMS;
        } else return SAOIcon.HELP;
    }

    protected SAOIcon getIcon() {
        return getIcon(buttonSlot.getStack());
    }

    protected String getCaption() {
        return buttonSlot.getHasStack() && buttonSlot.getStack().getItem() != null ? buttonSlot.getStack().getDisplayName() : UNKNOWN;
    }

    @Override
    public void draw(Minecraft mc, int cursorX, int cursorY) {
        super.draw(mc, cursorX, cursorY);

        if ((visibility > 0) && (enabled)) {
            final int left = getX(false);
            final int top = getY(false);
            final int iconOffset = (height - 16) / 2;
            final ItemStack stack = getStack();

            if (stack != null) {
                SAOGL.glString("x" + stack.stackSize, left + width + 2, top + height - 16, SAOColor.multiplyAlpha(getColor(hoverState(cursorX, cursorY), false), visibility), true);
                this.drawSlot(mc, stack, left + iconOffset, top + iconOffset);
            }
        }
    }

    private void drawSlot(Minecraft mc, ItemStack stack, int x, int y) {
        RenderHelper.enableGUIStandardItemLighting();

        itemRender.renderItemIntoGUI(mc.fontRendererObj, mc.getTextureManager(), stack, x, y, /*true*/false);
        RenderHelper.disableStandardItemLighting();
//        itemRender.renderItemOverlayIntoGUI(mc.fontRendererObj, mc.getTextureManager(), stack, x, y);
//        GL11.glDisable(GL11.GL_LIGHTING);
//        GL11.glEnable(GL11.GL_ALPHA_TEST);
//        GL11.glEnable(GL11.GL_BLEND);

        if (stack.isItemEnchanted()) renderEffectSlot(mc.getTextureManager(), x-  1, y - 1);
        else {
            SAOGL.glBlend(true);
            SAOGL.glAlpha(true);
        }
    }

    private void renderEffectSlot(TextureManager manager, int x, int y){
        GL11.glDepthFunc(GL11.GL_EQUAL);
        //GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(false);
        manager.bindTexture(RES_ITEM_GLINT);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(0.5F, 0.25F, 0.8F, 1.0F);
        //this.renderGlintSlot(x * 431278612 + y * 32178161, x - 2, y - 2, 20, 20);
        this.renderGlintSlot(x, y, 150, 20);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glDepthMask(true);
        //GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
    }

    private void renderGlintSlot(int x, int y, int width, int height){
        for (int j1 = 0; j1 < 2; ++j1)
        {
            OpenGlHelper.glBlendFunc(772, 1, 0, 0);
            float f = 0.00390625F;
            float f1 = 0.00390625F;
            float f2 = (float)(Minecraft.getSystemTime() % (long)(3000 + j1 * 1873)) / (3000.0F + (float)(j1 * 1873)) * 256.0F;
            float f3 = 0.0F;
            Tessellator tessellator = Tessellator.instance;
            float f4 = 4.0F;

            if (j1 == 1)
            {
                f4 = -1.0F;
            }

            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV((double)(x), (double)(y + height), (double)itemRender.zLevel, (double)((f2 + (float)height * f4) * f), (double)((f3 + (float)height) * f1));
            tessellator.addVertexWithUV((double)(x + width), (double)(y + height), (double)itemRender.zLevel, (double)((f2 + (float)width + (float)height * f4) * f), (double)((f3 + (float)height) * f1));
            tessellator.addVertexWithUV((double)(x + width), (double)(y), (double)itemRender.zLevel, (double)((f2 + (float)width) * f), (double)((f3 + 0.0F) * f1));
            tessellator.addVertexWithUV((double)(x), (double)(y), (double)itemRender.zLevel, (double)((f2 + 0.0F) * f), (double)((f3 + 0.0F) * f1));
            tessellator.draw();
        }
    }

    public void refreshSlot(Slot slot) {
        if (slot != null) {
            buttonSlot = slot;

            caption = getCaption();
            icon = getIcon();
        }

        if (isEmpty()) remove();
    }

    protected boolean isEmpty() {
        return (!buttonSlot.getHasStack());
    }

    public Slot getSlot() {
        return buttonSlot;
    }

    public int getSlotNumber() {
        return buttonSlot.slotNumber;
    }

    public ItemStack getStack() {
        return buttonSlot.getStack();
    }

    @Override
    int getColor(int hoverState, boolean bg) {

        return super.getColor(hoverState, bg);
    }

    @Override
    public boolean keyTyped(Minecraft mc, char ch, int key) {
        return true;
    }

    @Override
    public boolean mouseOver(int cursorX, int cursorY, int flag) {
        return focus = super.mouseOver(cursorX, cursorY, flag);
    }

    @Override
    public boolean mouseReleased(Minecraft mc, int cursorX, int cursorY, int button) {
        return super.mouseReleased(mc, cursorX, cursorY, button) || button == 1 || button == 2;
    }


}