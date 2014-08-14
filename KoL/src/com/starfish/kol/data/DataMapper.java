package com.starfish.kol.data;

public interface DataMapper<A, B> {
	public A process(B input);
}
