package com.github.kolandroid.kol.android.view;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.github.kolandroid.kol.android.BuildConfig;
import com.github.kolandroid.kol.android.chat.ChatScreen;
import com.github.kolandroid.kol.android.controller.Controller;
import com.github.kolandroid.kol.android.game.GameScreen;
import com.github.kolandroid.kol.android.login.LoginScreen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;
import com.github.kolandroid.kol.gamehandler.DataContext;
import com.github.kolandroid.kol.gamehandler.LoadingContext;
import com.github.kolandroid.kol.gamehandler.SettingsContext;
import com.github.kolandroid.kol.gamehandler.ViewContext;
import com.github.kolandroid.kol.request.ResponseHandler;
import com.github.kolandroid.kol.util.Logger;

import java.lang.ref.WeakReference;

public class AndroidViewContext implements ViewContext {
    private Handler activityLauncher;
    private Handler toastLauncher;
    private AndroidDataContext data;

    private SettingsContext settings;
    private ResponseHandler primaryRoute;
    private LoadingContext loadingContext;

    public AndroidViewContext(Context context) {
        this(context, LoadingContext.NONE);
    }

    public AndroidViewContext(Context context, LoadingContext loadingContext) {
        if (BuildConfig.DEBUG && Looper.getMainLooper().getThread() != Thread.currentThread()) {
            throw new RuntimeException("AndroidViewContext should only be created from the main thread.");
        }

        this.activityLauncher = new ActivityLauncher(context);
        this.toastLauncher = new ToastLauncher(context);
        this.data = new AndroidDataContext(context);
        this.loadingContext = loadingContext;
        this.settings = new AndroidSettingsContext(context);

        ScreenSelection screens = new ScreenSelection() {
            @Override
            public void displayExternal(Controller c) {
                IntentBuilder builder = new IntentBuilder(LoginScreen.class, c);
                Message.obtain(activityLauncher, 0, builder).sendToTarget();
            }

            @Override
            public void displayExternalDialog(Controller c) {
                IntentBuilder builder = new IntentBuilder(LoginScreen.class, c);
                Message.obtain(activityLauncher, 0, builder).sendToTarget();
            }

            @Override
            public void displayPrimary(Controller c, boolean replaceSameType) {
                IntentBuilder builder = new IntentBuilder(GameScreen.class, c);
                Message.obtain(activityLauncher, 0, builder).sendToTarget();
            }

            @Override
            public void displayDialog(Controller c) {
                IntentBuilder builder = new IntentBuilder(GameScreen.class, c);
                Message.obtain(activityLauncher, 0, builder).sendToTarget();
            }


            @Override
            public void displayChat(Controller c) {
                IntentBuilder builder = new IntentBuilder(ChatScreen.class, c);
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
        return loadingContext;
    }

    @Override
    public DataContext getDataContext() {
        return data;
    }

    @Override
    public void displayMessage(String message) {
        Message.obtain(toastLauncher, 0, message).sendToTarget();
    }

    @Override
    public SettingsContext getSettingsContext() {
        return settings;
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

    private static class ToastLauncher extends Handler {
        final WeakReference<Context> parent;

        public ToastLauncher(Context parent) {
            this.parent = new WeakReference<Context>(parent);
        }

        @Override
        public void handleMessage(Message m) {
            Context context = parent.get();
            if (context == null) {
                Logger.log("AndroidViewContext", "Attempted to display message using garbage-collected intent");
                return;
            }

            String message = (String) m.obj;
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
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
                Logger.log("AndroidViewContext", "Attempted to display model using garbage-collected intent");
                return;
            }

            IntentBuilder builder = (IntentBuilder) m.obj;
            Intent intent = builder.build(context);
            context.startActivity(intent);
        }
    }
}
