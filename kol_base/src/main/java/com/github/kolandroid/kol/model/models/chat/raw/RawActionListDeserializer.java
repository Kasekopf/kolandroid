package com.github.kolandroid.kol.model.models.chat.raw;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

public class RawActionListDeserializer implements
        JsonDeserializer<RawActionList> {
    @Override
    public RawActionList deserialize(JsonElement element, Type type,
                                     JsonDeserializationContext context) throws JsonParseException {
        ArrayList<RawAction> actions = new ArrayList<>();

        JsonObject jsonObject = element.getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            // For individual City objects, we can use default
            // deserialization:
            RawAction action = context.deserialize(entry.getValue(),
                    RawAction.class);
            action.setEntry(entry.getKey());
            actions.add(action);
        }

        return new RawActionList(actions);
    }
}