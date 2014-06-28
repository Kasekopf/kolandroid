package com.starfish.kol.android.util.searchlist;

import java.util.ArrayList;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.starfish.kol.android.R;
import com.starfish.kol.android.util.adapters.ListElementBuilder;
import com.starfish.kol.android.util.listbuilders.DefaultBuilder;
import com.starfish.kol.model.basic.ActionItem;
import com.starfish.kol.model.interfaces.ModelItem;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * 
 */
public class SearchListFragment<E extends ModelItem> extends DialogFragment {
	public static <E extends ActionItem> SearchListFragment<E> newInstance(String title, ArrayList<E> elements) {
		SearchListFragment<E> frag = new SearchListFragment<E>();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putSerializable("list", elements);
        args.putSerializable("selector", new ActionSelector<E>());
        frag.setArguments(args);
        return frag;
	}
	
	private OnListSelection<E> selector;
	private HighlightableListAdapter<E> adapter;
	private ArrayList<E> base;
	private ListElementBuilder<E> builder;
	
	public SearchListFragment() {
		// Required empty public constructor
	}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	Dialog d = super.onCreateDialog(savedInstanceState);
        String title = getArguments().getString("title");
    	d.setTitle(title);
    	return d;
    }

	public void setOnSelectionX(OnListSelection<E> select) {
		this.selector = select;
	}

	public void setItems(ArrayList<E> base) {
		this.base = base;
		if(adapter != null)
			adapter.setElements(base);
	}
    
	@SuppressWarnings("unchecked")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_search_list, container, false);

		Bundle args = getArguments();
		
		if(base == null && args.containsKey("list"))
			base = (ArrayList<E>)args.getSerializable("list");
		if(base == null)
			base = new ArrayList<E>();
		
		if(args.containsKey("selector"))
			selector = (OnListSelection<E>)args.getSerializable("selector");
		
		if(args.containsKey("builder"))
			builder = (ListElementBuilder<E>)args.getSerializable("builder");
		if(builder == null)
			builder = new DefaultBuilder<E>();
		
		adapter = new HighlightableListAdapter<E>(view.getContext(), base, builder);
		
		ListView list = (ListView)view.findViewById(R.id.list_display_list);
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
				E choice = (E)myAdapter.getItemAtPosition(myItemInt);
				if(selector != null)
					if(selector.selectItem(SearchListFragment.this, choice)) {
						if (getShowsDialog())
							SearchListFragment.this.dismiss();
					}
			}
		});
		list.setAdapter(adapter);
		
		final EditText text = (EditText)view.findViewById(R.id.list_display_text);
		text.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				adapter.changeFilter(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				//ignored
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				//ignored				
			}
		});
		
		return view;
	}
}
