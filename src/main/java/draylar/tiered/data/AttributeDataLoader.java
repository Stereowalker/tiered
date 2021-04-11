package draylar.tiered.data;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import draylar.tiered.api.PotentialAttribute;
import draylar.tiered.gson.EntityAttributeModifierDeserializer;
import draylar.tiered.gson.EquipmentSlotDeserializer;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Style;

public class AttributeDataLoader extends JsonReloadListener {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .registerTypeAdapter(AttributeModifier.class, new EntityAttributeModifierDeserializer())
            .registerTypeAdapter(EquipmentSlotType.class, new EquipmentSlotDeserializer())
            .registerTypeHierarchyAdapter(Style.class, new Style.Serializer())
            .create();

    private static final String PARSING_ERROR_MESSAGE = "Parsing error loading recipe {}";
    private static final String LOADED_RECIPES_MESSAGE = "Loaded {} recipes";
    private static final Logger LOGGER = LogManager.getLogger();

    private Map<ResourceLocation, PotentialAttribute> itemAttributes = ImmutableMap.of();

    public AttributeDataLoader() {
        super(GSON, "item_attributes");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> loader, IResourceManager manager, IProfiler profiler) {
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
}
