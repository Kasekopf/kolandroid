package com.starfish.kol.android.screen;

import android.app.Dialog;
import android.os.Bundle;

import com.starfish.kol.android.controller.Controller;

public class DialogScreen extends FragmentScreen {
	public static Bundle prepare(Controller controller) {
		return DialogScreen.prepare(controller);
	}
	
	public static Bundle prepare(Controller controller, String title) {
		Bundle bundle = new Bundle();
		bundle.putSerializable("controller", controller);
		bundle.putSerializable("title", title);
		return bundle;
	}
	
	public static DialogScreen display(Controller controller, Screen base) {
		DialogScreen res = new DialogScreen();
		res.setArguments(DialogScreen.prepare(controller));
	    res.show(base.getFragmentManager(), "dialog");
		return res;
	}
	
	public static DialogScreen display(Controller controller, Screen base, String title) {
		DialogScreen res = new DialogScreen();
		res.setArguments(DialogScreen.prepare(controller, title));
	    res.show(base.getFragmentManager(), "dialog");
		return res;
	}
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	Dialog d = super.onCreateDialog(savedInstanceState);
        String title = getArguments().getString("title");
        if(title != null)
        	d.setTitle(title);
    	return d;
    }
}
