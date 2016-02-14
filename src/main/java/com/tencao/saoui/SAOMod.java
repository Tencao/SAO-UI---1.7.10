package com.tencao.saoui;

import com.tencao.saoui.ui.SAOWindowGUI;
import com.tencao.saoui.util.*;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = SAOMod.MODID, name = SAOMod.NAME, version = SAOMod.VERSION)
@SideOnly(Side.CLIENT)
public class SAOMod {

    public static final String MODID = "saoui";
    public static final String NAME = "Sword Art Online UI";
    public static final String VERSION = "1.5";
    public static final double MAX_RANGE = 256.0D;
    public static final float UNKNOWN_TIME_DELAY = -1F;
    public static boolean IS_SPRINTING = false; // TODO: move somewhere else, maybe make skills have a activate/deactivate thing
    public static boolean IS_SNEAKING = false;

    @Mod.Instance(MODID)
    public static SAOMod instance;

    public static SAOWindowGUI getWindow(Minecraft mc) {
        return mc.currentScreen != null && mc.currentScreen instanceof SAOWindowViewGUI ? ((SAOWindowViewGUI) mc.currentScreen).getWindow() : null;
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(new SAOEventHandler());
        MinecraftForge.EVENT_BUS.register(new SAOEventHandler());
        FMLCommonHandler.instance().bus().register(new SAORenderHandler());
        MinecraftForge.EVENT_BUS.register(new SAORenderHandler());

        ConfigHandler.preInit(event);
        FriendsHandler.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        SAORenderHandler.replaceGUI = true;
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent evt) {
        Minecraft mc = Minecraft.getMinecraft();
        SAOGL.setFont(mc, SAOOption.CUSTOM_FONT.getValue());
    }
}
