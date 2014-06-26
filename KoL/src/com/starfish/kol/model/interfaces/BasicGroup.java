package com.starfish.kol.model.interfaces;

import java.util.ArrayList;

public class BasicGroup<E> implements ModelGroup<E> {
	/**
	 * Autogenerated by eclipse.
	 */
	private static final long serialVersionUID = 4571040689192726695L;
	
	private ArrayList<E> items;
	private String name;
	
	public BasicGroup(String name) {
		this(name, new ArrayList<E>());
	}
	
	public BasicGroup(String name, ArrayList<E> items) {
		this.name = name;
		this.items = items;
	}
	@Override
	public int size() {
		return items.size();
	}

	@Override
	public E get(int index) {
		return items.get(index);
	}
	
	public void add(E item) {
		items.add(item);
	}

	@Override
	public String getName() {
		return name;
	}
}
