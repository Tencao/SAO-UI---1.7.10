package com.tencao.saoui.util;

import com.tencao.saoui.SAOMod;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.StatCollector;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public enum SAOSkill {

	SPRINTING(SAOIcon.SPRINTING, () -> SAOMod.IS_SPRINTING, (mc, parent) -> SAOMod.IS_SPRINTING = !SAOMod.IS_SPRINTING),
	SNEAKING(SAOIcon.SNEAKING, () -> SAOMod.IS_SNEAKING, (mc, parent) -> SAOMod.IS_SNEAKING = !SAOMod.IS_SNEAKING),
	CRAFTING(SAOIcon.CRAFTING, () -> false, (mc, parent) -> {
		if (parent != null) mc.displayGuiScreen(parent);
		else {
			SAOMod.REPLACE_GUI_DELAY = 1;
			mc.displayGuiScreen(null);

			final int invKeyCode = mc.gameSettings.keyBindInventory.getKeyCode();

			KeyBinding.setKeyBindState(invKeyCode, true);
			KeyBinding.onTick(invKeyCode);
		}
	});

	public final SAOIcon icon;
	private final BooleanSupplier shouldHighlight;
	private final BiConsumer<Minecraft, GuiInventory> action;

	SAOSkill(SAOIcon saoIcon, BooleanSupplier shouldHighlight, BiConsumer<Minecraft, GuiInventory> action) {
		this.icon = saoIcon;
		this.shouldHighlight = shouldHighlight;
		this.action = action;
	}

	public final String toString() {
		final String name = name();

        return StatCollector.translateToLocal("skill" + name.charAt(0) + name.substring(1, name.length()).toLowerCase());
    }

	/**
	 * Whether this skill's button should highlight or not.
	 *
	 * @return whether it should be highlighted
	 *
	 */
	public boolean shouldHighlight() {
		return shouldHighlight.getAsBoolean();
	} // Doing it this way might come in handy when building an API

	/**
	 * Activate this skill.
	 *
	 * @param mc     The Minecraft instance
	 * @param parent The parent gui
     */
	public void activate(Minecraft mc, GuiInventory parent) {
		this.action.accept(mc, parent);
	}
}
