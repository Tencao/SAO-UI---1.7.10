package com.tencao.saoui;

import com.tencao.saoui.ui.SAOScreenGUI;
import com.tencao.saoui.util.SAOOption;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraftforge.client.GuiIngameForge;

@SideOnly(Side.CLIENT)
public class SAORenderHandler {
	
	private final Minecraft mc = Minecraft.getMinecraft();
	private boolean replaceGUI = SAOMod.replaceGUI;
	
	@SubscribeEvent
    public void checkingameGUI(TickEvent.RenderTickEvent e){
        boolean b = mc.ingameGUI instanceof SAOIngameGUI;
        if (mc.ingameGUI != null && SAOOption.DEFAULT_UI.value == b)
            mc.ingameGUI = b ? new GuiIngameForge(mc) : new SAOIngameGUI(mc);
    }
	
	@SubscribeEvent
	public void checkGuiInstance(TickEvent.RenderTickEvent e){
        if ((mc.currentScreen == null) && (mc.inGameHasFocus)) replaceGUI = true;
		else if (replaceGUI) {
            if (mc.currentScreen != null && !(mc.currentScreen instanceof SAOScreenGUI)) {
                if (SAOMod.REPLACE_GUI_DELAY > 0) SAOMod.REPLACE_GUI_DELAY--;
                else if ((mc.currentScreen instanceof GuiIngameMenu) || ((mc.currentScreen instanceof GuiInventory) && (!SAOOption.DEFAULT_INVENTORY.value))) {
                    final boolean inv = (mc.currentScreen instanceof GuiInventory);

                    mc.currentScreen.mc = mc;
                    if (mc.playerController.isInCreativeMode() && mc.currentScreen instanceof GuiInventory)
                         mc.displayGuiScreen(new GuiContainerCreative(mc.thePlayer));
                    else try {
						SAOSound.play(mc, SAOSound.ORB_DROPDOWN);
						mc.displayGuiScreen(new SAOIngameMenuGUI((GuiInventory) (inv ? mc.currentScreen : null)));
						replaceGUI = false;
					} catch (NullPointerException ignored) {
					}
                } else if ((mc.currentScreen instanceof GuiGameOver) && (!SAOOption.DEFAULT_DEATH_SCREEN.value)) {
                    mc.currentScreen.mc = mc;

                    if (mc.ingameGUI instanceof SAOIngameGUI){
                    	try {
                            mc.displayGuiScreen(null);
                    		mc.displayGuiScreen(new SAODeathGUI((GuiGameOver) mc.currentScreen));
                    		replaceGUI = false;
                    	}
                    	catch (NullPointerException ignored) {
                    	}
                    }
                }
            }
		}
	}
}
