package com.starfish.kol.model.basic;

import com.starfish.kol.model.Model;
import com.starfish.kol.model.interfaces.DeferredGameAction;
import com.starfish.kol.request.Request;
import com.starfish.kol.request.ResponseHandler;

public class BasicAction implements DeferredGameAction {
	private String url;
	
	public BasicAction(String url) {
		this.url = url;
	}
	
	@Override
	public void submit(Model<?> context) {
		context.makeRequest(new Request(url, ResponseHandler.none));
	}

}
