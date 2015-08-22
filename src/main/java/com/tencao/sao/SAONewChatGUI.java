package com.tencao.sao;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tencao.sao.util.SAOCommand;
import com.tencao.sao.util.SAOOption;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IChatComponent;

@SideOnly(Side.CLIENT)
public class SAONewChatGUI extends GuiNewChat {

    private static final String[] kills = {
            "death.fell.assist",
            "death.fell.assist.item",
            "death.fell.finish",
            "death.fell.finish.item",
            "death.attack.inFire.player",
            "death.attack.onFire.player",
            "death.attack.lava.player",
            "death.attack.drown.player",
            "death.attack.cactus.player",
            "death.attack.explosion.player",
            "death.attack.mob",
            "death.attack.player",
            "death.attack.player.item",
            "death.attack.arrow",
            "death.attack.arrow.item",
            "death.attack.fireball",
            "death.attack.fireball.item",
            "death.attack.thrown",
            "death.attack.thrown.item",
            "death.attack.indirectMagic",
            "death.attack.indirectMagic.item",
            "death.attack.thorns"
    };

	private final SAOIngameGUI parent;
	private final List<String> input;
	private final Minecraft minecraft;
	private final GuiNewChat oldChat;

	public SAONewChatGUI(SAOIngameGUI gui, Minecraft mc, GuiNewChat chat) {
		super(mc);
		parent = gui;
		input = new ArrayList<String>();
		minecraft = mc;
		oldChat = chat;
	}

    @Override
	public void printChatMessageWithOptionalDeletion(IChatComponent chat, int flag) {
        final String text = chat.getUnformattedText();

        for (final String kill : kills) {
            input.clear();

            if ((reformat(text, I18n.format(kill), input)) && (input.size() == (kill.endsWith(".item") ? 3 : 2))) {
                final String username = SAOMod.unformatName(input.get(0));
                final String killername = SAOMod.unformatName(input.get(1));

                if (SAOMod.isOnline(minecraft, username)) {
                    final EntityPlayer killer = SAOMod.findOnlinePlayer(minecraft, killername);

                    if (killer != null) {
                        SAOMod.onKillPlayer(killer);
                    }
                }
            }
        }
        
		if (false/*SAOOption.CLIENT_CHAT_PACKETS.value*/) {
			final String format0 = I18n.format("commands.message.display.incoming", "%s", "%s");
			final String format1 = I18n.format("commands.message.display.outgoing", "%s", "%s");
			
			input.clear();
            if ((reformat(text, format0, input)) && (input.size() == 2)) {
                final String username = SAOMod.unformatName(input.get(0));
                final String message = input.get(1);

                if (SAOMod.DEBUG) {
                    System.out.println("[SAO] " + text.replace(input.get(0), username));
                }

                final SAOCommand command = SAOCommand.getCommand(message);

                if (command != null) {
                    SAOMod.receiveSAOCommand(minecraft, command, username, command.getContent(message));
                } else {
                    parent.onMessage(username, message);
                }
            } else if (!reformat(text, format1, input)) {
                super.printChatMessageWithOptionalDeletion(chat, flag);
            }
        } else {
            super.printChatMessageWithOptionalDeletion(chat, flag);
        }

        oldChat.printChatMessageWithOptionalDeletion(chat, flag);
	}

	static boolean reformat(final String string, final String format, final List<String> output) {
		int formatIndex = 0;
		int stringIndex = 0;
		
		while (formatIndex < format.length()) {
			final char formatChar = format.charAt(formatIndex);
			
			if ((formatChar == '%') && (formatIndex + 1 < format.length()) && (
				(format.charAt(formatIndex + 1) == 's') ||
				(format.charAt(formatIndex + 1) == 'd') ||
				(format.charAt(formatIndex + 1) == 'i') ||
				(format.charAt(formatIndex + 1) == 'f') ||
				(format.charAt(formatIndex + 1) == 'c'))) {
				
				formatIndex += 2;
				String input = "";
				
				final char endChar = formatIndex < format.length()? format.charAt(formatIndex) : '\0';
				
				while (stringIndex < string.length()) {
					final char messageChar = string.charAt(stringIndex);
					
					if (messageChar == endChar) {
						break;
					} else {
						input += messageChar;
						stringIndex++;
					}
				}
				
				output.add(input);
			} else
			if (stringIndex < string.length()) {
				final char messageChar = string.charAt(stringIndex);
				
				if (messageChar == formatChar) {
					formatIndex++;
					stringIndex++;
				} else {
					break;
				}
			} else {
				break;
			}
		}
		
		return (stringIndex >= string.length()) && (formatIndex >= format.length());
	}

}
