package com.saomc.screens.death;

import com.saomc.GLCore;
import com.saomc.screens.buttons.Actions;
import com.saomc.screens.window.ScreenGUI;
import com.saomc.colorstates.CursorStatus;
import com.saomc.events.ConfigHandler;
import com.saomc.screens.Alert;
import com.saomc.screens.Elements;
import com.saomc.util.ColorUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class DeathScreen extends ScreenGUI {

    private final CursorStatus oldCursorStatus;

    public DeathScreen() {
        super();
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

        GLCore.glTranslatef(-width / 2, -height / 2, 0.0F);
        GLCore.glScalef(2.0F, 2.0F, 2.0F);

        super.drawScreen(cursorX, cursorY, f);

    }

    @Override
    public void actionPerformed(Elements element, Actions action, int data) {
        confirmClicked(this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled(), -1);
    }

    @Override
    protected void backgroundClicked(int cursorX, int cursorY, int button) {
        confirmClicked(this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled(), -1);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {

    }

    public void confirmClicked(boolean result, int id)
    {
        if (result)
        {
            mc.theWorld.sendQuittingDisconnectingPacket();
            mc.loadWorld((WorldClient)null);
            mc.displayGuiScreen(new GuiMainMenu());
        }
        else
        {
            mc.thePlayer.respawnPlayer();
            mc.displayGuiScreen(null);
        }
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

    /**
     * This is a dummy class intended to fix the ghost death screen
     */
    public static class DummyScreen extends DeathScreen {

        @Override
        public void initGui(){}

        @Override
        protected void keyTyped(char typedChar, int keyCode) {}


        @Override
        protected void actionPerformed(GuiButton button)
        {}

        @Override
        public void confirmClicked(boolean result, int id)
        {}

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks)
        {}

        @Override
        public void updateScreen()
        {}
    }
}