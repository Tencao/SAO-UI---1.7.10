package com.tencao.saoui.screens.window;

import com.tencao.saoui.events.ConfigHandler;
import com.tencao.saoui.screens.LabelGUI;
import com.tencao.saoui.screens.TextGUI;
import com.tencao.saoui.screens.ParentElement;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.util.StatCollector;

@SideOnly(Side.CLIENT)
public class MessageGUI extends Window {

    private final TextGUI textText;
    private final LabelGUI fromLable;

    public MessageGUI(ParentElement gui, int xPos, int yPos, int w, int h, String text, String from) {
        super(gui, xPos, yPos, w, h, ConfigHandler._MESSAGE_TITLE);
        final String fromString = StatCollector.translateToLocalFormatted(ConfigHandler._MESSAGE_FROM, from);

        elements.add(textText = new TextGUI(this, 0, 0, text, width));
        elements.add(fromLable = new LabelGUI(this, 0, 0, fromString, WindowAlign.RIGHT));
        textText.visibility = 0;
    }

    public final String getText() {
        return textText.getText();
    }

    public final void setText(String text) {
        textText.setText(text);
    }

    public final String getSender() {
        return fromLable.caption;
    }

    public final void setSender(String sender) {
        fromLable.caption = sender;
    }

    protected int getSize() {
        return Math.max(super.getSize() - 20, 40);
    }

    public boolean mouseReleased(Minecraft mc, int cursorX, int cursorY, int button) {
        if (button == 0) {
            if (textText.visibility < 1) {
                textText.visibility = 1;
            } else {
                mc.displayGuiScreen(null);
                mc.setIngameFocus();
            }
        }

        return super.mouseReleased(mc, cursorX, cursorY, button);
    }

}
