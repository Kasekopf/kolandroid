package com.starfish.kol.android.game.fragments;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.starfish.kol.android.R;
import com.starfish.kol.android.dialogs.FunkslingingDialog;
import com.starfish.kol.android.util.listbuilders.SubtextBuilder;
import com.starfish.kol.android.util.searchlist.SearchListFragment;
import com.starfish.kol.gamehandler.ViewContext;
import com.starfish.kol.model.elements.ActionElement;
import com.starfish.kol.model.models.fight.FightItem;
import com.starfish.kol.model.models.fight.FightModel;
import com.starfish.kol.model.models.fight.FightSkill;

public class FightFragment extends WebFragment<FightModel> {	
	public FightFragment() {
		super(R.layout.fragment_fight_screen);
	}

	@Override
	public void onCreateSetup(View view, FightModel base,
			Bundle savedInstanceState) {				
		final Button attack = (Button)view.findViewById(R.id.fight_attack);
		attack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ActionElement action = getModel().getAttack();
				if(action != null)
					action.submit((ViewContext)getActivity());
			}
		});
		
		final Button useskill = (Button)view.findViewById(R.id.fight_skill);
		useskill.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View btn) {
				ArrayList<FightSkill> skills = getModel().getSkills();
				SubtextBuilder<FightSkill> builder = new SubtextBuilder<FightSkill>();
				
				SearchListFragment<FightSkill> newFragment = SearchListFragment.newInstance("Choose a skill to use:", builder, skills);
			    newFragment.show(getFragmentManager(), "dialog");
			}
		});
		
		final Button useitem = (Button)view.findViewById(R.id.fight_items);
		useitem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View btn) {
				ArrayList<FightItem> items = getModel().getItems();
				
				DialogFragment newFragment;
				
				if(getModel().hasFunkslinging())
					newFragment = FunkslingingDialog.create(items);
				else
					newFragment = SearchListFragment.newInstance("Choose an item to use:", items);
				
			    newFragment.show(getFragmentManager(), "dialog");
			}			
		});
		
		if(base.isFightOver()) {
			attack.setEnabled(false);
			useskill.setEnabled(false);
			useitem.setEnabled(false);
		}			
		
		super.onCreateSetup(view, base, savedInstanceState);
	}
}
