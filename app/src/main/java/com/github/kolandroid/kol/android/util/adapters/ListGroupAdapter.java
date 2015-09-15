package com.github.kolandroid.kol.android.util.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.github.kolandroid.kol.android.binders.Binder;
import com.github.kolandroid.kol.android.binders.DefaultGroupBinder;
import com.github.kolandroid.kol.model.elements.interfaces.ModelGroup;

import java.util.ArrayList;
import java.util.List;

public class ListGroupAdapter<E extends ModelGroup<F>, F>
        extends BaseExpandableListAdapter {
    private final Context context;

    private final Binder<? super F> elementBinding;
    private final Binder<? super E> groupBinding;

    private List<E> baseList;

    public ListGroupAdapter(Context c, Binder<? super F> elementBinding) {
        this(c, new ArrayList<E>(), elementBinding);
    }

    public ListGroupAdapter(Context c, List<E> baseList, Binder<? super F> elementBinding) {
        this.baseList = baseList;
        this.context = c;
        this.elementBinding = elementBinding;
        this.groupBinding = DefaultGroupBinder.ONLY;
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
        if (convertView != null && "isChild".equals(convertView.getTag())) {
            view = convertView;
        } else {
            view = LayoutInflater.from(context).inflate(elementBinding.getView(), parent, false);
        }

        view.setTag("isChild");

        @SuppressWarnings("unchecked")
        F item = (F) getChild(groupPosition, childPosition);
        elementBinding.bind(view, item);
        return view;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        View view;
        if (convertView != null && "isGroup".equals(convertView.getTag())) {
            view = convertView;
        } else {
            view = LayoutInflater.from(context).inflate(groupBinding.getView(), parent, false);
        }

        view.setTag("isGroup");

        @SuppressWarnings("unchecked")
        E item = (E) getGroup(groupPosition);
        groupBinding.bind(view, item);
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
