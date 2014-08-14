package com.starfish.kol.gamehandler;

import com.starfish.kol.data.DataMapper;

public interface DataContext {
	public DataMapper<String, String> getSkillsImageFinder();
	public DataMapper<String, String> getItemsImageFinder();
}
