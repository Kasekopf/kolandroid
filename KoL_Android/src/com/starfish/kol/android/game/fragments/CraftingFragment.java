package com.starfish.kol.android.game.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;

import com.starfish.kol.android.R;
import com.starfish.kol.android.dialogs.WebDialog;
import com.starfish.kol.android.game.BaseGameFragment;
import com.starfish.kol.android.game.GameFragment;
import com.starfish.kol.android.util.CustomFragmentTabHost;
import com.starfish.kol.model.models.CraftingModel;
import com.starfish.kol.model.models.WebModel;
import com.starfish.kol.model.models.CraftingModel.CraftingSubModel;
import com.starfish.kol.model.util.LiveModel.LiveMessage;

public class CraftingFragment extends BaseGameFragment<Void, CraftingModel> {
	public CraftingFragment() {
		super(R.layout.fragment_tabs_screen);
	}

	@Override
	public void onCreateSetup(View view, final CraftingModel base,
			Bundle savedInstanceState) {
		final CustomFragmentTabHost host = (CustomFragmentTabHost) view
				.findViewById(R.id.tabs_tabhost);
		host.setup(getActivity(), getChildFragmentManager(),
				R.id.tabs_tabcontent);

		for(int i = 0; i < base.getNumberSlots(); i++) {
			CraftingSubModel model = base.getSlot(i);
			
			host.addTab(host.newTabSpec(model.getTitle()).setIndicator(model.getTitle()),
					CraftingSubFragment.class, GameFragment.getModelBundle(model));
		}

		host.setCurrentTab(base.getInitialSlot());

		WebModel results = base.getResultsPane();
		if (results != null) {
			DialogFragment newFragment = new WebDialog();
			newFragment.setArguments(GameFragment.getModelBundle(results));
			newFragment.show(getChildFragmentManager(), "dialog");
		}
	}

	@Override
	protected void recieveProgress(Void message) {
		// do nothing
	}

	public static class CraftingSubFragment extends
			BaseGameFragment<LiveMessage, CraftingSubModel> {
		private WebFragment<WebModel> fragment;
		
		public CraftingSubFragment() {
			super(R.layout.fragment_crafting_subscreen);
		}

		@Override
		public void onCreateSetup(View view, CraftingSubModel base,
				Bundle savedInstanceState) {
			fragment = new WebFragment<WebModel>();
			fragment.setArguments(GameFragment.getModelBundle(base.getBaseModel()));
			this.getChildFragmentManager().beginTransaction()
					.add(R.id.crafting_web, fragment).commit();
		}

		@Override
		protected void recieveProgress(LiveMessage message) {
			fragment.updateModel(this.getModel().getBaseModel());
		}

	}
}
