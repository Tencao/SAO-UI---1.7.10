package com.tencao.saoui.screens.buttons;

import com.tencao.saoui.screens.menu.Categories;
import com.tencao.saoui.screens.ParentElement;
import com.tencao.saoui.util.Skills;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;

public class SkillButton extends ButtonGUI {
    private final Skills skill;

    public SkillButton(ParentElement gui, int xPos, int yPos, Skills skill) {
        super(gui, Categories.SKILL, xPos, yPos, skill.toString(), skill.icon, skill.shouldHighlight());
        this.skill = skill;
    }

    public void action(Minecraft mc, GuiInventory parent) {
        this.skill.activate(mc, parent);
        this.highlight = skill.shouldHighlight();
    }
}
