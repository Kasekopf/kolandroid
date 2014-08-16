package com.starfish.kol.model.models.skill;

import com.starfish.kol.model.ChildModel;

public interface SkillsSubmodel extends ChildModel {
	public <Result> Result execute(SkillsVisitor<Result> visitor);
}
