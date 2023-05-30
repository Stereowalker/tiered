package com.stereowalker.tiered.compat;

import java.util.function.Consumer;

import com.stereowalker.tiered.Tiered;
import com.stereowalker.unionlib.util.ModHelper;

import net.minecraftforge.common.MinecraftForge;

public class CuriosCompat {
	public static void load() {
		if (ModHelper.isCuriosLoaded()) {
			boolean useCurios = false;
			try {Class<?> c = Class.forName("top.theillusivec4.curios.api.event.CurioAttributeModifierEvent"); System.out.println("Found Curio Class "+c); useCurios = true;} 
			catch (Exception e) {
				System.err.println(e.getMessage());
				System.err.println("Something went wrong and as a result, Curios support will not work");
				useCurios = false;
			}
			System.out.println("Use curio "+useCurios);
			if (useCurios) {
				MinecraftForge.EVENT_BUS.addListener((Consumer<top.theillusivec4.curios.api.event.CurioAttributeModifierEvent>)(event)->{
					Tiered.AppendAttributesToOriginal(
							event.getItemStack(), 
							event.getSlotContext().identifier(), 
							Tiered.isPreferredCurioSlot(event.getItemStack(), 
									event.getSlotContext().identifier()), "CurioAttributeModifiers", event.getModifiers(),
							template -> template.getRequiredCurioSlot(), 
							template -> template.getOptionalCurioSlot(), 
							(template, newMap) -> template.realize(event::addModifier, event.getSlotContext().identifier()));
				});
			}
		}
	}
}
