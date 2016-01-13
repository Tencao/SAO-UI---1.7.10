package com.tencao.saoui.ui;

import com.tencao.saoui.util.SAOID;
import com.tencao.saoui.util.SAOIcon;
import com.tencao.saoui.util.SAOOption;
import com.tencao.saoui.util.SAOParentGUI;

/**
 * Created by Tencao on 09/01/2016.
 */
public class OptionButton extends SAOButtonGUI {
    private final SAOOption option;

    public OptionButton(SAOParentGUI gui, int xPos, int yPos, SAOOption option) {
        super(gui, option.isCategory ? SAOID.OPT_CAT : SAOID.OPTION, xPos, yPos, option.toString(), SAOIcon.OPTION);
        this.highlight = option.getValue();
        this.option = option;
    }

    public SAOOption getOption() {
        return this.option;
    }

    public void action() {
        this.highlight = this.option.flip();
    }
}
