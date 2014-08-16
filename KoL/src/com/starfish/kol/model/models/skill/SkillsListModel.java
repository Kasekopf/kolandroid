package com.starfish.kol.model.models.skill;

import java.util.ArrayList;

import com.starfish.kol.connection.ServerReply;
import com.starfish.kol.connection.Session;
import com.starfish.kol.model.Model;
import com.starfish.kol.model.elements.OptionElement;
import com.starfish.kol.model.elements.OptionElement.OptionElementParser;
import com.starfish.kol.model.elements.interfaces.ModelGroup;
import com.starfish.kol.model.models.skill.SkillModelElement.Buff;
import com.starfish.kol.model.models.skill.SkillModelElement.Skill;
import com.starfish.kol.util.Regex;

public class SkillsListModel extends Model<Void> implements SkillsSubmodel {
	/**
	 * Autogenerated by eclipse.
	 */
	private static final long serialVersionUID = -6757825084689878953L;
	
	private static final Regex SKILLS_FORM = new Regex(
			"<form[^>]*skillform[^>]*>.*?</form>", 0);
	private static final Regex BUFFS_FORM = new Regex(
			"<form[^>]*buffform[^>]*>.*?</form>", 0);
	private static final Regex BUFFS_LIST = OptionElement
			.regexFor("whichskill");

	private static final Regex PWD = new Regex("<input[^>]*pwd[^>]*>", 0);
	private static final Regex EXTRACT_VALUE = new Regex(
			"value=[\"']?([0-9a-fA-F]*)", 1);

	private static final Regex OPTION_YOURSELF = new Regex(
			"<option value=[\"']?(\\d+)[\"']?>\\(yourself\\)</option>", 1);
	
	private ArrayList<ModelGroup<SkillModelElement>> skills;
	
	public SkillsListModel(Session s, ServerReply base) {
		super(s);
		this.skills = processSkills(base.html);
	}

	private ArrayList<ModelGroup<SkillModelElement>> processSkills(String html) {
		ArrayList<ModelGroup<SkillModelElement>> skills = new ArrayList<ModelGroup<SkillModelElement>>();

		String all_skills = SKILLS_FORM.extractSingle(html);
		final String pwd = EXTRACT_VALUE.extractSingle(PWD
				.extractSingle(all_skills));

		OptionElementParser<SkillModelElement> skillparser = new OptionElementParser<SkillModelElement>(
				"(select a skill)") {
			@Override
			public SkillModelElement make(OptionElement base) {
				return new Skill(getSession(), base, pwd);
			}
		};

		skills.addAll(OptionElement.extractObjectGroups(all_skills, "Skills",
				skillparser));

		String all_buffs = BUFFS_LIST.extractSingle(BUFFS_FORM
				.extractSingle(html));
		final String yourself = OPTION_YOURSELF.extractSingle(all_buffs);
		OptionElementParser<SkillModelElement> buffparser = new OptionElementParser<SkillModelElement>(
				"(select a buff)") {
			@Override
			public SkillModelElement make(OptionElement base) {
				return new Buff(getSession(), base, pwd, yourself);
			}
		};

		skills.addAll(OptionElement.extractObjectGroups(all_buffs, "Buffs",
				buffparser));

		return skills;
	}
	
	public ArrayList<ModelGroup<SkillModelElement>> getSkills() {
		return skills;
	}
	
	@Override
	public String getTitle() {
		return "Skills";
	}

	@Override
	public <Result> Result execute(SkillsVisitor<Result> visitor) {
		return visitor.execute(this);
	}
}
