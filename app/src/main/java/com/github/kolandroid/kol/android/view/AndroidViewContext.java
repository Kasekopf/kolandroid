package com.github.kolandroid.kol.android.view;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.github.kolandroid.kol.android.BuildConfig;
import com.github.kolandroid.kol.android.controller.Controller;
import com.github.kolandroid.kol.android.game.GameScreen;
import com.github.kolandroid.kol.android.login.LoginScreen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;
import com.github.kolandroid.kol.gamehandler.DataContext;
import com.github.kolandroid.kol.gamehandler.LoadingContext;
import com.github.kolandroid.kol.gamehandler.ViewContext;
import com.github.kolandroid.kol.request.ResponseHandler;

import java.lang.ref.WeakReference;

public class AndroidViewContext implements ViewContext {
    private Handler activityLauncher;
    private AndroidDataContext data;

    private ResponseHandler primaryRoute;

    public AndroidViewContext(Context context) {
        if (BuildConfig.DEBUG && Looper.getMainLooper().getThread() != Thread.currentThread()) {
            throw new RuntimeException("AndroidViewContext should only be created from the main thread.");
        }

        this.activityLauncher = new ActivityLauncher(context);
        this.data = new AndroidDataContext(context);

        ScreenSelection screens = new ScreenSelection() {
            @Override
            public void displayExternal(Controller c) {
                IntentBuilder builder = new IntentBuilder(LoginScreen.class, c);
                Message.obtain(activityLauncher, 0, builder).sendToTarget();
            }

            @Override
            public void displayPrimary(Controller c) {
                IntentBuilder builder = new IntentBuilder(GameScreen.class, c);
                Message.obtain(activityLauncher, 0, builder).sendToTarget();
            }

            @Override
            public void displayDialog(Controller c) {
                IntentBuilder builder = new IntentBuilder(GameScreen.class, c);
                Message.obtain(activityLauncher, 0, builder).sendToTarget();
            }
        };
        this.primaryRoute = new PrimaryRoute(screens);
    }

    @Override
    public ResponseHandler getPrimaryRoute() {
        return primaryRoute;
    }

    @Override
    public LoadingContext createLoadingContext() {
        return LoadingContext.NONE;
    }

    @Override
    public DataContext getDataContext() {
        return data;
    }

    private static class IntentBuilder {
        private final Class<?> toLaunch;
        private final Controller toInclude;

        public IntentBuilder(Class<?> toLaunch, Controller toInclude) {
            this.toLaunch = toLaunch;
            this.toInclude = toInclude;
        }

        public Intent build(Context context) {
            Intent intent = new Intent(context, toLaunch);
            intent.putExtra("controller", toInclude);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.i("ViewContext", "Constructing new intent for " + toInclude.getClass());
            return intent;
        }

    }

    private static class ActivityLauncher extends Handler {
        final WeakReference<Context> parent;

        public ActivityLauncher(Context parent) {
            this.parent = new WeakReference<Context>(parent);
        }

        @Override
        public void handleMessage(Message m) {
            Context context = parent.get();
            if (context == null) {
                Log.i("ViewContext", "Attempted to display model using garbage-collected intent");
                return;
            }

            IntentBuilder builder = (IntentBuilder) m.obj;
            Intent intent = builder.build(context);
            context.startActivity(intent);
        }
    }
}