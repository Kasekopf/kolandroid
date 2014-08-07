package com.starfish.kol.model.models.skill;

import com.starfish.kol.model.models.skill.SkillModelElement.Buff;
import com.starfish.kol.model.models.skill.SkillModelElement.RestorerItem;
import com.starfish.kol.model.models.skill.SkillModelElement.Skill;

public interface SkillModelVisitor {
	public void display(Skill skill);
	public void display(Buff buff);
	public void display(RestorerItem item);
}
