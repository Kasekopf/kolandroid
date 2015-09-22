package com.github.kolandroid.kol.android.view;

import android.app.Application;

import com.github.kolandroid.kol.data.DataCache;
import com.github.kolandroid.kol.data.RawItem;
import com.github.kolandroid.kol.data.RawSkill;
import com.github.kolandroid.kol.gamehandler.DataContext;

public class KolApplication extends Application implements DataContext {
    private final AndroidRawCache<RawSkill> skillsCache = new AndroidRawCache<RawSkill>("skillcache.txt", "skilloverridecache.txt") {
        @Override
        public RawSkill parse(String cacheLine) {
            return RawSkill.parse(cacheLine);
        }
    };

    private final AndroidRawCache<RawItem> itemsCache = new AndroidRawCache<RawItem>("itemcache.txt", "itemoverridecache.txt") {
        @Override
        public RawItem parse(String cacheLine) {
            return RawItem.parse(cacheLine);
        }
    };

    @Override
    public DataCache<String, RawSkill> getSkillCache() {
        if (skillsCache.isLoadRequired()) {
            synchronized (skillsCache) {
                if (skillsCache.isLoadRequired()) {
                    skillsCache.load(this);
                }
            }
        }

        return skillsCache;
    }

    @Override
    public DataCache<String, RawItem> getItemCache() {
        if (itemsCache.isLoadRequired()) {
            synchronized (itemsCache) {
                if (itemsCache.isLoadRequired()) {
                    itemsCache.load(this);
                }
            }
        }

        return itemsCache;
    }
}
