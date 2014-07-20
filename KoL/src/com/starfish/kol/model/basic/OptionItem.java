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

	private static final Regex OPTION_GROUP = new Regex(
			"(^|<optgroup[^>]*>).*?(?=<optgroup|$)", 0);
	private static final Regex OPTION_GROUP_NAME = new Regex(
			"<optgroup[^>]*label=[\"']?(.*?)[\"'>]", 1);

	private static final Regex OPTION_DISABLED = new Regex(
			"<option[^>]*disabled[^>]*>", 0);
	
	public final String text;
	public final String img;
	public final String value;
	public final boolean disabled;
	
	private OptionItem(String text, String img, String value, boolean disabled) {
		this.text = text;
		this.img = img;
		this.value = value;
		this.disabled = disabled;
	}
	
	public static Regex regexFor(String select) {
		return new Regex(
				"<select name=" + select + ">(.*?)</select>", 1);
	}
	
	public static ArrayList<ModelGroup<OptionItem>> extractOptionGroups(String dropdown, String defaultName) {
		ArrayList<ModelGroup<OptionItem>> options = new ArrayList<ModelGroup<OptionItem>>();
		
		for (String group : OPTION_GROUP.extractAllSingle(dropdown)) {
			String name = OPTION_GROUP_NAME.extractSingle(group);
			if(name == null) name = defaultName;
			
			ArrayList<OptionItem> option_group = extractOptions(group);
			BasicGroup<OptionItem> section = new BasicGroup<OptionItem>(name, option_group);
			options.add(section);
		}
		return options;
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
			boolean disabled = OPTION_DISABLED.matches(option[0]);

			if (num == null || num.length() == 0)
				continue;
			
			if(img == null) {
				img = "";
			} else {
				if (!img.contains("images.kingdomofloathing.com"))
					img = "images.kingdomofloathing.com/itemimages/" + img;
				if (!img.endsWith(".gif") && !img.endsWith(".png"))
					img += ".gif";
			}
			
			result.add(new OptionItem(text, img, num, disabled));
		}
		return result;
	}
}
