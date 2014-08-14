package com.starfish.kol.scraping.data;

public class SkillData implements Data {
	private final Integer id;
	private final String img;
	private final String name;
	
	public SkillData(Integer id, String name, String img) {
		this.id = id;
		this.name = name;
		this.img = img;
	}
	
	public String toString() {
		return "$Skill[" + name + " : " + img + "]";
	}

	@Override
	public String getValues() {
		return id + "," + name + "," + img;
	}
}
