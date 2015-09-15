package com.github.kolandroid.kol.gamehandler;

import com.github.kolandroid.kol.data.DataMapper;

public interface DataContext {
    DataMapper<String, String> getSkillsImageFinder();

    DataMapper<String, String> getItemsImageFinder();
}
