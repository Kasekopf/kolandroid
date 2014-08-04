package com.starfish.kol.model;

import com.starfish.kol.connection.PartialServerReply;
import com.starfish.kol.connection.ServerReply;
import com.starfish.kol.connection.Session;
import com.starfish.kol.gamehandler.LoadingContext;
import com.starfish.kol.request.Request;
import com.starfish.kol.request.ResponseHandler;

/**
 * A model which can update its contents with a single base url.
 *
 */
public abstract class LiveModel extends Model<LiveMessage> {
	/**
	 * Autogenerated by eclipse.
	 */
	private static final long serialVersionUID = 6439988316319232465L;

	private final String updateUrl;
	private boolean filling;
	private boolean foreground;

	public LiveModel(Session s, String updateUrl, boolean foreground) {
		super(s);
		
		this.filling = false;
		this.updateUrl = updateUrl;
		this.foreground = foreground;
	}
	
	public final void process(ServerReply response) {
		this.filling = true;
		this.loadContent(response);
		this.notifyView(LiveMessage.REFRESH);
	}

	protected abstract void loadContent(ServerReply content);

	public void update() {
		Request update = new Request(this.updateUrl, new ResponseHandler() {
			@Override
			public void handle(Session session, Request request,
					PartialServerReply response) {
				if (response.url.contains(updateUrl)) {
					LoadingContext loading = foreground ? getLoadingContext() : LoadingContext.NONE;				
					ServerReply fullResponse = response.complete(loading);
					if(fullResponse == null) {
						return; //error
					}
					process(fullResponse);
				} else {
					if(foreground)
						getGameHandler().handle(session, request, response);
					else
						System.out.println("LiveModel expected " + updateUrl + " but was redirected to " + response.url);
				}
			}
		});
		makeRequest(update);
	}
	
	public void access() {
		if (this.filling)
			return;
		this.filling = true;

		this.update();
	}

}