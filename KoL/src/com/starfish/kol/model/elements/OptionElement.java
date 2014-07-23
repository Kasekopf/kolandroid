package com.starfish.kol.model.elements;

import java.util.ArrayList;

import com.starfish.kol.model.elements.basic.BasicGroup;
import com.starfish.kol.model.elements.interfaces.ModelGroup;
import com.starfish.kol.util.Regex;

public class OptionElement {
	private static final Regex OPTION = new Regex(
			"<option([^<>]*?)>(.*?)</option>", 1, 2);
	private static final Regex OPTION_ID = new Regex(
			"value=[\"']?(-?\\d+)([<>\"' ]|$)", 1);
	private static final Regex OPTION_PIC = new Regex(
			"picurl=[\"']?([^<>\"' ]+)([<>\"' ]|$)", 1);

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
	
	private OptionElement(String text, String img, String value, boolean disabled) {
		this.text = text;
		this.img = img;
		this.value = value;
		this.disabled = disabled;
	}
	
	public static Regex regexFor(String select) {
		return new Regex(
				"<select name=" + select + ">(.*?)</select>", 1);
	}
	
	public static ArrayList<ModelGroup<OptionElement>> extractOptionGroups(String dropdown, String defaultName) {
		ArrayList<ModelGroup<OptionElement>> options = new ArrayList<ModelGroup<OptionElement>>();
		
		for (String group : OPTION_GROUP.extractAllSingle(dropdown)) {
			String name = OPTION_GROUP_NAME.extractSingle(group);
			if(name == null) name = defaultName;
			
			System.out.println(group);
			ArrayList<OptionElement> option_group = extractOptions(group);
			BasicGroup<OptionElement> section = new BasicGroup<OptionElement>(name, option_group);
			options.add(section);
		}
		return options;
	}
	
	public static ArrayList<OptionElement> extractOptions(String dropdown) {
		ArrayList<OptionElement> result = new ArrayList<OptionElement>();
		
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
			
			result.add(new OptionElement(text, img, num, disabled));
		}
		return result;
	}
}
