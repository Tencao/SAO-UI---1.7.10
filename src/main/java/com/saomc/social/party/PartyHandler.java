package com.saomc.social.party;

import com.saomc.screens.ParentElement;
import com.saomc.screens.buttons.ButtonState;
import com.saomc.screens.buttons.StateHandler;
import com.saomc.screens.menu.Categories;
import com.saomc.util.IconCore;
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
