package com.starfish.kol.android.controllers;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.starfish.kol.android.R;
import com.starfish.kol.android.binders.ElementBinder;
import com.starfish.kol.android.controller.LinkedModelController;
import com.starfish.kol.android.screen.Screen;
import com.starfish.kol.android.screen.ScreenSelection;
import com.starfish.kol.android.util.adapters.ListAdapter;
import com.starfish.kol.model.LiveModel.LiveMessage;
import com.starfish.kol.model.elements.ActionElement;
import com.starfish.kol.model.models.NavigationModel;

public class NavigationController extends LinkedModelController<LiveMessage, NavigationModel> {
	/**
	 * Autogenerated by eclipse.
	 */
	private static final long serialVersionUID = -9139598436492012011L;

	private transient ListAdapter<ActionElement> adapter;
	
	public NavigationController(NavigationModel model) {
		super(model);
	}

	@Override
	public int getView() {
		return R.layout.fragment_navigation_drawer;
	}

	@Override
	public void chooseScreen(ScreenSelection choice) {
		choice.displayDialog(this);
	}

	@Override
	public void recieveProgress(View view, NavigationModel model,
			LiveMessage message, Screen host) {
		if(adapter != null)
			adapter.setElements(getModel().getLocations());
	}

	@Override
	public void connect(View view, NavigationModel model, final Screen host) {
		ListView mDrawerListView = (ListView)view.findViewById(R.id.navigation_list);
		
        adapter = new ListAdapter<ActionElement>(view.getContext(), model.getLocations(), ElementBinder.ONLY);
        mDrawerListView.setAdapter(adapter);
        
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ActionElement choice = (ActionElement)parent.getItemAtPosition(position);
				choice.submit(host.getViewContext());
				host.close();
            }
        });
	}

}
