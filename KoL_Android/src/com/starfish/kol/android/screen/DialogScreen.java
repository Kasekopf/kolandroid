package com.starfish.kol.android.screen;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;

import com.starfish.kol.android.controller.Controller;

public class DialogScreen extends FragmentScreen {
	public static Bundle prepare(Controller controller) {
		Bundle bundle = new Bundle();
		bundle.putSerializable("controller", controller);
		return bundle;
	}

	public static Bundle prepare(Controller controller, String title) {
		Bundle bundle = DialogScreen.prepare(controller);
		bundle.putString("title", title);
		return bundle;
	}

	public static DialogScreen display(Controller controller, Screen base) {
		DialogScreen res = new DialogScreen();
		res.setArguments(DialogScreen.prepare(controller));
		res.show(base.getFragmentManager(), "dialog");
		return res;
	}

	public static DialogScreen display(Controller controller, Screen base,
			String title) {
		DialogScreen res = new DialogScreen();
		res.setArguments(DialogScreen.prepare(controller, title));
		res.show(base.getFragmentManager(), "dialog");
		return res;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog d = super.onCreateDialog(savedInstanceState);
		if (getArguments().containsKey("title")) {
			d.setTitle(getArguments().getString("title"));
		} else {
			d.requestWindowFeature(Window.FEATURE_NO_TITLE);
		}
		return d;
	}

	@Override
	public void close() {
		this.dismiss();
	}
}
