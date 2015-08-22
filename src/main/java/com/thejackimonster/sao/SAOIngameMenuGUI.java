package com.thejackimonster.sao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBasic;
import net.minecraft.stats.StatFileWriter;

import com.thejackimonster.sao.ui.SAOButtonGUI;
import com.thejackimonster.sao.ui.SAOCharacterView;
import com.thejackimonster.sao.ui.SAOContainerGUI;
import com.thejackimonster.sao.ui.SAOElementGUI;
import com.thejackimonster.sao.ui.SAOFriendGUI;
import com.thejackimonster.sao.ui.SAOFriendsGUI;
import com.thejackimonster.sao.ui.SAOIconGUI;
import com.thejackimonster.sao.ui.SAOInventoryGUI;
import com.thejackimonster.sao.ui.SAOLabelGUI;
import com.thejackimonster.sao.ui.SAOListGUI;
import com.thejackimonster.sao.ui.SAOMenuGUI;
import com.thejackimonster.sao.ui.SAOPanelGUI;
import com.thejackimonster.sao.ui.SAOPartyGUI;
import com.thejackimonster.sao.ui.SAOQuestGUI;
import com.thejackimonster.sao.ui.SAOScreenGUI;
import com.thejackimonster.sao.ui.SAOSlotGUI;
import com.thejackimonster.sao.ui.SAOStateButtonGUI;
import com.thejackimonster.sao.ui.SAOTextGUI;
import com.thejackimonster.sao.ui.SAOVLineGUI;
import com.thejackimonster.sao.ui.SAOWindowGUI;
import com.thejackimonster.sao.util.SAOAction;
import com.thejackimonster.sao.util.SAOActionHandler;
import com.thejackimonster.sao.util.SAOAlign;
import com.thejackimonster.sao.util.SAOColor;
import com.thejackimonster.sao.util.SAOCommand;
import com.thejackimonster.sao.util.SAOInventory;
import com.thejackimonster.sao.util.SAOOption;
import com.thejackimonster.sao.util.SAOParentGUI;
import com.thejackimonster.sao.util.SAOID;
import com.thejackimonster.sao.util.SAOIcon;
import com.thejackimonster.sao.util.SAOSkill;
import com.thejackimonster.sao.util.SAOSub;
import com.thejackimonster.sao.util.SAOStateHandler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SAOIngameMenuGUI extends SAOScreenGUI {

	private final List<Entry<SAOID, SAOMenuGUI>> menus;

	private int flowY;

	private SAOMenuGUI sub;

	private SAOPanelGUI info;
	private SAOLabelGUI infoCaption;
	private SAOTextGUI infoText;

	private final int openSpecial;

	public SAOIngameMenuGUI(int mode) {
		super();
		menus = new ArrayList<Entry<SAOID, SAOMenuGUI>>();
		openSpecial = mode;
		info = null;
	}

	public SAOIngameMenuGUI() {
		this(0);
	}

	protected void init() {
		menus.clear();
		
		SAOIconGUI action1, action2, action;
		
		elements.add(action1 = new SAOIconGUI(this, SAOID.PROFILE, 0, 0, SAOIcon.PROFILE));
		elements.add(action2 = new SAOIconGUI(action1, SAOID.SOCIAL, 0, 24, SAOIcon.SOCIAL));
		elements.add(action = new SAOIconGUI(action2, SAOID.MESSAGE, 0, 24, SAOIcon.MESSAGE));
		elements.add(action = new SAOIconGUI(action, SAOID.NAVIGATION, 0, 24, SAOIcon.NAVIGATION));
		elements.add(action = new SAOIconGUI(action, SAOID.SETTINGS, 0, 24, SAOIcon.SETTINGS));
		
		if (openSpecial == 1) {
			openMenu(action1, action1.ID());
		} else
		if (openSpecial == 2) {
			openMenu(action2, action2.ID());
		}
		
		flowY = -height;
	}

	public int getX(boolean relative) {
		return width * 2 / 5;
	}

	public int getY(boolean relative) {
		return flowY;
	}

	public void updateScreen() {
		super.updateScreen();
		
		if (flowY < height / 2) {
			flowY = (flowY + height / 2 - 32) / 2;
		}
	}

	protected void keyTyped(char ch, int key) {
        if (((openSpecial == 1) && (key == mc.gameSettings.keyBindInventory.getKeyCode())) ||
        	((openSpecial == 2) && (key == mc.gameSettings.keyBindPlayerList.getKeyCode()))) {
            super.keyTyped(ch, Keyboard.KEY_ESCAPE);
        } else {
        	super.keyTyped(ch, key);
        }
    }

	public void actionPerformed(SAOElementGUI element, SAOAction action, int data) {
		super.actionPerformed(element, action, data);
		
		final SAOID id = element.ID();
		
		if (id.menuFlag) {
			if (isMenuOpen(id)) {
				closeMenu(element, id);
			} else {
				openMenu(element, id);
			}
		} else if (id != SAOID.NONE) {
			action(element, id, action, data);
		}
	}

	private boolean isMenuOpen(SAOID id) {
		for (final Entry<SAOID, SAOMenuGUI> entry : menus) {
			if (entry.getKey() == id) {
				return true;
			}
		}
		
		return false;
	}

	private void action(SAOElementGUI element, SAOID id, SAOAction action, int data) {
		if (id == SAOID.LOGOUT) {
			if (SAOOption.LOGOUT.value) {
				element.enabled = false;
				mc.theWorld.sendQuittingDisconnectingPacket();
				
				mc.loadWorld(null);
				mc.displayGuiScreen(new GuiMainMenu());
			}
		} else
		if (id == SAOID.HELP) {
			mc.displayGuiScreen(new GuiIngameMenu());
		} else
		if ((id == SAOID.OPTION) && (element instanceof SAOButtonGUI)) {
			final SAOButtonGUI button = (SAOButtonGUI) element;
			SAOOption option0 = null;
			
			for (final SAOOption option : SAOOption.values()) {
				if (button.caption.equals(option.toString())) {
					option0 = option;
					break;
				}
			}
			
			if (option0 == null) {
				mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
			} else if (element.parent instanceof SAOContainerGUI) {
				final SAOContainerGUI parent = (SAOContainerGUI) element.parent;
				
				option0.value = !option0.value;
				button.highlight = option0.value;
				
				switch (option0) {
				case DEFAULT_UI:
					for (final SAOElementGUI element0 : parent.elements) {
						if (element0 instanceof SAOButtonGUI) {
							final SAOButtonGUI button0 = (SAOButtonGUI) element0;
							
							if (button0.caption.equals(SAOOption.CROSS_HAIR.name)) {
								SAOOption.CROSS_HAIR.value = option0.value;
								button0.highlight = SAOOption.CROSS_HAIR.value;
								button0.enabled = !option0.value;
								
								SAOMod.setOption(SAOOption.CROSS_HAIR);
							}
						}
					}
					
					break;
				default:
					break;
				}
				
				SAOMod.setOption(option0);
				SAOMod.saveAllOptions();
			}
		} else
		if ((id == SAOID.MESSAGE) && (mc.ingameGUI instanceof SAOIngameGUI)) {
			((SAOIngameGUI) mc.ingameGUI).viewMessageAuto();
		} else
		if ((id == SAOID.MESSAGE_BOX) && (element.parent instanceof SAOMenuGUI) && (((SAOMenuGUI) element.parent).parent instanceof SAOFriendGUI)) {
			final String username = ((SAOFriendGUI) ((SAOMenuGUI) element.parent).parent).caption;
			
			final String format = I18n.format("commands.message.usage", new Object[0]);
			final String cmd = format.substring(0, format.indexOf(' '));
			
			final String message = SAOJ8String.join(" ", cmd, username, "");
			
			mc.displayGuiScreen(new GuiChat(message));
		} else
		if ((id == SAOID.SKILL) && (element instanceof SAOButtonGUI)) {
			final SAOButtonGUI button = (SAOButtonGUI) element;
			
			switch (button.icon) {
			case CRAFTING:
				mc.displayGuiScreen(new GuiInventory(mc.thePlayer));
				break;
			case SPRINTING:
				SAOMod.IS_SPRINTING = !SAOMod.IS_SPRINTING;
				button.highlight = SAOMod.IS_SPRINTING;
				break;
			case SNEAKING:
				SAOMod.IS_SNEAKING = !SAOMod.IS_SNEAKING;
				button.highlight = SAOMod.IS_SNEAKING;
				break;
			default:
				break;
			}
		} else
		if ((id == SAOID.INVITE_PLAYER) && (element instanceof SAOButtonGUI)) {
			final String name = ((SAOButtonGUI) element).caption;
			
			if (!SAOMod.isPartyMember(name)) {
				SAOMod.inviteParty(mc, name);
			}
		} else
		if (id == SAOID.CREATE) {
			element.enabled = !SAOMod.createParty(mc, 2.5);
			
			if (!element.enabled) {
				mc.displayGuiScreen(null);
				mc.setIngameFocus();
			}
		} else
		if (id == SAOID.DISSOLVE) {
			element.enabled = false;
			
			final boolean isLeader = SAOMod.isPartyLeader(SAOMod.getName(mc));
			
			final String title = isLeader? SAOMod._PARTY_DISSOLVING_TITLE : SAOMod._PARTY_LEAVING_TITLE;
			final String text = isLeader? SAOMod._PARTY_DISSOLVING_TEXT : SAOMod._PARTY_LEAVING_TEXT;
			
			mc.displayGuiScreen(SAOWindowViewGUI.viewConfirm(title, text, new SAOActionHandler() {

				public void actionPerformed(SAOElementGUI element, SAOAction action, int data) {
					final SAOID id = element.ID();
					
					if (id == SAOID.CONFIRM) {
						SAOMod.dissolveParty(mc);
					}
					
					mc.displayGuiScreen(null);
					mc.setIngameFocus();
				}

			}));
		} else
		if ((id == SAOID.SLOT) && (element instanceof SAOSlotGUI) && (element.parent instanceof SAOInventoryGUI)) {
			final SAOSlotGUI slot = (SAOSlotGUI) element;
			final SAOInventoryGUI inventory = (SAOInventoryGUI) element.parent;
			
			final SAOInventory type = inventory.filter;
			final Container container = inventory.slots;
			final ItemStack stack = slot.getStack();
			
			System.out.println(action + " " + data);
			
			if (stack != null) {
				if (action == SAOAction.LEFT_RELEASED) {
					final Slot current = findSwapSlot(container, slot.getSlot(), type);
					
					if ((current != null) && (current.slotNumber != slot.getSlotNumber())) {
						inventory.handleMouseClick(mc, slot.getSlot(), slot.getSlotNumber(), 0, 0);
						inventory.handleMouseClick(mc, current, current.slotNumber, 0, 0);
						inventory.handleMouseClick(mc, slot.getSlot(), slot.getSlotNumber(), 0, 0);
					}
				} else
				if (action == SAOAction.RIGHT_RELEASED) {
					inventory.handleMouseClick(mc, slot.getSlot(), slot.getSlotNumber(), 1, 4);
				} else
				if ((action == SAOAction.MIDDLE_RELEASED) ||
					((action == SAOAction.KEY_TYPED) && (data == mc.gameSettings.keyBindPickBlock.getKeyCode()))) {
					String caption = null;
					String text = "";
					
					for (final Object line : stack.getTooltip(mc.thePlayer, false)) {
						if (caption != null) {
							text += String.valueOf(line) + "\n";
						} else {
							caption = String.valueOf(line);
						}
					}
					
					setInfo(caption, text);
				} else
				if ((action == SAOAction.KEY_TYPED) && (data == mc.gameSettings.keyBindDrop.getKeyCode())) {
					inventory.handleMouseClick(mc, slot.getSlot(), slot.getSlotNumber(), 0, 4);
				}
			}
		} else
		if ((id == SAOID.QUEST) && (element instanceof SAOQuestGUI)) {
			final SAOQuestGUI quest = (SAOQuestGUI) element;
			final Achievement ach0 = quest.getAchievement();
			
			setInfo(quest.caption, ach0.getDescription());
		}
	}

	private Slot findSwapSlot(Container container, Slot swap, SAOInventory type) {
		if (type == SAOInventory.EQUIPMENT) {
			if (swap.slotNumber < 9) {
				return findEmptySlot(container, 9);
			} else {
				for (int i = 5; i < 9; i++) {
					if (container.getSlot(i).isItemValid(swap.getStack())) {
						return container.getSlot(i);
					}
				}
				
				return null;
			}
		} else
		if (type == SAOInventory.WEAPONS) {
			if (swap.slotNumber >= 36) {
				return findEmptySlot(container, 9);
			} else {
				return container.getSlot(36);
			}
		} else
		if (type == SAOInventory.ACCESSORY) {
			if (swap.slotNumber >= 36) {
				return findEmptySlot(container, 9);
			} else {
				return container.getSlot(37);
			}
		} else
		if (type == SAOInventory.ITEMS) {
			if (swap.slotNumber >= 36) {
				return findEmptySlot(container, 9);
			} else {
				return findEmptySlot(container, 38);
			}
		} else {
			return null;
		}
	}

	private Slot currentSlot(Container container) {
		return container.getSlotFromInventory(mc.thePlayer.inventory, mc.thePlayer.inventory.currentItem);
	}

	private Slot findEmptySlot(Container container, int startIndex) {
		for (int i = startIndex; i < container.inventorySlots.size(); i++) {
			final Slot slot = container.getSlot(i);
			
			if (!slot.getHasStack()) {
				return slot;
			}
		}
		
		return null;
	}

	private void setInfo(String caption, String text) {
		if (info != null) {
			if (infoCaption == null) {
				info.elements.add(infoCaption = new SAOLabelGUI(info, 15, 0, info.width - 15, caption, SAOAlign.LEFT));
				infoCaption.fontColor = SAOColor.DEFAULT_BOX_FONT_COLOR;
			} else {
				infoCaption.caption = caption;
			}
			
			if (infoText == null) {
				info.elements.add(infoText = new SAOTextGUI(info, 15, 0, text, info.width - 15));
				infoText.fontColor = SAOColor.DEFAULT_BOX_FONT_COLOR;
			} else {
				infoText.setText(text);
			}
		}
	}

	private void openMenu(SAOElementGUI element, SAOID id) {
		final int menuOffsetX = element.width + 14;
		final int menuOffsetY = element.height / 2;
		
		SAOMenuGUI menu = null;
		SAOMenuGUI subMenu = sub;
		
		if (id == SAOID.PROFILE) {
			menu = new SAOMenuGUI(element, menuOffsetX, menuOffsetY, 100, 60);
			
			menu.elements.add(new SAOButtonGUI(menu, SAOID.EQUIPMENT, 0, 0, "Equipment", SAOIcon.EQUIPMENT));
			menu.elements.add(new SAOButtonGUI(menu, SAOID.ITEMS, 0, 0, "Items", SAOIcon.ITEMS));
			menu.elements.add(new SAOButtonGUI(menu, SAOID.SKILLS, 0, 0, "Skills", SAOIcon.SKILLS));
			
			sub = SAOSub.createMainProfileSub(mc, element, -189, menuOffsetY);
			info = SAOSub.addInfo(sub);
			
			infoCaption = null;
			infoText = null;
			
			final String[] profile = SAOSub.addProfileContent(mc);
			
			setInfo(profile[0], profile[1]);
		} else
		if (id == SAOID.SOCIAL) {
			menu = new SAOMenuGUI(element, menuOffsetX, menuOffsetY, 100, 60);
			
			menu.elements.add(new SAOButtonGUI(menu, SAOID.GUILD, 0, 0, "Guild", SAOIcon.GUILD));
			menu.elements.add(new SAOButtonGUI(menu, SAOID.PARTY, 0, 0, "Party", SAOIcon.PARTY));
			menu.elements.add(new SAOButtonGUI(menu, SAOID.FRIENDS, 0, 0, "Friend", SAOIcon.FRIEND));
			
			sub = SAOSub.createSocialSub(mc, element, -189, menuOffsetY);
			info = SAOSub.addInfo(sub);
			
			infoCaption = null;
			infoText = null;
		} else
		if (id == SAOID.NAVIGATION) {
			menu = new SAOMenuGUI(element, menuOffsetX, menuOffsetY, 100, 60);
			
			menu.elements.add(new SAOButtonGUI(menu, SAOID.QUESTS, 0, 0, "Quest", SAOIcon.QUEST));
			menu.elements.add(new SAOButtonGUI(menu, SAOID.FIELD_MAP, 0, 0, "Field Map", SAOIcon.FIELD_MAP));
			menu.elements.add(new SAOButtonGUI(menu, SAOID.DUNGEON_MAP, 0, 0, "Dungeon Map", SAOIcon.DUNGEON_MAP));
			
			sub = SAOSub.createNavigationSub(mc, element, -189, menuOffsetY);
			info = SAOSub.addInfo(sub);
			
			infoCaption = null;
			infoText = null;
		} else
		if (id == SAOID.SETTINGS) {
			menu = new SAOMenuGUI(element, menuOffsetX, menuOffsetY, 100, 60);
			
			menu.elements.add(new SAOButtonGUI(menu, SAOID.OPTIONS, 0, 0, "Option", SAOIcon.OPTION));
			menu.elements.add(new SAOButtonGUI(menu, SAOID.HELP, 0, 0, "Help", SAOIcon.HELP));
			menu.elements.add(new SAOStateButtonGUI(menu, SAOID.LOGOUT, 0, 0, SAOOption.LOGOUT.value? "Logout" : "", SAOIcon.LOGOUT, new SAOStateHandler() {
			
				public boolean isStateEnabled(Minecraft mc, SAOStateButtonGUI button) {
					if (SAOOption.LOGOUT.value) {
						if (button.caption.length() == 0) {
							button.caption = "Logout";
						}
					} else {
						if (button.caption.length() > 0) {
							button.caption = "";
						}
					}
					
					return button.enabled;
				}
			
			}));
		} else
		if (id == SAOID.OPTIONS) {
			menu = new SAOListGUI(element, menuOffsetX, menuOffsetY, 130, 100);
			
			menu.elements.add(new SAOButtonGUI(menu, SAOID.OPTION, 0, 0, "Options..", SAOIcon.HELP));
			
			for (final SAOOption option : SAOOption.values()) {
				final SAOButtonGUI button = new SAOButtonGUI(menu, SAOID.OPTION, 0, 0, option.toString(), SAOIcon.OPTION);
				
				button.highlight = option.value;
				
				if (option == SAOOption.CROSS_HAIR) {
					button.enabled = !SAOOption.DEFAULT_UI.value;
				}
				
				menu.elements.add(button);
			}
		} else
		if (id == SAOID.EQUIPMENT) {
			menu = new SAOMenuGUI(element, menuOffsetX, menuOffsetY, 100, 60);
			
			menu.elements.add(new SAOButtonGUI(menu, SAOID.WEAPONS, 0, 0, "Weapons", SAOIcon.EQUIPMENT));
			menu.elements.add(new SAOButtonGUI(menu, SAOID.EQUIPPED, 0, 0, "Equipped", SAOIcon.ARMOR));
			menu.elements.add(new SAOButtonGUI(menu, SAOID.ACCESSORY, 0, 0, "Accessory", SAOIcon.ACCESSORY));
		} else
		if (id == SAOID.PARTY) {
			menu = new SAOMenuGUI(element, menuOffsetX, menuOffsetY, 100, 60);
			
			menu.elements.add(new SAOPartyGUI(menu, SAOID.INVITE_LIST, 0, 0, "Invite", SAOIcon.INVITE, true));
			menu.elements.add(new SAOPartyGUI(menu, SAOID.CREATE, 0, 0, "Create", SAOIcon.CREATE, false));
			menu.elements.add(new SAOPartyGUI(menu, SAOID.DISSOLVE, 0, 0, "Dissolve", SAOIcon.CANCEL, true));
			
			sub = SAOSub.resetPartySub(mc, sub);
			info = SAOSub.addInfo(sub);
			
			infoCaption = null;
			infoText = null;
		} else
		if (id == SAOID.INVITE_LIST) {
			menu = new SAOListGUI(element, menuOffsetX, menuOffsetY, 100, 60);
			
			for (final EntityPlayer player : SAOMod.listOnlinePlayers(mc)) {
				final String name = SAOMod.getName(player);
				final boolean member = SAOMod.isPartyMember(name);
				
				final SAOButtonGUI button = new SAOStateButtonGUI(menu, SAOID.INVITE_PLAYER, 0, 0, name, SAOIcon.INVITE, new SAOStateHandler() {
				
					public boolean isStateEnabled(Minecraft mc, SAOStateButtonGUI button) {
						return !SAOMod.isPartyMember(button.caption);
					}
				
				});
				
				button.enabled = !member;
				menu.elements.add(button);
			}
		} else
		if (id == SAOID.ITEMS) {
			menu = new SAOInventoryGUI(element, menuOffsetX, menuOffsetY, 150, 100, mc.thePlayer.inventoryContainer, SAOInventory.ITEMS);
		} else
		if (id == SAOID.SKILLS) {
			menu = new SAOListGUI(element, menuOffsetX, menuOffsetY, 100, 60);
			
			for (final SAOSkill skill : SAOSkill.values()) {
				final SAOButtonGUI button = new SAOButtonGUI(menu, skill.id, 0, 0, skill.toString(), skill.icon);
				
				switch (skill) {
				case SPRINTING:
					button.highlight = SAOMod.IS_SPRINTING;
					break;
				case SNEAKING:
					button.highlight = SAOMod.IS_SNEAKING;
					break;
				default:
					break;
				}
				
				menu.elements.add(button);
			}
		} else
		if (id == SAOID.WEAPONS) {
			menu = new SAOInventoryGUI(element, menuOffsetX, menuOffsetY, 150, 100, mc.thePlayer.inventoryContainer, SAOInventory.WEAPONS);
		} else
		if (id == SAOID.EQUIPPED) {
			menu = new SAOInventoryGUI(element, menuOffsetX, menuOffsetY, 150, 100, mc.thePlayer.inventoryContainer, SAOInventory.EQUIPMENT);
		} else
		if (id == SAOID.ACCESSORY) {
			menu = new SAOInventoryGUI(element, menuOffsetX, menuOffsetY, 150, 100, mc.thePlayer.inventoryContainer, SAOInventory.ACCESSORY);
		} else
		if (id == SAOID.FRIENDS) {
			menu = new SAOFriendsGUI(mc, element, menuOffsetX, menuOffsetY, 150, 100);
			
			sub = SAOSub.resetFriendsSub(mc, sub);
			info = SAOSub.addInfo(sub);
			
			infoCaption = null;
			infoText = null;
		} else
		if ((id == SAOID.FRIEND) && (element instanceof SAOFriendGUI)) {
			if (((SAOFriendGUI) element).highlight) {
				menu = new SAOMenuGUI(element, menuOffsetX, menuOffsetY, 100, 60);
				
				menu.elements.add(new SAOButtonGUI(menu, SAOID.MESSAGE_BOX, 0, 0, "Message Box", SAOIcon.MESSAGE));
				menu.elements.add(new SAOButtonGUI(menu, SAOID.POSITION_CHECK, 0, 0, "Position Check", SAOIcon.FIELD_MAP));
				menu.elements.add(new SAOButtonGUI(menu, SAOID.OTHER_PROFILE, 0, 0, "Profile", SAOIcon.PARTY));
			} else {
				menu = null;
				
				SAOMod.addFriendRequest(mc, ((SAOFriendGUI) element).caption);
				element.enabled = false;
			}
		} else
		if ((id == SAOID.OTHER_PROFILE) && (element.parent instanceof SAOMenuGUI) && (((SAOMenuGUI) element.parent).parent instanceof SAOFriendGUI)) {
			menu = null;
			
			final EntityPlayer player = SAOMod.findOnlinePlayer(mc, ((SAOFriendGUI) ((SAOMenuGUI) element.parent).parent).caption);
			
			if (player != null) {
				sub = SAOSub.resetProfileSub(mc, sub, player);
				info = SAOSub.addInfo(sub);
				
				infoCaption = null;
				infoText = null;
				
				final String[] profile = SAOSub.addProfileContent(player);
				
				setInfo(profile[0], profile[1]);
			}
		} else
		if ((id == SAOID.POSITION_CHECK) && (element.parent instanceof SAOMenuGUI) && (((SAOMenuGUI) element.parent).parent instanceof SAOFriendGUI)) {
			menu = null;
			
			final EntityPlayer player = SAOMod.findOnlinePlayer(mc, ((SAOFriendGUI) ((SAOMenuGUI) element.parent).parent).caption);
			
			if (player != null) {
				sub = SAOSub.resetCheckPositionSub(mc, sub, player, 1, null);
				info = SAOSub.addInfo(sub);
				
				infoCaption = null;
				infoText = null;
				
				final String[] position = SAOSub.addPositionContent(player, mc.thePlayer);
				
				setInfo(position[0], position[1]);
			}
		} else
		if (id == SAOID.QUESTS) {
			menu = null;
			
			sub = SAOSub.resetQuestsSub(mc, sub, mc.thePlayer);
			info = SAOSub.addInfo(sub);
			
			infoCaption = null;
			infoText = null;
		} else
		if (id == SAOID.FIELD_MAP) {
			menu = null;
			
			sub = SAOSub.resetCheckPositionSub(mc, sub, mc.thePlayer, 4, "-Field Map-");
			info = SAOSub.addInfo(sub);
			
			infoCaption = null;
			infoText = null;
			
			final String[] position = SAOSub.addPositionContent(mc.thePlayer, mc.thePlayer);
			
			setInfo(position[0], position[1]);
		} else
		if (id == SAOID.DUNGEON_MAP) {
			menu = null;
			
			sub = SAOSub.resetCheckPositionSub(mc, sub, mc.thePlayer, 1, "-Dungeon Map-");
			info = SAOSub.addInfo(sub);
			
			infoCaption = null;
			infoText = null;
			
			final String[] position = SAOSub.addPositionContent(mc.thePlayer, mc.thePlayer);
			
			setInfo(position[0], position[1]);
		}
		
		if ((sub != subMenu) && (subMenu != null)) {
			for (int i = menus.size() - 1; i >= 0; i--) {
				final Entry<SAOID, SAOMenuGUI> entry = menus.get(i);
				
				if (entry.getValue() == subMenu) {
					menus.remove(i);
					break;
				}
			}
			
			elements.remove(subMenu);
		}
		
		if (menu != null) {
			final List<SAOElementGUI> list;
			
			if ((element.parent != null) && (element.parent instanceof SAOContainerGUI)) {
				list = ((SAOContainerGUI) element.parent).elements;
			} else {
				list = elements;
			}
			
			for (final SAOElementGUI element0 : list) {
				if (element0.ID() == id) {
					if (element0 instanceof SAOButtonGUI) {
						((SAOButtonGUI) element0).highlight = true;
					} else
					if (element0 instanceof SAOIconGUI) {
						((SAOIconGUI) element0).highlight = true;
					}
				} else {
					element0.enabled = false;
				}
			}
			
			openMenu(id, menu);
			
			if ((sub != subMenu) && (sub != null)) {
				openMenu(id, sub);
			}
		}
	}

	private void openMenu(final SAOID id, final SAOMenuGUI menu) {
		menus.add(new Entry<SAOID, SAOMenuGUI>() {
		
			public SAOMenuGUI setValue(SAOMenuGUI none) {
				return null;
			}
		
			public SAOMenuGUI getValue() {
				return menu;
			}
		
			public SAOID getKey() {
				return id;
			}
		
		});
		
		elements.add(menu);
	}

	private void closeMenu(SAOElementGUI element, SAOID id) {
		final SAOID[] closes = new SAOID[menus.size()];
		int closeIndex = 0;
		
		for (int i = menus.size() - 1; i >= 0; i--) {
			final Entry<SAOID, SAOMenuGUI> entry = menus.get(i);
			
			if ((entry.getKey().hasParent(id)) || (entry.getKey() == id)) {
				if (entry.getValue().elements.contains(info)) {
					info = null;
					infoCaption = null;
					infoText = null;
				}
				
				if (entry.getValue() == sub) {
					sub = null;
				}
				
				elements.remove(entry.getValue());
				menus.remove(i);
			}
		}
		
		if (element != null) {
			final List<SAOElementGUI> list;
			
			if ((element.parent != null) && (element.parent instanceof SAOContainerGUI)) {
				list = ((SAOContainerGUI) element.parent).elements;
			} else {
				list = elements;
			}
			
			for (final SAOElementGUI element0 : list) {
				if (element0.ID() == id) {
					if (element0 instanceof SAOButtonGUI) {
						((SAOButtonGUI) element0).highlight = false;
					} else
					if (element0 instanceof SAOIconGUI) {
						((SAOIconGUI) element0).highlight = false;
					}
				} else {
					element0.enabled = true;
				}
			}
		}
	}

}
