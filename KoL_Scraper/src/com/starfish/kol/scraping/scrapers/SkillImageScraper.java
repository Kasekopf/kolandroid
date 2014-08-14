package com.starfish.kol.scraping.scrapers;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.starfish.kol.scraping.data.DataWriter;
import com.starfish.kol.scraping.data.SkillData;
import com.starfish.kol.util.Regex;


public class SkillImageScraper extends Scraper<Integer, SkillData> {
	private final int MAX_SKILL = 15033;
	
	@Override
	protected Iterator<Integer> getTargetList() {
		ArrayList<Integer> all = new ArrayList<Integer>();
		for(int i = 1; i < MAX_SKILL; i++)
			all.add(i);
		return all.iterator();
	}

	@Override
	protected String getTarget(Integer id) {
		return "desc_skill.php?whichskill=" + id;
	}

	private static final Regex IMG = new Regex("<img[^>]*>", 0);
	private static final Regex IMG_SRC = new Regex("src=[\"']?([^\"'> ]*)[\"'> ]", 1);
	
	private static final Regex NAME = new Regex("<font[^>]*><b>(.*?)</b>", 1);
	
	@Override
	protected SkillData process(Integer id, String url, String html) {
		if(html.contains("No skill found."))
			return null;
		
		String img_file = IMG_SRC.extractSingle(IMG.extractSingle(html));
		String name = NAME.extractSingle(html);
		if(name == null || img_file == null)
			return null;
		return new SkillData(id, name, img_file);
	}

	@Override
	protected void onFinish(List<SkillData> results) {
		DataWriter writer = new DataWriter("skills.dat", "Skills", 1);
		writer.write(results);
		writer.close();
	}
}
