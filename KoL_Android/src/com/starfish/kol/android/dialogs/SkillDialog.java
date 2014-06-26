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
import com.starfish.kol.android.util.searchlist.OnListSelection;
import com.starfish.kol.model.models.SkillsModel.SkillItem;

public class SkillDialog extends DialogFragment {
	public static SkillDialog create(SkillItem base) {
		SkillDialog dialog = new SkillDialog();
		Bundle args = new Bundle();
		args.putSerializable("skill", base);
		dialog.setArguments(args);
		return dialog;
	}

	private OnListSelection<SkillDialogResult> selector;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		return dialog;
	}

	public void setOnSelection(OnListSelection<SkillDialogResult> select) {
		this.selector = select;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View rootView = (View)inflater.inflate(R.layout.dialog_skill_screen,
				container, false);

		SkillItem skill = (SkillItem) this.getArguments().getSerializable("skill");
		
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
		    	
		    	SkillDialogResult result = new SkillDialogResult(num, play);
		    	if(selector != null) {
		    		selector.selectItem(result);
		    	}

				SkillDialog.this.dismiss();
			}	    	
	    });
		return rootView;
	}
	
	public class SkillDialogResult
	{
		private String num;
		private String player;
		public SkillDialogResult(String num, String player) {
			this.num = num;
			this.player = player;
		}
		
		public String getNum() {
			return num;
		}
		
		public String getPlayer() {
			 return player;
		}
	}
}
