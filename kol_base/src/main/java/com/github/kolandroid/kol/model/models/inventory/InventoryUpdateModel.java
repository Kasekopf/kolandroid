package com.github.kolandroid.kol.model.models.inventory;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.model.Model;
import com.github.kolandroid.kol.session.Session;
import com.github.kolandroid.kol.util.Logger;
import com.github.kolandroid.kol.util.Regex;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;

public class InventoryUpdateModel extends Model {
    private static final Regex INV_UPDATE = new Regex("updateInv\\((\\{[^>]*\\})\\)", 1);

    private final String str;
    private final Map<String, Integer> updates;

    /**
     * Create a new model in the provided session.
     *
     * @param s Session to use in all future requests by this model.
     */
    public InventoryUpdateModel(Session s, ServerReply content) {
        super(s);

        updates = new HashMap<>();

        String update = INV_UPDATE.extractSingle(content.html, "{}");
        this.str = update;
        try {
            JsonObject element = new JsonParser().parse(update).getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : element.entrySet()) {
                try {
                    String key = entry.getKey();
                    int value = entry.getValue().getAsInt();
                    updates.put(key, value);
                } catch (Exception e) {
                    Logger.log("InventoryUpdateModel", "Unable to parse " + entry.getValue() + " as integer");
                }
            }
        } catch (Exception e) {
            Logger.log("InventoryUpdateModel", "Unable to parse " + update + " as JSON object");
        }
    }

    public Map<String, Integer> getUpdates() {
        return updates;
    }

    @Override
    public String toString() {
        return str;
    }
}
