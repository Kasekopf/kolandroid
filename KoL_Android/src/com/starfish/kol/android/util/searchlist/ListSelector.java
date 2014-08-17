package com.starfish.kol.android.util.searchlist;

import com.starfish.kol.android.screen.Screen;

public interface ListSelector<E> {
	public boolean selectItem(Screen host, E item);
}
