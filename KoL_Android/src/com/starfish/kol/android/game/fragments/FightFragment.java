package com.starfish.kol.android.game.fragments;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.starfish.kol.android.R;
import com.starfish.kol.android.util.searchlist.SearchListFragment;
import com.starfish.kol.model.basic.ActionItem;
import com.starfish.kol.model.models.FightModel;

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
				ActionItem action = getModel().getAttack();
				if(action != null)
					action.submit(getModel());
			}
		});
		
		final Button useskill = (Button)view.findViewById(R.id.fight_skill);
		useskill.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View btn) {
				ArrayList<ActionItem> skills = getModel().getSkills();

				SearchListFragment<ActionItem> newFragment = SearchListFragment.newInstance("Choose a skill to use:", skills);
			    newFragment.show(getFragmentManager(), "dialog");
			}
		});
		
		final Button useitem = (Button)view.findViewById(R.id.fight_items);
		useitem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View btn) {
				ArrayList<ActionItem> items = getModel().getItems();
				
				SearchListFragment<ActionItem> newFragment = SearchListFragment.newInstance("Choose an item to use:", items);
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
