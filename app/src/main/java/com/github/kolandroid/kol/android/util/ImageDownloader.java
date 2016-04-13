package com.github.kolandroid.kol.android.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Looper;
import android.view.Window;
import android.widget.ImageView;

import com.github.kolandroid.kol.android.BuildConfig;
import com.github.kolandroid.kol.util.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;

public class ImageDownloader {
    private static final Hashtable<ImageSlot, String> pendingViews = new Hashtable<>();
    private static final Hashtable<String, ArrayList<ImageSlot>> pendingTasks = new Hashtable<>();
    private static final Hashtable<String, Bitmap> cache = new Hashtable<>();

    public static void loadIconFromUrl(Dialog dialog, String url) {
        ImageDownloader.loadFromUrl(new DialogIconSlot(dialog), url);
    }

    public static void loadFromUrl(ImageView view, String url) {
        ImageDownloader.loadFromUrl(new ViewSlot(view), url);
    }

    private static void loadFromUrl(ImageSlot slot, String url) {
        if (BuildConfig.DEBUG && Looper.getMainLooper().getThread() != Thread.currentThread()) {
            throw new RuntimeException("AndroidViewContext should only be created from the main thread.");
        }

        if (url == null) return;
        if (url.length() <= 1) return;
        if (!url.startsWith("http://") && !url.startsWith("https://")) url = "http://" + url;

        String oldUrl = pendingViews.get(slot);
        if (oldUrl != null && oldUrl.contentEquals(url))
            return; //We're already loading this content into the view
        pendingViews.put(slot, url);

        //Attempt to load the image from our cache
        if (cache.containsKey(url)) {
            slot.fill(cache.get(url));
            return;
        }

        if (pendingTasks.containsKey(url)) {
            pendingTasks.get(url).add(slot);
            return;
        }

        ArrayList<ImageSlot> list = new ArrayList<>();
        list.add(slot);
        pendingTasks.put(url, list);

        //Log.i("ImageDownloader", "Starting download from " + url);
        DownloadImageTask task = new DownloadImageTask(url);
        task.execute();
    }

    private interface ImageSlot {
        void fill(Bitmap result);

        int hashCode();

        boolean equals(Object other);

        Context getContext();
    }

    private abstract static class WeakSlot<E> implements ImageSlot {
        private final WeakReference<E> base;
        private final WeakReference<Context> context;

        public WeakSlot(E base, Context context) {
            this.base = new WeakReference<>(base);
            this.context = new WeakReference<Context>(context);
        }

        protected abstract void fill(E base, Bitmap result);

        public void fill(Bitmap result) {
            E slot = base.get();
            if (slot != null)
                this.fill(slot, result);
        }

        public int hashCode() {
            E slot = base.get();
            if (slot != null)
                return slot.hashCode();
            return base.hashCode();
        }

        public Context getContext() {
            return context.get();
        }

        public boolean equals(Object other) {
            if (other instanceof WeakSlot<?>) {
                E slot = base.get();
                WeakSlot<?> o = (WeakSlot<?>) other;

                if (slot == null)
                    return o.base.get() == null;
                return slot.equals(o.base.get());
            }
            return false;
        }
    }

    public static class ViewSlot extends WeakSlot<ImageView> {
        public ViewSlot(ImageView base) {
            super(base, base.getContext());
        }

        @Override
        protected void fill(ImageView view, Bitmap result) {
            view.setImageBitmap(result);
        }
    }


    public static class DialogIconSlot extends WeakSlot<Dialog> {
        public DialogIconSlot(Dialog base) {
            super(base, base.getContext());
        }

        @Override
        protected void fill(Dialog dialog, Bitmap result) {
            dialog.setFeatureDrawable(Window.FEATURE_LEFT_ICON, new BitmapDrawable(dialog.getContext().getResources(), result));
        }
    }

    private static class DownloadImageTask extends AsyncTask<Void, Void, Bitmap> {
        private final String url;

        public DownloadImageTask(String url) {
            this.url = url;
        }

        @Override
        protected Bitmap doInBackground(Void... noArgs) {
            Bitmap result = null;
            InputStream in;

            // Search for a non-null context to load the assets folder
            Context context = null;
            for (ImageSlot slot : pendingTasks.get(url)) {
                context = slot.getContext();
                if (context != null)
                    break;
            }

            if (context == null) {
                return null; // All views have been garbage collected anyway
            }

            try {
                in = context.getAssets().open(url.replace("http://images.kingdomofloathing.com/", "images/"));
                result = BitmapFactory.decodeStream(in);
                in.close();
                return result;
            } catch (IOException e) {
                // Failed to find the file in assets, so we have to download it from online
            }

            try {
                in = new URL(url).openStream();
                result = BitmapFactory.decodeStream(in);
                in.close();
            } catch (Exception e) {
                Logger.log("ImageDownloader", "Unable to download: " + url);
            }

            return result;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            //System.out.println("Got bitmap for " + url);
            if (!pendingTasks.containsKey(url)) {
                System.out.println("...No task in hash table?");
                return;
            }

            if (result != null) {
                for (ImageSlot slot : pendingTasks.get(url)) {
                    if (pendingViews.get(slot) == null)
                        continue; //another load has completed and filled the view
                    if (!url.contentEquals(pendingViews.get(slot)))
                        continue; //the view has made a new request; throw away the image
                    slot.fill(result);
                    pendingViews.remove(slot);
                }
                cache.put(url, result);
            }

            pendingTasks.remove(url);
        }
    }
}
