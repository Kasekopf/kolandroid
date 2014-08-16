package com.starfish.kol.model.models.skill;

public interface SkillsVisitor<Result> {
	public Result execute(SkillsListModel model);
	public Result execute(ItemsListModel model);
}
