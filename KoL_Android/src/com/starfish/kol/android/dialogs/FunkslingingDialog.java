package com.starfish.kol.android.dialogs;

import java.util.ArrayList;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.starfish.kol.android.R;
import com.starfish.kol.android.util.ListElementFragment;
import com.starfish.kol.android.util.ListElementFragment.OnViewCompleted;
import com.starfish.kol.android.util.listbuilders.DefaultBuilder;
import com.starfish.kol.android.util.searchlist.OnListSelection;
import com.starfish.kol.android.util.searchlist.SearchListFragment;
import com.starfish.kol.gamehandler.ViewContext;
import com.starfish.kol.model.elements.FightItem;

public class FunkslingingDialog extends DialogFragment {
	public static FunkslingingDialog create(ArrayList<FightItem> items) {
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

	private ListElementFragment<FightItem> item1Frag, item2Frag;
	private ListElementFragment<FightItem> selected;
	
	private void swapSelected() {
		if(selected == item1Frag)
			setSelected(item2Frag);
		else
			setSelected(item1Frag);
	}
	
	private void setSelected(ListElementFragment<FightItem> frag) {
		if(this.selected != null) {
			this.selected.getView().setPressed(false);
		}
		
		this.selected = frag;
		frag.getView().setPressed(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = (View)inflater.inflate(R.layout.dialog_funkslinging,
				container, false);
		@SuppressWarnings("unchecked")
		ArrayList<FightItem> items = (ArrayList<FightItem>)this.getArguments().getSerializable("items");
		
		item1Frag = ListElementFragment.newInstance(new DefaultBuilder<FightItem>(), FightItem.NONE);
		item1Frag.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				setSelected(item1Frag);
				return true;
			}
		});
		item1Frag.setOnViewCompleted(new OnViewCompleted() {
			@Override
			public void onCompleted(View view) {
				view.setPressed(true);
			}			
		});
		this.selected = item1Frag;
		
		FragmentTransaction trans = getChildFragmentManager().beginTransaction().replace(R.id.funksling_item1, item1Frag);
		trans.commit();

		item2Frag = ListElementFragment.newInstance(new DefaultBuilder<FightItem>(), FightItem.NONE);
		item2Frag.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				setSelected(item2Frag);
				return true;
			}
		});
		trans = getChildFragmentManager().beginTransaction().replace(R.id.funksling_item2, item2Frag);
		trans.commit();
		
		//setSelected(item1Frag);
				
		SearchListFragment<FightItem> listfrag = SearchListFragment.newInstance("", items);
		trans = getChildFragmentManager()
				.beginTransaction().replace(R.id.funksling_list, listfrag);
		trans.commit();
		
		listfrag.setOnSelectionX(new OnListSelection<FightItem>() {
			@Override
			public boolean selectItem(DialogFragment list, FightItem item) {
				selected.setValue(item);
				swapSelected();
				return true;
			}			
		});
		
		Button submit = (Button)rootView.findViewById(R.id.funksling_submit);
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				FightItem item1 = item1Frag.getValue();
				FightItem item2 = item2Frag.getValue();
				
				boolean submitted = item1.useWith((ViewContext)getActivity(), item2);
				if(submitted)
					FunkslingingDialog.this.dismiss();
			}			
		});
		
		return rootView;
	}
}
