package com.saomc.screens.menu;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.saomc.GLCore;
import com.saomc.colorstates.CursorStatus;
import com.saomc.resources.StringNames;
import com.saomc.screens.*;
import com.saomc.screens.buttons.*;
import com.saomc.util.ColorUtil;
import com.saomc.util.IconCore;
import com.saomc.util.LogCore;
import com.saomc.util.OptionCore;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.Language;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.ForgeHooksClient;
import org.apache.commons.io.Charsets;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Stream;

public class MainMenuGUI extends GuiMainMenu implements ParentElement {

    private float rotationYaw;
    private float rotationPitch;
    private float prevRotationYaw;
    private float prevRotationPitch;
    private static final float ROTATION_FACTOR = 0.50F;
    protected static CursorStatus CURSOR_STATUS = CursorStatus.SHOW;
    private final Cursor emptyCursor;
    private int mouseX, mouseY;
    private int mouseDown;
    private float mouseDownValue;
    private boolean cursorHidden = false;
    private boolean lockCursor = false;
    private final List<Map.Entry<Categories, MenuSlotGUI>> menus;
    private final List<Map.Entry<Categories, MenuGUI>> submenus;
    protected final List<Elements> elements;
    private int flowY;
    private int flowX, jumpX;

    /** The RNG used by the Main Menu Screen. */
    private static final Random rand = new Random();
    /** Counts the number of screen updates. */
    private float updateCounter;
    /** The splash message. */
    private String splashText;
    /** Texture allocated for the current viewport of the main menu's panorama background. */
    private DynamicTexture viewportTexture;
    private static final ResourceLocation splashTexts = new ResourceLocation("texts/splashes.txt");
    private static final ResourceLocation minecraftTitleTextures = new ResourceLocation("textures/gui/title/minecraft.png");
    /** An array of all the paths to the panorama pictures. */
    private static final ResourceLocation[] titlePanoramaPaths = new ResourceLocation[] {new ResourceLocation("textures/gui/title/background/panorama_0.png"), new ResourceLocation("textures/gui/title/background/panorama_1.png"), new ResourceLocation("textures/gui/title/background/panorama_2.png"), new ResourceLocation("textures/gui/title/background/panorama_3.png"), new ResourceLocation("textures/gui/title/background/panorama_4.png"), new ResourceLocation("textures/gui/title/background/panorama_5.png")};
    private ResourceLocation field_110351_G;

    public MainMenuGUI() {
        menus = new ArrayList<>();
        submenus = new ArrayList<>();
        elements = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = new Cursor(1, 1, 0, 0, 1, BufferUtils.createIntBuffer(1), null);
        } catch (LWJGLException e) {
            e.printStackTrace();
        } finally {
            emptyCursor = cursor;
        }

        this.splashText = "missingno";
        BufferedReader bufferedreader = null;

        try
        {
            ArrayList arraylist = new ArrayList();
            bufferedreader = new BufferedReader(new InputStreamReader(Minecraft.getMinecraft().getResourceManager().getResource(splashTexts).getInputStream(), Charsets.UTF_8));
            String s;

            while ((s = bufferedreader.readLine()) != null)
            {
                s = s.trim();

                if (!s.isEmpty())
                {
                    arraylist.add(s);
                }
            }

            if (!arraylist.isEmpty())
            {
                do
                {
                    this.splashText = (String)arraylist.get(rand.nextInt(arraylist.size()));
                }
                while (this.splashText.hashCode() == 125780783);
            }
        }
        catch (IOException ioexception1)
        {}
        finally
        {
            if (bufferedreader != null)
            {
                try
                {
                    bufferedreader.close();
                }
                catch (IOException ioexception)
                {}
            }
        }

        this.updateCounter = rand.nextFloat();
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    @Override
    public void drawScreen(int cursorX, int cursorY, float partialTicks)
    {
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        this.renderSkybox(mouseX, mouseY, partialTicks);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        Tessellator tessellator = Tessellator.instance;
        short short1 = 274;
        int k = this.width / 2 - short1 / 2;
        byte b0 = 30;
        this.drawGradientRect(0, 0, this.width, this.height, -2130706433, 16777215);
        this.drawGradientRect(0, 0, this.width, this.height, 0, Integer.MIN_VALUE);
        this.mc.getTextureManager().bindTexture(minecraftTitleTextures);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        if ((double)this.updateCounter < 1.0E-4D)
        {
            this.drawTexturedModalRect(k, b0, 0, 0, 99, 44);
            this.drawTexturedModalRect(k + 99, b0, 129, 0, 27, 44);
            this.drawTexturedModalRect(k + 99 + 26, b0, 126, 0, 3, 44);
            this.drawTexturedModalRect(k + 99 + 26 + 3, b0, 99, 0, 26, 44);
            this.drawTexturedModalRect(k + 155, b0, 0, 45, 155, 44);
        }
        else
        {
            this.drawTexturedModalRect(k, b0, 0, 0, 155, 44);
            this.drawTexturedModalRect(k + 155, b0, 0, 45, 155, 44);
        }

        tessellator.setColorOpaque_I(-1);
        GL11.glPushMatrix();
        GL11.glTranslatef((float)(this.width / 2 + 90), 70.0F, 0.0F);
        GL11.glRotatef(-20.0F, 0.0F, 0.0F, 1.0F);
        float f1 = 1.8F - MathHelper.abs(MathHelper.sin((float)(Minecraft.getSystemTime() % 1000L) / 1000.0F * (float)Math.PI * 2.0F) * 0.1F);
        f1 = f1 * 100.0F / (float)(this.fontRendererObj.getStringWidth(this.splashText) + 32);
        GL11.glScalef(f1, f1, f1);
        this.drawCenteredString(this.fontRendererObj, this.splashText, 0, -8, -256);
        GL11.glPopMatrix();

        List<String> brandings = Lists.reverse(FMLCommonHandler.instance().getBrandings(true));
        for (int i = 0; i < brandings.size(); i++)
        {
            String brd = brandings.get(i);
            if (!Strings.isNullOrEmpty(brd))
            {
                this.drawString(this.fontRendererObj, brd, 2, this.height - ( 10 + i * (this.fontRendererObj.FONT_HEIGHT + 1)), 16777215);
            }
        }
        ForgeHooksClient.renderMainMenu(this, fontRendererObj, width, height);
        String s1 = "Copyright Mojang AB. Do not distribute!";
        this.drawString(this.fontRendererObj, s1, this.width - this.fontRendererObj.getStringWidth(s1) - 2, this.height - 10, -1);

        if (this.elements == null) return;
        for (Elements el: this.elements) if (el == null) return;
        mouseX = cursorX;
        mouseY = cursorY;

        rotationYaw = getCursorX() * ROTATION_FACTOR;
        rotationPitch = getCursorY() * ROTATION_FACTOR;

        int j;

        for (j = 0; j < this.buttonList.size(); ++j)
        {
            ((GuiButton)this.buttonList.get(j)).drawButton(this.mc, mouseX, mouseY);
        }

        for (j = 0; j < this.labelList.size(); ++j)
        {
            ((GuiLabel)this.labelList.get(j)).drawLabel(this.mc, mouseX, mouseY);
        }

        GLCore.glStartUI(mc);

        for (int i = elements.size() - 1; i >= 0; i--) elements.get(i).draw(mc, cursorX, cursorY);

        if (CURSOR_STATUS == CursorStatus.SHOW) {
            GLCore.glBlend(true);
            GLCore.tryBlendFuncSeparate(770, 771, 1, 0);
            GLCore.glBindTexture(OptionCore.SAO_UI.getValue() ? StringNames.gui : StringNames.guiCustom);

            if (mouseDown != 0) {
                final float fval = partialTicks * 0.1F;

                if (mouseDownValue + fval < 1.0F) mouseDownValue += fval;
                else mouseDownValue = 1.0F;

                GLCore.glColorRGBA(ColorUtil.CURSOR_COLOR.multiplyAlpha(mouseDownValue));
                GLCore.glTexturedRect(cursorX - 7, cursorY - 7, 35, 115, 15, 15);

                GLCore.glColorRGBA(ColorUtil.DEFAULT_COLOR);
            } else {
                mouseDownValue = 0;

                GLCore.glColorRGBA(ColorUtil.CURSOR_COLOR);
            }

            GLCore.glTexturedRect(cursorX - 7, cursorY - 7, 20, 115, 15, 15);
        }

        GLCore.glEndUI(mc);
    }

    /**
     * Draws the main menu panorama
     */
    private void drawPanorama(int x, int y, float ticks)
    {
        Tessellator tessellator = Tessellator.instance;
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        Project.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        byte b0 = 8;

        for (int k = 0; k < b0 * b0; ++k)
        {
            GL11.glPushMatrix();
            float f1 = ((float)(k % b0) / (float)b0 - 0.5F) / 64.0F;
            float f2 = ((float)(k / b0) / (float)b0 - 0.5F) / 64.0F;
            float f3 = 0.0F;
            GL11.glRotatef(prevRotationPitch + (rotationPitch - prevRotationPitch) * ticks, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(prevRotationYaw + (rotationYaw - prevRotationYaw) * ticks -90.0F, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(f1, f2, f3);

            for (int l = 0; l < 6; ++l)
            {
                GL11.glPushMatrix();

                if (l == 1)
                {
                    GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
                }

                if (l == 2)
                {
                    GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                }

                if (l == 3)
                {
                    GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
                }

                if (l == 4)
                {
                    GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
                }

                if (l == 5)
                {
                    GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                }

                this.mc.getTextureManager().bindTexture(titlePanoramaPaths[l]);
                tessellator.startDrawingQuads();
                tessellator.setColorRGBA_I(16777215, 255 / (k + 1));
                float f4 = 0.0F;
                tessellator.addVertexWithUV(-1.0D, -1.0D, 1.0D, (double)(0.0F + f4), (double)(0.0F + f4));
                tessellator.addVertexWithUV(1.0D, -1.0D, 1.0D, (double)(1.0F - f4), (double)(0.0F + f4));
                tessellator.addVertexWithUV(1.0D, 1.0D, 1.0D, (double)(1.0F - f4), (double)(1.0F - f4));
                tessellator.addVertexWithUV(-1.0D, 1.0D, 1.0D, (double)(0.0F + f4), (double)(1.0F - f4));
                tessellator.draw();
                GL11.glPopMatrix();
            }

            GL11.glPopMatrix();
            GL11.glColorMask(true, true, true, false);
        }

        tessellator.setTranslation(0.0D, 0.0D, 0.0D);
        GL11.glColorMask(true, true, true, true);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPopMatrix();
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    /**
     * Rotate and blurs the skybox view in the main menu
     */
    private void rotateAndBlurSkybox(float p_73968_1_)
    {
        this.mc.getTextureManager().bindTexture(this.field_110351_G);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, 256, 256);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColorMask(true, true, true, false);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        byte b0 = 3;

        for (int i = 0; i < b0; ++i)
        {
            tessellator.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F / (float)(i + 1));
            int j = this.width;
            int k = this.height;
            float f1 = (float)(i - b0 / 2) / 256.0F;
            tessellator.addVertexWithUV((double)j, (double)k, (double)this.zLevel, (double)(0.0F + f1), 1.0D);
            tessellator.addVertexWithUV((double)j, 0.0D, (double)this.zLevel, (double)(1.0F + f1), 1.0D);
            tessellator.addVertexWithUV(0.0D, 0.0D, (double)this.zLevel, (double)(1.0F + f1), 0.0D);
            tessellator.addVertexWithUV(0.0D, (double)k, (double)this.zLevel, (double)(0.0F + f1), 0.0D);
        }

        tessellator.draw();
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glColorMask(true, true, true, true);
    }

    /**
     * Renders the skybox in the main menu
     */
    private void renderSkybox(int p_73971_1_, int p_73971_2_, float p_73971_3_)
    {
        this.mc.getFramebuffer().unbindFramebuffer();
        GL11.glViewport(0, 0, 256, 256);
        this.drawPanorama(p_73971_1_, p_73971_2_, p_73971_3_);
        this.rotateAndBlurSkybox(p_73971_3_);
        this.rotateAndBlurSkybox(p_73971_3_);
        this.rotateAndBlurSkybox(p_73971_3_);
        this.rotateAndBlurSkybox(p_73971_3_);
        this.rotateAndBlurSkybox(p_73971_3_);
        this.rotateAndBlurSkybox(p_73971_3_);
        this.rotateAndBlurSkybox(p_73971_3_);
        this.mc.getFramebuffer().bindFramebuffer(true);
        GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        float f1 = this.width > this.height ? 120.0F / (float)this.width : 120.0F / (float)this.height;
        float f2 = (float)this.height * f1 / 256.0F;
        float f3 = (float)this.width * f1 / 256.0F;
        tessellator.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F);
        int k = this.width;
        int l = this.height;
        tessellator.addVertexWithUV(0.0D, (double)l, (double)this.zLevel, (double)(0.5F - f2), (double)(0.5F + f3));
        tessellator.addVertexWithUV((double)k, (double)l, (double)this.zLevel, (double)(0.5F - f2), (double)(0.5F - f3));
        tessellator.addVertexWithUV((double)k, 0.0D, (double)this.zLevel, (double)(0.5F + f2), (double)(0.5F - f3));
        tessellator.addVertexWithUV(0.0D, 0.0D, (double)this.zLevel, (double)(0.5F + f2), (double)(0.5F + f3));
        tessellator.draw();
    }

    @Override
    public void initGui() {
        if (CURSOR_STATUS != CursorStatus.DEFAULT) hideCursor();

        init();

        this.viewportTexture = new DynamicTexture(256, 256);
        this.field_110351_G = this.mc.getTextureManager().getDynamicTextureLocation("background", this.viewportTexture);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        if (calendar.get(2) + 1 == 11 && calendar.get(5) == 9)
        {
            this.splashText = "Happy birthday, ez!";
        }
        else if (calendar.get(2) + 1 == 6 && calendar.get(5) == 1)
        {
            this.splashText = "Happy birthday, Notch!";
        }
        else if (calendar.get(2) + 1 == 12 && calendar.get(5) == 24)
        {
            this.splashText = "Merry X-mas!";
        }
        else if (calendar.get(2) + 1 == 1 && calendar.get(5) == 1)
        {
            this.splashText = "Happy new year!";
        }
        else if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31)
        {
            this.splashText = "OOoooOOOoooo! Spooky!";
        }

    }

    public void init() {
        rotationYaw = 0;
        rotationPitch = 0;
        prevRotationPitch = 0;
        prevRotationYaw = 0;
        menus.clear();
        submenus.clear();
        elements.clear();

        IconGUI action1, action2, action;

        elements.add(action1 = new IconGUI(this, Categories.SINGLEPLAYER, 0, 0, IconCore.PROFILE));
        elements.add(action2 = new IconGUI(action1, Categories.MULTIPLAYER, 0, 24, IconCore.SOCIAL));
        elements.add(action = new IconGUI(action2, Categories.REALMS, 0, 24, IconCore.GUILD));
        elements.add(action = new IconGUI(action, Categories.MODS, 0, 24, IconCore.CRAFTING));
        elements.add(action = new IconGUI(action, Categories.LANGUAGES, 0, 24, IconCore.MESSAGE));
        elements.add(action = new IconGUI(action, Categories.OPTIONS, 0, 24, IconCore.SETTINGS));
        elements.add(action = new IconGUI(action, Categories.QUIT, 0, 24, IconCore.LOGOUT));

        flowY = -height;

    }

    private int getCursorX() {
        if (OptionCore.CURSOR_TOGGLE.getValue()) return lockCursor ? 0 : (width / 2 - mouseX) / 2;
        else return !super.isCtrlKeyDown() ? (width / 2 - mouseX) / 2 : 0;
    }

    private int getCursorY() {
        if (OptionCore.CURSOR_TOGGLE.getValue()) return lockCursor ? 0 : (height / 2 - mouseY) / 2;
        else return !super.isCtrlKeyDown() ? (height / 2 - mouseY) / 2 : 0;
    }

    @Override
    public int getX(boolean relative) {
        return getCursorX() + width * 2 / 5 + (flowX - jumpX) / 2;
    }

    @Override
    public int getY(boolean relative) {
        return getCursorY() + flowY;
    }

    private void moveX(final int mode, final MenuGUI menu) {
        final int value = menu.x > 0 ? menu.x + menu.width : menu.x;

        jumpX += mode * value;
        flowX += mode * value;
    }

    private void openMenu(Elements element, Categories id) {
        final int menuOffsetX = element.width + 14;
        final int menuOffsetY = element.height / 2;

        MenuSlotGUI menu = null;
        if (id == Categories.SINGLEPLAYER) {

            menu = new MenuSlotGUI(element, menuOffsetX, menuOffsetY);

            menu.elements.add(new ButtonSlotGUI(menu, Categories.NEWGAME, 0, 0, StatCollector.translateToLocal("New Game"), IconCore.INVITE));
            menu.elements.add(new ButtonSlotGUI(menu, Categories.LOADGAME, 0, 0, StatCollector.translateToLocal("Load Game"), IconCore.MESSAGE_RECEIVED));
            menu.elements.add(new ButtonGUI(menu, Categories.DELETEGAME, 0, 0, StatCollector.translateToLocal("Delete Game"), IconCore.CANCEL));
        }
        else if (id == Categories.NEWGAME){

        }
        else if (id == Categories.LANGUAGES){
            LogCore.logInfo("Languages called");
            menu = new ListGUI(element, menuOffsetX, menuOffsetY, 100, 20);

            final MenuSlotGUI mnu = menu;
            mc.getLanguageManager().getLanguages().stream().forEachOrdered(lang -> {
                final ButtonSlotGUI button = new ButtonSlotGUI(mnu, Categories.LANGUAGE, 0, 0, lang.toString(), IconCore.INVITE);
                button.highlight = lang == mc.getLanguageManager().getCurrentLanguage();
                mnu.elements.add(button);
            });
        }
        if (menu != null) {
            final List<Elements> list;

            list = element.parent != null && element.parent instanceof ContainerGUI ? ((ContainerGUI) element.parent).elements : elements;

            for (final Elements element0 : list) {
                if (element0.ID() == id) {
                    if (element0 instanceof ButtonGUI) {
                        ((ButtonGUI) element0).highlight = true;
                    } else if (element0 instanceof IconGUI) {
                        ((IconGUI) element0).highlight = true;
                    }
                } else element0.enabled = false;
            }
        }

        if (menu != null) openMenu(id, menu);
        else LogCore.logWarn("Invalid menu call");
    }

    private void openMenu(final Categories id, final MenuSlotGUI menu) {
        moveX(+1, menu);

        menus.add(new Map.Entry<Categories, MenuSlotGUI>() {

            @Override
            public MenuSlotGUI setValue(MenuSlotGUI none) {
                return null;
            }

            @Override
            public MenuSlotGUI getValue() {
                return menu;
            }

            @Override
            public Categories getKey() {
                return id;
            }

        });

        elements.add(menu);
    }

    private void openSubMenu(final Categories id, final MenuGUI menu) {
        moveX(+1, menu);

        submenus.add(new Map.Entry<Categories, MenuGUI>() {

            @Override
            public MenuGUI setValue(MenuGUI none) {
                return null;
            }

            @Override
            public MenuGUI getValue() {
                return menu;
            }

            @Override
            public Categories getKey() {
                return id;
            }

        });

        elements.add(menu);
    }

    private void closeMenu(Elements element, Categories id) {
        for (int i = menus.size() - 1; i >= 0; i--) {
            final Map.Entry<Categories, MenuSlotGUI> entry = menus.get(i);

            if (id != Categories.MENU)
                if ((entry.getKey().hasParent(id)) || (entry.getKey() == id)) {

                    moveX(-1, entry.getValue());

                    elements.remove(entry.getValue());
                    menus.remove(i);
                }

        }

        if (id != Categories.MENU)
            if (element != null) {
                final List<Elements> list;

                if (element.parent != null && element.parent instanceof ContainerGUI)
                    list = ((ContainerGUI) element.parent).elements;
                else list = elements;

                for (final Elements element0 : list) {
                    if (element0.ID() == id) {
                        if (element0 instanceof ButtonGUI) ((ButtonGUI) element0).highlight = false;
                        else if (element0 instanceof IconGUI) ((IconGUI) element0).highlight = false;
                    } else element0.enabled = true;
                }
            }

    }

    private void closeSubMenu(Elements element, Categories id) {
        for (int i = submenus.size() - 1; i >= 0; i--) {
            final Map.Entry<Categories, MenuGUI> entry = submenus.get(i);

            if (id != Categories.MENU)
                if ((entry.getKey().hasParent(id)) || (entry.getKey() == id)) {

                    moveX(-1, entry.getValue());

                    elements.remove(entry.getValue());
                    submenus.remove(i);
                }

        }
    }

    @Override
    public void updateScreen() {
        prevRotationPitch = rotationPitch;
        prevRotationYaw = rotationYaw;

        if (flowY < height / 2) flowY = (flowY + height / 2 - 32) / 2;

        flowX /= 2;

        if (this.elements == null) return;
        for (int i = elements.size() - 1; i >= 0; i--) {
            if (elements.get(i).removed()) {
                elements.get(i).close(mc);
                elements.remove(i);
                continue;
            }

            elements.get(i).update(mc);
        }
    }

    @Override
    public void actionPerformed(Elements element, Actions action, int data) {
        final Categories id = element.ID();

        if (id.isMenu()) {
            if (isMenuOpen(id)) {
                System.out.print(element.ID() + " passed to actionPerformed 1 \n");
                element.click(mc.getSoundHandler(), false);
                closeMenu(element, id);
            } else {
                System.out.print(element.ID() + " passed to actionPerformed 2 \n");
                element.click(mc.getSoundHandler(), true);
                openMenu(element, id);
            }
        } else if (id != Categories.NONE) {
            System.out.print(element.ID() + " passed to actionPerformed 3 \n");
            element.click(mc.getSoundHandler(), false);
            action(element, id, action, data);
        }
    }

    private void action(Elements element, Categories id, Actions action, int data) {
        if (id == Categories.LANGUAGE){
            final ButtonSlotGUI button = (ButtonSlotGUI) element;
            if (!button.highlight){
                SortedSet<Language> languages = mc.getLanguageManager().getLanguages();

                for (Language language : languages){
                    if (language.toString().equals(button.caption))
                        mc.getLanguageManager().setCurrentLanguage(language);
                }

                reloadList(element, id);
            }
        } else if (id == Categories.OPTIONS) {
            mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
        } else if (id == Categories.QUIT) {
            element.enabled = false;
            mc.shutdown();
        }
    }

    private boolean isMenuOpen(Categories id) {
        return menus.stream().anyMatch(entry -> entry.getKey() == id);
    }

    @Override
    public void confirmClicked(boolean result, int id){}

    @Override
    protected void mouseClicked(int cursorX, int cursorY, int button) {
        super.mouseClicked(cursorX, cursorY, button);
        mouseDown |= (0x1 << button);

        boolean clickedElement = false;

        for (int i = elements.size() - 1; i >= 0; i--) {
            if (i >= elements.size()) {
                if (elements.size() > 0) i = elements.size() - 1;
                else break;
            }

            if (elements.get(i).mouseOver(cursorX, cursorY)) {
                if (elements.get(i).mousePressed(mc, cursorX, cursorY, button))
                    actionPerformed(elements.get(i), Actions.getAction(button, true), button);

                clickedElement = true;
            }
        }

        if (!clickedElement) backgroundClicked(cursorX, cursorY, button);
    }

    @Override
    protected void mouseReleased(int cursorX, int cursorY, int button) {
        super.mouseReleased(cursorX, cursorY, button);
        mouseDown &= ~(0x1 << button);

        for (int i = elements.size() - 1; i >= 0; i--) {
            if (i >= elements.size()) {
                if (elements.size() > 0) i = elements.size() - 1;
                else break;
            }
            if (elements.get(i).mouseOver(cursorX, cursorY, button) && elements.get(i).mouseReleased(mc, cursorX, cursorY, button))
                actionPerformed(elements.get(i), Actions.getAction(button, false), button);
        }
    }

    protected void backgroundClicked(int cursorX, int cursorY, int button) {
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();

        if (Mouse.hasWheel()) {
            final int x = Mouse.getEventX() * width / mc.displayWidth;
            final int y = height - Mouse.getEventY() * height / mc.displayHeight - 1;
            final int delta = Mouse.getEventDWheel();

            if (delta != 0) mouseWheel(x, y, delta);
        }
    }

    @Override
    protected void keyTyped(char ch, int key) {
        if (OptionCore.CURSOR_TOGGLE.getValue() && super.isCtrlKeyDown()) lockCursor = !lockCursor;
        super.keyTyped(ch, key);

        elements.stream().filter(element -> element.focus && element.keyTyped(mc, ch, key)).forEach(element -> actionPerformed(element, Actions.KEY_TYPED, key));
    }

    @Override
    public void onGuiClosed() {
        showCursor();

        close();
    }

    protected void close() {
        elements.stream().forEach(el -> el.close(mc));
        elements.clear();
    }

    private void mouseWheel(int cursorX, int cursorY, int delta) {
        elements.stream().filter(element -> element.mouseOver(cursorX, cursorY) && element.mouseWheel(mc, cursorX, cursorY, delta)).forEach(element -> actionPerformed(element, Actions.MOUSE_WHEEL, delta));
    }

    protected void hideCursor() {
        if (!cursorHidden) toggleHideCursor();
    }

    protected void showCursor() {
        if (cursorHidden) toggleHideCursor();
    }

    protected void toggleHideCursor() {
        cursorHidden = !cursorHidden;
        try {
            Mouse.setNativeCursor(cursorHidden ? emptyCursor: null);
        } catch (LWJGLException ignored) {}
    }

    private void reloadList(Elements element, Categories id){
        if (element != null) {
            final List<Elements> list;

            if (element.parent != null && element.parent instanceof ContainerGUI)
                list = ((ContainerGUI) element.parent).elements;
            else list = elements;

            for (final Elements element0 : list) {
                if (element0.ID() == id) {
                    if (((ButtonGUI) element0).highlight && !((OptionButton)element).getOption().getValue()) ((ButtonGUI) element0).highlight = false;
                } else element0.enabled = true;
            }
        }
    }
}
