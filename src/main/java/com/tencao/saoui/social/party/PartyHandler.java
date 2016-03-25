package com.tencao.saoui.social.party;

import com.tencao.saoui.screens.ParentElement;
import com.tencao.saoui.screens.buttons.ButtonState;
import com.tencao.saoui.screens.buttons.StateHandler;
import com.tencao.saoui.screens.menu.Categories;
import com.tencao.saoui.util.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;

@SideOnly(Side.CLIENT)
public class PartyHandler extends ButtonState {

    private PartyHandler(ParentElement gui, Categories saoID, int xPos, int yPos, int w, int h, String string, IconCore iconCore) {
        super(gui, saoID, xPos, yPos, w, h, string, iconCore, new SAOPartyStateHandler(saoID));
    }

    private PartyHandler(ParentElement gui, Categories saoID, int xPos, int yPos, int w, String string, IconCore iconCore) {
        this(gui, saoID, xPos, yPos, w, 20, string, iconCore);
    }

    public PartyHandler(ParentElement gui, Categories saoID, int xPos, int yPos, String string, IconCore iconCore) {
        this(gui, saoID, xPos, yPos, 100, string, iconCore);
    }

    private static final class SAOPartyStateHandler implements StateHandler {

        private final Categories id;

        private SAOPartyStateHandler(Categories id) {
            this.id = id;
        }

        public boolean isStateEnabled(Minecraft mc, ButtonState button) {
            return PartyHelper.instance().shouldHighlight(id);
        }

    }

}
