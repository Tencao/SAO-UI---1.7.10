package com.tencao.saoui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Stream;

import com.mojang.realmsclient.dto.PlayerInfo;
import com.tencao.saoui.commands.Command;
import com.tencao.saoui.commands.CommandType;
import com.tencao.saoui.ui.SAOConfirmGUI;
import com.tencao.saoui.ui.SAOElementGUI;
import com.tencao.saoui.ui.SAOScreenGUI;
import com.tencao.saoui.ui.SAOWindowGUI;
import com.tencao.saoui.util.*;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkCheckHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.entity.EntityOtherPlayerMP;
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
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

@Mod(modid = SAOMod.MODID, name = SAOMod.NAME, version = SAOMod.VERSION)
@SideOnly(Side.CLIENT)
public class SAOMod {

	public static final String MODID = "saoui";
	public static final String NAME = "Sword Art Online UI";
	public static final String VERSION = "1.3";
	public static final double MAX_RANGE = 256.0D;
	public static final float UNKNOWN_TIME_DELAY = -1F;
	public static boolean IS_SPRINTING = false; // TODO: move somewhere else, maybe make skills have a activate/deactivate thing
	public static boolean IS_SNEAKING = false;
    public static int REPLACE_GUI_DELAY = 0;
	public static boolean replaceGUI;

    @Mod.Instance(MODID)
    public static SAOMod instance;

	public static SAOWindowGUI getWindow(Minecraft mc) {
		return mc.currentScreen != null && mc.currentScreen instanceof SAOWindowViewGUI ? ((SAOWindowViewGUI) mc.currentScreen).getWindow() : null;
	}

	@NetworkCheckHandler()
	public boolean matchModVersions(Map<String, String> remoteVersions, Side side) { // This will at least detect if the server has SAOsoc forge mod. Now to detect plugin
		//System.out.println(side + " handshake.\nContains saoui: " + remoteVersions.containsKey("saoui") + "\nContains saosoc: " + remoteVersions.containsKey("saosoc"));
		return true; // TODO: check if contains SAOSOC, which version?, save to some struct
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		FMLCommonHandler.instance().bus().register(new SAOEventHandler());
		MinecraftForge.EVENT_BUS.register(new SAOEventHandler());

		ConfigHandler.preInit(event);
        FriendsHandler.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		replaceGUI = true;
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent evt) {
        Minecraft mc = Minecraft.getMinecraft();
		FMLCommonHandler.instance().bus().register(new SAORenderHandler());
		MinecraftForge.EVENT_BUS.register(new SAORenderHandler());

        SAOGL.setFont(mc, SAOOption.CUSTOM_FONT.getValue());
	}
}
