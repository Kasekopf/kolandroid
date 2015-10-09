package com.github.kolandroid.kol.session;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.session.data.FightActionBarData;
import com.github.kolandroid.kol.session.data.PwdData;
import com.github.kolandroid.kol.util.Callback;
import com.github.kolandroid.kol.util.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionCache {
    private final Map<Class<?>, CacheItem> cache;

    private final Session session;

    public SessionCache(Session session) {
        this.session = session;
        this.cache = new ConcurrentHashMap<>();

        init(Session.class, new BasicCacheItem<>(session));
        init(PwdData.class, new BasicCacheItem<PwdData>(null));
        init(FightActionBarData.class, new LiveCacheItem<FightActionBarData>(session) {
            @Override
            protected FightActionBarData process(ServerReply reply) {
                return FightActionBarData.create(reply);
            }

            @Override
            protected void computeUrl(SessionCache cache, final Callback<String> callback, Callback<Void> failure) {
                cache.access(PwdData.class, new Callback<PwdData>() {
                    @Override
                    public void execute(PwdData item) {
                        callback.execute("actionbar.php?action=fetch&d=" + System.currentTimeMillis() + "&pwd=" + item.getPwd());
                    }
                }, failure);
            }

            @Override
            Class[] dependencies() {
                return new Class[]{PwdData.class};
            }
        });
    }

    private <E> void init(Class<E> id, CacheItem<E> container) {
        cache.put(id, container);
        for (Class dependency : container.dependencies()) {
            if (cache.containsKey(dependency)) {
                cache.get(dependency).addDependent(container);
            } else {
                Logger.log("SessionCache", "Container for " + id + " attempted to declare dependency for " + dependency + ", but this was not yet stored in the cache");
            }
        }
    }

    public <E> void access(Class<E> id, Callback<E> callback, Callback<Void> failure) {
        if (cache.containsKey(id)) {
            CacheItem<E> cached = (CacheItem<E>) cache.get(id);
            cached.access(this, callback, failure);
        } else {
            failure.execute(null);
        }
    }

    public <E> void put(Class<E> id, E value) {
        if (cache.containsKey(id)) {
            CacheItem<E> cached = (CacheItem<E>) cache.get(id);
            cached.fill(value);
        }
    }

}
