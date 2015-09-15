package com.github.kolandroid.kol.model.models.skill;

import com.github.kolandroid.kol.model.GroupModel.ChildModel;

public interface SkillsSubmodel extends ChildModel {
    <Result> Result execute(SkillsVisitor<Result> visitor);
}
