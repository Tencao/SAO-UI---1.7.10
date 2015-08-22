package com.tencao.sao;

import com.tencao.sao.ui.SAOAlertGUI;
import com.tencao.sao.ui.SAOElementGUI;
import com.tencao.sao.util.SAOColor;
import com.tencao.sao.util.SAOCursorStatus;
import com.tencao.sao.util.SAOID;
import com.tencao.sao.ui.SAOScreenGUI;
import com.tencao.sao.util.SAOAction;
import net.minecraft.client.gui.GuiGameOver;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.io.IOException;

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

        elements.add(new SAOAlertGUI(this, 0, 0, SAOMod._DEAD_ALERT, SAOColor.DEAD_COLOR));
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
        GL11.glTranslatef(-width / 2, -height / 2, 0.0F);
        GL11.glScalef(2.0F, 2.0F, 2.0F);

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

        if (id == SAOID.ALERT) {
            gameOver.confirmClicked(true, 1);
        }
    }

    @Override
    protected void backgroundClicked(int cursorX, int cursorY, int button) {
        if (!mc.theWorld.getWorldInfo().isHardcoreModeEnabled()) {
            if (button == 0) {
                if (!((SAOIngameGUI) mc.ingameGUI).backgroundClicked(cursorX, cursorY, button)) {
                    gameOver.confirmClicked(false, 1);
                    mc.setIngameFocus();
                }
            }
        }
    }

    @Override
    public void close() {
        super.close();

        CURSOR_STATUS = oldCursorStatus;
    }

}
