package com.tencao.saoui.screens.death;

import com.tencao.saoui.GLCore;
import com.tencao.saoui.colorstates.CursorStatus;
import com.tencao.saoui.events.ConfigHandler;
import com.tencao.saoui.screens.Alert;
import com.tencao.saoui.screens.Elements;
import com.tencao.saoui.screens.buttons.Actions;
import com.tencao.saoui.screens.menu.Categories;
import com.tencao.saoui.screens.window.ScreenGUI;
import com.tencao.saoui.util.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiGameOver;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class DeathScreen extends ScreenGUI {

    private final GuiGameOver gameOver;
    private final CursorStatus oldCursorStatus;

    public DeathScreen(GuiGameOver guiGamOver) {
        super();
        gameOver = guiGamOver;
        oldCursorStatus = CURSOR_STATUS;

        CURSOR_STATUS = CursorStatus.HIDDEN;
    }

    @Override
    protected void init() {
        super.init();

        elements.add(new Alert(this, 0, 0, ConfigHandler._DEAD_ALERT, this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled() ? ColorUtil.HARDCORE_DEAD_COLOR : ColorUtil.DEAD_COLOR));
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

        GLCore.glStart();
        GLCore.glTranslatef(-width / 2, -height / 2, 0);
        GLCore.glScalef(2, 2, 2);

        super.drawScreen(cursorX, cursorY, f);

        GLCore.glEnd();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
    }

    @Override
    public void actionPerformed(Elements element, Actions action, int data) {
        final Categories id = element.ID();

        element.click(mc.getSoundHandler(), false);

        // id isn't needed here anyway ^-^
        if (id == Categories.ALERT) gameOver.confirmClicked(this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled(), -1);
    }

    @Override
    protected void backgroundClicked(int cursorX, int cursorY, int button) {
        gameOver.confirmClicked(this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled(), -1);
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