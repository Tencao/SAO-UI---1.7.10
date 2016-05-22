package com.saomc.screens.window.ui;

import com.saomc.screens.ParentElement;
import com.saomc.screens.buttons.ButtonGUI;
import com.saomc.screens.menu.Categories;
import com.saomc.util.IconCore;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.stats.Achievement;

@SideOnly(Side.CLIENT)
public class AchievementList extends ButtonGUI {

    private final Achievement achievement;

    public AchievementList(ParentElement gui, int xPos, int yPos, int w, int h, Achievement ach0) {
        super(gui, Categories.QUEST, xPos, yPos, w, h, ach0.getStatName().getFormattedText(), IconCore.QUEST);
        achievement = ach0;
    }

    public AchievementList(ParentElement gui, int xPos, int yPos, int w, Achievement ach0) {
        this(gui, xPos, yPos, w, 20, ach0);
    }

    public AchievementList(ParentElement gui, int xPos, int yPos, Achievement ach0) {
        this(gui, xPos, yPos, 150, ach0);
    }

    public Achievement getAchievement() {
        return achievement;
    }

}
