package com.starfish.kol.android.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.starfish.kol.android.R;
import com.starfish.kol.android.view.AndroidViewContext;
import com.starfish.kol.model.interfaces.DeferredGameAction;
import com.starfish.kol.model.models.inventory.EquipmentPocketModel.CustomOutfitBuilder;

public class SaveOutfitDialog extends DialogFragment {
	public static SaveOutfitDialog create(CustomOutfitBuilder builder) {
		SaveOutfitDialog dialog = new SaveOutfitDialog();
		Bundle args = new Bundle();
		args.putSerializable("builder", builder);
		dialog.setArguments(args);
		return dialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.setTitle("Save outfit as:");
		return dialog;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View rootView = (View) inflater.inflate(
				R.layout.dialog_saveoutfit, container, false);
		
		final CustomOutfitBuilder builder = (CustomOutfitBuilder)this.getArguments().getSerializable("builder");
		final EditText nameentry = (EditText)rootView.findViewById(R.id.dialog_saveoutfit_text);
		
		Button submit = (Button)rootView.findViewById(R.id.dialog_saveoutfit_submit);
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String name = nameentry.getText().toString();
				if(name == "") return;
				
				DeferredGameAction action = builder.saveOutfit(name);
				action.submit(new AndroidViewContext(getActivity()));
				SaveOutfitDialog.this.dismiss();
			}
		});
		
		return rootView;
	}
}
