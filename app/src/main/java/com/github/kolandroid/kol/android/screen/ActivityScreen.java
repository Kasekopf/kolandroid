package com.github.kolandroid.kol.android.screen;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.github.kolandroid.kol.android.controller.Controller;
import com.github.kolandroid.kol.android.controllers.MessageController;
import com.github.kolandroid.kol.android.view.AndroidViewContext;
import com.github.kolandroid.kol.android.view.ControllerPasser;
import com.github.kolandroid.kol.android.view.ErrorHandler;
import com.github.kolandroid.kol.model.models.MessageModel;
import com.github.kolandroid.kol.util.Logger;

import java.util.UUID;

public abstract class ActivityScreen extends ActionBarActivity implements Screen {
    private AndroidViewContext base;

    private Controller currentController = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ErrorHandler(this));
        this.setContentView(this.getContentView());
        this.base = this.createViewContext();

        Controller controller = null;
        if (savedInstanceState != null && savedInstanceState.containsKey("controller")) {
            controller = (Controller) savedInstanceState.getSerializable("controller");
            Logger.log(this.getClass().getSimpleName(), "Restarting with " + controller);
        } else if (getIntent() != null && getIntent().hasExtra("controllerId")) {
            controller = ControllerPasser.popController((UUID) getIntent().getSerializableExtra("controllerId"));
            if (controller == null) {
                Logger.log(this.getClass().getSimpleName(), "Unable to find controller to display; controller in intent was already accessed");
            } else {
                Logger.log(this.getClass().getSimpleName(), "Starting with " + controller);
            }
        } else {
            Logger.log(this.getClass().getSimpleName(), "Unable to find controller to display");
        }

        controller = this.setup(savedInstanceState, controller);
        if (controller == null) {
            MessageModel error = new MessageModel("0x534fa: Unable to determine controller to display", MessageModel.ErrorType.ERROR);
            controller = new MessageController(error);
        }

        this.currentController = controller;
        displayController(controller, false);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        Logger.log(this.getLocalClassName(), "Saving instance state");
        if (currentController != null) {
            Logger.log(this.getLocalClassName(), "Saved controller: " + currentController);
            savedInstanceState.putSerializable("controller", currentController);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Logger.log(this.getLocalClassName(), "Destroyed (finished=" + isFinishing() + ")");
    }

    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent(intent);

        if (intent.hasExtra("controllerId")) {
            Controller controller = ControllerPasser.popController((UUID) getIntent().getSerializableExtra("controllerId"));
            if (controller == null) {
                Logger.log(this.getLocalClassName(), "Received new intent, but controller id was stale");
            } else {
                currentController = controller;
                displayController(controller, true);
            }
        } else {
            // the intent to launch this came from a back action
            // i.e. back from the chat
            // Do not shift the displayed controller
            Logger.log("ActivityScreen", this.getClass() + " received intent without controller id");
        }
    }

    protected AndroidViewContext createViewContext() {
        return new AndroidViewContext(this, getClass());
    }

    public abstract Controller setup(Bundle savedInstanceState, Controller firstController);

    protected abstract void displayController(Controller controller, boolean addToBackStack);

    protected abstract int getContentView();

    @Override
    public FragmentManager getChildFragmentManager() {
        return this.getFragmentManager();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public AndroidViewContext getViewContext() {
        return base;
    }

    @Override
    public void close() {
        // do nothing
    }
}
