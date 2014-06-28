package com.starfish.kol.android.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.starfish.kol.android.R;
import com.starfish.kol.android.view.ApplicationView;
import com.starfish.kol.model.interfaces.DeferredGameAction;
import com.starfish.kol.model.models.SkillsModel.SkillItem;

public class SkillDialog extends DialogFragment {
	public static SkillDialog create(SkillItem base) {
		SkillDialog dialog = new SkillDialog();
		Bundle args = new Bundle();
		args.putSerializable("skill", base);
		dialog.setArguments(args);
		return dialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		return dialog;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {		
		final View rootView = (View)inflater.inflate(R.layout.dialog_skill_screen,
				container, false);

		final SkillItem skill = (SkillItem) this.getArguments().getSerializable("skill");
		
	    TextView text = (TextView)rootView.findViewById(R.id.dialog_skill_text);
	    text.setText(skill.getText());
	    
	    TextView subtext = (TextView)rootView.findViewById(R.id.dialog_skill_subtext);
	    subtext.setText(skill.getSubtext());
	    
	    if(!skill.isBuff()) {
	    	EditText player = (EditText)rootView.findViewById(R.id.dialog_skill_player);
	    	player.setEnabled(false);
	    }
	    
	    Button submit = (Button)rootView.findViewById(R.id.dialog_skill_submit);
	    submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
		    	EditText number = (EditText)rootView.findViewById(R.id.dialog_skill_number);
		    	EditText player = (EditText)rootView.findViewById(R.id.dialog_skill_player);
		    	
		    	String num = number.getText().toString();
		    	String play = player.getText().toString();
		    	if(num == null || play == null || num.length() == 0)
		    		return;
		    	if(play.equalsIgnoreCase("(yourself)"))
		    		play = "";
		    	
		    	DeferredGameAction action = skill.cast(num, play);
		    	ApplicationView view = (ApplicationView)getActivity().getApplication();
		    	view.executeAction(action);
		    	
				SkillDialog.this.dismiss();
			}	    	
	    });
		return rootView;
	}
}
