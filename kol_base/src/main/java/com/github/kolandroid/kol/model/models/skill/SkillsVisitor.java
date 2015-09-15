package com.github.kolandroid.kol.model.models.skill;

public interface SkillsVisitor<Result> {
    Result execute(SkillsListModel model);

    Result execute(ItemsListModel model);
}
