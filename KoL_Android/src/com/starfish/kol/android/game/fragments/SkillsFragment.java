package com.starfish.kol.android.game.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.View;

import com.starfish.kol.android.R;
import com.starfish.kol.android.dialogs.MultiUseDialog;
import com.starfish.kol.android.dialogs.SkillDialog;
import com.starfish.kol.android.dialogs.WebDialog;
import com.starfish.kol.android.game.BaseGameFragment;
import com.starfish.kol.android.game.GameFragment;
import com.starfish.kol.android.util.CustomFragmentTabHost;
import com.starfish.kol.android.util.CustomFragmentTabHost.OnCreateFragmentListener;
import com.starfish.kol.android.util.listbuilders.DefaultBuilder;
import com.starfish.kol.android.util.listbuilders.SkillsBuilder;
import com.starfish.kol.android.util.searchlist.GroupSearchListFragment;
import com.starfish.kol.android.util.searchlist.OnListSelection;
import com.starfish.kol.android.util.searchlist.SearchListFragment;
import com.starfish.kol.model.elements.RestorerItem;
import com.starfish.kol.model.elements.SkillElement;
import com.starfish.kol.model.models.SkillsModel;
import com.starfish.kol.model.models.WebModel;

public class SkillsFragment extends BaseGameFragment<Void, SkillsModel> implements OnCreateFragmentListener {
	public SkillsFragment() {
		super(R.layout.fragment_tabs_screen);
	}

	@Override
	public void onCreateSetup(View view, SkillsModel base,
			Bundle savedInstanceState) {
		final CustomFragmentTabHost host = (CustomFragmentTabHost) view
				.findViewById(R.id.tabs_tabhost);
		host.setup(getActivity(), getChildFragmentManager(),
				R.id.tabs_tabcontent);

		Bundle skillBund = new Bundle();
		skillBund.putSerializable("builder", new SkillsBuilder());
		skillBund.putSerializable("list", base.getSkills());
		host.addTab(host.newTabSpec("skills").setIndicator("Skills"), GroupSearchListFragment.class, skillBund);
		
		Bundle itemBund = new Bundle();
		itemBund.putSerializable("builder", new DefaultBuilder<RestorerItem>());
		itemBund.putSerializable("list", base.getRestorers());
		host.addTab(host.newTabSpec("restore").setIndicator("MP Restoreres"), SearchListFragment.class, itemBund);
		
		if(base.getJustUsedItem())
			host.setCurrentTab(1);
		else
			host.setCurrentTab(0);
		host.setOnCreateFragmentListener(this);
		
		WebModel results = base.getResultsPane();
		if(results != null) {
			DialogFragment newFragment = new WebDialog();
			newFragment.setArguments(GameFragment.getModelBundle(results));
		    newFragment.show(getFragmentManager(), "dialog");
		}
	}
	
	@Override
	public void setup(Fragment f, String tag) {
		final SkillsModel model = this.getModel();
		switch(tag) {
		case "skills":
			@SuppressWarnings("unchecked")
			final GroupSearchListFragment<SkillElement> fragment = (GroupSearchListFragment<SkillElement>)f;
			fragment.setOnSelectionX(new OnListSelection<SkillElement>() {
				@Override
				public boolean selectItem(DialogFragment list, final SkillElement item) {
					if(item.getIsDisabled()) return false;
					
					SkillDialog.create(item).show(getFragmentManager(), "skilloptions");
					return true;
				}
			});
			break;
		case "restore":
			@SuppressWarnings("unchecked")
			final SearchListFragment<RestorerItem> itemFrag = (SearchListFragment<RestorerItem>)f;
			
			itemFrag.setOnSelectionX(new OnListSelection<RestorerItem>() {
				@Override
				public boolean selectItem(DialogFragment list, final RestorerItem item) {
					MultiUseDialog.create(model, item).show(getFragmentManager(), "multiuseitem");
					return true;
				}				
			});
			
		}
	}

	@Override
	protected void recieveProgress(Void message) {
		//do nothing
	}
}
