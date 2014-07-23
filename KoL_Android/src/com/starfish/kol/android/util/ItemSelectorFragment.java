package com.starfish.kol.android.util;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.starfish.kol.android.util.adapters.ListElementBuilder;
import com.starfish.kol.android.util.searchlist.OnListSelection;
import com.starfish.kol.android.util.searchlist.SearchListFragment;
import com.starfish.kol.model.elements.interfaces.ModelElement;

public class ItemSelectorFragment<E extends ModelElement> extends Fragment {
	public static <E extends ModelElement> Bundle getBundle(String title, ListElementBuilder<E> builder, ArrayList<E> list, E def) {
		Bundle bund = new Bundle();
		bund.putString("title", title);
		bund.putSerializable("builder", builder);
		bund.putSerializable("list", list);
		bund.putSerializable("default", def);
		return bund;
	}
	
	private View rootView;
	private ListElementBuilder<E> builder;
	private E selected;
	
	public void setSelected(E selected) {
		this.selected = selected;
		if(builder != null && rootView != null)
			builder.fillChild(rootView, selected);
	}
	
	public void setItems(ArrayList<E> items) {
		getArguments().putSerializable("list", items);
	}
	
	public E getSelected() {
		return selected;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Bundle args = getArguments();

		builder = (ListElementBuilder<E>)args.getSerializable("builder");
		rootView = inflater.inflate(builder.getChildLayout(), container, false);
		rootView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				SearchListFragment<E> newFragment = new SearchListFragment<E>();
				newFragment.setArguments(getArguments());
				newFragment.setOnSelectionX(new OnListSelection<E>() {
					@Override
					public boolean selectItem(DialogFragment list, E item) {
						setSelected(item);
						return true;
					}			
				});
			    newFragment.show(getFragmentManager(), "dialog");
			}
		});

		E def = (E)args.getSerializable("default");
		this.setSelected(def);
		return rootView;
	}
}
