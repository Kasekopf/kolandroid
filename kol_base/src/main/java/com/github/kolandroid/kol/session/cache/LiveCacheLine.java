package com.github.kolandroid.kol.session.cache;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.gamehandler.LoadingContext;
import com.github.kolandroid.kol.request.Request;
import com.github.kolandroid.kol.request.ResponseHandler;
import com.github.kolandroid.kol.session.Session;
import com.github.kolandroid.kol.util.Callback;
import com.github.kolandroid.kol.util.Logger;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * A single cache line which holds a single item. This item can be recomputed on access,
 * if it does not already exist, by consulting a web URL.
 *
 * @param <E> The type of the item inside the cache.
 */
public abstract class LiveCacheLine<E extends Serializable> extends CacheLine<E> {
    // Session to make all requests in
    private final Session session;

    // All listeners for a newly computed item, to be notified if the computation succeeds
    private final ArrayList<Callback<E>> listeners;
    // All listeners for a newly computed item, to be notified if the computation fails
    private final ArrayList<Callback<Void>> failureListeners;

    // True if a url request is currently loading
    private boolean loading;

    /**
     * Create a new LiveCacheLine in the provided session.
     *
     * @param s         The session to use for any requests.
     */
    public LiveCacheLine(Session s) {
        this.session = s;

        this.listeners = new ArrayList<>();
        this.failureListeners = new ArrayList<>();

        this.loading = false;
    }

    /**
     * Recompute the stored item by accessing an online URL.
     * @param cache     Cache which provides any relevant dependencies
     * @param complete  Callback to call when the stored item is recomputed
     * @param failure   Callback to call when we are unable to compute this item
     */
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
        }

        this.computeUrl(cache, new Callback<String>() {
            @Override
            public void execute(final String updateUrl) {
                Logger.log("LiveCacheLine", "Starting request for " + updateUrl);
                Request r = new Request(updateUrl);
                r.makeAsync(session, LoadingContext.NONE, new ResponseHandler() {
                    @Override
                    public void handle(Session session, ServerReply response) {
                        if (response != null && response.url != null && response.url.contains(updateUrl)) {
                            E result = process(response);
                            if (result != null) {
                                ArrayList<Callback<E>> toNotify;
                                synchronized (LiveCacheLine.this) {
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
                        synchronized (LiveCacheLine.this) {
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

    /**
     * Process a new ServerReply into the desired cache item.
     * @param reply A new ServerReply from consulting the provided URL
     * @return A newly computed cache item, or null if the ServerReply was inappropriate
     */
    protected abstract E process(ServerReply reply);

    /**
     * Determine the URL used to recompute this item, possibly consulting the cache.
     * @param cache     Cache which provides any relevant dependencies
     * @param callback  Callback to call when the URL is determined
     * @param failure   Callback to call when we are unable to determine the URL
     */
    protected abstract void computeUrl(SessionCache cache, Callback<String> callback, Callback<Void> failure);

    /**
     * Default to no dependencies.
     * @return []
     */
    @Override
    protected Class[] dependencies() {
        return new Class[0];
    }
}
