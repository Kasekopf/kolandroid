package com.starfish.kol.android.controllers.inventory;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.starfish.kol.android.R;
import com.starfish.kol.android.controller.Controller;
import com.starfish.kol.android.screen.Screen;
import com.starfish.kol.android.screen.ScreenSelection;
import com.starfish.kol.model.elements.interfaces.DeferredGameAction;
import com.starfish.kol.model.models.inventory.EquipmentPocketModel.CustomOutfitBuilder;

public class CustomOutfitController implements Controller {
	/**
	 * Autogenerated by eclipse.
	 */
	private static final long serialVersionUID = -7098637980143983599L;
	
	private final CustomOutfitBuilder base;
	
	public CustomOutfitController(CustomOutfitBuilder base) {
		this.base = base;
	}
	
	@Override
	public int getView() {
		return R.layout.dialog_saveoutfit;
	}

	@Override
	public void connect(View view, final Screen host) {
		final EditText nameentry = (EditText)view.findViewById(R.id.dialog_saveoutfit_text);
		Button submit = (Button)view.findViewById(R.id.dialog_saveoutfit_submit);
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String name = nameentry.getText().toString();
				if(name == "") return;
				
				DeferredGameAction action = base.saveOutfit(name);
				action.submit(host.getViewContext());
				host.close();
			}
		});
	}

	@Override
	public void disconnect(Screen host) {
		// do nothing
	}

	@Override
	public void chooseScreen(ScreenSelection choice) {
		choice.displayDialog(this);
	}

}
