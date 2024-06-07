package com.example.musichub.helper.uliti;

import com.example.musichub.model.hub.HubSection;
import com.example.musichub.model.hub.SectionHubPlaylist;
import com.example.musichub.model.hub.SectionHubSong;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class HubSectionTypeAdapter implements JsonDeserializer<HubSection> {
    @Override
    public HubSection deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        if (!jsonObject.has("sectionType")) {
            throw new JsonParseException("Missing sectionType field");
        }

        String sectionType = jsonObject.get("sectionType").getAsString();

        switch (sectionType) {
            case "playlist":
                return context.deserialize(jsonObject, SectionHubPlaylist.class);
            case "song":
                return context.deserialize(jsonObject, SectionHubSong.class);
            default:
                throw new JsonParseException("Unknown sectionType: " + sectionType);
        }
    }
}
