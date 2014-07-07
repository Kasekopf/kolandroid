package com.starfish.kol.android.game.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.starfish.kol.android.R;
import com.starfish.kol.model.basic.ActionItem;
import com.starfish.kol.model.models.ChoiceModel;

public class ChoiceFragment extends WebFragment<ChoiceModel> {
	public ChoiceFragment() {
		super(R.layout.fragment_choice_screen);
	}

	@Override
	public void onCreateSetup(View view, ChoiceModel base,
			Bundle savedInstanceState) {		
		LinearLayout options = (LinearLayout)view.findViewById(R.id.choice_choices);
		for(ActionItem option : base.getOptions()) {
			Log.i("ChoiceFragment", "Making button for " + option.getText());
			Button optionBtn = new Button(options.getContext());
			optionBtn.setText(option.getText());
			optionBtn.setWidth(options.getWidth());
			
			final ActionItem thisOption = option;
			optionBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					thisOption.submit(getModel());
				}
			});
			options.addView(optionBtn);
		}

		super.onCreateSetup(view, base, savedInstanceState);
	}
}
