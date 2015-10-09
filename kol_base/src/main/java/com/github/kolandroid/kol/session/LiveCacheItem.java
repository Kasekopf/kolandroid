package com.github.kolandroid.kol.session;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.gamehandler.LoadingContext;
import com.github.kolandroid.kol.request.Request;
import com.github.kolandroid.kol.request.ResponseHandler;
import com.github.kolandroid.kol.util.Callback;
import com.github.kolandroid.kol.util.Logger;

import java.util.ArrayList;

public abstract class LiveCacheItem<E> extends CacheItem<E> {
    private final Session session;

    private final ArrayList<Callback<E>> listeners;
    private final ArrayList<Callback<Void>> failureListeners;

    private boolean loading;

    /**
     * Create a new LiveCacheItem with the provided info.
     *
     * @param s         The session to use for any requests.
     */
    public LiveCacheItem(Session s) {
        this.session = s;

        this.listeners = new ArrayList<>();
        this.failureListeners = new ArrayList<>();

        this.loading = false;
    }

    @Override
    public void recompute(SessionCache cache, Callback<E> complete, Callback<Void> failure) {
        synchronized (this) {
            if (loading) {
                listeners.add(complete);
                failureListeners.add(failure);
                return;
            }

            loading = true;
            listeners.add(complete);
            failureListeners.add(failure);

            this.computeUrl(cache, new Callback<String>() {
                @Override
                public void execute(final String updateUrl) {
                    Logger.log("LiveCacheItem", "Starting request for " + updateUrl);
                    Request r = new Request(updateUrl);
                    r.makeAsync(session, LoadingContext.NONE, new ResponseHandler() {
                        @Override
                        public void handle(Session session, ServerReply response) {
                            if (response != null && response.url != null && response.url.contains(updateUrl)) {
                                E result = process(response);
                                if (result != null) {
                                    ArrayList<Callback<E>> toNotify;
                                    synchronized (this) {
                                        loading = false;
                                        toNotify = new ArrayList<>(listeners);
                                        listeners.clear();
                                        failureListeners.clear();
                                    }

                                    for (Callback<E> listener : toNotify) {
                                        listener.execute(result);
                                    }
                                    return;
                                }
                            }

                            ArrayList<Callback<Void>> toNotify;
                            synchronized (this) {
                                loading = false;
                                toNotify = new ArrayList<>(failureListeners);
                                listeners.clear();
                                failureListeners.clear();
                            }

                            for (Callback<Void> listener : toNotify) {
                                listener.execute(null);
                            }
                        }
                    });
                }
            }, failure);
        }
    }

    abstract E process(ServerReply reply);

    abstract void computeUrl(SessionCache cache, Callback<String> callback, Callback<Void> failure);

    @Override
    Class[] dependencies() {
        return new Class[0];
    }
}
