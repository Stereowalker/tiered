package draylar.tiered.gson;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import net.minecraft.inventory.EquipmentSlotType;

public class EquipmentSlotDeserializer implements JsonDeserializer<EquipmentSlotType> {

    @Override
    public EquipmentSlotType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return EquipmentSlotType.fromString(json.getAsString().toLowerCase());
    }
}
