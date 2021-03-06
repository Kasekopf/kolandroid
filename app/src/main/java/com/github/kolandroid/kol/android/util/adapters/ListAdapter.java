package com.github.kolandroid.kol.android.util.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.github.kolandroid.kol.android.binders.Binder;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter<E> extends BaseAdapter {
    private final Binder<? super E> binder;
    private final Context context;

    private List<E> baseList;

    public ListAdapter(Context c, Binder<? super E> binder) {
        this(c, new ArrayList<E>(), binder);
    }

    public ListAdapter(Context c, List<E> baseList, Binder<? super E> binder) {
        this.baseList = new ArrayList<>(baseList);
        this.context = c;
        this.binder = binder;
    }

    public void setElements(List<E> base) {
        this.baseList = new ArrayList<>(base);
        super.notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        throw new RuntimeException("Should not manually change list elements");
    }

    @Override
    public int getCount() {
        return baseList.size();
    }

    public void addItem(E item) {
        this.baseList.add(item);
        super.notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        return baseList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public Context getContext() {
        return context;
    }

    @SuppressWarnings("unchecked")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null)
            view = LayoutInflater.from(context).inflate(binder.getView(), parent, false);
        else
            view = convertView;

        binder.bind(view, (E) this.getItem(position));
        return view;
    }
}
