package com.starfish.kol.model;

import com.starfish.kol.connection.Session;

public abstract class GroupModel<Child extends ChildModel> extends Model<Void> {
	/**
	 * Autogenerated by eclipse.
	 */
	private static final long serialVersionUID = 8057138180603838561L;

	public GroupModel(Session s) {
		super(s);
	}
	
	public abstract int getActiveChild();
	public abstract Child[] getChildren();
}