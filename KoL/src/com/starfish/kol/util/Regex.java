package com.starfish.kol.util;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex {
	private final Pattern pattern;
	private final int[] groups;

	public Regex(String exp, int... groups) {
		pattern = Pattern.compile(exp, Pattern.DOTALL);
		this.groups = groups;
	}

	public ArrayList<String[]> extractAll(String text) {
		ArrayList<String[]> results = new ArrayList<String[]>();
		if (text == null)
			return results;

		Matcher m = pattern.matcher(text);
		while (m.find()) {
			String[] res = new String[groups.length];
			for (int i = 0; i < groups.length; i++)
				res[i] = m.group(groups[i]);
			results.add(res);
		}
		return results;
	}

	public ArrayList<String> extractAllSingle(String text) {
		ArrayList<String> results = new ArrayList<String>();
		if (text == null)
			return results;

		Matcher m = pattern.matcher(text);
		while (m.find()) {
			String res = m.group(groups[0]);
			if (res == null)
				continue;
			results.add(res);
		}
		return results;
	}

	public String extractSingle(String text) {
		if (text == null)
			return null;
		Matcher m = pattern.matcher(text);
		if (!m.find())
			return null;
		return m.group(groups[0]);
	}

	public String[] extract(String text) {
		if (text == null)
			return null;

		Matcher m = pattern.matcher(text);
		if (!m.find())
			return null;

		String[] res = new String[groups.length];
		for (int i = 0; i < groups.length; i++)
			res[i] = m.group(groups[i]);
		return res;
	}

	public boolean matches(String text) {
		Matcher m = pattern.matcher(text);
		return m.find();
	}

	public String replaceAll(String text, String with) {
		if (text == null)
			return null;
		if (with == null)
			with = "";
		return pattern.matcher(text).replaceAll(with);
	}
}
