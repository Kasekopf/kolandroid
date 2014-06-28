package com.starfish.kol.android.util.searchlist;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.starfish.kol.android.R;
import com.starfish.kol.android.util.adapters.ListFullBuilder;
import com.starfish.kol.android.util.adapters.ListGroupAdapter;
import com.starfish.kol.model.basic.BasicGroup;
import com.starfish.kol.model.interfaces.ModelGroup;
import com.starfish.kol.util.Regex;

public class HighlightableListGroupAdapter<F> extends ListGroupAdapter<ModelGroup<F>, F>{
	private List<ModelGroup<F>> displayed;
	private List<ModelGroup<F>> base;
	private Regex filter;
	
	public HighlightableListGroupAdapter(Context context, List<ModelGroup<F>> base, ListFullBuilder<ModelGroup<F>, F> builder) {
		super(context, builder);
		
		this.displayed = new ArrayList<ModelGroup<F>>();
		this.filter = new Regex("");
		this.setElements(base);
	}
	
	public void changeFilter(String newValue) {
		filter = new Regex(newValue);
		displayed.clear();
		this.setElements(base);
	}
	
	@Override
	public void setElements(List<ModelGroup<F>> base) {
		this.base = base;
		displayed.clear();
		for(ModelGroup<F> elem : base) {
			BasicGroup<F> newgroup = new BasicGroup<F>(elem.getName());
			for(int i = 0; i < elem.size(); i++) {
				F item = elem.get(i);
				if(filter.matches(item.toString()))
					newgroup.add(item);
			}
			
			if(newgroup.size() > 0)
				displayed.add(newgroup);
		}
		super.setElements(displayed);
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		View view = super.getChildView(groupPosition, childPosition, isLastChild, convertView, parent);
		
		TextView text = (TextView)view.findViewById(R.id.list_item_text);
		String highlighted = filter.replaceAll(text.getText().toString(), "<b>$0</b>");
		text.setText(Html.fromHtml(highlighted));
		return view;
	}
}
