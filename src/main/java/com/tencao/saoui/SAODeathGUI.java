package com.tencao.saoui;

import com.tencao.saoui.ui.SAOAlertGUI;
import com.tencao.saoui.ui.SAOElementGUI;
import com.tencao.saoui.ui.SAOScreenGUI;
import com.tencao.saoui.util.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiGameOver;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class SAODeathGUI extends SAOScreenGUI {

    private final GuiGameOver gameOver;
    private final SAOCursorStatus oldCursorStatus;

    public SAODeathGUI(GuiGameOver guiGamOver) {
        super();
        gameOver = guiGamOver;
        oldCursorStatus = CURSOR_STATUS;

        CURSOR_STATUS = SAOCursorStatus.HIDDEN;
    }

    @Override
    protected void init() {
        super.init();

        elements.add(new SAOAlertGUI(this, 0, 0, ConfigHandler._DEAD_ALERT, this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled() ? SAOColor.HARDCORE_DEAD_COLOR : SAOColor.DEAD_COLOR));
    }

    @Override
    public int getX(boolean relative) {
        return super.getX(relative) + width / 2;
    }

    @Override
    public int getY(boolean relative) {
        return super.getY(relative) + height / 2;
    }

    @Override
    public void drawScreen(int cursorX, int cursorY, float f) {
        drawDefaultBackground();

        GL11.glPushMatrix();
        GL11.glTranslatef(-width / 2, -height / 2, 0);
        GL11.glScalef(2, 2, 2);

        super.drawScreen(cursorX, cursorY, f);

        GL11.glPopMatrix();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
    }

    @Override
    public void actionPerformed(SAOElementGUI element, SAOAction action, int data) {
        final SAOID id = element.ID();

        element.click(mc.getSoundHandler(), false);

        // id isn't needed here anyway ^-^
        if (id == SAOID.ALERT) gameOver.confirmClicked(this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled(), -1);
    }

    @Override
    protected void backgroundClicked(int cursorX, int cursorY, int button) {
    }

    @Override
    public void close() {
        super.close();
        CURSOR_STATUS = oldCursorStatus;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }

}