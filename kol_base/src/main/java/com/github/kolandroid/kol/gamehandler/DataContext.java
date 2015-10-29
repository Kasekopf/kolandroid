package com.github.kolandroid.kol.gamehandler;

import com.github.kolandroid.kol.data.DataCache;
import com.github.kolandroid.kol.data.RawItem;
import com.github.kolandroid.kol.data.RawSkill;
import com.github.kolandroid.kol.session.Session;
import com.github.kolandroid.kol.session.cache.SessionCache;

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

    /**
     * Get the version identifier for the given resource
     *
     * @param name The resource to check version information of
     * @return The version of the specified resource, or 0 if the resource is not present.
     */
    int getVersion(String name);
}
