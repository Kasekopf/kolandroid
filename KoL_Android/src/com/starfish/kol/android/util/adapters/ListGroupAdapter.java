package com.starfish.kol.android.util.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.starfish.kol.model.elements.interfaces.ModelGroup;

public class ListGroupAdapter<E extends ModelGroup<F>, F>
		extends BaseExpandableListAdapter {
	private List<E> baseList;
	private Context context;
	private ListFullBuilder<E, F> builder;

	public ListGroupAdapter(Context c, ListFullBuilder<E, F> builder) {
		this(c, new ArrayList<E>(), builder);
	}

	public ListGroupAdapter(Context c, List<E> baseList, ListFullBuilder<E, F> builder) {
		this.baseList = baseList;
		this.context = c;
		this.builder = builder;
	}

	public void setElements(List<E> baseList) {
		this.baseList = baseList;
		this.notifyDataSetChanged();
	}
	
	public Context getContext() {
		return context;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return baseList.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}
	
	@Override
	public int getChildrenCount(int groupPosition) {
		return baseList.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return baseList.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return baseList.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		View view;
		if(convertView != null && "isChild".equals(convertView.getTag())) {
			view = convertView;
		} else {
			view = LayoutInflater.from(context).inflate(builder.getChildLayout(), parent, false);
		}

		view.setTag("isChild");
		
		@SuppressWarnings("unchecked")
		F item = (F)getChild(groupPosition, childPosition);
		builder.fillChild(view, item);
		return view;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		View view;
		if(convertView != null && "isGroup".equals(convertView.getTag())) {
			view = convertView;
		} else {
			view = LayoutInflater.from(context).inflate(builder.getGroupLayout(), parent, false);
		}

		view.setTag("isGroup");
		
		@SuppressWarnings("unchecked")
		E item = (E)getGroup(groupPosition);
		builder.fillGroup(view, item);
		return view;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}
