package com.tencao.saoui.util;

import com.tencao.saoui.SAOMod;
import com.tencao.saoui.SAOWindowViewGUI;
import com.tencao.saoui.commands.Command;
import com.tencao.saoui.commands.CommandType;
import com.tencao.saoui.ui.SAOConfirmGUI;
import com.tencao.saoui.ui.SAOWindowGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;

import java.util.stream.Stream;

public class PartyHelper {
    private static PartyHelper instance = new PartyHelper();
    private String[] party;

    private PartyHelper() {

    }

    public static PartyHelper instance() {
        return instance;
    }

    public void receiveInvite(Minecraft mc, String username, String... args) {
        if (party == null || party.length <= 1) {
            final GuiScreen keepScreen = mc.currentScreen;
            final boolean ingameFocus = mc.inGameHasFocus;

            final String text = StatCollector.translateToLocalFormatted(ConfigHandler._PARTY_INVITATION_TEXT, username);

            mc.displayGuiScreen(SAOWindowViewGUI.viewConfirm(ConfigHandler._PARTY_INVITATION_TITLE, text, (element, action, data) -> {
                final SAOID id = element.ID();

                if (id == SAOID.CONFIRM) {
                    if (args.length > 0) {
                        party = new String[args.length + 1];
                        System.arraycopy(args, 0, party, 0, args.length);
                        party[party.length - 1] = StaticPlayerHelper.getName(mc);
                    } else party = null;
                    mc.thePlayer.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("ptJoin")));

                    new Command(CommandType.CONFIRM_INVITE_PARTY, username).send(mc);
                } else new Command(CommandType.CANCEL_INVITE_PARTY, username).send(mc);

                mc.displayGuiScreen(keepScreen);

                if (ingameFocus) mc.setIngameFocus();
                else mc.setIngameNotInFocus();
            }));

            if (ingameFocus) mc.setIngameNotInFocus();
        }
    }

    public String[] listMembers() {
        return party;
    }

    public boolean isMember(String username) {
        return username.equals(StaticPlayerHelper.getName(Minecraft.getMinecraft())) || hasParty() && Stream.of(party).anyMatch(member -> member.equals(username));
    }

    public boolean isLeader(String username) {
        return hasParty() && party[0].equals(username);
    }

    private void addPlayer(Minecraft mc, String username) {
        create(mc);
        if (!isMember(username)) {
            final String[] resized = new String[party.length + 1];
            System.arraycopy(party, 0, resized, 0, party.length);
            resized[party.length] = username;
            party = resized;
            if (isLeader(StaticPlayerHelper.getName(mc))) {
                Stream.of(party).filter(pl -> !pl.equals(StaticPlayerHelper.getName(mc))).forEach(member -> new Command(CommandType.UPDATE_PARTY, member, '+' + username).send(mc));
                Stream.of(party).filter(pl -> !pl.equals(StaticPlayerHelper.getName(mc))).forEach(member -> new Command(CommandType.UPDATE_PARTY, username, '+' + member).send(mc));
            }
            mc.thePlayer.addChatMessage(new ChatComponentText(StatCollector.translateToLocalFormatted("ptJoin", username)));
        }
    }

    private void removePlayer(Minecraft mc, String username) {
        if (isMember(username)) { // TODO: kick member
            final String[] resized = new String[party.length - 1];
            int index = 0;

            for (final String member : party) if (!member.equals(username)) resized[index++] = member;

            if (resized.length > 1) {
                party = resized;
                if (isLeader(StaticPlayerHelper.getName(mc))) Stream.of(party).filter(pl -> !pl.equals(StaticPlayerHelper.getName(mc))).forEach(member -> new Command(CommandType.UPDATE_PARTY, member, '-' + username).send(mc));
            } else party = null;
            mc.thePlayer.addChatMessage(new ChatComponentText(StatCollector.translateToLocalFormatted("ptLeft", username)));
        }
    }

    public void receiveUpdate(Minecraft mc, String username, String[] args) {
        if (isLeader(username)) {
            for(String a: args) {
                if (a.charAt(0) == '+') addPlayer(mc, a.substring(1));
                else if (a.charAt(0) == '-') removePlayer(mc, a.substring(1));
            }
        }
    }

    public void create(Minecraft mc) {
        if (hasParty()) return;
        party = new String[]{StaticPlayerHelper.getName(mc)};
    }

    public void invite(Minecraft mc, String username) {
        if (!isMember(username))
            new Command(CommandType.INVITE_PARTY, username, hasParty() ? party[0] : StaticPlayerHelper.getName(mc)).send(mc);
    }

    public void sendDissolve(Minecraft mc) {
        if (hasParty()) {
            if (isLeader(StaticPlayerHelper.getName(mc))) {
                Stream.of(party).skip(1).forEach(member -> new Command(CommandType.DISSOLVE_PARTY, member).send(mc));
                mc.thePlayer.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("ptDissolve")));
            }
            else {
                new Command(CommandType.DISSOLVE_PARTY, party[0]).send(mc);
                mc.thePlayer.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("ptLeave")));
            }
        }

        party = null;
    }

    public void receiveDissolve(Minecraft mc, String username) {
        if (isLeader(StaticPlayerHelper.getName(mc))) removePlayer(mc, username);
        else if (isLeader(username)) {
            final SAOWindowGUI window = SAOMod.getWindow(mc);

            if (window != null && window.getTitle().equals(ConfigHandler._PARTY_INVITATION_TITLE) && window instanceof SAOConfirmGUI)
                ((SAOConfirmGUI) window).cancel();

            party = null;
            mc.thePlayer.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("ptLeave")));
        }
    }

    public void receiveConfirmation(Minecraft mc, String username, String... args) { // Keeping args for later (will be needed for auth/PT UUID system)
        create(mc);
        if (isLeader(StaticPlayerHelper.getName(mc)) && !isMember(username)) addPlayer(mc, username);
        else new Command(CommandType.DISSOLVE_PARTY, username).send(mc);
    }

    public boolean hasParty() {
        return party != null;
    }

    public boolean shouldHighlight(SAOID id) {
        return id.equals(SAOID.DISSOLVE) ? isEffective() : id.equals(SAOID.INVITE_LIST) && (!isEffective() || isLeader(StaticPlayerHelper.getName(Minecraft.getMinecraft())));
    }

    public boolean isEffective() {
        return hasParty() && party.length > 1;
    }
}
