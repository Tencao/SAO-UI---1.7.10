package com.tencao.saoui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Stream;

import org.lwjgl.input.Keyboard;

import com.tencao.saoui.ui.SAOButtonGUI;
import com.tencao.saoui.ui.SAOContainerGUI;
import com.tencao.saoui.ui.SAOElementGUI;
import com.tencao.saoui.ui.SAOFriendGUI;
import com.tencao.saoui.ui.SAOFriendsGUI;
import com.tencao.saoui.ui.SAOIconGUI;
import com.tencao.saoui.ui.SAOInventoryGUI;
import com.tencao.saoui.ui.SAOLabelGUI;
import com.tencao.saoui.ui.SAOListGUI;
import com.tencao.saoui.ui.SAOMenuGUI;
import com.tencao.saoui.ui.SAOPanelGUI;
import com.tencao.saoui.ui.SAOPartyGUI;
import com.tencao.saoui.ui.SAOQuestGUI;
import com.tencao.saoui.ui.SAOScreenGUI;
import com.tencao.saoui.ui.SAOSlotGUI;
import com.tencao.saoui.ui.SAOStateButtonGUI;
import com.tencao.saoui.ui.SAOTextGUI;
import com.tencao.saoui.util.SAOAction;
import com.tencao.saoui.util.SAOAlign;
import com.tencao.saoui.util.SAOColor;
import com.tencao.saoui.util.SAOID;
import com.tencao.saoui.util.SAOIcon;
import com.tencao.saoui.util.SAOInventory;
import com.tencao.saoui.util.SAOJString;
import com.tencao.saoui.util.SAOOption;
import com.tencao.saoui.util.SAOSkill;
import com.tencao.saoui.util.SAOString;
import com.tencao.saoui.util.SAOSub;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class SAOIngameMenuGUI extends SAOScreenGUI {

    private final List<Entry<SAOID, SAOMenuGUI>> menus;

    private int flowY;
    private int flowX, jumpX;
    private SAOOption openOptCat = null;

    private SAOMenuGUI sub;

    private SAOPanelGUI info;
    private SAOLabelGUI infoCaption;
    private SAOTextGUI infoText;

    private final SAOString[] infoData = new SAOString[2];

    private final GuiInventory parentInv;
    public World world;


    public SAOIngameMenuGUI(GuiInventory vanillaGUI) {
        super();
		menus = new ArrayList<>();
        parentInv = vanillaGUI;
        info = null;
    }

	@Override
    protected void init() {
        super.init();
        menus.clear();

        SAOIconGUI action1, action2, action;

        elements.add(action1 = new SAOIconGUI(this, SAOID.PROFILE, 0, 0, SAOIcon.PROFILE));
        elements.add(action2 = new SAOIconGUI(action1, SAOID.SOCIAL, 0, 24, SAOIcon.SOCIAL));
        elements.add(action = new SAOIconGUI(action2, SAOID.MESSAGE, 0, 24, SAOIcon.MESSAGE));
        elements.add(action = new SAOIconGUI(action, SAOID.NAVIGATION, 0, 24, SAOIcon.NAVIGATION));
        elements.add(action = new SAOIconGUI(action, SAOID.SETTINGS, 0, 24, SAOIcon.SETTINGS));

        if (parentInv != null) openMenu(action1, action1.ID());

        flowY = -height;
    }

    @Override
    public int getX(boolean relative) {
        return super.getX(relative) + width * 2 / 5 + (flowX - jumpX) / 2;
    }

    @Override
    public int getY(boolean relative) {
        return super.getY(relative) + flowY;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (flowY < height / 2) flowY = (flowY + height / 2 - 32) / 2;

        flowX /= 2;

        if (infoData[0] != null && infoData[1] != null) updateInfo(infoData[0].toString(), infoData[1].toString());
    }
    
    @Override
    protected void keyTyped(char ch, int key) {
        if (parentInv != null) super.keyTyped(ch, Keyboard.KEY_ESCAPE);
        else super.keyTyped(ch, key);
    }

    @Override
    public void actionPerformed(SAOElementGUI element, SAOAction action, int data) {
        final SAOID id = element.ID();

        if (id.menuFlag) {
            if (isMenuOpen(id)) {
            	element.click(mc.getSoundHandler(), false);
                closeMenu(element, id);
            } else {
            	element.click(mc.getSoundHandler(), true);
            	openMenu(element, id);
            }
        } else if (id != SAOID.NONE) {
        	element.click(mc.getSoundHandler(), false);
            action(element, id, action, data);
        }
    }

    private boolean isMenuOpen(SAOID id) {
        return menus.stream().anyMatch(entry -> entry.getKey() == id);
    }

    private void action(SAOElementGUI element, SAOID id, SAOAction action, int data) {
        if (id == SAOID.LOGOUT) {
            if (SAOOption.LOGOUT.value) {
                element.enabled = false;
                mc.theWorld.sendQuittingDisconnectingPacket();

                mc.loadWorld(null);
                mc.displayGuiScreen(new GuiMainMenu());
            }
        } else if (id == SAOID.MENU) {
            mc.displayGuiScreen(new GuiIngameMenu());
        } else if (id == SAOID.OPTION && element instanceof SAOButtonGUI) {
            final SAOButtonGUI button = (SAOButtonGUI) element;

            Optional<SAOOption> optionalOp = Stream.of(SAOOption.values()).filter(op -> button.caption.equals(op.toString())).findFirst();
            final SAOOption option = optionalOp.orElse(null);

            if (option == null) {
                mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
            } else if (element.parent instanceof SAOContainerGUI) {
                final SAOContainerGUI parent = (SAOContainerGUI) element.parent;

                option.value = !option.value;
                button.highlight = option.value;

                switch (option) {
                    case DEFAULT_UI:
                        parent.elements.stream().filter(el -> el instanceof SAOButtonGUI).map(el -> (SAOButtonGUI) el)
                                .filter(btn -> btn.caption.equals(SAOOption.CROSS_HAIR.name)).forEach(btn -> {
                            SAOOption.CROSS_HAIR.value = option.value;
                            btn.highlight = SAOOption.CROSS_HAIR.value;
                            btn.enabled = !option.value;
                            SAOMod.setOption(SAOOption.CROSS_HAIR);
                        });

                        break;
                    default:
                        break;
                }

                SAOMod.setOption(option);
                SAOMod.saveAllOptions();
            }
        } else if (id == SAOID.MESSAGE && mc.ingameGUI instanceof SAOIngameGUI) {
            //((SAOIngameGUI) mc.ingameGUI).viewMessageAuto();
        } else if (id == SAOID.MESSAGE_BOX && element.parent instanceof SAOMenuGUI && ((SAOMenuGUI) element.parent).parent instanceof SAOFriendGUI) {
        	/*final String username = ((SAOFriendGUI) ((SAOMenuGUI) element.parent).parent).caption;

            final String format = I18n.format("commands.message.usage");
            final String cmd = format.substring(0, format.indexOf(' '));

            final String message = SAOJ8String.join(" ", cmd, username, "");

            mc.displayGuiScreen(new GuiChat(message));*/
        } else if ((id == SAOID.SKILL) && (element instanceof SAOButtonGUI)) {
            final SAOButtonGUI button = (SAOButtonGUI) element;

            switch (button.icon) {
                case CRAFTING:
                    if (parentInv != null) mc.displayGuiScreen(parentInv);
                    else {
                        SAOMod.REPLACE_GUI_DELAY = 1;
                        mc.displayGuiScreen(null);

                        final int invKeyCode = mc.gameSettings.keyBindInventory.getKeyCode();

                        KeyBinding.setKeyBindState(invKeyCode, true);
                        KeyBinding.onTick(invKeyCode);
                    }

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
        } else if (id == SAOID.INVITE_PLAYER && element instanceof SAOButtonGUI) {
            final String name = ((SAOButtonGUI) element).caption;

            if (!SAOMod.isPartyMember(name)) SAOMod.inviteParty(mc, name);
        } else if (id == SAOID.CREATE) {
            element.enabled = false;//!SAOMod.createParty(mc, 2.5);

            //noinspection ConstantConditions while party is bugged
            if (!element.enabled) {
                mc.displayGuiScreen(null);
                mc.setIngameFocus();
            }
        } else if (id == SAOID.DISSOLVE) {
            element.enabled = false;

            final boolean isLeader = SAOMod.isPartyLeader(SAOMod.getName(mc));

            final String title = isLeader ? SAOMod._PARTY_DISSOLVING_TITLE : SAOMod._PARTY_LEAVING_TITLE;
            final String text = isLeader ? SAOMod._PARTY_DISSOLVING_TEXT : SAOMod._PARTY_LEAVING_TEXT;

			mc.displayGuiScreen(SAOWindowViewGUI.viewConfirm(title, text, (element1, action1, data1) -> {
                final SAOID id1 = element1.ID();

                if (id1 == SAOID.CONFIRM) SAOMod.dissolveParty(mc);

                mc.displayGuiScreen(null);
                mc.setIngameFocus();
            }));
        } else if (id == SAOID.SLOT && element instanceof SAOSlotGUI && element.parent instanceof SAOInventoryGUI) {
            final SAOSlotGUI slot = (SAOSlotGUI) element;
            final SAOInventoryGUI inventory = (SAOInventoryGUI) element.parent;

            final SAOInventory type = inventory.filter;
            final Container container = inventory.slots;
            final ItemStack stack = slot.getStack();

            //System.out.println(action + " " + data);

            if (stack != null) {
                if (action == SAOAction.LEFT_RELEASED) {
                    final Slot current = findSwapSlot(container, slot.getSlot(), type);

                    if (current != null && current.slotNumber != slot.getSlotNumber()) {
                        inventory.handleMouseClick(mc, slot.getSlot(), slot.getSlotNumber(), 0, 0);
                        inventory.handleMouseClick(mc, current, current.slotNumber, 0, 0);
                        inventory.handleMouseClick(mc, slot.getSlot(), slot.getSlotNumber(), 0, 0);
                    }
                } else if (action == SAOAction.RIGHT_RELEASED) {
                    inventory.handleMouseClick(mc, slot.getSlot(), slot.getSlotNumber(), 1, 4);
                } else if (action == SAOAction.MIDDLE_RELEASED || action == SAOAction.KEY_TYPED && data == mc.gameSettings.keyBindPickBlock.getKeyCode()) {
                    SAOString caption = null;
                    StringBuilder text = new StringBuilder();

                    for (final Object line : stack.getTooltip(mc.thePlayer, false))
                        if (caption != null) text.append(line).append('\n');
                        else caption = new SAOJString(line);

                    setInfo(caption, new SAOJString(text.toString()));
                } else if (action == SAOAction.KEY_TYPED && data == mc.gameSettings.keyBindDrop.getKeyCode())
                    inventory.handleMouseClick(mc, slot.getSlot(), slot.getSlotNumber(), 0, 4);
            }
        } else if ((id == SAOID.QUEST) && (element instanceof SAOQuestGUI)) {
            final SAOQuestGUI quest = (SAOQuestGUI) element;
            final Achievement ach0 = quest.getAchievement();

            setInfo(new SAOJString(quest.caption), new SAOJString(ach0.getDescription()));
        }
    }

    private Slot findSwapSlot(Container container, Slot swap, SAOInventory type) {
        if (type == SAOInventory.EQUIPMENT) {
            if (swap.slotNumber < 9) return findEmptySlot(container, 9);
            else {
                for (int i = 5; i < 9; i++)
                    if (container.getSlot(i).isItemValid(swap.getStack())) return container.getSlot(i);

                return null;
            }
        } else if (type == SAOInventory.WEAPONS) {
            return swap.slotNumber >= 36 ? findEmptySlot(container, 9) : container.getSlot(36);
        } else if (type == SAOInventory.ACCESSORY) {
            return swap.slotNumber >= 36 ? findEmptySlot(container, 9) : container.getSlot(37);
        } else if (type == SAOInventory.ITEMS) {
            if (swap.slotNumber >= 36) return findEmptySlot(container, 9);
            else {
                Slot slot = findEmptySlot(container, 36);

                return slot == null ? currentSlot(container) : slot;
            }
        } else return null;
    }

    private Slot currentSlot(Container container) {
        return container.getSlotFromInventory(mc.thePlayer.inventory, mc.thePlayer.inventory.currentItem);
    }

    private Slot findEmptySlot(Container container, int startIndex) {
        @SuppressWarnings("unchecked") // Cuz goddammit can't you guys declare it as List<Slot>? è_é
                Optional<Slot> optSlot = container.inventorySlots.stream().skip(startIndex - 1).filter(obj -> !((Slot) obj).getHasStack()).findFirst();
        return optSlot.orElse(null);
    }

    private void updateInfo(String caption, String text) {
        //System.out.println(caption + " : " + text);

        if (info != null) {
            if (infoCaption == null) {
                info.elements.add(infoCaption = new SAOLabelGUI(info, 15, 0, info.width - 15, caption, SAOAlign.LEFT));
                infoCaption.fontColor = SAOColor.DEFAULT_BOX_FONT_COLOR;
            } else infoCaption.caption = caption;

            if (infoText == null) {
                info.elements.add(infoText = new SAOTextGUI(info, 15, 0, text, info.width - 15));
                infoText.fontColor = SAOColor.DEFAULT_BOX_FONT_COLOR;
            } else infoText.setText(text);
        }
    }

    private void setInfo(SAOString caption, SAOString text) {
        infoData[0] = caption;
        infoData[1] = text;

        updateInfo(infoData[0] != null ? infoData[0].toString() : "", infoData[1] != null ? infoData[1].toString() : "");
    }

    private void openMenu(SAOElementGUI element, SAOID id) {
        final int menuOffsetX = element.width + 14;
        final int menuOffsetY = element.height / 2;

        SAOMenuGUI menu = null;
        SAOMenuGUI subMenu = sub;

        if (id == SAOID.PROFILE) {
            menu = new SAOMenuGUI(element, menuOffsetX, menuOffsetY, 100, 60);

            menu.elements.add(new SAOButtonGUI(menu, SAOID.EQUIPMENT, 0, 0, StatCollector.translateToLocal("guiEquipment"), SAOIcon.EQUIPMENT));
            menu.elements.add(new SAOButtonGUI(menu, SAOID.ITEMS, 0, 0, StatCollector.translateToLocal("guiItems"), SAOIcon.ITEMS));
            menu.elements.add(new SAOButtonGUI(menu, SAOID.SKILLS, 0, 0, StatCollector.translateToLocal("guiSkills"), SAOIcon.SKILLS));

            sub = SAOSub.createMainProfileSub(mc, element, -189, menuOffsetY);
            info = SAOSub.addInfo(sub);

            infoCaption = null;
            infoText = null;

            final SAOString[] profile = SAOSub.addProfileContent(mc);

            setInfo(profile[0], profile[1]);
        } else if (id == SAOID.SOCIAL) {/*
            menu = new SAOMenuGUI(element, menuOffsetX, menuOffsetY, 100, 60);
            menu.enabled = false;

            menu.elements.add(new SAOButtonGUI(menu, SAOID.GUILD, 0, 0, StatCollector.translateToLocal("guiGuild"), SAOIcon.GUILD));
            menu.elements.add(new SAOButtonGUI(menu, SAOID.PARTY, 0, 0, StatCollector.translateToLocal("guiParty"), SAOIcon.PARTY));
            menu.elements.add(new SAOButtonGUI(menu, SAOID.FRIENDS, 0, 0, StatCollector.translateToLocal("guiFriends"), SAOIcon.FRIEND));
            
            sub = SAOSub.createSocialSub(mc, element, -189, menuOffsetY);
            info = SAOSub.addInfo(sub);

            infoCaption = null;
            infoText = null;*/
        } else if (id == SAOID.NAVIGATION) {
            menu = new SAOMenuGUI(element, menuOffsetX, menuOffsetY, 100, 60);

            menu.elements.add(new SAOButtonGUI(menu, SAOID.QUESTS, 0, 0, StatCollector.translateToLocal("guiQuest"), SAOIcon.QUEST));
            menu.elements.add(new SAOButtonGUI(menu, SAOID.FIELD_MAP, 0, 0, StatCollector.translateToLocal("guiFieldMap"), SAOIcon.FIELD_MAP));
            menu.elements.add(new SAOButtonGUI(menu, SAOID.DUNGEON_MAP, 0, 0, StatCollector.translateToLocal("guiDungMap"), SAOIcon.DUNGEON_MAP));

            sub = SAOSub.createNavigationSub(mc, element, -189, menuOffsetY);
            info = SAOSub.addInfo(sub);

            infoCaption = null;
            infoText = null;
        } else if (id == SAOID.SETTINGS) {
            menu = new SAOMenuGUI(element, menuOffsetX, menuOffsetY, 100, 60);

            menu.elements.add(new SAOButtonGUI(menu, SAOID.OPTIONS, 0, 0, StatCollector.translateToLocal("guiOption"), SAOIcon.OPTION));
            menu.elements.add(new SAOButtonGUI(menu, SAOID.MENU, 0, 0, StatCollector.translateToLocal("guiMenu"), SAOIcon.HELP));
			menu.elements.add(new SAOStateButtonGUI(menu, SAOID.LOGOUT, 0, 0, SAOOption.LOGOUT.value? StatCollector.translateToLocal("guiLogout") : "", SAOIcon.LOGOUT, (mc1, button) -> {
                if (SAOOption.LOGOUT.value) {
                    if (button.caption.length() == 0) button.caption = "Logout";
                } else if (button.caption.length() > 0) button.caption = "";

                return button.enabled;
            }));
        } else if (id == SAOID.OPTIONS) {
            menu = new SAOListGUI(element, menuOffsetX, menuOffsetY, 130, 100);

            menu.elements.add(new SAOButtonGUI(menu, SAOID.OPTION, 0, 0, StatCollector.translateToLocal("guiOptions"), SAOIcon.HELP));

            final SAOMenuGUI mnu = menu;
            Stream.of(SAOOption.values()).filter(opt -> opt.category == null).forEach(option -> {
                final SAOButtonGUI button = new SAOButtonGUI(mnu, option.isCategory ? SAOID.OPT_CAT : SAOID.OPTION, 0, 0, option.toString(), SAOIcon.OPTION);
                button.highlight = option.value;
                if (option == SAOOption.CROSS_HAIR) button.enabled = !SAOOption.DEFAULT_UI.value;
                mnu.elements.add(button);
            });
        } else if (id == SAOID.OPT_CAT) {
            openOptCat = SAOOption.fromString(((SAOButtonGUI) element).caption);
            menu = new SAOListGUI(element, menuOffsetX, menuOffsetY, 130, 100);

            final SAOMenuGUI mnu = menu;
            Stream.of(SAOOption.values()).filter(opt -> opt.category == openOptCat).forEach(option -> {
                final SAOButtonGUI button = new SAOButtonGUI(mnu, option.isCategory ? SAOID.OPT_CAT : SAOID.OPTION, 0, 0, option.toString(), SAOIcon.OPTION);
                button.highlight = option.value;
                mnu.elements.add(button);
            });
        } else if (id == SAOID.EQUIPMENT) {
            menu = new SAOMenuGUI(element, menuOffsetX, menuOffsetY, 100, 60);

            menu.elements.add(new SAOButtonGUI(menu, SAOID.WEAPONS, 0, 0, StatCollector.translateToLocal("guiWeapons"), SAOIcon.EQUIPMENT));
            menu.elements.add(new SAOButtonGUI(menu, SAOID.EQUIPPED, 0, 0, StatCollector.translateToLocal("guiEquipped"), SAOIcon.ARMOR));
            menu.elements.add(new SAOButtonGUI(menu, SAOID.ACCESSORY, 0, 0, StatCollector.translateToLocal("guiAccessory"), SAOIcon.ACCESSORY));
        } else if (id == SAOID.PARTY) {
            menu = new SAOMenuGUI(element, menuOffsetX, menuOffsetY, 100, 60);
            menu.enabled = false;

            if (world.isRemote){
                menu.enabled = false;
            }
            menu.elements.add(new SAOPartyGUI(menu, SAOID.INVITE_LIST, 0, 0, StatCollector.translateToLocal("guiInvite"), SAOIcon.INVITE, true));
            menu.elements.add(new SAOPartyGUI(menu, SAOID.CREATE, 0, 0, StatCollector.translateToLocal("guiCreate"), SAOIcon.CREATE, false));
            menu.elements.add(new SAOPartyGUI(menu, SAOID.DISSOLVE, 0, 0, StatCollector.translateToLocal("guiDissolve"), SAOIcon.CANCEL, true));

            sub = SAOSub.resetPartySub(mc, sub);
            info = SAOSub.addInfo(sub);

            infoCaption = null;
            infoText = null;
        } else if (id == SAOID.INVITE_LIST) {
            menu = new SAOListGUI(element, menuOffsetX, menuOffsetY, 100, 60);
            menu.enabled = false;
            if (world.isRemote) menu.enabled = false;

            final SAOMenuGUI mnu = menu;
            SAOMod.listOnlinePlayers(mc).stream().map(SAOMod::getName).forEach(name -> {
                final SAOButtonGUI button = new SAOStateButtonGUI(mnu, SAOID.INVITE_PLAYER, 0, 0, name, SAOIcon.INVITE, (mc1, button1) -> !SAOMod.isPartyMember(button1.caption));
                button.enabled = !SAOMod.isPartyMember(name);
                mnu.elements.add(button);
            });

        } else if (id == SAOID.ITEMS) {
            menu = new SAOInventoryGUI(element, menuOffsetX, menuOffsetY, 150, 100, mc.thePlayer.inventoryContainer, SAOInventory.ITEMS);
        } else if (id == SAOID.SKILLS) {
            menu = new SAOListGUI(element, menuOffsetX, menuOffsetY, 100, 60);

            final SAOMenuGUI mnu = menu;
            Stream.of(SAOSkill.values()).forEach(skill -> mnu.elements.add(new SAOButtonGUI(mnu, skill.id, 0, 0, skill.toString(), skill.icon, skill.shouldHighlight())));

        } else if (id == SAOID.WEAPONS) { // TODO: Some optimization could be done here. Laterz.
            menu = new SAOInventoryGUI(element, menuOffsetX, menuOffsetY, 150, 100, mc.thePlayer.inventoryContainer, SAOInventory.WEAPONS);
        } else if (id == SAOID.EQUIPPED) {
            menu = new SAOInventoryGUI(element, menuOffsetX, menuOffsetY, 150, 100, mc.thePlayer.inventoryContainer, SAOInventory.EQUIPMENT);
        } else if (id == SAOID.ACCESSORY) {
            menu = new SAOInventoryGUI(element, menuOffsetX, menuOffsetY, 150, 100, mc.thePlayer.inventoryContainer, SAOInventory.ACCESSORY);
        } else if (id == SAOID.FRIENDS) {
            menu = new SAOFriendsGUI(mc, element, menuOffsetX, menuOffsetY, 150, 100);
            menu.enabled = false;
            if (world.isRemote){
                menu.enabled = false;
            }

            sub = SAOSub.resetFriendsSub(mc, sub);
            info = SAOSub.addInfo(sub);

            infoCaption = null;
            infoText = null;
        } else if ((id == SAOID.FRIEND) && (element instanceof SAOFriendGUI)) {/*
            if (((SAOFriendGUI) element).highlight) {
                menu = new SAOMenuGUI(element, menuOffsetX, menuOffsetY, 100, 60);
                menu.enabled = false;
                if (world.isRemote){
                    menu.enabled = false;
                }

                menu.elements.add(new SAOButtonGUI(menu, SAOID.MESSAGE_BOX, 0, 0, StatCollector.translateToLocal("guiMessageBox"), SAOIcon.MESSAGE));
                menu.elements.add(new SAOButtonGUI(menu, SAOID.POSITION_CHECK, 0, 0, StatCollector.translateToLocal("guiPositionCheck"), SAOIcon.FIELD_MAP));
                menu.elements.add(new SAOButtonGUI(menu, SAOID.OTHER_PROFILE, 0, 0, StatCollector.translateToLocal("guiProfile"), SAOIcon.PARTY));
            } else {
                menu = null;

                //SAOMod.addFriendRequest(mc, ((SAOFriendGUI) element).caption);
                element.enabled = false;
            }*/
        } else if (id == SAOID.OTHER_PROFILE && element.parent instanceof SAOMenuGUI && ((SAOMenuGUI) element.parent).parent instanceof SAOFriendGUI) {
            menu = null;

            final EntityPlayer player = SAOMod.findOnlinePlayer(mc, ((SAOFriendGUI) ((SAOMenuGUI) element.parent).parent).caption);

            if (player != null) {
                sub = SAOSub.resetProfileSub(mc, sub, player);
                info = SAOSub.addInfo(sub);

                infoCaption = null;
                infoText = null;

                final SAOString[] profile = SAOSub.addProfileContent(player);

                setInfo(profile[0], profile[1]);
            }
        } else if (id == SAOID.POSITION_CHECK && element.parent instanceof SAOMenuGUI && ((SAOMenuGUI) element.parent).parent instanceof SAOFriendGUI) {
            menu = null;

            final EntityPlayer player = SAOMod.findOnlinePlayer(mc, ((SAOFriendGUI) ((SAOMenuGUI) element.parent).parent).caption);

            if (player != null) {
                sub = SAOSub.resetCheckPositionSub(mc, sub, player, 1, null);
                info = SAOSub.addInfo(sub);

                infoCaption = null;
                infoText = null;

                final SAOString[] position = SAOSub.addPositionContent(player, mc.thePlayer);

                setInfo(position[0], position[1]);
            }
        } else if (id == SAOID.QUESTS) {
            menu = null;

            sub = SAOSub.resetQuestsSub(mc, sub, mc.thePlayer);
            info = SAOSub.addInfo(sub);

            infoCaption = null;
            infoText = null;
        } else if (id == SAOID.FIELD_MAP) {
            menu = null;

            sub = SAOSub.resetCheckPositionSub(mc, sub, mc.thePlayer, 4, '-' + StatCollector.translateToLocal("guiFieldMap") + '-');
            info = SAOSub.addInfo(sub);

            infoCaption = null;
            infoText = null;

            final SAOString[] position = SAOSub.addPositionContent(mc.thePlayer, mc.thePlayer);

            setInfo(position[0], position[1]);
        } else if (id == SAOID.DUNGEON_MAP) {
            menu = null;

            sub = SAOSub.resetCheckPositionSub(mc, sub, mc.thePlayer, 1, '-' + StatCollector.translateToLocal("guiDungMap") + '-');
            info = SAOSub.addInfo(sub);

            infoCaption = null;
            infoText = null;

            final SAOString[] position = SAOSub.addPositionContent(mc.thePlayer, mc.thePlayer);

            setInfo(position[0], position[1]);
        }

        if (sub != subMenu && subMenu != null) {
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

            list = element.parent != null && element.parent instanceof SAOContainerGUI ? ((SAOContainerGUI) element.parent).elements : elements;

            for (final SAOElementGUI element0 : list) {
                if (element0.ID() == id) {
                    if (element0 instanceof SAOButtonGUI) {
                        if (id == SAOID.OPT_CAT) {
                            SAOOption curr = openOptCat;
                            final SAOOption comp = SAOOption.fromString(((SAOButtonGUI) element0).caption);
                            while (curr == comp) {
                                ((SAOButtonGUI) element0).highlight = curr == openOptCat;
                                curr = curr.category;
                            }
                        } else ((SAOButtonGUI) element0).highlight = true;
                    } else if (element0 instanceof SAOIconGUI) {
                        ((SAOIconGUI) element0).highlight = true;
                    }
                } else element0.enabled = false;
            }

            openMenu(id, menu);

            if (sub != subMenu && sub != null) openMenu(id, sub);
        }
    }

    private void moveX(final int mode, final SAOMenuGUI menu) {
        final int value = menu.x > 0 ? menu.x + menu.width : menu.x;

        jumpX += mode * value;
        flowX += mode * value;
    }

    private void openMenu(final SAOID id, final SAOMenuGUI menu) {
        moveX(+1, menu);

        menus.add(new Entry<SAOID, SAOMenuGUI>() {

            @Override
            public SAOMenuGUI setValue(SAOMenuGUI none) {
                return null;
            }

            @Override
            public SAOMenuGUI getValue() {
                return menu;
            }

            @Override
            public SAOID getKey() {
                return id;
            }

        });

        elements.add(menu);
    }    

    private void closeMenu(SAOElementGUI element, SAOID id) {
        for (int i = menus.size() - 1; i >= 0; i--) {
            final Entry<SAOID, SAOMenuGUI> entry = menus.get(i);

            if ((entry.getKey().hasParent(id)) || (entry.getKey() == id)) {
                if (entry.getValue().elements.contains(info)) {
                    info = null;
                    infoCaption = null;
                    infoText = null;
                }

                if (entry.getValue() == sub) sub = null;

                moveX(-1, entry.getValue());

                elements.remove(entry.getValue());
                menus.remove(i);
            }
        }

        if (element != null) {
            final List<SAOElementGUI> list;

            if (element.parent != null && element.parent instanceof SAOContainerGUI)
                list = ((SAOContainerGUI) element.parent).elements;
            else list = elements;

            for (final SAOElementGUI element0 : list) {
                if (element0.ID() == id) {
                    if (element0 instanceof SAOButtonGUI) ((SAOButtonGUI) element0).highlight = false;
                    else if (element0 instanceof SAOIconGUI) ((SAOIconGUI) element0).highlight = false;
                } else element0.enabled = true;
            }
        }
    }

}
