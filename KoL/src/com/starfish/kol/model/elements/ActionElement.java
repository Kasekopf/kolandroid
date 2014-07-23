package com.starfish.kol.model.elements;

import com.starfish.kol.connection.Session;
import com.starfish.kol.model.elements.basic.BasicAction;
import com.starfish.kol.model.elements.interfaces.ModelElement;


public class ActionElement extends BasicAction implements ModelElement
{
	/**
	 * Autogenerated by eclipse.
	 */
	private static final long serialVersionUID = 8834750738130793384L;
	
	private final String text;
	private final String img;
	
	public ActionElement(Session session, String text, String action) {
		this(session, text, "", action);
	}

	public ActionElement(Session session, String text, String img, String action) {
		super(session, action);
		
		this.text = text;
		this.img = img;
	}
	
	@Override
	public String getText() {
		return this.text;
	}

	@Override
	public String getImage() {
		return this.img;
	}
		
	@Override
	public String toString() {
		return this.text;
	}
}