package com.github.kolandroid.kol.android.util.adapters;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.kolandroid.kol.android.controller.Controller;
import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.util.Logger;

import java.util.ArrayList;
import java.util.List;

public class PagerControllerAdapter<E extends Controller> extends PagerAdapter {
    private final Screen host;
    private ArrayList<E> base;

    public PagerControllerAdapter(Screen host) {
        this.host = host;
        this.base = new ArrayList<>();
    }

    public PagerControllerAdapter(Screen host, ArrayList<E> base) {
        this.host = host;
        this.base = new ArrayList<>(base);
    }

    public void setElements(List<E> base) {
        this.base = new ArrayList<>(base);
        super.notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        throw new RuntimeException("Should not manually change list elements");
    }

    @Override
    public int getCount() {
        return base.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (position >= base.size()) {
            Logger.log("PagerControllerAdapter", "Unable to load position " + position + " of size " + base.size() + "list");
            return null;
        }

        Controller controller = base.get(position);
        Logger.log("PageControllerAdapter", "Creating " + position + " with tag " + controller);
        LayoutInflater inflater = LayoutInflater.from(host.getActivity());
        View view = inflater.inflate(controller.getView(), container, false);
        controller.attach(view, host);
        controller.connect(view, host);
        container.addView(view);
        view.setTag(controller);
        return controller;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (object instanceof Controller) ((Controller) object).disconnect(host);

        Logger.log("PageControllerAdapter", "Removing " + position + " with tag " + object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        Object tag = view.getTag();
        return tag == object;
    }
}
