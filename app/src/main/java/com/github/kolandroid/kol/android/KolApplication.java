package com.github.kolandroid.kol.android;

import android.app.Application;

import com.github.kolandroid.kol.android.view.AndroidRawCache;
import com.github.kolandroid.kol.data.DataCache;
import com.github.kolandroid.kol.data.RawItem;
import com.github.kolandroid.kol.data.RawSkill;
import com.github.kolandroid.kol.gamehandler.DataContext;

public class KolApplication extends Application implements DataContext {
    private AndroidRawCache<RawSkill> skillsCache;
    private AndroidRawCache<RawItem> itemsCache;

    @Override
    public void onCreate() {
        super.onCreate();

        itemsCache = new AndroidRawCache<RawItem>("itemcache.txt", "itemoverridecache.txt") {
            @Override
            public RawItem parse(String cacheLine) {
                return RawItem.parse(cacheLine);
            }
        };

        skillsCache = new AndroidRawCache<RawSkill>("skillcache.txt", "skilloverridecache.txt") {
            @Override
            public RawSkill parse(String cacheLine) {
                return RawSkill.parse(cacheLine);
            }
        };
    }

    @Override
    public DataCache<String, RawSkill> getSkillCache() {
        if (!skillsCache.loaded()) {
            synchronized (skillsCache) {
                if (!skillsCache.loaded()) {
                    skillsCache.load(this);
                }
            }
        }

        return skillsCache;
    }

    @Override
    public DataCache<String, RawItem> getItemCache() {
        if (!itemsCache.loaded()) {
            synchronized (itemsCache) {
                if (!itemsCache.loaded()) {
                    itemsCache.load(this);
                }
            }
        }

        return itemsCache;
    }
}
