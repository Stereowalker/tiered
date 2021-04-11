package draylar.tiered.gson;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import net.minecraft.util.text.Color;
import net.minecraft.util.text.TextFormatting;

public class TextColorDeserializer implements JsonDeserializer<Color> {

    @Override
    public Color deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Color.fromTextFormatting(TextFormatting.getValueByName(json.getAsString().toUpperCase()));
    }
}
