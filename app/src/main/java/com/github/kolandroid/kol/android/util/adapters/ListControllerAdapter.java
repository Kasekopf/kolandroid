package com.github.kolandroid.kol.android.util.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.github.kolandroid.kol.android.controller.Controller;
import com.github.kolandroid.kol.android.screen.Screen;

import java.util.ArrayList;
import java.util.List;

public class ListControllerAdapter<E extends Controller> extends BaseAdapter {
    private final Screen host;

    private List<E> baseList;

    public ListControllerAdapter(Screen host) {
        this(host, new ArrayList<E>());
    }

    public ListControllerAdapter(Screen host, List<E> baseList) {
        this.host = host;
        this.baseList = new ArrayList<>(baseList);
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

    @SuppressWarnings("unchecked")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        E controller = (E) this.getItem(position);

        View view;
        if (convertView != null) {
            E oldController = (E) convertView.getTag();
            oldController.disconnect(host);

            if (oldController.getView() != controller.getView()) {
                convertView = null;
            }
        }

        if (convertView == null) {
            view = LayoutInflater.from(host.getActivity()).inflate(controller.getView(), parent, false);
        } else {
            view = convertView;
        }

        view.setTag(controller);
        controller.attach(view, host);
        controller.connect(view, host);
        return view;
    }
}
