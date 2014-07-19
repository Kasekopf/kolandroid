package com.starfish.kol.android.dialogs;

import java.util.ArrayList;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.starfish.kol.android.R;
import com.starfish.kol.android.util.searchlist.SearchListFragment;
import com.starfish.kol.model.basic.ActionItem;

public class FunkslingingDialog extends DialogFragment {
	public static FunkslingingDialog create(ArrayList<ActionItem> items) {
		FunkslingingDialog dialog = new FunkslingingDialog();
		Bundle args = new Bundle();
		args.putSerializable("items", items);
		dialog.setArguments(args);
		return dialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.setTitle("Select items to use:");
		return dialog;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = (View)inflater.inflate(R.layout.dialog_funkslinging,
				container, false);
		@SuppressWarnings("unchecked")
		ArrayList<ActionItem> items = (ArrayList<ActionItem>)this.getArguments().getSerializable("items");
		
		SearchListFragment<ActionItem> listfrag = SearchListFragment.newInstance("", items);
		FragmentTransaction trans = getChildFragmentManager()
				.beginTransaction().replace(R.id.funksling_list, listfrag);
		trans.commit();
				
		/*
	    ListAdapter<ActionItem> adapter = new ListAdapter<ActionItem>(this.getActivity(), item.getActions(), new DefaultBuilder<ActionItem>());
	    
	    ListView list = (ListView)rootView.findViewById(R.id.dialog_item_list);
	    list.setAdapter(adapter);
	    list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> ad, View list, int pos,
					long arg3) {
				ActionItem select = (ActionItem)ad.getItemAtPosition(pos);
				
				ApplicationView view = (ApplicationView)getActivity().getApplication();
				if(select != null && view != null) {
					view.executeAction(select);
					FunkslingingDialog.this.dismiss();
				}
			}
	    });
		
	    TextView text = (TextView)rootView.findViewById(R.id.dialog_item_text);
	    text.setText(item.getText());
	    
	    ImageView img = (ImageView)rootView.findViewById(R.id.dialog_item_image);
	    ImageDownloader.loadFromUrl(img, item.getImage());
	    */
		return rootView;
	}
}
