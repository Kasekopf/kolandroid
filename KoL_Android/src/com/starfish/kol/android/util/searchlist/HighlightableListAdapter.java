package com.starfish.kol.android.util.searchlist;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.starfish.kol.android.R;
import com.starfish.kol.android.util.adapters.ListAdapter;
import com.starfish.kol.android.util.adapters.ListElementBuilder;
import com.starfish.kol.util.Regex;

public class HighlightableListAdapter<E> extends ListAdapter<E>{
	private List<E> displayed;
	private List<E> base;
	private Regex filter;
	
	public HighlightableListAdapter(Context context, List<E> base, ListElementBuilder<E> builder) {
		super(context, builder);
		
		this.displayed = new ArrayList<E>();
		this.filter = new Regex("");
		this.setElements(base);
	}
	
	public void changeFilter(String newValue) {		
		filter = new Regex("(?i)\\Q" + newValue + "\\E");
		displayed.clear();
		this.setElements(base);
	}
	
	@Override
	public void setElements(List<E> base) {
		this.base = base;
		displayed.clear();
		for(E elem : base) {
			if(filter.matches(elem.toString()))
				displayed.add(elem);
		}
		super.setElements(displayed);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);

		TextView text = (TextView)view.findViewById(R.id.list_item_text);
		String highlighted = filter.replaceAll(text.getText().toString(), "<b>$0</b>");
		text.setText(Html.fromHtml(highlighted));
		return view;
	}
}
