package com.starfish.kol.android.game.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.View;

import com.starfish.kol.android.R;
import com.starfish.kol.android.dialogs.BuffDialog;
import com.starfish.kol.android.dialogs.MultiUseDialog;
import com.starfish.kol.android.dialogs.WebDialog;
import com.starfish.kol.android.game.GameFragment;
import com.starfish.kol.android.util.CustomFragmentTabHost;
import com.starfish.kol.android.util.CustomFragmentTabHost.OnCreateFragmentListener;
import com.starfish.kol.android.util.listbuilders.DefaultBuilder;
import com.starfish.kol.android.util.listbuilders.SkillsBuilder;
import com.starfish.kol.android.util.searchlist.GroupSearchListFragment;
import com.starfish.kol.android.util.searchlist.ListFragment;
import com.starfish.kol.android.util.searchlist.OnListSelection;
import com.starfish.kol.android.util.searchlist.SearchListFragment;
import com.starfish.kol.android.view.ModelWrapper;
import com.starfish.kol.model.models.WebModel;
import com.starfish.kol.model.models.skill.SkillModelElement;
import com.starfish.kol.model.models.skill.SkillModelElement.Buff;
import com.starfish.kol.model.models.skill.SkillModelElement.RestorerItem;
import com.starfish.kol.model.models.skill.SkillModelElement.Skill;
import com.starfish.kol.model.models.skill.SkillModelVisitor;
import com.starfish.kol.model.models.skill.SkillsModel;

public class SkillsFragment extends GameFragment<Void, SkillsModel> implements
		OnCreateFragmentListener {
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
		host.addTab(host.newTabSpec("skills").setIndicator("Skills"),
				GroupSearchListFragment.class, skillBund);

		Bundle itemBund = new Bundle();
		itemBund.putSerializable("builder", new DefaultBuilder<RestorerItem>());
		itemBund.putSerializable("list", base.getRestorers());
		host.addTab(host.newTabSpec("restore").setIndicator("MP Restoreres"),
				SearchListFragment.class, itemBund);

		if (base.getJustUsedItem())
			host.setCurrentTab(1);
		else
			host.setCurrentTab(0);
		host.setOnCreateFragmentListener(this);

		WebModel results = base.getResultsPane();
		if (results != null) {
			DialogFragment newFragment = new WebDialog();
			newFragment.setArguments(ModelWrapper.bundle(results));
			newFragment.show(getFragmentManager(), "dialog");
		}
	}

	public <T extends SkillModelElement> void setSelectionListener(Fragment f) {
		@SuppressWarnings("unchecked")
		ListFragment<T> fragment = (ListFragment<T>)f;
		fragment.setOnSelectionX(new OnListSelection<T>() {
			@Override
			public boolean selectItem(DialogFragment list, T item) {
				if (item.getIsDisabled())
					return false;

				item.select(new DialogSkillElementVisitor());
				return true;
			}			
		});
	}
	
	@Override
	public void setup(Fragment f, String tag) {
		this.setSelectionListener(f);
	}

	@Override
	protected void recieveProgress(Void message) {
		// do nothing
	}

	private class DialogSkillElementVisitor implements SkillModelVisitor {
		@Override
		public void display(Skill skill) {
			MultiUseDialog.create(skill, "Cast").show(getFragmentManager(),
					"skilloptions");
		}

		@Override
		public void display(Buff buff) {
			BuffDialog.create(buff).show(getFragmentManager(), "buffoptions");
		}

		@Override
		public void display(RestorerItem item) {
			MultiUseDialog.create(item, "Use").show(getFragmentManager(),
					"multiuseitem");
		}
	}
}
