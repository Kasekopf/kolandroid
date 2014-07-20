package com.starfish.kol.android.game.fragments.inventory;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TabHost.TabSpec;

import com.starfish.kol.android.R;
import com.starfish.kol.android.dialogs.WebDialog;
import com.starfish.kol.android.game.BaseGameFragment;
import com.starfish.kol.android.game.GameFragment;
import com.starfish.kol.android.util.CustomFragmentTabHost;
import com.starfish.kol.model.Model;
import com.starfish.kol.model.models.WebModel;
import com.starfish.kol.model.models.inventory.InventoryModel;

public class InventoryFragment extends BaseGameFragment<Void, InventoryModel> {
	public InventoryFragment() {
		super(R.layout.fragment_tabs_screen);
	}

	@Override
	public void onCreateSetup(View view, InventoryModel base,
			Bundle savedInstanceState) {

		final CustomFragmentTabHost host = (CustomFragmentTabHost) view
				.findViewById(R.id.tabs_tabhost);
		host.setup(getActivity(), getChildFragmentManager(),
				R.id.tabs_tabcontent);

		addTab(host, "recent", base.getRecent(), InventoryPaneFragment.class);
		addTab(host, "consum", base.getConsume(), InventoryPaneFragment.class);
		addTab(host, "equip", base.getEquip(), EquipmentPaneFragment.class);
		addTab(host, "misc", base.getMisc(), InventoryPaneFragment.class);
		
		int current = base.getInitialChosen();
		if(current == 3)
			host.setCurrentTab(0);
		else
			host.setCurrentTab(current + 1);
		
		WebModel results = base.getResultsPane();
		if(results != null) {
			DialogFragment newFragment = new WebDialog();
			newFragment.setArguments(GameFragment.getModelBundle(results));
		    newFragment.show(getFragmentManager(), "dialog");
		}
	}
	
	private <S, T extends Model<S>, U extends BaseGameFragment<S, T>> void addTab(CustomFragmentTabHost host, String name, T model, Class<U> tabType) {
		Bundle bundle = BaseGameFragment.getModelBundle(model);
		TabSpec tab = host.newTabSpec(name).setIndicator(name);
		host.addTab(tab, tabType, bundle);
	}

	@Override
	protected void recieveProgress(Void message) {
		//do nothing
	}
}
