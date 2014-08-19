package com.starfish.kol.model.models.skill;

import com.starfish.kol.model.GroupModel.ChildModel;

public interface SkillsSubmodel extends ChildModel {
	public <Result> Result execute(SkillsVisitor<Result> visitor);
}
