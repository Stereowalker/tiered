package com.stereowalker.tiered.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.lang.reflect.Type;

public class EntityAttributeModifierSerializer implements JsonSerializer<AttributeModifier> {

    @Override
    public JsonElement serialize(AttributeModifier src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        obj.addProperty("amount", src.getAmount());
        obj.addProperty("operation", src.getOperation().toString());
        obj.addProperty("name", src.getName());
        return obj;
    }
}