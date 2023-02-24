package com.stereowalker.tiered.gson;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.stereowalker.unionlib.world.entity.AccessorySlot;

public class AccessoryGroupDeserializer implements JsonDeserializer<AccessorySlot.Group> {

    @Override
    public AccessorySlot.Group deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return AccessorySlot.Group.byName(json.getAsString().toLowerCase());
    }
}
