package com.github.kolandroid.kol.session;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.session.cache.CombatActionBarData;
import com.github.kolandroid.kol.util.Callback;

import java.util.concurrent.ConcurrentHashMap;

public class SessionCache {
    private ConcurrentHashMap<Class<?>, CacheItem> cache;

    public SessionCache(Session session) {
        init(Session.class, new BasicCacheItem<>(session));

        init(CombatActionBarData.class, new LiveCacheItem<CombatActionBarData>(session, "") {
            @Override
            protected CombatActionBarData process(ServerReply reply) {
                return CombatActionBarData.create(reply);
            }
        });
    }

    private <E> void init(Class<E> id, CacheItem<E> container) {
        cache.put(id, container);
    }

    public <E> void access(Class<E> id, Callback<E> callback, Callback<Void> failure) {
        if (cache.containsKey(id)) {
            CacheItem<E> cached = (CacheItem<E>) cache.get(id);
            cached.access(callback, failure);
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
