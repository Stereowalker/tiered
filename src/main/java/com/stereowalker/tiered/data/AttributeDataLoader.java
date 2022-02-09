package com.stereowalker.tiered.data;

import java.util.Map;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.stereowalker.tiered.api.PotentialAttribute;
import com.stereowalker.tiered.gson.EntityAttributeModifierDeserializer;
import com.stereowalker.tiered.gson.EntityAttributeModifierSerializer;
import com.stereowalker.tiered.gson.EquipmentSlotDeserializer;

import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class AttributeDataLoader extends SimpleJsonResourceReloadListener {

    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .registerTypeAdapter(AttributeModifier.class, new EntityAttributeModifierDeserializer())
            .registerTypeAdapter(AttributeModifier.class, new EntityAttributeModifierSerializer())
            .registerTypeAdapter(EquipmentSlot.class, new EquipmentSlotDeserializer())
            .registerTypeHierarchyAdapter(Style.class, new Style.Serializer())
            .create();

    private static final String PARSING_ERROR_MESSAGE = "Parsing error loading recipe {}";
    private static final String LOADED_RECIPES_MESSAGE = "Loaded {} recipes";
    private static final Logger LOGGER = LogManager.getLogger();

    private Map<ResourceLocation, PotentialAttribute> itemAttributes = new HashMap<>();

    public AttributeDataLoader() {
        super(GSON, "item_attributes");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> loader, ResourceManager manager, ProfilerFiller profiler) {
        Map<ResourceLocation, PotentialAttribute> readItemAttributes = Maps.newHashMap();

        for (Map.Entry<ResourceLocation, JsonElement> entry : loader.entrySet()) {
            ResourceLocation identifier = entry.getKey();

            try {
                PotentialAttribute itemAttribute = GSON.fromJson(entry.getValue(), PotentialAttribute.class);
                readItemAttributes.put(new ResourceLocation(itemAttribute.getID()), itemAttribute);
            } catch (IllegalArgumentException | JsonParseException exception) {
                LOGGER.error(PARSING_ERROR_MESSAGE, identifier, exception);
            }
        }

        itemAttributes = readItemAttributes;
        LOGGER.info(LOADED_RECIPES_MESSAGE, readItemAttributes.size());
    }

    /**
     * Returns a list of potential item attributes ({@link PotentialAttribute}) read from "data/modid/item_attributes".
     *
     * @return  list of potential read item attributes
     */
    public Map<ResourceLocation, PotentialAttribute> getItemAttributes() {
        return itemAttributes;
    }
    public void clear() {
        itemAttributes.clear();
    }
    public void replace(Map<ResourceLocation, PotentialAttribute> i){
        itemAttributes = i;
    }
}
