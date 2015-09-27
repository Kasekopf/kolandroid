package com.github.kolandroid.kol.android.view;

import com.github.kolandroid.kol.android.controller.Controller;
import com.github.kolandroid.kol.util.Logger;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ControllerPasser {
    private static final Map<UUID, Controller> storage = new ConcurrentHashMap<>();

    public static UUID placeController(Controller toPlace) {
        UUID newId = UUID.randomUUID();
        if (toPlace != null) {
            synchronized (toPlace) {
                // synchronized to Establish a happens-before relationship for all setup
                // of the controller, to insure all variables are properly initialized when
                // the GUI thread accesses the variable
                storage.put(newId, toPlace);
            }
        }
        return newId;
    }

    public static Controller popController(UUID id) {
        Controller result = storage.remove(id);
        if (result == null) {
            return null;
        }

        if (storage.size() > 10) {
            Logger.log("ControllerPasser", "Contents: " + storage.size() + "; Controller leak?");
        }
        synchronized (result) {
            // synchronized to finish establishing the happens-before relationship
            return result;
        }
    }
}
