package com.starfish.kol.android.util;

import java.io.Serializable;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

import com.starfish.kol.android.util.adapters.ListElementBuilder;

public class ListElementFragment<E extends Serializable> extends Fragment {
	public static <E extends Serializable> ListElementFragment<E> newInstance(ListElementBuilder<E> builder, E defaultValue) {
		ListElementFragment<E> frag = new ListElementFragment<E>();
        Bundle args = new Bundle();
        args.putSerializable("builder", builder);
        args.putSerializable("default", defaultValue);
        frag.setArguments(args);
        return frag;
	}
	
	private E selected;
	
	private ListElementBuilder<E> builder;
	private View rootView;
	private OnTouchListener onTouch;
	private OnViewCompleted onCompleted;
	
	public void setValue(E value) {
		this.selected = value;
		builder.fillChild(rootView, value);
		getArguments().putSerializable("default", value);
	}
	
	public E getValue() {
		return selected;
	}
	
	public void setOnTouchListener(OnTouchListener listener) {
		onTouch = listener;
		if(rootView != null)
			rootView.setOnTouchListener(listener);
	}
	
	public void setOnViewCompleted(OnViewCompleted completed) {
		onCompleted = completed;
		if(rootView != null)
			completed.onCompleted(rootView);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Bundle args = getArguments();
		Log.i("ListElementFragment", "Creating View");
		this.builder = (ListElementBuilder<E>)args.getSerializable("builder");
		this.rootView = inflater.inflate(builder.getChildLayout(), container, false);
		
		if(onTouch != null)
			this.rootView.setOnTouchListener(onTouch);
		
		E defaultValue = (E)args.getSerializable("default");
		builder.fillChild(rootView, defaultValue);
		this.selected = defaultValue;
		
		if(onCompleted != null)
			this.onCompleted.onCompleted(rootView);
		return rootView;
	}
	
	public interface OnViewCompleted
	{
		public void onCompleted(View view);
	}
}
