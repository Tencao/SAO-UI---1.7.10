package com.tencao.saoui.screens.buttons;

import com.tencao.saoui.screens.menu.Categories;
import com.tencao.saoui.util.IconCore;
import com.tencao.saoui.screens.ParentElement;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;

@SideOnly(Side.CLIENT)
public class ButtonState extends ButtonGUI {

    private final StateHandler state;

    public ButtonState(ParentElement gui, Categories saoID, int xPos, int yPos, int w, int h, String string, IconCore iconCore, StateHandler handler) {
        super(gui, saoID, xPos, yPos, w, h, string, iconCore);
        state = handler;
    }

    public ButtonState(ParentElement gui, Categories saoID, int xPos, int yPos, int w, String string, IconCore iconCore, StateHandler handler) {
        this(gui, saoID, xPos, yPos, w, 20, string, iconCore, handler);
    }

    public ButtonState(ParentElement gui, Categories saoID, int xPos, int yPos, String string, IconCore iconCore, StateHandler handler) {
        this(gui, saoID, xPos, yPos, 100, string, iconCore, handler);
    }

    public void update(Minecraft mc) {
        if (state != null) enabled = state.isStateEnabled(mc, this);

        super.update(mc);
    }

}
