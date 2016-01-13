package com.tencao.saoui.ui;

import com.tencao.saoui.util.SAOID;
import com.tencao.saoui.util.SAOParentGUI;
import com.tencao.saoui.util.SAOSkill;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;

public class SkillButton extends SAOButtonGUI {
    private final SAOSkill skill;

    public SkillButton(SAOParentGUI gui, int xPos, int yPos, SAOSkill skill) {
        super(gui, SAOID.SKILL, xPos, yPos, skill.toString(), skill.icon, skill.shouldHighlight());
        this.skill = skill;
    }

    public void action(Minecraft mc, GuiInventory parent) {
        this.skill.activate(mc, parent);
        this.highlight = skill.shouldHighlight();
    }
}
