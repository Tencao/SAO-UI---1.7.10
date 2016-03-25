package com.tencao.saoui.screens.buttons;

import com.tencao.saoui.screens.menu.Categories;
import com.tencao.saoui.util.IconCore;
import com.tencao.saoui.util.OptionCore;
import com.tencao.saoui.screens.ParentElement;

/**
 * Created by Tencao on 09/01/2016.
 */
public class OptionButton extends ButtonGUI {
    private final OptionCore option;

    public OptionButton(ParentElement gui, int xPos, int yPos, OptionCore option) {
        super(gui, option.isCategory ? Categories.OPT_CAT : Categories.OPTION, xPos, yPos, option.toString(), IconCore.OPTION);
        this.highlight = option.getValue();
        this.option = option;
    }

    public OptionCore getOption() {
        return this.option;
    }

    public void action() {
        this.highlight = this.option.flip();
    }
}
