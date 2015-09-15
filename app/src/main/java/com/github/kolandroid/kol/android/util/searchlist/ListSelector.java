package com.github.kolandroid.kol.android.util.searchlist;

import com.github.kolandroid.kol.android.screen.Screen;

public interface ListSelector<E> {
    boolean selectItem(Screen host, E item);
}
