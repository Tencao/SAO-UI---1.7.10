package com.tencao.saoui.screens.menu;

import com.tencao.saoui.GLCore;
import com.tencao.saoui.screens.ParentElement;
import com.tencao.saoui.screens.buttons.ButtonGUI;
import com.tencao.saoui.screens.inventory.InventoryCore;
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
public class Slots extends ButtonGUI {

    private static final String UNKNOWN = "???";
    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");

    private Slot buttonSlot;
    protected static RenderItem itemRender = new RenderItem();

    public Slots(ParentElement gui, int xPos, int yPos, int w, int h, Slot slot) {
        super(gui, Categories.SLOT, xPos, yPos, w, h);
        buttonSlot = slot;
        super.caption = this.getCaption();
        super.icon = this.getIcon();
    }

    public Slots(ParentElement gui, int xPos, int yPos, int w, Slot slot) {
        this(gui, xPos, yPos, w, 20, slot);
    }

    public Slots(ParentElement gui, int xPos, int yPos, Slot slot) {
        this(gui, xPos, yPos, 150, slot);
    }

    public static IconCore getIcon(ItemStack stack) {
        if (stack != null) {
            if (InventoryCore.WEAPONS.isFine(stack, false)) return IconCore.EQUIPMENT;
            else if (InventoryCore.EQUIPMENT.isFine(stack, false)) return IconCore.ARMOR;
            else if (InventoryCore.ACCESSORY.isFine(stack, false)) return IconCore.ACCESSORY;
            else return IconCore.ITEMS;
        } else return IconCore.HELP;
    }

    protected IconCore getIcon() {
        return getIcon(buttonSlot.getStack());
    }

    protected String getCaption() {
        return buttonSlot.getHasStack() && buttonSlot.getStack().getItem() != null ? buttonSlot.getStack().getDisplayName() : UNKNOWN;
    }

    @Override
    public void draw(Minecraft mc, int cursorX, int cursorY) {
        super.draw(mc, cursorX, cursorY);

        if ((visibility > 0) && (enabled)) {
            GLCore.glStart();
            final int left = getX(false);
            final int top = getY(false);
            final int iconOffset = (height - 16) / 2;
            final ItemStack stack = getStack();

            if (stack != null) {
                GLCore.glString("x" + stack.stackSize, left + width + 2, top + height - 16, ColorUtil.multiplyAlpha(getColor(hoverState(cursorX, cursorY), false), visibility), true);
                this.drawSlot(mc, stack, left + iconOffset, top + iconOffset);
            }
            GLCore.glEnd();
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

        if (stack.isItemEnchanted()) renderEffectSlot(mc, x-  1, y - 1);
        else {
            GLCore.glBlend(true);
            GLCore.glAlphaTest(true);
        }
    }

    private void renderEffectSlot(Minecraft mc, int x, int y){
        GLCore.glDepthFunc(GL11.GL_EQUAL);
        GLCore.depthMask(false);
        mc.getTextureManager().bindTexture(RES_ITEM_GLINT);
        GLCore.glAlphaTest(true);
        GLCore.glBlend(true);
        GLCore.glColor(0.5F, 0.25F, 0.8F, 1.0F);
        this.renderGlintSlot(x, y, 150, 20);
        GLCore.tryBlendFuncSeparate(770, 771, 1, 0);
        GLCore.depthMask(true);
        GLCore.glDepthFunc(GL11.GL_LEQUAL);
    }

    private void renderGlintSlot(int x, int y, int width, int height){
        for (int j1 = 0; j1 < 2; ++j1)
        {
            GLCore.tryBlendFuncSeparate(772, 1, 0, 0);
            float f = 0.00390625F;
            float f1 = 0.00390625F;
            float f2 = (float)(Minecraft.getSystemTime() % (long)(3000 + j1 * 1873)) / (3000.0F + (float)(j1 * 1873)) * 256.0F;
            float f3 = 0.0F;
            float f4 = 4.0F;

            if (j1 == 1)
            {
                f4 = -1.0F;
            }

            GLCore.begin();
            GLCore.addVertex((double)(x), (double)(y + height), (double)itemRender.zLevel, (double)((f2 + (float)height * f4) * f), (double)((f3 + (float)height) * f1));
            GLCore.addVertex((double)(x + width), (double)(y + height), (double)itemRender.zLevel, (double)((f2 + (float)width + (float)height * f4) * f), (double)((f3 + (float)height) * f1));
            GLCore.addVertex((double)(x + width), (double)(y), (double)itemRender.zLevel, (double)((f2 + (float)width) * f), (double)((f3 + 0.0F) * f1));
            GLCore.addVertex((double)(x), (double)(y), (double)itemRender.zLevel, (double)((f2 + 0.0F) * f), (double)((f3 + 0.0F) * f1));
            GLCore.draw();
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
    protected int getColor(int hoverState, boolean bg) {

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