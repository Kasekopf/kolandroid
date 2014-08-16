package com.starfish.kol.android.screen;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.starfish.kol.android.controller.Controller;
import com.starfish.kol.gamehandler.ViewContext;

public class FragmentScreen extends DialogFragment implements Screen {
	private Controller controller = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.controller = (Controller)this.getArguments().getSerializable("controller");
		
		int layoutid = controller.getView();
		View view = inflater.inflate(layoutid, container, false);
		controller.connect(view, this);
		return view;
	}


	@Override
	public void onDestroyView() {
		if(controller != null)
			controller.disconnect();
		super.onDestroyView();
	}
	
	public static Bundle prepare(Controller controller) {
		Bundle bundle = new Bundle();
		bundle.putSerializable("controller", controller);
		return bundle;
	}
	
	public static FragmentScreen create(Controller controller) {
		FragmentScreen res = new FragmentScreen();
		res.setArguments(FragmentScreen.prepare(controller));
		return res;
	}

	@Override
	public ViewContext getViewContext() {
		return (ViewContext)this.getActivity();
	}
}
