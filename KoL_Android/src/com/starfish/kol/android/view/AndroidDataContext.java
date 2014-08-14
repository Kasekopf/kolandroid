package com.starfish.kol.android.view;

import android.content.Context;

import com.starfish.kol.data.DataMapper;
import com.starfish.kol.gamehandler.DataContext;

public class AndroidDataContext implements DataContext {
	private FileMapper skillsImages;
	private FileMapper itemsImages;
	
	public AndroidDataContext(Context context) {
		
	}
	
	public DataMapper<String, String> getSkillsImageFinder() {
		return skillsImages;
	}
	
	public DataMapper<String, String> getItemsImageFinder() {
		return itemsImages;
	}
}
