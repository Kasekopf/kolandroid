package com.github.kolandroid.kol.gamehandler;

import com.github.kolandroid.kol.data.DataCache;
import com.github.kolandroid.kol.data.RawItem;
import com.github.kolandroid.kol.data.RawSkill;

public interface DataContext {
    DataCache<String, RawSkill> getSkillCache();

    DataCache<String, RawItem> getItemCache();
}
