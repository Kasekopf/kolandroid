package com.github.kolandroid.kol.android.view;

import android.util.Log;

import com.github.kolandroid.kol.android.controller.Controller;
import com.github.kolandroid.kol.android.controllers.AccountSettingsController;
import com.github.kolandroid.kol.android.controllers.ChoiceController;
import com.github.kolandroid.kol.android.controllers.CraftingController;
import com.github.kolandroid.kol.android.controllers.fight.FightController;
import com.github.kolandroid.kol.android.controllers.inventory.ClosetController;
import com.github.kolandroid.kol.android.controllers.inventory.ItemStorageController;
import com.github.kolandroid.kol.android.controllers.skills.SkillsController;
import com.github.kolandroid.kol.android.controllers.web.WebController;
import com.github.kolandroid.kol.android.login.LoginController;
import com.github.kolandroid.kol.android.screen.ScreenSelection;
import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.connection.Session;
import com.github.kolandroid.kol.model.models.AccountSettingsModel;
import com.github.kolandroid.kol.model.models.ChoiceModel;
import com.github.kolandroid.kol.model.models.CraftingModel;
import com.github.kolandroid.kol.model.models.WebModel;
import com.github.kolandroid.kol.model.models.fight.FightModel;
import com.github.kolandroid.kol.model.models.inventory.ClosetModel;
import com.github.kolandroid.kol.model.models.inventory.InventoryModel;
import com.github.kolandroid.kol.model.models.skill.SkillsModel;
import com.github.kolandroid.kol.request.ResponseHandler;

public class PrimaryRoute implements ResponseHandler {
    private final ScreenSelection screens;

    public PrimaryRoute(ScreenSelection screens) {
        this.screens = screens;
    }

    private Controller getController(Session session, ServerReply response) {
        /**
         * Reset the session if a logout was received.
         */
        if (response.url.contains("login.php?notloggedin=1")) {
            Log.i("Primary Route", "Logout seen");
            //The session was logged out.
            return new LoginController();
        }

        Log.i("Primary Route", "Creating model for response: " + response.url);

        /**
         * Specifically handle simulated requests.
         * Prevents later models from matching html content.
         */
        if (response.url.contains("fake.php")) {
            WebModel model = WebModel.create(session, response);
            return new WebController(model);
        }


        if (response.url.contains("login.php")) {
            return new LoginController();
        }

        if (response.url.contains("fight.php")) {
            FightModel model = new FightModel(session, response);
            return new FightController(model);
        }

        if (response.url.contains("choice.php")) {
            //Ignore a couple badly behaved choice adventures.
            int whichchoice = ChoiceModel.extractChoiceId(response);
            if (whichchoice == 985 /* Odd-jobs board */) {
                //do nothing
            } else {
                ChoiceModel model = new ChoiceModel(session, response);
                return new ChoiceController(model);
            }
        }

        if (response.url.contains("inventory.php")) {
            InventoryModel model = new InventoryModel(session, response);
            return new ItemStorageController<>(model);
        }

        if (response.url.contains("closet.php")) {
            ClosetModel model = new ClosetModel(session, response);
            return new ClosetController(model);
        }

        if (response.url.contains("skills.php")) {
            SkillsModel model = new SkillsModel(session, response);
            return new SkillsController(model);
        }

        if (response.url.contains("craft.php")) {
            CraftingModel model = new CraftingModel(session, response);
            return new CraftingController(model);
        }

        if (response.url.contains("account.php")) {
            AccountSettingsModel model = new AccountSettingsModel(session, response);
            return new AccountSettingsController(model);
        }

        WebModel model = WebModel.create(session, response);
        return new WebController(model);
    }

    @Override
    public void handle(Session session, ServerReply response) {
        Controller controller = getController(session, response);
        controller.chooseScreen(screens);
    }
}
