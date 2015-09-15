package com.github.kolandroid.kol.model.models.skill;

import com.github.kolandroid.kol.model.models.skill.SkillModelElement.Buff;
import com.github.kolandroid.kol.model.models.skill.SkillModelElement.RestorerItem;
import com.github.kolandroid.kol.model.models.skill.SkillModelElement.Skill;

public interface SkillModelVisitor {
    void display(Skill skill);

    void display(Buff buff);

    void display(RestorerItem item);
}
