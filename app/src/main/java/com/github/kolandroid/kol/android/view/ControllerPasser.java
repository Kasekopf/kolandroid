package com.github.kolandroid.kol.android.view;

import com.github.kolandroid.kol.android.controller.Controller;
import com.github.kolandroid.kol.util.Logger;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Provide static storage for the passing of controllers.
 * Intents have a 1MB limit on stored data; this allows unique identifiers to be passed
 * within intents as substitutes for controllers.
 */
public class ControllerPasser {
    private static final Map<UUID, Controller> storage = new ConcurrentHashMap<>();

    /**
     * Place the controller in a static context for 1-time retrieval using the returned UUID
     *
     * @param toPlace The controller to temporarily store.
     * @return A UUID used to reaccess the controller
     */
    public static UUID placeController(Controller toPlace) {
        UUID newId = UUID.randomUUID();
        if (toPlace != null) {
            synchronized (toPlace) {
                // synchronized to establish a happens-before relationship for all setup
                // of the controller, to ensure all variables are properly initialized when
                // the GUI thread accesses the variable
                storage.put(newId, toPlace);
            }
        }
        return newId;
    }

    /**
     * Retrieve a previously stored controller using the provided UUID.
     * This can only be done ONCE successfully per UUID.
     * @param id    The id provided for the controller to retrieve
     * @return The stored controller, or null if the controller has already been popped previously.
     */
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
