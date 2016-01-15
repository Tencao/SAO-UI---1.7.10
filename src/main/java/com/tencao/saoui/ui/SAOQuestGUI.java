package com.tencao.saoui.ui;

import com.tencao.saoui.util.SAOID;
import com.tencao.saoui.util.SAOIcon;
import com.tencao.saoui.util.SAOParentGUI;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.stats.Achievement;

@SideOnly(Side.CLIENT)
public class SAOQuestGUI extends SAOButtonGUI {

    private final Achievement achievement;

    public SAOQuestGUI(SAOParentGUI gui, int xPos, int yPos, int w, int h, Achievement ach0) {
        super(gui, SAOID.QUEST, xPos, yPos, w, h, ach0.func_150951_e().getFormattedText(), SAOIcon.QUEST);
        achievement = ach0;
    }

    public SAOQuestGUI(SAOParentGUI gui, int xPos, int yPos, int w, Achievement ach0) {
        this(gui, xPos, yPos, w, 20, ach0);
    }

    public SAOQuestGUI(SAOParentGUI gui, int xPos, int yPos, Achievement ach0) {
        this(gui, xPos, yPos, 150, ach0);
    }

    public Achievement getAchievement() {
        return achievement;
    }

}
