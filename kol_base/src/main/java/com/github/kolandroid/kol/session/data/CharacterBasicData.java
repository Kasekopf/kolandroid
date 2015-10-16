package com.github.kolandroid.kol.session.data;

import java.io.Serializable;

public interface CharacterBasicData extends Serializable {
    String getPwdHash();

    int getCurrentHP();

    int getCurrentMP();

    int getMaxHP();

    int getMaxMP();

    int getMeat();

    int getAdventures();

    int getBaseMuscle();

    int getBuffedMuscle();

    int getBaseMyst();

    int getBuffedMyst();

    int getBaseMoxie();

    int getBuffedMoxie();

    String getName();

    String getTitle();

    String getAvatar();
}
