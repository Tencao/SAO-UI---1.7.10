package com.tencao.saoui.colorstates;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ColorCursor {

    private static final long STATE_TIME = 24 * 60 * 60 * 20; // DAY IN TICKS

    private ColorState colorState;
    private long downgradeTime;

    public ColorCursor(ColorState defaultState, boolean set) {
        colorState = defaultState;
        if (set) set(defaultState);
        else downgradeTime = 0;
    }

    public ColorCursor() {
        this(ColorState.INNOCENT, false);
    }

    public final void update() {
        if (downgradeTime == 0) colorState = ColorState.INNOCENT;
        else downgradeTime--;
    }

    public final void set(ColorState state) {
        if (state.ordinal() >= colorState.ordinal()) {
            colorState = state;
            downgradeTime = STATE_TIME;
        }
    }

    public final ColorState get() {
        return colorState;
    }

}