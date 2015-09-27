package com.github.kolandroid.kol.android.view;

import android.app.Activity;
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
import com.github.kolandroid.kol.android.controller.UpdateController;
import com.github.kolandroid.kol.android.game.GameScreen;
import com.github.kolandroid.kol.android.login.LoginScreen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;
import com.github.kolandroid.kol.gamehandler.DataContext;
import com.github.kolandroid.kol.gamehandler.LoadingContext;
import com.github.kolandroid.kol.gamehandler.ViewContext;
import com.github.kolandroid.kol.util.Logger;

import java.lang.ref.WeakReference;
import java.util.UUID;

public class AndroidViewContext implements ViewContext {
    private Handler activityLauncher;
    private Handler toastLauncher;
    private DataContext data;

    private AndroidSettingsContext settings;
    private PrimaryRoute primaryRoute;
    private LoadingContext loadingContext;

    public AndroidViewContext(Context context, Class<? extends Activity> sendDialogsTo) {
        this(context, LoadingContext.NONE, sendDialogsTo);
    }

    public AndroidViewContext(Context context, LoadingContext loadingContext, final Class<? extends Activity> sendDialogsTo) {
        if (BuildConfig.DEBUG && Looper.getMainLooper().getThread() != Thread.currentThread()) {
            throw new RuntimeException("AndroidViewContext should only be created from the main thread.");
        }

        this.activityLauncher = new ActivityLauncher(context);
        this.toastLauncher = new ToastLauncher(context);
        this.data = (DataContext) context.getApplicationContext();
        this.loadingContext = loadingContext;
        this.settings = new AndroidSettingsContext(context);

        ScreenSelection screens = new ScreenSelection() {
            @Override
            public void displayExternal(Controller c) {
                IntentBuilder builder = new IntentBuilder(LoginScreen.class, c, Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                Message.obtain(activityLauncher, 0, builder).sendToTarget();
            }

            @Override
            public void displayExternalDialog(Controller c, boolean cancellable) {
                IntentBuilder builder = new IntentBuilder(LoginScreen.class, c, Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                Message.obtain(activityLauncher, 0, builder).sendToTarget();
            }

            @Override
            public void displayPrimaryUpdate(UpdateController c, boolean displayIfUnable) {
                IntentBuilder builder = new IntentBuilder(GameScreen.class, c);
                Message.obtain(activityLauncher, 0, builder).sendToTarget();
            }

            @Override
            public void displayPrimary(Controller c) {
                IntentBuilder builder = new IntentBuilder(GameScreen.class, c);
                Message.obtain(activityLauncher, 0, builder).sendToTarget();
            }

            @Override
            public void displayDialog(Controller c) {
                IntentBuilder builder = new IntentBuilder(sendDialogsTo, c);
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
    public PrimaryRoute getPrimaryRoute() {
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
    public AndroidSettingsContext getSettingsContext() {
        return settings;
    }

    private static class IntentBuilder {
        private static byte count = 2;
        private final Class<?> toLaunch;
        private final Controller toInclude;
        private final int intentFlags;

        public IntentBuilder(Class<?> toLaunch, Controller toInclude) {
            this(toLaunch, toInclude, Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }

        public IntentBuilder(Class<?> toLaunch, Controller toInclude, int intentFlags) {
            this.toLaunch = toLaunch;
            this.toInclude = toInclude;
            this.intentFlags = intentFlags;
        }

        public Intent build(Context context) {
            Intent intent = new Intent(context, toLaunch);
            UUID staticId = ControllerPasser.placeController(toInclude);
            intent.putExtra("controllerId", staticId);
            intent.addFlags(intentFlags);
            Log.i("ViewContext", "Constructing new intent for " + toInclude.getClass());
            return intent;
        }
    }

    private static class ToastLauncher extends Handler {
        final WeakReference<Context> parent;

        public ToastLauncher(Context parent) {
            this.parent = new WeakReference<>(parent);
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
            this.parent = new WeakReference<>(parent);
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
