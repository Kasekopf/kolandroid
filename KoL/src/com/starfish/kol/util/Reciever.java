package com.starfish.kol.util;

public interface Reciever<E> {
	public abstract void pass(E obj);
}
