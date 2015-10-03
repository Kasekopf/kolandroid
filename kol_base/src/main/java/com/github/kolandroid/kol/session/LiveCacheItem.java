package com.github.kolandroid.kol.session;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.gamehandler.LoadingContext;
import com.github.kolandroid.kol.request.Request;
import com.github.kolandroid.kol.request.ResponseHandler;
import com.github.kolandroid.kol.util.Callback;
import com.github.kolandroid.kol.util.Logger;

import java.util.ArrayList;

public abstract class LiveCacheItem<E> implements CacheItem<E> {
    private final Session session;
    private final String updateUrl;

    private ArrayList<Callback<E>> listeners;
    private ArrayList<Callback<Void>> failureListeners;

    private E contents;
    private boolean loading;

    /**
     * Create a new LiveCacheItem with the provided info.
     *
     * @param s         The session to use for any requests.
     * @param updateUrl The url to consult for content.
     */
    public LiveCacheItem(Session s, String updateUrl) {
        this.session = s;
        this.updateUrl = updateUrl;

        this.loading = false;
    }

    @Override
    public void access(Callback<E> callback, Callback<Void> failure) {
        synchronized (this) {
            if (loading) {
                listeners.add(callback);
                failureListeners.add(failure);
                return;
            }

            if (contents == null) {
                Logger.log("LiveCacheItem", "Starting request for " + updateUrl);
                loading = true;
                Request r = new Request(updateUrl);
                r.makeAsync(session, LoadingContext.NONE, new ResponseHandler() {
                    @Override
                    public void handle(Session session, ServerReply response) {
                        if (response != null && canHandle(response.url)) {
                            E result = process(response);
                            if (result != null) {
                                ArrayList<Callback<E>> toNotify;
                                synchronized (this) {
                                    loading = false;
                                    contents = result;
                                    toNotify = new ArrayList<>(listeners);
                                    listeners.clear();
                                    failureListeners.clear();
                                }

                                for (Callback<E> listener : toNotify) {
                                    listener.execute(contents);
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
                return;
            }
        }

        callback.execute(contents);
    }

    protected abstract E process(ServerReply reply);

    @Override
    public void fill(E content) {
        synchronized (this) {
            this.contents = content;
        }
    }

    /**
     * Determine if the provided url is a valid server response.
     * By default, the url must contain the requested url.
     *
     * @param url The url the server responded with
     * @return True if the url should be parsed normally.
     */
    protected boolean canHandle(String url) {
        return url.contains(updateUrl);
    }
}
