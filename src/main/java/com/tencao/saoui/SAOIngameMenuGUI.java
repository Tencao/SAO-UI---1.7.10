package com.tencao.saoui;

import baubles.api.BaublesApi;
import com.tencao.saoui.ui.*;
import com.tencao.saoui.util.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Stream;

@SideOnly(Side.CLIENT)
@cpw.mods.fml.common.Optional.Interface(iface = "baubles.api.IBauble", modid = "Baubles")
public class SAOIngameMenuGUI extends SAOScreenGUI {

    private final List<Entry<SAOID, SAOMenuGUI>> menus;
    private final SAOString[] infoData = new SAOString[2];
    private final GuiInventory parentInv;
    public World world;
    private int flowY;
    private int flowX, jumpX;
    private SAOOption openOptCat = null;
    private SAOMenuGUI sub;
    private SAOPanelGUI info;
    private SAOLabelGUI infoCaption;
    private SAOTextGUI infoText;


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
    public void updateScreen() { // TODO: when player holds a key (shift/alt/...) and moves his cursor it doesn't move the UI -> possibility to exit the mc screen
        super.updateScreen();
        if (flowY < height / 2) flowY = (flowY + height / 2 - 32) / 2;

        flowX /= 2;

        if (infoData[0] != null && infoData[1] != null) updateInfo(infoData[0].toString(), infoData[1].toString());
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
        if (id == SAOID.MENU) {
            mc.displayGuiScreen(new GuiIngameMenu());
        } else if (id == SAOID.SLOT && element instanceof SAOSlotGUI && element.parent instanceof SAOInventoryGUI) {
            final SAOSlotGUI slot = (SAOSlotGUI) element;
            final SAOInventoryGUI inventory = (SAOInventoryGUI) element.parent;
            final IInventory inventoryBauble = BaublesApi.getBaubles(mc.thePlayer);

            final SAOInventory type = inventory.filter;
            final Container container = inventory.slots;
            final ItemStack stack = slot.getStack();

            if (stack != null) {
                if (action == SAOAction.LEFT_RELEASED) {
                    final Slot current = findSwapSlot(container, slot.getSlot(), type);

                    if (type == SAOInventory.ACCESSORY && current != null && current.slotNumber != slot.getSlotNumber()) {
                        inventoryBauble.openChest();
                        inventory.handleMouseClick(mc, slot.getSlot(), slot.getSlotNumber(), 0, 0);
                        inventory.handleMouseClick(mc, current, current.slotNumber, 0, 0);
                        inventory.handleMouseClick(mc, slot.getSlot(), slot.getSlotNumber(), 0, 0);
                        inventoryBauble.closeChest();
                    }
                    else if (current != null && current.slotNumber != slot.getSlotNumber()) {
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
        } else if (id == SAOID.SKILL && element instanceof SkillButton) {
            ((SkillButton) element).action(mc, parentInv);
        } else if (id == SAOID.INVITE_PLAYER && element instanceof SAOButtonGUI) {
            final String name = ((SAOButtonGUI) element).caption;

            PartyHelper.instance().invite(mc, name);
        } else if (id == SAOID.DISSOLVE) {
            element.enabled = false;

            final boolean isLeader = PartyHelper.instance().isLeader(StaticPlayerHelper.getName(mc));

            final String title = isLeader ? ConfigHandler._PARTY_DISSOLVING_TITLE : ConfigHandler._PARTY_LEAVING_TITLE;
            final String text = isLeader ? ConfigHandler._PARTY_DISSOLVING_TEXT : ConfigHandler._PARTY_LEAVING_TEXT;

            mc.displayGuiScreen(SAOWindowViewGUI.viewConfirm(title, text, (element1, action1, data1) -> {
                final SAOID id1 = element1.ID();

                if (id1 == SAOID.CONFIRM) PartyHelper.instance().sendDissolve(mc);

                mc.displayGuiScreen(null);
                mc.setIngameFocus();
            }));
        } else if (id == SAOID.MESSAGE && mc.ingameGUI instanceof SAOIngameGUI) {
            // ((SAOIngameGUI) mc.ingameGUI).viewMessageAuto();
        } else if (id == SAOID.MESSAGE_BOX && element.parent instanceof SAOMenuGUI && ((SAOMenuGUI) element.parent).parent instanceof SAOFriendGUI) {
            final String username = ((SAOFriendGUI) ((SAOMenuGUI) element.parent).parent).caption;

            final String format = I18n.format("commands.message.usage");
            final String cmd = format.substring(0, format.indexOf(' '));

            final String message = SAOJ8String.join(" ", cmd, username, "");

            mc.displayGuiScreen(new GuiChat(message));
        } else if ((id == SAOID.QUEST) && (element instanceof SAOQuestGUI)) {
            final SAOQuestGUI quest = (SAOQuestGUI) element;
            final Achievement ach0 = quest.getAchievement();

            setInfo(new SAOJString(quest.caption), new SAOJString(ach0.getDescription()));
        } else if (id == SAOID.OPTION && element instanceof OptionButton) {
            final OptionButton button = (OptionButton) element;

            if (button.getOption() == SAOOption.VANILLA_OPTIONS) {
                mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
            } else {
                button.action();
            }
        } else if (id == SAOID.LOGOUT) {
            if (SAOOption.LOGOUT.getValue()) {
                element.enabled = false;
                mc.theWorld.sendQuittingDisconnectingPacket();

                mc.loadWorld(null);
                mc.displayGuiScreen(new GuiMainMenu());
            }
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
        } else if (type == SAOInventory.BOWS) {
            return swap.slotNumber >= 37 ? findEmptySlot(container, 9) : container.getSlot(37);
        } else if (type == SAOInventory.PICKAXE) {
            return swap.slotNumber >= 38 ? findEmptySlot(container, 9) : container.getSlot(38);
        } else if (type == SAOInventory.AXE) {
            return swap.slotNumber >= 39 ? findEmptySlot(container, 9) : container.getSlot(39);
        } else if (type == SAOInventory.SHOVEL) {
            return swap.slotNumber >= 40 ? findEmptySlot(container, 9) : container.getSlot(40);
        } else if (type == SAOInventory.ACCESSORY) {
            IInventory baubles = SAOInventory.getBaubles(mc.thePlayer);
            if (baubles != null) {
                if (Objects.equals(swap.inventory, baubles)) return findEmptySlot(container, 9);
                else {
                    for (int i = 0; i < baubles.getSizeInventory(); i++) {
                        if (baubles.isItemValidForSlot(i, swap.getStack())) return container.getSlot(i);
                    }
                }
            }
            return null;
        } else if (type == SAOInventory.CONSUMABLES) {
            return swap.slotNumber >= 41 ? findEmptySlot(container, 9) : container.getSlot(41);
        } else if (type == SAOInventory.ITEMS) {
            if (swap.slotNumber >= 42) return findEmptySlot(container, 9);
            else {
                Slot slot = findEmptySlot(container, 42);

                if (slot == null){
                    return currentSlot(container);
                } else return slot;
            }
        } else return null;
    }

    private Slot currentSlot(Container container) {
        return container.getSlotFromInventory(mc.thePlayer.inventory, mc.thePlayer.inventory.currentItem);
    }

    @cpw.mods.fml.common.Optional.Method(modid = "Baubles")
    private Slot currentBaubleSlot(Container container) {
        return container.getSlotFromInventory(BaublesApi.getBaubles(mc.thePlayer), mc.thePlayer.inventory.currentItem);
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
        // Core Menu
        if (id == SAOID.PROFILE) {
            menu = new SAOMenuGUI(element, menuOffsetX, menuOffsetY, 100, 60);

            menu.elements.add(new SAOButtonGUI(menu, SAOID.EQUIPMENT, 0, 0, StatCollector.translateToLocal("guiEquipment"), SAOIcon.EQUIPMENT));
            menu.elements.add(new SAOButtonGUI(menu, SAOID.ITEMS, 0, 0, StatCollector.translateToLocal("guiItems"), SAOIcon.ITEMS));
            menu.elements.add(new SAOButtonGUI(menu, SAOID.SKILLS, 0, 0, StatCollector.translateToLocal("guiSkills"), SAOIcon.SKILLS));

            sub = SAOSub.createMainProfileSub(mc, element, -189, menuOffsetY);
            info = SAOSub.addInfo(sub);

            final SAOString[] profile = SAOSub.addProfileContent(mc);

            setInfo(profile[0], profile[1]);
        } else if (id == SAOID.SOCIAL) {
            setInfo(null, null);
            menu = new SAOMenuGUI(element, menuOffsetX, menuOffsetY, 100, 60);

            menu.elements.add(new SAOButtonGUI(menu, SAOID.GUILD, 0, 0, StatCollector.translateToLocal("guiGuild"), SAOIcon.GUILD));
            menu.elements.add(new SAOButtonGUI(menu, SAOID.PARTY, 0, 0, StatCollector.translateToLocal("guiParty"), SAOIcon.PARTY));
            menu.elements.add(new SAOButtonGUI(menu, SAOID.FRIENDS, 0, 0, StatCollector.translateToLocal("guiFriends"), SAOIcon.FRIEND));

            sub = SAOSub.createSocialSub(mc, element, -189, menuOffsetY);
            info = SAOSub.addInfo(sub);

            infoCaption = null;
            infoText = null;
        } else if (id == SAOID.MESSAGE) {
            setInfo(null, null);
            menu = new SAOMenuGUI(element, menuOffsetX, menuOffsetY, 100, 60);

            menu.elements.add(new SAOButtonGUI(menu, SAOID.MESSAGE_BOX, 0, 0, StatCollector.translateToLocal("guiMessageBox"), SAOIcon.MESSAGE));

            sub = SAOSub.createSocialSub(mc, element, -189, menuOffsetY);
            info = SAOSub.addInfo(sub);

            infoCaption = null;
            infoText = null;
        } else if (id == SAOID.NAVIGATION) {
            setInfo(null, null);
            menu = new SAOMenuGUI(element, menuOffsetX, menuOffsetY, 100, 60);

            menu.elements.add(new SAOButtonGUI(menu, SAOID.QUESTS, 0, 0, StatCollector.translateToLocal("guiQuest"), SAOIcon.QUEST));
            if (SAOOption.DEBUG_MODE.getValue())menu.elements.add(new SAOButtonGUI(menu, SAOID.FIELD_MAP, 0, 0, StatCollector.translateToLocal("guiFieldMap"), SAOIcon.FIELD_MAP));
            if (SAOOption.DEBUG_MODE.getValue()) menu.elements.add(new SAOButtonGUI(menu, SAOID.DUNGEON_MAP, 0, 0, StatCollector.translateToLocal("guiDungMap"), SAOIcon.DUNGEON_MAP));

            sub = SAOSub.createNavigationSub(mc, element, -189, menuOffsetY);
            info = SAOSub.addInfo(sub);

        } else if (id == SAOID.SETTINGS) {
            menu = new SAOMenuGUI(element, menuOffsetX, menuOffsetY, 100, 60);

            menu.elements.add(new SAOButtonGUI(menu, SAOID.OPTIONS, 0, 0, StatCollector.translateToLocal("guiOption"), SAOIcon.OPTION));
            menu.elements.add(new SAOButtonGUI(menu, SAOID.MENU, 0, 0, StatCollector.translateToLocal("guiMenu"), SAOIcon.HELP));
            menu.elements.add(new SAOStateButtonGUI(menu, SAOID.LOGOUT, 0, 0, SAOOption.LOGOUT.getValue() ? StatCollector.translateToLocal("guiLogout") : "", SAOIcon.LOGOUT, (mc1, button) -> {
                if (SAOOption.LOGOUT.getValue()) {
                    if (button.caption.length() == 0) button.caption = "Logout";
                } else if (button.caption.length() > 0) button.caption = "";

                return button.enabled;
            }));
        }
        //Profile
        else if (id == SAOID.EQUIPMENT) {
            menu = new SAOMenuGUI(element, menuOffsetX, menuOffsetY, 100, 60);

            if (SAOInventory.WEAPONS != null || SAOInventory.BOWS != null || SAOInventory.PICKAXE != null || SAOInventory.AXE != null || SAOInventory.SHOVEL != null)
                menu.elements.add(new SAOButtonGUI(menu, SAOID.TOOLS, 0, 0, StatCollector.translateToLocal("guiTools"), SAOIcon.EQUIPMENT));
            menu.elements.add(new SAOButtonGUI(menu, SAOID.ARMOR, 0, 0, StatCollector.translateToLocal("guiEquipped"), SAOIcon.ARMOR));
            menu.elements.add(new SAOButtonGUI(menu, SAOID.ACCESSORY, 0, 0, StatCollector.translateToLocal("guiAccessory"), SAOIcon.ACCESSORY));
            menu.elements.add(new SAOButtonGUI(menu, SAOID.CONSUMABLES, 0, 0, StatCollector.translateToLocal("guiConsumable"), SAOIcon.ITEMS));
        } else if (id == SAOID.ITEMS) {
            menu = new SAOInventoryGUI(element, menuOffsetX, menuOffsetY, 150, 100, mc.thePlayer.inventoryContainer, SAOInventory.ITEMS);
        } else if (id == SAOID.SKILLS) {
            menu = new SAOListGUI(element, menuOffsetX, menuOffsetY, 100, 60);

            final SAOMenuGUI mnu = menu;
            Stream.of(SAOSkill.values()).forEach(skill -> mnu.elements.add(new SkillButton(mnu, 0, 0, skill)));
        }
        //Profile -> Equipment
        else if (id == SAOID.TOOLS) { // TODO: Some optimization could be done here. Laterz.
            if (SAOOption.COMPACT_INVENTORY.getValue()){
                menu = new SAOInventoryGUI(element, menuOffsetX, menuOffsetY, 150, 100, mc.thePlayer.inventoryContainer, SAOInventory.COMPATTOOLS);
            } else {
                menu = new SAOMenuGUI(element, menuOffsetX, menuOffsetY, 100, 60);
                if (mc.thePlayer.inventoryContainer.getInventory().stream().filter(st -> st != null).anyMatch(st -> SAOInventory.WEAPONS.isFine((ItemStack) st, true))) menu.elements.add(new SAOButtonGUI(menu, SAOID.WEAPONS, 0, 0, StatCollector.translateToLocal("guiWeapons"), SAOIcon.EQUIPMENT));
                if (mc.thePlayer.inventoryContainer.getInventory().stream().filter(st -> st != null).anyMatch(st -> SAOInventory.BOWS.isFine((ItemStack) st, true))) menu.elements.add(new SAOButtonGUI(menu, SAOID.BOWS, 0, 0, StatCollector.translateToLocal("guiBows"), SAOIcon.EQUIPMENT));
                if (mc.thePlayer.inventoryContainer.getInventory().stream().filter(st -> st != null).anyMatch(st -> SAOInventory.PICKAXE.isFine((ItemStack) st, true))) menu.elements.add(new SAOButtonGUI(menu, SAOID.PICKAXE, 0, 0, StatCollector.translateToLocal("guiPickaxes"), SAOIcon.EQUIPMENT));
                if (mc.thePlayer.inventoryContainer.getInventory().stream().filter(st -> st != null).anyMatch(st -> SAOInventory.AXE.isFine((ItemStack) st, true))) menu.elements.add(new SAOButtonGUI(menu, SAOID.AXE, 0, 0, StatCollector.translateToLocal("guiAxes"), SAOIcon.EQUIPMENT));
                if (mc.thePlayer.inventoryContainer.getInventory().stream().filter(st -> st != null).anyMatch(st -> SAOInventory.SHOVEL.isFine((ItemStack) st, true))) menu.elements.add(new SAOButtonGUI(menu, SAOID.SHOVEL, 0, 0, StatCollector.translateToLocal("guiShovels"), SAOIcon.EQUIPMENT));
                if (menu.elements.isEmpty()) menu.elements.add(new SAOEmptySlot(menu, 0, 0));
            }
        } else if (id == SAOID.WEAPONS) {
            menu = new SAOInventoryGUI(element, menuOffsetX, menuOffsetY, 150, 100, mc.thePlayer.inventoryContainer, SAOInventory.WEAPONS);
        } else if (id == SAOID.BOWS) {
            menu = new SAOInventoryGUI(element, menuOffsetX, menuOffsetY, 150, 100, mc.thePlayer.inventoryContainer, SAOInventory.BOWS);
        } else if (id == SAOID.PICKAXE) {
            menu = new SAOInventoryGUI(element, menuOffsetX, menuOffsetY, 150, 100, mc.thePlayer.inventoryContainer, SAOInventory.PICKAXE);
        } else if (id == SAOID.AXE) {
            menu = new SAOInventoryGUI(element, menuOffsetX, menuOffsetY, 150, 100, mc.thePlayer.inventoryContainer, SAOInventory.AXE);
        } else if (id == SAOID.SHOVEL) {
            menu = new SAOInventoryGUI(element, menuOffsetX, menuOffsetY, 150, 100, mc.thePlayer.inventoryContainer, SAOInventory.SHOVEL);
        } else if (id == SAOID.ARMOR) {
            menu = new SAOInventoryGUI(element, menuOffsetX, menuOffsetY, 150, 100, mc.thePlayer.inventoryContainer, SAOInventory.EQUIPMENT);
        } else if (id == SAOID.ACCESSORY) {
            if (SAOInventory.isBaublesLoaded()) menu = new SAOInventoryGUI(element, menuOffsetX, menuOffsetY, 150, 100, mc.thePlayer.inventoryContainer, SAOInventory.ACCESSORY);
            else menu.elements.add(new SAOEmptySlot(menu, 0, 0));
        } else if (id == SAOID.CONSUMABLES) {
            menu = new SAOInventoryGUI(element, menuOffsetX, menuOffsetY, 150, 100, mc.thePlayer.inventoryContainer, SAOInventory.CONSUMABLES);
        }
        //Social
        else if (id == SAOID.PARTY) {
            menu = new SAOMenuGUI(element, menuOffsetX, menuOffsetY, 100, 60);

            menu.elements.add(new SAOPartyGUI(menu, SAOID.INVITE_LIST, 0, 0, StatCollector.translateToLocal("guiInvite"), SAOIcon.INVITE));
            menu.elements.add(new SAOPartyGUI(menu, SAOID.DISSOLVE, 0, 0, StatCollector.translateToLocal("guiDissolve"), SAOIcon.CANCEL));

            sub = SAOSub.resetPartySub(mc, sub);
            info = SAOSub.addInfo(sub);

        } else if (id == SAOID.FRIENDS) {
            setInfo(null, null);
            menu = new SAOFriendsGUI(mc, element, menuOffsetX, menuOffsetY, 100, 100);

            sub = SAOSub.resetFriendsSub(mc, sub);
            info = SAOSub.addInfo(sub);

            infoCaption = null;
            infoText = null;
        }
        //Social -> Party
        else if (id == SAOID.INVITE_LIST) { // TODO: make all of these update in real-time (whole class needs probs massive rewrite)
            menu = new SAOListGUI(element, menuOffsetX, menuOffsetY, 100, 60);

            final SAOMenuGUI mnu = menu;
            if (StaticPlayerHelper.listOnlinePlayers(mc) != null)
                StaticPlayerHelper.listOnlinePlayers(mc, true, 5).stream().map(StaticPlayerHelper::getName).forEach(name -> {
                    final SAOButtonGUI button = new SAOStateButtonGUI(mnu, SAOID.INVITE_PLAYER, 0, 0, name, SAOIcon.INVITE, (mc1, button1) -> !PartyHelper.instance().isMember(button1.caption));
                    button.enabled = !PartyHelper.instance().isMember(name);
                    mnu.elements.add(button);
                });

        }
        //Social -> Friends
        else if ((id == SAOID.FRIEND) && (element instanceof SAOFriendGUI)) {
            setInfo(null, null);
            if (((SAOFriendGUI) element).highlight) {
                System.out.println("Add friends menu request");
                menu = new SAOMenuGUI(element, menuOffsetX, menuOffsetY, 100, 60);
                menu.elements.add(new SAOButtonGUI(menu, SAOID.POSITION_CHECK, 0, 0, StatCollector.translateToLocal("guiPositionCheck"), SAOIcon.FIELD_MAP));
                menu.elements.add(new SAOButtonGUI(menu, SAOID.OTHER_PROFILE, 0, 0, StatCollector.translateToLocal("guiProfile"), SAOIcon.PARTY));
            } else {
                menu = null;
                System.out.println("Add friend request");
                FriendsHandler.instance().addFriendRequests(mc, ((SAOFriendGUI) element).caption);
            }
        } else if (id == SAOID.OTHER_PROFILE && element.parent instanceof SAOMenuGUI && ((SAOMenuGUI) element.parent).parent instanceof SAOFriendGUI) {
            menu = null;

            final EntityPlayer player = StaticPlayerHelper.findOnlinePlayer(mc, ((SAOFriendGUI) ((SAOMenuGUI) element.parent).parent).caption);

            if (player != null) {
                sub = SAOSub.resetProfileSub(mc, sub, player);
                info = SAOSub.addInfo(sub);

                infoCaption = null;
                infoText = null;

                final SAOString[] profile = SAOSub.addProfileContent(player);

                setInfo(profile[0], profile[1]);
            } else setInfo(null, null);
        } else if (id == SAOID.POSITION_CHECK && element.parent instanceof SAOMenuGUI && ((SAOMenuGUI) element.parent).parent instanceof SAOFriendGUI) {
            menu = null;

            final EntityPlayer player = StaticPlayerHelper.findOnlinePlayer(mc, ((SAOFriendGUI) ((SAOMenuGUI) element.parent).parent).caption);

            if (player != null) {
                sub = SAOSub.resetCheckPositionSub(mc, sub, player, 1, null);
                info = SAOSub.addInfo(sub);

                final SAOString[] position = SAOSub.addPositionContent(player, mc.thePlayer);

                setInfo(position[0], position[1]);
            } else setInfo(null, null);
        }
        //Navigation
        else if (id == SAOID.QUESTS) {
            setInfo(null, null);
            menu = null;

            sub = SAOSub.resetQuestsSub(mc, sub, mc.thePlayer);
            info = SAOSub.addInfo(sub);
        } else if (id == SAOID.FIELD_MAP) {
            menu = null;

            sub = SAOSub.resetCheckPositionSub(mc, sub, mc.thePlayer, 4, '-' + StatCollector.translateToLocal("guiFieldMap") + '-');
            info = SAOSub.addInfo(sub);

            final SAOString[] position = SAOSub.addPositionContent(mc.thePlayer, mc.thePlayer);

            setInfo(position[0], position[1]);
        } else if (id == SAOID.DUNGEON_MAP) {
            setInfo(null, null);
            menu = null;

            sub = SAOSub.resetCheckPositionSub(mc, sub, mc.thePlayer, 1, '-' + StatCollector.translateToLocal("guiDungMap") + '-');
            info = SAOSub.addInfo(sub);

            final SAOString[] position = SAOSub.addPositionContent(mc.thePlayer, mc.thePlayer);

            setInfo(position[0], position[1]);
        }
        //Options
        else if (id == SAOID.OPTIONS) {
            menu = new SAOListGUI(element, menuOffsetX, menuOffsetY, 130, 100);

            final SAOMenuGUI mnu = menu;
            Stream.of(SAOOption.values()).filter(opt -> opt.category == null).forEach(option -> mnu.elements.add(new OptionButton(mnu, 0, 0, option)));
        } else if (id == SAOID.OPT_CAT) {
            openOptCat = ((OptionButton) element).getOption();
            menu = new SAOListGUI(element, menuOffsetX, menuOffsetY, 130, 100);

            final SAOMenuGUI mnu = menu;
            Stream.of(SAOOption.values()).filter(opt -> opt.category == openOptCat).forEach(option -> mnu.elements.add((new OptionButton(mnu, 0, 0, option))));
        }
        //Misc
        if (sub != subMenu && subMenu != null) {
            menus.removeIf(entry -> entry.getValue() == subMenu);

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
