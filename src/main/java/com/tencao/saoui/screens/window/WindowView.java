package com.tencao.saoui.screens.window;

import com.tencao.saoui.screens.buttons.ConfirmGUI;
import com.tencao.saoui.screens.buttons.ActionHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class WindowView extends ScreenGUI {

    private final int windowWidth, windowHeight;

    private WindowView(int width, int height) {
        super();
        windowWidth = width;
        windowHeight = height;
    }

    public static WindowView viewMessage(final String username, final String message) {
        return new WindowView(200, 40) {

            @Override
            protected Window createWindow(int width, int height) {
                return new MessageGUI(this, 0, 0, width, height, message, username);
            }

        };
    }

    public static WindowView viewConfirm(final String title, final String message, final ActionHandler handler) {
        return new WindowView(200, 60) {
            @Override
            protected Window createWindow(int width, int height) {
                return new ConfirmGUI(this, 0, 0, width, height, title, message, handler);
            }
        };
    }

    @Override
    protected void init() {
        super.init();
        elements.add(createWindow(windowWidth, windowHeight));
    }

    protected abstract Window createWindow(int width, int height);

    public final Window getWindow() {
        return (Window) elements.get(0);
    }

    @Override
    public int getX(boolean relative) {
        return super.getX(relative) + (width - windowWidth) / 2;
    }

    @Override
    public int getY(boolean relative) {
        return super.getY(relative) + (height - windowHeight) / 2;
    }

    @Override
    public void drawScreen(int cursorX, int cursorY, float f) {
        drawDefaultBackground();

        super.drawScreen(cursorX, cursorY, f);
    }

    @Override
    protected void backgroundClicked(int cursorX, int cursorY, int button) {
    }

}
