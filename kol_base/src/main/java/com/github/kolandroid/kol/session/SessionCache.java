package com.github.kolandroid.kol.session;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.session.cache.CombatActionBarData;
import com.github.kolandroid.kol.util.Callback;
import com.github.kolandroid.kol.util.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionCache {
    private final Map<Class<?>, CacheItem> cache;

    private final Session session;
    private String pwd;

    public SessionCache(Session session) {
        this.session = session;
        this.cache = new ConcurrentHashMap<>();
        this.pwd = "0";
        init(Session.class, new BasicCacheItem<>(session));

    }

    public synchronized void prepare(String pwd) {
        if (pwd != null && !pwd.isEmpty() && !pwd.equals(this.pwd)) {
            Logger.log("SessionCache", "Detected new pwdhash: " + pwd);
            this.pwd = pwd;
            init(CombatActionBarData.class, new LiveCacheItem<CombatActionBarData>(session, "actionbar.php?action=fetch&d=" + System.currentTimeMillis() + "&pwd=" + pwd) {
                @Override
                protected CombatActionBarData process(ServerReply reply) {
                    return CombatActionBarData.create(reply);
                }
            });
        }
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
