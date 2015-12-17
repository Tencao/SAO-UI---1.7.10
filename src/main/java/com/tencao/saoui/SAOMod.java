package com.tencao.saoui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.tencao.saoui.ui.SAOConfirmGUI;
import com.tencao.saoui.ui.SAOElementGUI;
import com.tencao.saoui.ui.SAOScreenGUI;
import com.tencao.saoui.ui.SAOWindowGUI;
import com.tencao.saoui.util.SAOAction;
import com.tencao.saoui.util.SAOActionHandler;
import com.tencao.saoui.util.SAOColorCursor;
import com.tencao.saoui.util.SAOColorState;
import com.tencao.saoui.util.SAOCommand;
import com.tencao.saoui.util.SAOFriendRequest;
import com.tencao.saoui.util.SAOID;
import com.tencao.saoui.util.SAOOption;
import com.tencao.saoui.util.SAOResources;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiPlayerInfo;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

@Mod(modid = SAOMod.MODID, name = SAOMod.NAME, version = SAOMod.VERSION)
@SideOnly(Side.CLIENT)
public class SAOMod implements Runnable {

	public static final String MODID = "saoui";
	public static final String NAME = "Sword Art Online UI";
	public static final String VERSION = "1.3";

	private static final double MAX_RANGE = 256.0D;
	private static final float HEALTH_ANIMATION_FACTOR = 0.075F;
    private static final float HEALTH_FRAME_FACTOR = HEALTH_ANIMATION_FACTOR * HEALTH_ANIMATION_FACTOR * 0x40 * 0x64;

	public static final float UNKNOWN_TIME_DELAY = -1F;

	public static boolean IS_SPRINTING = false;
	public static boolean IS_SNEAKING = false;

	public static boolean DEBUG = false;

	private static File friendsFile;
	private static String[] friends;
	private static List<SAOFriendRequest> friendRequests;
	private static String[] party;

	private static String _FRIEND_REQUEST_TITLE;
	private static String _FRIEND_REQUEST_TEXT;

	private static String _PARTY_INVITATION_TITLE;
	private static String _PARTY_INVITATION_TEXT;

	public static String _PARTY_DISSOLVING_TITLE;
	public static String _PARTY_DISSOLVING_TEXT;

	public static String _PARTY_LEAVING_TITLE;
	public static String _PARTY_LEAVING_TEXT;

	public static String _MESSAGE_TITLE;
	public static String _MESSAGE_FROM;

    public static String _DEAD_ALERT;

	private static Thread mcModThread, renderManagerUpdate;

	private static Map<UUID, Float> healthSmooth;
    private static Map<UUID, Float> hungerSmooth;
    private static Map<UUID, SAOColorCursor> colorStates;

	private static int partyTicks;
	private static Configuration config;

    public static int REPLACE_GUI_DELAY = 0;

	private boolean replaceGUI;
    
    @EventHandler
	public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new SAOEventHandler());
        //MinecraftForge.EVENT_BUS.register(new SAOIngameGUI(Minecraft.getMinecraft()));
        
		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		
		friendRequests = new ArrayList<SAOFriendRequest>();
		
		DEBUG = config.get(Configuration.CATEGORY_GENERAL, "debug", DEBUG).getBoolean();
		
		_FRIEND_REQUEST_TITLE = config.get(Configuration.CATEGORY_GENERAL, "friend.request.title", SAOResources.FRIEND_REQUEST_TITLE).getString();
		_FRIEND_REQUEST_TEXT = config.get(Configuration.CATEGORY_GENERAL, "friend.request.text", SAOResources.FRIEND_REQUEST_TEXT).getString();
		
		_PARTY_INVITATION_TITLE = config.get(Configuration.CATEGORY_GENERAL, "party.invitation.title", SAOResources.PARTY_INVITATION_TITLE).getString();
		_PARTY_INVITATION_TEXT = config.get(Configuration.CATEGORY_GENERAL, "party.invitation.text", SAOResources.PARTY_INVITATION_TEXT).getString();
		
		_PARTY_DISSOLVING_TITLE = config.get(Configuration.CATEGORY_GENERAL, "party.dissolving.title", SAOResources.PARTY_DISSOLVING_TITLE).getString();
		_PARTY_DISSOLVING_TEXT = config.get(Configuration.CATEGORY_GENERAL, "party.dissolving.text", SAOResources.PARTY_DISSOLVING_TEXT).getString();
		
		_PARTY_LEAVING_TITLE = config.get(Configuration.CATEGORY_GENERAL, "party.leaving.title", SAOResources.PARTY_LEAVING_TITLE).getString();
		_PARTY_LEAVING_TEXT = config.get(Configuration.CATEGORY_GENERAL, "party.leaving.text", SAOResources.PARTY_LEAVING_TEXT).getString();
		
		_MESSAGE_TITLE = config.get(Configuration.CATEGORY_GENERAL, "message.title", SAOResources.MESSAGE_TITLE).getString();
		_MESSAGE_FROM = config.get(Configuration.CATEGORY_GENERAL, "message.from", SAOResources.MESSAGE_FROM).getString();

        _DEAD_ALERT = config.get(Configuration.CATEGORY_GENERAL, "dead.alert", SAOResources.DEAD_ALERT).getString();

		for (final SAOOption option : SAOOption.values()) {
			option.value = config.get(Configuration.CATEGORY_GENERAL, "option." + option.name().toLowerCase(), option.value).getBoolean();
		}
		
		config.save();
	}


	@EventHandler
	public void init(FMLInitializationEvent event) {
		final Minecraft mc = Minecraft.getMinecraft();
		
		friendsFile = new File(mc.mcDataDir, ".sao_friends");
		
		if (!friendsFile.exists()) {
			writeFriends(friends);
		}
		
		friends = loadFriends();
		
		final RenderManager manager = RenderManager.instance;
		
		if (renderManagerUpdate != null) {
			final Thread thread = renderManagerUpdate;
			renderManagerUpdate = null;
			thread.interrupt();
		}
		
		renderManagerUpdate = new Thread() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				while (manager != null) {
                    try {/*
                        for (final Field field : manager.getClass().getDeclaredFields()) {
                            if (Map.class.isAssignableFrom(field.getType())) {
                                field.setAccessible(true);

                                final Map playerRenderMap = (Map) field.get(manager);

                                for (final Object entry : playerRenderMap.entrySet()) {
                                    final Object value = ((Entry) entry).getValue();

                                    if ((value instanceof Render) && (!(value instanceof SAORenderBase))) {
                                        final Render render = new SAORenderBase((Render) value);
                                        ((Entry) entry).setValue(render);
                                    }
                                }
                            }
                        }  */        	
                
                    	
                    	for (final Object key : manager.entityRenderMap.keySet()) {
                    		if (key instanceof Class<?>) {
                    			final Class<?> class0 = (Class<?>) key;
							
                    			if (EntityLivingBase.class.isAssignableFrom(class0)) {
                    				final Object value = manager.entityRenderMap.get(key);
								
                    				if ((value instanceof Render) && (!(value instanceof SAORenderBase))) {
                    					final Render render = new SAORenderBase((Render) value);
                    					manager.entityRenderMap.put(key, render);
                    					render.setRenderManager(manager);
                    				}
                    			}
                    		}
                    	}
                    } catch (Exception e) {
                    	SAOMod.sleep(1000L);
    				}					
					SAOMod.sleep(10000L);
				}
			}
		
		};
		
		renderManagerUpdate.start();
		
		if (mcModThread != null) {
			final Thread thread = mcModThread;
			mcModThread = null;
			thread.interrupt();
		}
		
		if (healthSmooth == null) {
			healthSmooth = new HashMap<UUID, Float>();
		} else {
			healthSmooth.clear();
		}

        if (hungerSmooth == null) {
            hungerSmooth = new HashMap<UUID, Float>();
        } else {
            hungerSmooth.clear();
        }

        if (colorStates == null) {
            colorStates = new HashMap<UUID, SAOColorCursor>();
        } else {
            colorStates.clear();
        }
		
		replaceGUI = true;
		(mcModThread = new Thread(this)).start();
	}

	private static final boolean sleep(long time) {
		try {
			Thread.sleep(time);
			return true;
		} catch (InterruptedException e) {
			return false;
		}
	}

    @Override
	public void run() {
    	long time = System.currentTimeMillis();
        long lasttime = time;

        long delay;
		while ((mcModThread != null) && (!mcModThread.isInterrupted())) {
			final Minecraft mc = Minecraft.getMinecraft();

            time = System.currentTimeMillis();
            delay = Math.abs(time - lasttime);
            lasttime = time;
            
			if (mc == null) {
				sleep(2500);
				continue;
			}

            for (final SAOColorCursor cursor : colorStates.values()) {
                cursor.update(delay);
            }
            
			if (mc.thePlayer == null) {
				IS_SPRINTING = false;
				IS_SNEAKING = false;
			} else
			if (mc.inGameHasFocus) {
				if (IS_SPRINTING) {
					KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
				}
				
				if (IS_SNEAKING) {
					KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
				}
			}

            if (!SAOOption.DEFAULT_UI.value && mc.ingameGUI != null && (!(mc.ingameGUI instanceof SAOIngameGUI))) {
                mc.ingameGUI = new SAOIngameGUI(mc);
                continue;
            } else if (SAOOption.DEFAULT_UI.value && mc.ingameGUI != null && (mc.ingameGUI instanceof SAOIngameGUI)) {
                mc.ingameGUI = new GuiIngameForge(mc);
                continue;
            }

			if (replaceGUI) {
                if ((mc.currentScreen != null) && (!(mc.currentScreen instanceof SAOScreenGUI))) {
                    if (REPLACE_GUI_DELAY > 0) {
                        REPLACE_GUI_DELAY--;
                    } else if ((mc.currentScreen instanceof GuiIngameMenu) || ((mc.currentScreen instanceof GuiInventory) &&
                            (!SAOOption.DEFAULT_INVENTORY.value))) {
                        final boolean inv = (mc.currentScreen instanceof GuiInventory);

                        mc.currentScreen.mc = mc;
                        if (mc.playerController.isInCreativeMode() && mc.currentScreen instanceof GuiInventory)
                        {
                             mc.displayGuiScreen(new GuiContainerCreative(mc.thePlayer));
                        }
                        else try {
							SAOSound.play(mc, SAOSound.ORB_DROPDOWN);
							mc.displayGuiScreen(new SAOIngameMenuGUI((GuiInventory) (inv ? mc.currentScreen : null)));
							replaceGUI = false;
						} catch (NullPointerException e) {
							continue;
						}
                       
                    } else if ((mc.currentScreen instanceof GuiGameOver) && (!SAOOption.DEFAULT_DEATH_SCREEN.value)) {
                        mc.currentScreen.mc = mc;

                        if (mc.ingameGUI instanceof SAOIngameGUI){
                        	try {
                                mc.displayGuiScreen((GuiScreen)null);
                        		mc.displayGuiScreen(new SAODeathGUI((GuiGameOver) mc.currentScreen));
                        		replaceGUI = false;
                        	}
                        	catch (NullPointerException e) {
                        		continue;
                        	}
                        }
                    }
                }
			} else if ((mc.currentScreen == null) && (mc.inGameHasFocus)) {
                mc.displayGuiScreen((GuiScreen)null);
				replaceGUI = true;
			}
			
			sleep(1);
			
			synchronized (friendRequests) {
				for (int i = friendRequests.size() - 1; i >= 0; i--) {
					if (i >= friendRequests.size()) {
						continue;
					}
					
					if (--friendRequests.get(i).ticks <= 0) {
						friendRequests.remove(i);
					}
				}
			}
			
			if ((party != null) && (partyTicks > 0) && (--partyTicks <= 0)) {
				final String name = getName(mc);
				
				if (party.length <= 1) {
					dissolveParty(mc);
				} else
				if (isPartyLeader(name)) {
					final boolean[] online = isOnline(mc, party);
					final String[] remove = new String[party.length];
					
					int count = 0;
					
					for (int i = 0; i < online.length; i++) {
						if (!online[i]) {
							remove[count++] = party[i];
						}
					}
					
					for (int i = 0; i < count; i++) {
						removeParty(mc, remove[i]);
					}
				} else {
					final String leader = party[0];
					
					if (!isOnline(mc, leader)) {
						dissolveParty(mc);
					}
				}
				
				if (party != null) {
					partyTicks = 1000;
				}
			} else if (party == null) {
				partyTicks = 0;
			}
		}
	}

	public static final List<EntityPlayer> listOnlinePlayers(Minecraft mc, boolean search, double range) {
		final List<EntityPlayer> players = new ArrayList<EntityPlayer>();
		
		if (mc.thePlayer == null) {
			return players;
		}
		
		NetHandlerPlayClient nethandlerplayclient = mc.thePlayer.sendQueue;
        List<?> list = nethandlerplayclient.playerInfoList;

        if (!search) {
        	range = MAX_RANGE;
        }
        
        final AxisAlignedBB box = AxisAlignedBB.getBoundingBox(
            mc.thePlayer.posX - range, mc.thePlayer.posY - range, mc.thePlayer.posZ - range,
            mc.thePlayer.posX + range, mc.thePlayer.posY + range, mc.thePlayer.posZ + range
        );
        
        final List<?> entities = mc.theWorld.getEntitiesWithinAABB(EntityPlayer.class, box);
        
        for (final Object element : list) {
	        if (element instanceof GuiPlayerInfo) {
				final GuiPlayerInfo info = (GuiPlayerInfo) element;
				final String infoName = unformatName(info.name);
				
				for (final Object element0 : entities) {
					if (element0 instanceof EntityPlayer) {
						final EntityPlayer player = (EntityPlayer) element0;
						final String playerName = getName(player);
						
						if (infoName.equals(playerName)) {
							players.add(player);
							break;
						}
					}
				}
	        }
        }
        
        return players;
	}

	public static final List<EntityPlayer> listOnlinePlayers(Minecraft mc) {
		return listOnlinePlayers(mc, true, mc.gameSettings.renderDistanceChunks * 16.0D);
	}

	public static EntityPlayer findOnlinePlayer(Minecraft mc, String username) {
		final List<EntityPlayer> players = listOnlinePlayers(mc);
		
		for (final EntityPlayer player : players) {
			if (getName(player).equals(username)) {
				return player;
			}
		}
		
		return null;
	}

	public static final boolean[] isOnline(Minecraft mc, String[] names) {
		final List<EntityPlayer> players = listOnlinePlayers(mc);
		final boolean[] online = new boolean[names.length];
		
		for (int i = 0; i < names.length; i++) {
			for (final EntityPlayer player : players) {
				if (getName(player).equals(names[i])) {
					online[i] = true;
					break;
				}
			}
		}
		
		return online;
	}

	public static final boolean isOnline(Minecraft mc, String name) {
		return isOnline(mc, new String[] { name })[0];
	}

	public static final void sendSAOCommand(Minecraft mc, SAOCommand command, String username, String... args) {
		if ((mc.thePlayer == null) || (!SAOOption.CLIENT_CHAT_PACKETS.value)) {
			return;
		}
		
		final String format = I18n.format("commands.message.usage", new Object[0]);
		final String cmd = format.substring(0, format.indexOf(' '));
		
		final String message = SAOJ8String.join(" ", cmd, username, String.valueOf(command), SAOJ8String.join(" ", args));
		
		mc.thePlayer.sendChatMessage(message);
	}

	public static final void receiveSAOCommand(final Minecraft mc, SAOCommand command, final String username, final String... args) {
		if ((mc.thePlayer == null) || (!SAOOption.CLIENT_CHAT_PACKETS.value)) {
			return;
		}
		
		if (command == SAOCommand.INVITE_PARTY) {
			if (!isPartyMember(getName(mc))) {
				final GuiScreen keepScreen = mc.currentScreen;
				final boolean ingameFocus = mc.inGameHasFocus;
				
				final String text = String.format(_PARTY_INVITATION_TEXT, username);
				
				mc.displayGuiScreen(SAOWindowViewGUI.viewConfirm(_PARTY_INVITATION_TITLE, text, new SAOActionHandler() {

					public void actionPerformed(SAOElementGUI element, SAOAction action, int data) {
						final SAOID id = element.ID();
						
						if (id == SAOID.CONFIRM) {
							party = args.length > 0? args : null;
							
							if (party != null) {
								partyTicks = 1000;
							}
							
							sendSAOCommand(mc, SAOCommand.CONFIRM_INVITE_PARTY, username);
						} else {
							sendSAOCommand(mc, SAOCommand.CANCEL_INVITE_PARTY, username);
						}
						
						mc.displayGuiScreen(keepScreen);
						
						if (ingameFocus) {
							mc.setIngameFocus();
						} else {
							mc.setIngameNotInFocus();
						}
					}

				}));
				
				if (ingameFocus) {
					mc.setIngameNotInFocus();
				}
			}
		} else
		if (command == SAOCommand.DISSOLVE_PARTY) {
			if (isPartyLeader(getName(mc))) {
				removeParty(mc, username);
			} else
			if (isPartyLeader(username)) {
				final SAOWindowGUI window = getWindow(mc);
				
				if ((window != null) && (window.getTitle().equals(_PARTY_INVITATION_TITLE)) && (window instanceof SAOConfirmGUI)) {
					((SAOConfirmGUI) window).cancel();
				}
				
				party = null;
			}
		} else
		if (command == SAOCommand.UPDATE_PARTY) {
			if (isPartyLeader(username)) {
				party = args.length <= 1? null : args;
			}
		} else
		if (command == SAOCommand.CONFIRM_INVITE_PARTY) {
			if (isPartyLeader(getName(mc))) {
				final boolean inParty = isPartyMember(username);
				
				if ((inParty) && (args.length > 0)) {
					addParty(mc, args[0]);
				} else {
					addParty(mc, username);
				}
			} else if (isPartyMember(getName(mc))) {
				sendSAOCommand(mc, command, party[0], username);
			} else {
				sendSAOCommand(mc, SAOCommand.DISSOLVE_PARTY, username);
			}
		} else
		if (command == SAOCommand.ADD_FRIEND_REQUEST) {
			if (!isFriend(username)) {
				final GuiScreen keepScreen = mc.currentScreen;
				final boolean ingameFocus = mc.inGameHasFocus;
				
				final String text = String.format(_FRIEND_REQUEST_TEXT, username);
				
				mc.displayGuiScreen(SAOWindowViewGUI.viewConfirm(_FRIEND_REQUEST_TITLE, text, new SAOActionHandler() {

					public void actionPerformed(SAOElementGUI element, SAOAction action, int data) {
						final SAOID id = element.ID();
						
						if ((id == SAOID.CONFIRM) && ((isFriend(username)) || (addFriends(username)))) {
							sendSAOCommand(mc, SAOCommand.ACCEPT_ADD_FRIEND, username);
						} else {
							sendSAOCommand(mc, SAOCommand.CANCEL_ADD_FRIEND, username);
						}
						
						mc.displayGuiScreen(keepScreen);
						
						if (ingameFocus) {
							mc.setIngameFocus();
						} else {
							mc.setIngameNotInFocus();
						}
					}

				}));
				
				if (ingameFocus) {
					mc.setIngameNotInFocus();
				}
			} else {
				sendSAOCommand(mc, SAOCommand.ACCEPT_ADD_FRIEND, username);
			}
		} else
		if (command == SAOCommand.ACCEPT_ADD_FRIEND) {
			synchronized (friendRequests) {
				int index = -1;
				
				for (int i = 0; i < friendRequests.size(); i++) {
					if (friendRequests.get(i).equals(username)) {
						index = i;
						break;
					}
				}
				
				if (index >= 0) {
					if ((isFriend(username)) || (addFriends(username))) {
						friendRequests.remove(index);
					}
				}
			}
		} else
		if (command == SAOCommand.CANCEL_ADD_FRIEND) {
			synchronized (friendRequests) {
				int index = -1;
				
				for (int i = 0; i < friendRequests.size(); i++) {
					if (friendRequests.get(i).equals(username)) {
						index = i;
						break;
					}
				}
				
				if (index >= 0) {
					friendRequests.remove(index);
				}
			}
		}
	}

	private static final String[] loadFriends() {
		try {
			final FileInputStream stream = new FileInputStream(friendsFile);
			final String[] friends;
			
			if (stream.available() != 0) {
				final int count = (stream.read() & 0xFF);
				
				friends = new String[count];
				
				for (int i = 0; i < count; i++) {
					final int length = (stream.read() & 0xFF);
					final byte[] bytes = new byte[length];
					
					stream.read(bytes, 0, length);
					
					friends[i] = new String(bytes);
				}
			} else {
				friends = new String[0];
			}
			
			stream.close();
			
			return friends;
		} catch (IOException e) {
			return new String[0];
		}
	}

	public static final String[] listFriends() {
		if (friends == null) {
			friends = loadFriends();
		}
		
		return friends;
	}

	public static final void addFriendRequest(Minecraft mc, String... names) {
		synchronized (friendRequests) {
			for (final String name : names) {
				if ((!friendRequests.contains(name)) && (!isFriend(name))) {
					friendRequests.add(new SAOFriendRequest(name, 10000));
					sendSAOCommand(mc, SAOCommand.ADD_FRIEND_REQUEST, name);
				}
			}
		}
	}

	public static final boolean addFriends(String... names) {
		friends = listFriends();
		final String[] newNames = new String[names.length];
		int index = 0;
		
		for (int i = 0; i < names.length; i++) {
			boolean found = false;
			
			for (int j = 0; j < friends.length; j++) {
				if (friends[j].equals(names[i])) {
					found = true;
					break;
				}
			}
			
			if (!found) {
				newNames[index++] = names[i];
			}
		}
		
		if (index > 0) {
			final String[] resized = new String[index];
			System.arraycopy(newNames, 0, resized, 0, resized.length);
			return addRawFriends(resized);
		} else {
			return true;
		}
	}

	public static final boolean isFriend(String name) {
		for (final String friend : listFriends()) {
			if (name.equals(friend)) {
				return true;
			}
		}
		
		return false;
	}

	public static final boolean isFriend(EntityPlayer player) {
		return isFriend(getName(player));
	}

	private static final boolean addRawFriends(String[] names) {
		friends = listFriends();
		
		final String[] resized = new String[friends.length + names.length];
		
		System.arraycopy(friends, 0, resized, 0, friends.length);
		System.arraycopy(names, 0, resized, friends.length, names.length);
		
		if (writeFriends(resized)) {
			friends = resized;
			return true;
		} else {
			return false;
		}
	}

	private static final boolean writeFriends(String[] friends) {
		final String[] data = friends == null? new String[0] : friends;
		
		synchronized (friendsFile) {
			try {
				final FileOutputStream stream = new FileOutputStream(friendsFile);
				
				final int count = (data.length % 0x100);
				stream.write(count);
				
				for (int i = 0; i < count; i++) {
					final byte[] bytes = data[i].getBytes();
					final int length = (bytes.length % 0x100);
					
					stream.write(length);
					stream.write(bytes, 0, length);
				}
				
				stream.flush();
				stream.close();
				
				return true;
			} catch (IOException e) {
				if (SAOMod.DEBUG) {
					System.out.println(e);
				}
				
				return false;
			}
		}
	}

	public static final String[] listPartyMembers() {
		return party;
	}

	public static final boolean isPartyMember(String username) {
		if (party != null) {
			for (final String member : party) {
				if (member.equals(username)) {
					return true;
				}
			}
		}
		
		return false;
	}

	public static final boolean isPartyLeader(String username) {
		return (party != null) && (party[0].equals(username));
	}

	private static final void addParty(Minecraft mc, String username) {
		if ((party != null) && (!isPartyMember(username))) {
			final String[] resized = new String[party.length + 1];
			
			System.arraycopy(party, 0, resized, 0, party.length);
			resized[party.length] = username;
			
			party = resized;
			
			updateParty(mc);
		}
	}

	private static final void removeParty(Minecraft mc, String username) {
		if (isPartyMember(username)) {
			final String[] resized = new String[party.length - 1];
			int index = 0;
			
			for (final String member : party) {
				if (!member.equals(username)) {
					resized[index++] = member;
				}
			}
			
			if (resized.length > 1) {
				party = resized;
				updateParty(mc);
			} else {
				party = null;
			}
		}
	}

	private static final void updateParty(Minecraft mc) {
		if (party != null) {
			final String memberString = SAOJ8String.join(" ", party);
			
			for (final String member : party) {
				if (!member.equals(getName(mc))) {
					sendSAOCommand(mc, SAOCommand.UPDATE_PARTY, member, memberString);
				}
			}
		}
	}

	public static final boolean createParty(Minecraft mc, double range) {
		final List<EntityPlayer> found = listOnlinePlayers(mc, true, range);
		
		if (found.contains(mc.thePlayer)) {
			found.remove(mc.thePlayer);
		}
		
		if (found.size() > 0) {
			party = new String[] {
				getName(mc)
			};
			
			partyTicks = 10000;
			
			for (final EntityPlayer player : found) {
				inviteParty(mc, getName(player));
			}
			
			return true;
		} else {
			return false;
		}
	}

	public static final void inviteParty(Minecraft mc, String username) {
		if ((party != null) && (!isPartyMember(username))) {
			sendSAOCommand(mc, SAOCommand.INVITE_PARTY, username, party[0]);
		}
	}

	public static final void dissolveParty(Minecraft mc) {
		if (party != null) {
			if (party[0].equals(getName(mc))) {
				for (int i = 1; i < party.length; i++) {
					sendSAOCommand(mc, SAOCommand.DISSOLVE_PARTY, party[i]);
				}
			} else {
				sendSAOCommand(mc, SAOCommand.DISSOLVE_PARTY, party[0]);
			}
		}
		
		partyTicks = 0;
		party = null;
	}

	public static final String getName(EntityPlayer player) {
        return player == null ? "" : player.getDisplayName();
	}

	public static final String getName(Minecraft mc) {
		return getName(mc.thePlayer);
	}

	public static final String unformatName(String name) {
		int index = name.indexOf("§");
		
		while (index != -1) {
			if (index + 1 < name.length()) {
				name = name.replace(name.substring(index, index + 2), "");
			} else {
				name = name.replace("§", "");
			}
			
			index = name.indexOf("§");
		}
		
		return name;
	}

	public static final SAOWindowGUI getWindow(Minecraft mc) {
		if ((mc.currentScreen != null) && (mc.currentScreen instanceof SAOWindowViewGUI)) {
			return ((SAOWindowViewGUI) mc.currentScreen).getWindow();
		} else {
			return null;
		}
	}

	public static final float getHealth(final Minecraft mc, final Entity entity, final float time) {
		if (SAOOption.SMOOTH_HEALTH.value) {
			final float healthReal;
			final UUID uuid = entity.getUniqueID();
			
			if (entity instanceof EntityLivingBase) {
				healthReal = ((EntityLivingBase) entity).getHealth();
			} else {
				healthReal = entity.isDead? 0F : 1F;
			}
			
			if (healthSmooth.containsKey(uuid)) {
				float healthValue = healthSmooth.get(uuid);
				
				if ((healthReal <= 0) && (entity instanceof EntityLivingBase)) {
					final float value = (float) (18 - ((EntityLivingBase) entity).deathTime) / 18;
					
					if (value <= 0) {
						healthSmooth.remove(uuid);
					}
					
					return healthValue * value;
				} else
				if (Math.round(healthValue * 10) != Math.round(healthReal * 10)) {
					healthValue = healthValue + (healthReal - healthValue) * (gameTimeDelay(mc, time) * HEALTH_ANIMATION_FACTOR);
				} else {
					healthValue = healthReal;
				}
				
				healthSmooth.put(uuid, healthValue);
				return healthValue;
			} else {
				healthSmooth.put(uuid, healthReal);
				return healthReal;
			}
		} else {
			return (entity instanceof EntityLivingBase? ((EntityLivingBase) entity).getHealth() : (entity.isDead? 0F : 1F));
		}
	}

	private static final int gameFPS(Minecraft mc) {
		final List<String> output = new ArrayList<String>();
		
		if (SAONewChatGUI.reformat(mc.debug, "%s fps, %s chunk updates", output)) {
			try {
				return Integer.parseInt(output.get(0));
			} catch (NumberFormatException e) {
				return mc.getLimitFramerate();
			}
		} else {
			return mc.getLimitFramerate();
		}
	}

	private static final float gameTimeDelay(Minecraft mc, float time) {
		if (time >= 0F) {
			return time;
		} else {
			return HEALTH_FRAME_FACTOR / gameFPS(mc);
		}
	}

	public static final float getMaxHealth(final Entity entity) {
		return entity instanceof EntityLivingBase? ((EntityLivingBase) entity).getMaxHealth() : 1F;
	}
	
    public static float getHungerFract(final Minecraft mc, final Entity entity, final float time) {
        if (!(entity instanceof EntityPlayer)) return 1.0F;
        EntityPlayer player = (EntityPlayer) entity;
        final float hunger;
        if (SAOOption.SMOOTH_HEALTH.value) {
            final UUID uuid = entity.getUniqueID();

            hunger = player.getFoodStats().getFoodLevel();

            if (hungerSmooth.containsKey(uuid)) {
                float hungerValue = hungerSmooth.get(uuid);

                if (hunger <= 0) {
                    final float value = (float) (18 - player.deathTime) / 18;

                    if (value <= 0) hungerSmooth.remove(uuid);

                    return hungerValue * value;
                } else if (Math.round(hungerValue * 10) != Math.round(hunger * 10))
                    hungerValue = hungerValue + (hunger - hungerValue) * (gameTimeDelay(mc, time) * HEALTH_ANIMATION_FACTOR);
                else hungerValue = hunger;

                hungerSmooth.put(uuid, hungerValue);
                return hungerValue / 20.0F;
            } else {
                hungerSmooth.put(uuid, hunger);
                return hunger / 20.0F;
            }
        } else return player.getFoodStats().getFoodLevel() / 20.0F;
    }

    public static SAOColorState getColorState(final EntityPlayer entity) {
        final UUID uuid = entity.getUniqueID();

        if (!colorStates.containsKey(uuid)) {
            colorStates.put(uuid, new SAOColorCursor());
        }

        return colorStates.get(uuid).get();
    }
    
    public static void onDamagePlayer(final EntityPlayer entity) {
        final UUID uuid = entity.getUniqueID();

        if (colorStates.containsKey(uuid)) {
            colorStates.get(uuid).set(SAOColorState.VIOLENT);
        } else {
            colorStates.put(uuid, new SAOColorCursor(SAOColorState.VIOLENT, true));
        }
    }
    
    public static void onKillPlayer(final EntityPlayer entity) {
        final UUID uuid = entity.getUniqueID();

        if (colorStates.containsKey(uuid)) {
            colorStates.get(uuid).set(SAOColorState.KILLER);
        } else {
            colorStates.put(uuid, new SAOColorCursor(SAOColorState.KILLER, true));
        }
    }

	public static final void setOption(SAOOption option) {
		config.get(Configuration.CATEGORY_GENERAL, "option." + option.name().toLowerCase(), option.value).set(option.value);
	}

	public static final void saveAllOptions() {
		config.save();
	}

	public static boolean isCreative(AbstractClientPlayer player) {
        boolean check = player.capabilities.isCreativeMode;
        return check;
    }
}
