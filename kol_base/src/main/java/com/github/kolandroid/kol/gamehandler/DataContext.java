package com.github.kolandroid.kol.gamehandler;

import com.github.kolandroid.kol.data.DataCache;
import com.github.kolandroid.kol.data.RawItem;
import com.github.kolandroid.kol.data.RawSkill;
import com.github.kolandroid.kol.session.Session;
import com.github.kolandroid.kol.session.SessionCache;

public interface DataContext {
    /**
     * Obtain the cache for all skill information
     *
     * @return A cache for skill information
     */
    DataCache<String, RawSkill> getSkillCache();

    /**
     * Obtain the cache for all item information
     * @return A cache for item information
     */
    DataCache<String, RawItem> getItemCache();

    /**
     * Obtain a cache for player session information
     *
     * @return A cache for player session information
     */
    SessionCache getSessionCache(Session session);
}
