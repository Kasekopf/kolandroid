package com.github.kolandroid.kol.android.view;

import android.util.Log;

import com.github.kolandroid.kol.android.controller.Controller;
import com.github.kolandroid.kol.android.controllers.AccountSettingsController;
import com.github.kolandroid.kol.android.controllers.CraftingController;
import com.github.kolandroid.kol.android.controllers.MessageController;
import com.github.kolandroid.kol.android.controllers.chat.ChatController;
import com.github.kolandroid.kol.android.controllers.fight.FightController;
import com.github.kolandroid.kol.android.controllers.inventory.ClosetController;
import com.github.kolandroid.kol.android.controllers.inventory.InventoryController;
import com.github.kolandroid.kol.android.controllers.inventory.InventoryUpdateController;
import com.github.kolandroid.kol.android.controllers.skills.SkillsController;
import com.github.kolandroid.kol.android.controllers.web.WebController;
import com.github.kolandroid.kol.android.login.LoginController;
import com.github.kolandroid.kol.android.screen.ScreenSelection;
import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.connection.Session;
import com.github.kolandroid.kol.model.models.AccountSettingsModel;
import com.github.kolandroid.kol.model.models.CraftingModel;
import com.github.kolandroid.kol.model.models.MessageModel;
import com.github.kolandroid.kol.model.models.WebModel;
import com.github.kolandroid.kol.model.models.chat.stubs.ChatStubModel;
import com.github.kolandroid.kol.model.models.fight.FightModel;
import com.github.kolandroid.kol.model.models.inventory.ClosetModel;
import com.github.kolandroid.kol.model.models.inventory.InventoryModel;
import com.github.kolandroid.kol.model.models.inventory.InventoryUpdateModel;
import com.github.kolandroid.kol.model.models.login.CreateCharacterModel;
import com.github.kolandroid.kol.model.models.login.LoginModel;
import com.github.kolandroid.kol.model.models.skill.SkillsModel;
import com.github.kolandroid.kol.request.ResponseHandler;
import com.github.kolandroid.kol.util.Callback;
import com.github.kolandroid.kol.util.Logger;

public class PrimaryRoute implements ResponseHandler, Callback<Controller> {
    private final ScreenSelection screens;

    public PrimaryRoute(ScreenSelection screens) {
        this.screens = screens;
    }

    private Controller getController(Session session, ServerReply response) {
        Log.i("PrimaryRoute", "Creating model for response: " + response.url);

        if (response.url.contains("androiderror.php")) {
            MessageModel model = new MessageModel(session, response);
            return new MessageController<MessageModel>(model);
        }

        if (!response.hasBody()) {
            if (response.html.contains("parent.mainpane.updateInv")) {
                InventoryUpdateModel model = new InventoryUpdateModel(session, response);
                return new InventoryUpdateController(model);
            } else {
                Logger.log("Captured empty body: ", response.url + "; " + response.html);
                return null;
            }
        }

        /**
         * Specifically handle simulated requests.
         * Prevents later models from matching html content.
         */
        if (response.url.contains("fake.php")) {
            WebModel model = new WebModel(session, response);
            return new WebController(model);
        }

        if (response.url.contains("donatepopup.php")) {
            MessageModel model = new MessageModel("Thanks for donating to KoL! Unfortunately, this unofficial mobile app does not yet support donations. Please use the mobile web browser.", MessageModel.ErrorType.NONE);
            return new MessageController<MessageModel>(model);
        }

        if (response.url.contains("login.php")) {
            if (response.url.contains("notloggedin=1")) {
                Logger.log("PrimaryRoute", "Forced logout encountered");
            }
            LoginModel model = new LoginModel(session, response);
            return new LoginController(model);
        }

        if (response.url.contains("create.php")) {
            CreateCharacterModel model = new CreateCharacterModel(session, response);
            return new WebController(model);
        }

        if (response.url.contains("fight.php")) {
            FightModel model = new FightModel(session, response);
            return new FightController(model);
        }

        /*
        if (response.url.contains("choice.php")) {
            //Ignore a couple badly behaved choice adventures.
            int whichchoice = ChoiceModel.extractChoiceId(response);
            if (whichchoice == 985 /* Odd-jobs board /) {
                //do nothing
            } else {
                ChoiceModel model = new ChoiceModel(session, response);
                return new ChoiceController(model);
            }
        }
        */
        if (response.url.contains("inventory.php")) {
            InventoryModel model = new InventoryModel(session, response);
            return new InventoryController(model);
        }

        if (response.url.contains("closet.php")) {
            ClosetModel model = new ClosetModel(session, response);
            return new ClosetController(model);
        }

        if (response.url.contains("skillz.php")) {
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

        if (response.url.contains("chat.php")) {
            // Ignore the server reply; we'll just connect to the ChatService for an update
            return new ChatController(new ChatStubModel(session));
        }

        WebModel model = new WebModel(session, response);
        return new WebController(model);
    }

    @Override
    public void handle(Session session, ServerReply response) {
        if (response == null)
            return;

        ServerReply resultsPane = response.extractResultsPane();
        if (resultsPane == null) {
            Controller controller = getController(session, response);
            execute(controller);
        } else if (response.url.contains("androiddisplay=results")) {
            Logger.log("PrimaryRoute", "Results pane was contained in redundant results pane");
            Controller controller = getController(session, resultsPane);
            execute(controller);
        } else {
            Logger.log("PrimaryRoute", "Split results pane off of response " + response.url);
            ServerReply base = response.removeResultsPane();
            Controller mainController = getController(session, base);
            execute(mainController);
            Controller resultsController = getController(session, resultsPane);
            execute(resultsController);
        }
    }

    @Override
    public void execute(Controller controller) {
        if (controller != null) controller.chooseScreen(screens);
    }
}
