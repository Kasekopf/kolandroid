package com.starfish.kol.android.view;

import android.util.Log;

import com.starfish.kol.android.controller.Controller;
import com.starfish.kol.android.controller.GameFragmentController;
import com.starfish.kol.android.controllers.ChoiceController;
import com.starfish.kol.android.controllers.CraftingController;
import com.starfish.kol.android.controllers.FightController;
import com.starfish.kol.android.controllers.WebController;
import com.starfish.kol.android.controllers.inventory.InventoryController;
import com.starfish.kol.android.screen.ScreenSelection;
import com.starfish.kol.connection.ServerReply;
import com.starfish.kol.connection.Session;
import com.starfish.kol.model.models.ChoiceModel;
import com.starfish.kol.model.models.CraftingModel;
import com.starfish.kol.model.models.WebModel;
import com.starfish.kol.model.models.fight.FightModel;
import com.starfish.kol.model.models.inventory.InventoryModel;
import com.starfish.kol.model.models.login.LoginModel;
import com.starfish.kol.model.models.login.LoginStatus;
import com.starfish.kol.model.models.skill.SkillsModel;
import com.starfish.kol.request.Request;
import com.starfish.kol.request.ResponseHandler;

public class PrimaryRoute implements ResponseHandler {
	private final ScreenSelection screens;
	
	public PrimaryRoute(ScreenSelection screens) {
		this.screens = screens;
	}
	
	private Controller getController(Session session, ServerReply response) {
		/**
		 * Reset the session if a logout was recieved.
		 */
		if(response.url.contains("login.php?notloggedin=1")) {
			Log.i("Primary Route", "Logout seen");
			//The session was logged out.
			LoginModel model = new LoginModel();
			return new GameFragmentController<LoginStatus, LoginModel>(model);
		}
		
		Log.i("Primary Route", "Creating model for response: " + response.url);
		
		/**
		 * Specifically handle simulated requests.
		 * Prevents later models from matching html content.
		 */
		if(response.url.contains("fake.php")) {
			WebModel model = new WebModel(session, response);
			return new WebController(model);
		}
		
		
		if(response.url.contains("login.php")) {
			LoginModel model = new LoginModel();
			return new GameFragmentController<LoginStatus, LoginModel>(model);
		}
		
		if(response.url.contains("fight.php")) {
			FightModel model = new FightModel(session, response);
			return new FightController(model);
		}
		
		if(response.url.contains("choice.php")) {
			ChoiceModel model = new ChoiceModel(session, response);
			return new ChoiceController(model);
		}
		
		if(response.url.contains("inventory.php")) {
			InventoryModel model = new InventoryModel(session, response);
			return new InventoryController(model);
		}
		
		if(response.url.contains("skills.php")) {
			SkillsModel model = new SkillsModel(session, response);
			return new GameFragmentController<Void, SkillsModel>(model);
		}
		
		if(response.url.contains("craft.php")) {
			CraftingModel model = new CraftingModel(session, response);
			return new CraftingController(model);
		}
		
		WebModel model = new WebModel(session, response);
		return new WebController(model);
	}
	
	@Override
	public void handle(Session session, Request request, ServerReply response) {
		Controller controller = getController(session, response);
		controller.chooseScreen(screens);
	}

}
