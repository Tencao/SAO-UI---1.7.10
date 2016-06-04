package com.saomc;

import com.saomc.events.*;
import com.saomc.screens.window.Window;
import com.saomc.screens.window.WindowView;
import com.saomc.util.UpdateChecker;
import com.saomc.util.OptionCore;
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
    public static final String VERSION = "1.7.10-1.6.0-Dev1";
    public static final float UNKNOWN_TIME_DELAY = -1F;

    @Mod.Instance(MODID)
    public static SAOCore instance;

    public static Window getWindow(Minecraft mc) {
        return mc.currentScreen != null && mc.currentScreen instanceof WindowView ? ((WindowView) mc.currentScreen).getWindow() : null;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(new EventCore());
        MinecraftForge.EVENT_BUS.register(new EventCore());

        ConfigHandler.preInit(event);
        FriendsHandler.preInit(event);

        if (!UpdateChecker.hasChecked())
            new UpdateChecker().start();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent evt) {
        Minecraft mc = Minecraft.getMinecraft();
        GLCore.setFont(mc, OptionCore.CUSTOM_FONT.getValue());
    }
}
