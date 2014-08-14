package com.starfish.kol.gamehandler;

import com.starfish.kol.connection.ServerReply;
import com.starfish.kol.connection.Session;
import com.starfish.kol.model.Model;
import com.starfish.kol.model.models.ChoiceModel;
import com.starfish.kol.model.models.CraftingModel;
import com.starfish.kol.model.models.WebModel;
import com.starfish.kol.model.models.fight.FightModel;
import com.starfish.kol.model.models.inventory.InventoryModel;
import com.starfish.kol.model.models.login.LoginModel;
import com.starfish.kol.model.models.skill.SkillsModel;
import com.starfish.kol.request.Request;
import com.starfish.kol.request.ResponseHandler;

/**
 * The standard ResponseHandler for most of the app, used to display an
 *  arbitrary game page with the appropriate view and model.
 *
 */
public class GameHandler implements ResponseHandler {
	//Context used to map models to views.
	private ViewContext view;
	
	/**
	 * Create a new ResponseHandler which displays arbitrary game pages
	 *  on the provided view context.
	 *  
	 * @param view	Context to display new models.
	 */
	public GameHandler(ViewContext view) {
		this.view = view;
	}
	
	/**
	 * Map an arbitrary ServerReply to the appropriate Model.
	 * 
	 * @param session	Current session, passed to all created models.
	 * @param response	The server reply to handle.
	 * @return The model associated with the response page.
	 */
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
	
	/**
	 * Process a new reply from the server. In this case, display this response
	 *  in a newly generated view.
	 * 
	 * @param session
	 *            The session used when making the request.
	 * @param request
	 *            The request which generated this reply.
	 * @param response
	 *            The response recieved from the server.
	 */
	@Override
	public void handle(Session session, Request request, ServerReply response) {
		Model<?> model = loadModel(session, response);
		view.display(model);
	}
}
