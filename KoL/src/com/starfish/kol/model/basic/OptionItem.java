package com.starfish.kol.model.basic;

import java.util.ArrayList;

import com.starfish.kol.model.interfaces.ModelGroup;
import com.starfish.kol.util.Regex;

public class OptionItem {
	private static final Regex OPTION = new Regex(
			"<option([^<>]*?)>(.*?)</option>", 1, 2);
	private static final Regex OPTION_ID = new Regex(
			"value=\"?(\\d+)([<>\" ]|$)", 1);
	private static final Regex OPTION_PIC = new Regex(
			"picurl=\"?([^<>\" ]+)([<>\" ]|$)", 1);
	
	public final String text;
	public final String img;
	public final String value;
	
	public OptionItem(String text, String img, String value) {
		this.text = text;
		this.img = img;
		this.value = value;
	}
	
	public static Regex regexFor(String select) {
		return new Regex(
				"<select name=" + select + ">(.*?)</select>", 1);
	}
	
	public static ArrayList<ModelGroup<OptionItem>> extractOptionGroups(String dropdown) {
		return null;
	}
	
	public static ArrayList<OptionItem> extractOptions(String dropdown) {
		ArrayList<OptionItem> result = new ArrayList<OptionItem>();
		
		ArrayList<String[]> options = OPTION.extractAll(dropdown);

		for (String[] option : options) {
			if (option == null)
				continue;

			String num = OPTION_ID.extractSingle(option[0]);
			String img = OPTION_PIC.extractSingle(option[0]);
			String text = option[1];

			if (num == null || img == null || num.length() == 0)
				continue;

			if (!img.contains("images.kingdomofloathing.com"))
				img = "images.kingdomofloathing.com/itemimages/" + img;
			if (!img.endsWith(".gif") && !img.endsWith(".png"))
				img += ".gif";
			result.add(new OptionItem(text, img, num));
		}
		return result;
	}
}
