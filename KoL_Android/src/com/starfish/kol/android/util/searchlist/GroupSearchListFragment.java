package com.starfish.kol.android.util.searchlist;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

import com.starfish.kol.android.R;
import com.starfish.kol.android.util.adapters.ListFullBuilder;
import com.starfish.kol.android.util.listbuilders.DefaultBuilder;
import com.starfish.kol.model.basic.ActionItem;
import com.starfish.kol.model.interfaces.ModelGroup;
import com.starfish.kol.model.interfaces.ModelItem;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * 
 */
@SuppressLint("ValidFragment")
public class GroupSearchListFragment<F extends ModelItem> extends
		DialogFragment {
	public static <F extends ActionItem> GroupSearchListFragment<F> newInstance(
			String title, ArrayList<ModelGroup<F>> elements) {
		GroupSearchListFragment<F> frag = new GroupSearchListFragment<F>();
		Bundle args = new Bundle();
		args.putString("title", title);
		args.putSerializable("list", elements);
		args.putSerializable("selector", new ActionSelector<F>());
		frag.setArguments(args);
		return frag;
	}

	private ArrayList<ModelGroup<F>> base;
	private OnListSelection<F> selector;
	private ListFullBuilder<ModelGroup<F>, F> builder;
	private HighlightableListGroupAdapter<F> adapter;
	private ExpandableListView list;

	public GroupSearchListFragment() {
		this(new DefaultBuilder<F>());
	}

	protected GroupSearchListFragment(ListFullBuilder<ModelGroup<F>, F> builder) {
		this.builder = builder;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog d = super.onCreateDialog(savedInstanceState);
		String title = getArguments().getString("title");
		d.setTitle(title);
		return d;
	}

	public void setOnSelectionX(OnListSelection<F> select) {
		this.selector = select;
	}

	public void setItems(ArrayList<ModelGroup<F>> base) {
		this.base = base;
		if(adapter != null && list != null) {
			adapter.setElements(base);
			for (int i = 0; i < adapter.getGroupCount(); i++)
				list.expandGroup(i);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_group_list, container,
				false);
		
		Bundle args = getArguments();
		
		if(base == null && args.containsKey("list"))
			base = (ArrayList<ModelGroup<F>>)args.getSerializable("list");
		if(base == null)
			base = new ArrayList<ModelGroup<F>>();
		
		if(args.containsKey("builder"))
			builder = (ListFullBuilder<ModelGroup<F>, F>)args.getSerializable("builder");
		if(builder == null)
			builder = new DefaultBuilder<F>();
		
		if(args.containsKey("selector") && selector == null)
			selector = (OnListSelection<F>)args.getSerializable("selector");
		
		adapter = new HighlightableListGroupAdapter<F>(view.getContext(), base, builder);
		list = (ExpandableListView) view.findViewById(R.id.list_display_list);
		list.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView arg0, View arg1,
					int groupPosition, int childPosition, long id) {
				F choice = (F) adapter.getChild(groupPosition, childPosition);
				if (selector != null)
					if (selector.selectItem(GroupSearchListFragment.this, choice)) {
						if (getShowsDialog())
							GroupSearchListFragment.this.dismiss();
					}
				return true;
			}

		});
		list.setAdapter(adapter);
		for (int i = 0; i < adapter.getGroupCount(); i++)
			list.expandGroup(i);

		final EditText text = (EditText) view
				.findViewById(R.id.list_display_text);
		text.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				adapter.changeFilter(s.toString());
				for (int i = 0; i < adapter.getGroupCount(); i++)
					list.expandGroup(i);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// ignored
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// ignored
			}
		});

		return view;
	}
}
