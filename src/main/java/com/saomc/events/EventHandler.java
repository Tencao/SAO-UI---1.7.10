package com.saomc.events;

import com.saomc.SAOCore;
import com.saomc.SoundCore;
import com.saomc.commands.Command;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

@SideOnly(Side.CLIENT)
public class EventHandler {

    private static final Minecraft mc = Minecraft.getMinecraft();

    static void nameNotification(ClientChatReceivedEvent e){
        if (!(mc.currentScreen instanceof GuiConnecting) && e.message.getUnformattedTextForChat().contains(mc.thePlayer.getDisplayName()))
            SoundCore.play(mc, SoundCore.MESSAGE);
    }

    static void abilityCheck() {
        if (mc.thePlayer == null) {
            SAOCore.IS_SPRINTING = false;
            SAOCore.IS_SNEAKING = false;
        } else if (mc.inGameHasFocus) {
            if (SAOCore.IS_SPRINTING) KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
            if (SAOCore.IS_SNEAKING) KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
        }
    }

    static void chatCommand(ClientChatReceivedEvent evt) {
        if (!(mc.currentScreen instanceof GuiConnecting) && Command.processCommand(evt.message.getUnformattedText())) evt.setCanceled(true);// TODO: add pm feature and PT chat
    }

}
