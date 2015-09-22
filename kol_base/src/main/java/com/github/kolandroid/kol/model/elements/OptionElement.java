package com.github.kolandroid.kol.model.elements;

import com.github.kolandroid.kol.model.elements.basic.BasicGroup;
import com.github.kolandroid.kol.model.elements.interfaces.ModelGroup;
import com.github.kolandroid.kol.util.Regex;

import java.util.ArrayList;

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
            "disabled=", 0);

    public final String text;
    public final String img;
    public final String value;
    public final boolean disabled;

    public OptionElement(String text, String img, String value,
                          boolean disabled) {
        this.text = text;
        this.img = img;
        this.value = value;
        this.disabled = disabled;
    }

    public static Regex regexFor(String select) {
        return new Regex("<select name=" + select + ">(.*?)</select>", 1);
    }

    public static <T> ArrayList<ModelGroup<T>> extractObjectGroups(
            String dropdown, String defaultName, OptionElementParser<T> parser) {
        ArrayList<ModelGroup<T>> result = new ArrayList<>();
        for (ModelGroup<OptionElement> optionGroup : extractOptionGroups(
                dropdown, defaultName)) {
            BasicGroup<T> group = new BasicGroup<>(optionGroup.getName());
            for (OptionElement option : optionGroup) {
                if (parser.toExclude != null
                        && option.text.contains(parser.toExclude))
                    continue;
                group.add(parser.make(option));
            }
            result.add(group);
        }
        return result;
    }

    public static <T> ArrayList<T> extractObjects(String dropdown,
                                                  OptionElementParser<T> parser) {
        ArrayList<T> result = new ArrayList<>();
        for (OptionElement option : extractOptions(dropdown)) {
            if (parser.toExclude != null
                    && option.text.contains(parser.toExclude))
                continue;
            result.add(parser.make(option));
        }
        return result;
    }

    public static ArrayList<ModelGroup<OptionElement>> extractOptionGroups(
            String dropdown, String defaultName) {
        ArrayList<ModelGroup<OptionElement>> options = new ArrayList<>();

        for (String group : OPTION_GROUP.extractAllSingle(dropdown)) {
            String name = OPTION_GROUP_NAME.extractSingle(group, defaultName);

            ArrayList<OptionElement> option_group = extractOptions(group);
            BasicGroup<OptionElement> section = new BasicGroup<>(
                    name, option_group);
            options.add(section);
        }
        return options;
    }

    public static ArrayList<OptionElement> extractOptions(String dropdown) {
        ArrayList<OptionElement> result = new ArrayList<>();

        ArrayList<String[]> options = OPTION.extractAll(dropdown);

        for (String[] option : options) {
            if (option == null)
                continue;

            String num = OPTION_ID.extractSingle(option[0]);
            String img = OPTION_PIC.extractSingle(option[0], "");
            String text = option[1];
            boolean disabled = OPTION_DISABLED.matches(option[0]);

            if (num == null || num.length() == 0)
                continue;

            if (!img.isEmpty()) {
                if (!img.contains("images.kingdomofloathing.com"))
                    img = "images.kingdomofloathing.com/itemimages/" + img;
                if (!img.endsWith(".gif") && !img.endsWith(".png"))
                    img += ".gif";
            }

            result.add(new OptionElement(text, img, num, disabled));
        }
        return result;
    }

    public static abstract class OptionElementParser<T> {
        private final String toExclude;

        public OptionElementParser(String toExclude) {
            this.toExclude = toExclude;
        }

        public abstract T make(OptionElement base);
    }
}
