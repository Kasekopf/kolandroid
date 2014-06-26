package com.starfish.kol.android.util.adapters;

import android.view.View;

import com.starfish.kol.model.interfaces.ModelGroup;

public interface ListFullBuilder<E extends ModelGroup<F>, F> extends ListElementBuilder<F>{
	public int getGroupLayout();
	public int getChildLayout();
	
	public void fillGroup(View view, E group);
	public void fillChild(View view, F child);
}
