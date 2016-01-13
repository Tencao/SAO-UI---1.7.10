package com.tencao.saoui.commands;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.util.IChatComponent;

/**
 * Created by Tencao on 09/01/2016.
 * Compatability for 1.7.10
 */
public class CommandTypeCompat extends Event {

    public IChatComponent message;
    /**
     * Introduced in 1.8:
     * 0 : Standard Text Message
     * 1 : 'System' message, displayed as standard text.
     * 2 : 'Status' message, displayed above action bar, where song notifications are.
     */
    public final byte type;
    public CommandTypeCompat(byte type, IChatComponent message)
    {
        this.type = type;
        this.message = message;
    }

}
