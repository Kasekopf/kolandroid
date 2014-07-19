package com.starfish.kol.gamehandler;

import com.starfish.kol.connection.Connection.ServerReply;
import com.starfish.kol.connection.Session;
import com.starfish.kol.model.Model;
import com.starfish.kol.model.models.ChoiceModel;
import com.starfish.kol.model.models.CraftingModel;
import com.starfish.kol.model.models.FightModel;
import com.starfish.kol.model.models.InventoryModel;
import com.starfish.kol.model.models.LoginModel;
import com.starfish.kol.model.models.SkillsModel;
import com.starfish.kol.model.models.WebModel;
import com.starfish.kol.request.ResponseHandler;
import com.starfish.kol.request.Request;

public class GameHandler implements ResponseHandler {
	private ViewContext view;
	
	public GameHandler(ViewContext view) {
		this.view = view;
	}
	
	private Model<?> loadModel(Session session, ServerReply response) {
		/**
		 * Reset the session if a logout was recieved.
		 */
		if(response.url.contains("login.php?notloggedin=1")) {
			System.out.println("Logout seen");
			//The session was logged out.
			return new LoginModel();
		}
		
		System.out.println("Creating model for response: " + response.url);
		
		/**
		 * Specifically handle simulated requests.
		 * Prevents later models from matching html content.
		 */
		if(response.url.contains("fake.php")) {
			return new WebModel(session, response);
		}
		
		
		if(response.url.contains("login.php")) {
			return new LoginModel();
		}
		
		if(response.url.contains("fight.php")) {
			return new FightModel(session, response);
		}
		
		if(response.url.contains("choice.php")) {
			return new ChoiceModel(session, response);
		}
		
		if(response.url.contains("inventory.php")) {
			return new InventoryModel(session, response);
		}
		
		if(response.url.contains("skills.php")) {
			return new SkillsModel(session, response);
		}
		
		if(response.url.contains("craft.php")) {
			return new CraftingModel(session, response);
		}
		
		return new WebModel(session, response);
	}

	@Override
	public boolean handle(Session session, Request request, ServerReply response) {
		Model<?> model = loadModel(session, response);
		view.display(model);
		return true;
	}
}
