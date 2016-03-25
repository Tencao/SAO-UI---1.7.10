package com.tencao.saoui;

import com.tencao.saoui.events.ConfigHandler;
import com.tencao.saoui.events.FriendsHandler;
import com.tencao.saoui.events.EventHandler;
import com.tencao.saoui.events.RenderHandler;
import com.tencao.saoui.screens.window.Window;
import com.tencao.saoui.screens.window.WindowView;
import com.tencao.saoui.util.*;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = SAOCore.MODID, name = SAOCore.NAME, version = SAOCore.VERSION)
@SideOnly(Side.CLIENT)
public class SAOCore {

    public static final String MODID = "saoui";
    public static final String NAME = "Sword Art Online UI";
    public static final String VERSION = "1.5.1";
    public static final double MAX_RANGE = 256.0D;
    public static final float UNKNOWN_TIME_DELAY = -1F;
    public static boolean IS_SPRINTING = false; // TODO: move somewhere else, maybe make skills have a activate/deactivate thing
    public static boolean IS_SNEAKING = false;

    @Mod.Instance(MODID)
    public static SAOCore instance;

    public static Window getWindow(Minecraft mc) {
        return mc.currentScreen != null && mc.currentScreen instanceof WindowView ? ((WindowView) mc.currentScreen).getWindow() : null;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(new EventHandler());
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        FMLCommonHandler.instance().bus().register(new RenderHandler());
        MinecraftForge.EVENT_BUS.register(new RenderHandler());

        ConfigHandler.preInit(event);
        FriendsHandler.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        RenderHandler.replaceGUI = true;
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent evt) {
        Minecraft mc = Minecraft.getMinecraft();
        GLCore.setFont(mc, OptionCore.CUSTOM_FONT.getValue());
    }
}
