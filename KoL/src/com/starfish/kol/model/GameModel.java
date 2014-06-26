package com.starfish.kol.model;

import com.starfish.kol.connection.Connection.ServerReply;
import com.starfish.kol.connection.Session;
import com.starfish.kol.model.models.ChoiceModel;
import com.starfish.kol.model.models.CraftingModel;
import com.starfish.kol.model.models.FightModel;
import com.starfish.kol.model.models.InventoryModel;
import com.starfish.kol.model.models.LoginModel;
import com.starfish.kol.model.models.SkillsModel;
import com.starfish.kol.model.models.WebModel;
import com.starfish.kol.model.models.chat.ChatModel;
import com.starfish.kol.request.Request;
import com.starfish.kol.request.ResponseHandler;

public class GameModel implements ResponseHandler {	
	private ViewMaker view;
	private Session session;
	private ChatModel chat;
	
	public void attachView(ViewMaker view) {
		this.view = view;
	}
	
	public LoginModel start() {
		this.session = new Session(this);
		
		chat = new ChatModel();
		chat.connect(this, session);
		
		return new LoginModel();
	}
	
	public ChatModel getChatModel() {
		return chat;
	}
	
	public void connect(Model<?> m) {
		m.connect(this, session);
	}
	
	protected <E extends Model<?>> void displayModel(Class<E> type, E model) {
		view.display(type, model);
	}
	
	@Override
	public boolean handle(Session session, Request request, ServerReply response) {
		if(view == null) {
			System.err.println("No view attached to model");
			return true;	
		}

		/**
		 * Reset the session if a logout was recieved.
		 */
		if(response.url.contains("login.php?notloggedin=1")) {
			System.out.println("Logout seen");
			//The session was logged out.
			this.session = new Session(this);
		}
		
		
		System.out.println("Creating model for response: " + response.url);
		
		/**
		 * Specifically handle simulated requests.
		 * Prevents later models from matching html content.
		 */
		if(response.url.contains("fake.php")) {
			view.display(WebModel.class, new WebModel(response));
			return true;
		}
		
		
		if(response.url.contains("login.php")) {
			view.display(LoginModel.class, new LoginModel());
			return true;
		}
		
		if(response.url.contains("fight.php")) {
			view.display(FightModel.class, new FightModel(response));
			return true;
		}
		
		if(response.url.contains("choice.php")) {
			view.display(ChoiceModel.class, new ChoiceModel(response));
			return true;
		}
		
		if(response.url.contains("inventory.php")) {
			view.display(InventoryModel.class, new InventoryModel(response));
			return true;
		}
		
		if(response.url.contains("skills.php")) {
			view.display(SkillsModel.class, new SkillsModel(response));
			return true;
		}
		
		if(response.url.contains("craft.php")) {
			view.display(CraftingModel.class, new CraftingModel(response));
			return true;
		}
		
		view.display(WebModel.class, new WebModel(response));
		return true;
	}
}
