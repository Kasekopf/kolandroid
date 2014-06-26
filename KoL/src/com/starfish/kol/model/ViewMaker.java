package com.starfish.kol.model;


public interface ViewMaker {
	public <E extends Model<?>> boolean display(Class<E> type, E model);
	public void Log(String tag, String message);
}
