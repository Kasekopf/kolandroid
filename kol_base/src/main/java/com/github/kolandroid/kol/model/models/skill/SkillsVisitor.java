package com.github.kolandroid.kol.model.models.skill;

import java.io.Serializable;

public interface SkillsVisitor<Result> extends Serializable {
    Result execute(SkillsListModel model);

    Result execute(ItemRestorersModel model);
}
