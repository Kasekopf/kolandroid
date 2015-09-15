package com.github.kolandroid.kol.android.view;

import android.content.Context;

import com.github.kolandroid.kol.data.DataMapper;
import com.github.kolandroid.kol.gamehandler.DataContext;

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
