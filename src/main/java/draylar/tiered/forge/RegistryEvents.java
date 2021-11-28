package draylar.tiered.forge;

import draylar.tiered.api.CustomEntityAttributes;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD, modid = "tiered")
public class RegistryEvents
{
	@SubscribeEvent
	public static void registerAttributes(final RegistryEvent.Register<Attribute> event) {
		CustomEntityAttributes.registerAll(event.getRegistry());
	}
}
