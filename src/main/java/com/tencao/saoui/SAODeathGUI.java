package com.tencao.saoui;

import com.tencao.saoui.ui.SAOAlertGUI;
import com.tencao.saoui.ui.SAOElementGUI;
import com.tencao.saoui.util.SAOColor;
import com.tencao.saoui.util.SAOCursorStatus;
import com.tencao.saoui.util.SAOID;
import com.tencao.saoui.ui.SAOScreenGUI;
import com.tencao.saoui.util.SAOAction;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.resources.I18n;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class SAODeathGUI extends SAOScreenGUI implements GuiYesNoCallback {

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

    	if (this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled()){
            elements.add(new SAOAlertGUI(this, 0, 0, SAOMod._DEAD_ALERT, SAOColor.HARDCORE_DEAD_COLOR));
    	}
    	else {
            elements.add(new SAOAlertGUI(this, 0, 0, SAOMod._DEAD_ALERT, SAOColor.DEAD_COLOR));
    	}
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

        GuiYesNo guiyesno = new GuiYesNo(this, I18n.format("deathScreen.quit.confirm", new Object[0]), "", data);
        this.mc.displayGuiScreen(guiyesno);
        guiyesno.func_146350_a(20);
        if (id == SAOID.ALERT) {
            gameOver.confirmClicked(false, 0);
        }
    }

    protected void backgroundClicked(int cursorX, int cursorY, int button) {
        if (!mc.theWorld.getWorldInfo().isHardcoreModeEnabled()) {
            if (!((SAOIngameGUI) this.mc.ingameGUI).backgroundClicked(cursorX, cursorY, button)) {
                this.mc.thePlayer.respawnPlayer();
                this.mc.displayGuiScreen((GuiScreen)null);
                mc.setIngameFocus();
            }
        }
        else if (this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled()) {
            if (!((SAOIngameGUI) this.mc.ingameGUI).backgroundClicked(cursorX, cursorY, button)) {
                this.mc.theWorld.sendQuittingDisconnectingPacket();
                this.mc.loadWorld((WorldClient)null);
                this.mc.displayGuiScreen(new GuiMainMenu());
            }
        }
        else {
            this.mc.theWorld.sendQuittingDisconnectingPacket();
            this.mc.loadWorld((WorldClient)null);
            this.mc.displayGuiScreen(new GuiMainMenu());        	
        }
    }

    @Override
    public void close() {
        super.close();
        CURSOR_STATUS = oldCursorStatus;
    }

}