package com.github.kolandroid.kol.android.view;

import android.content.Context;

import com.github.kolandroid.kol.data.DataCache;
import com.github.kolandroid.kol.data.RawData;
import com.github.kolandroid.kol.util.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AndroidRawCache<E extends RawData> implements DataCache<String, E> {
    private final Map<String, E> preloadedCache;
    private final Map<String, E> overrideCache;
    private final String cacheFile;
    private final String overrideFile;

    private volatile boolean updated;
    private volatile boolean loadRequired;

    protected AndroidRawCache(String cacheFile, String overrideFile) {
        preloadedCache = new ConcurrentHashMap<>();
        overrideCache = new ConcurrentHashMap<>();

        this.cacheFile = cacheFile;
        this.overrideFile = overrideFile;
        updated = false;
        loadRequired = true;
    }

    public boolean isLoadRequired() {
        return loadRequired;
    }

    public void load(Context context) {
        loadRequired = true;
        try {
            InputStream preloaded = context.getAssets().open(cacheFile);
            load(preloadedCache, preloaded);
            Logger.log("AndroidRawCache", "Loaded " + preloadedCache.size() + " items from " + cacheFile);
        } catch (IOException e) {
            Logger.log("AndroidRawCache", "Unable to open file " + cacheFile);
        }
    }

    private void load(Map<String, E> loadInto, InputStream cache) throws IOException {
        String cacheLine;
        BufferedReader cacheReader = new BufferedReader(new InputStreamReader(cache));
        while ((cacheLine = cacheReader.readLine()) != null) {
            E cacheItem = parse(cacheLine);
            if (cacheItem != null) {
                loadInto.put(cacheItem.getId(), cacheItem);
            }
        }
    }

    @Override
    public E find(String input) {
        if (overrideCache.containsKey(input)) {
            return overrideCache.get(input);
        }

        if (preloadedCache.containsKey(input)) {
            return preloadedCache.get(input);
        }
        return null;
    }

    @Override
    public void store(E data) {
        if (data == null)
            return;

        E existing = find(data.getId());
        if (!data.equals(existing)) {
            Logger.log("AndroidRawCache", "Storing updated cache value: " + data);
            overrideCache.put(data.getId(), data);
            updated = true;
        }
    }

    public abstract E parse(String cacheLine);
}
