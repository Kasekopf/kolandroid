package com.github.kolandroid.kol.util;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class wrapping the java Regex engine, to streamline the parsing of html. (Yes, yes, don't do that. Shhhhh.)
 */
public class Regex {
    // Regex pattern to be matched against any incoming strings
    private final Pattern pattern;
    // Groups to extract from input strings
    private final int[] groups;

    /**
     * Prepare a new Regex to capture the provided groups.
     *
     * @param exp    The regular expression to match with
     * @param groups Which groups to capture, in order
     */
    public Regex(String exp, int... groups) {
        pattern = Pattern.compile(exp, Pattern.DOTALL);
        this.groups = groups;
    }

    /**
     * Extract a list of all match-sets in the string.
     * Each element of the resulting list comprises a single match of the regular expression,
     * and the array contains the groups (specified in the constructor) for that match.
     *
     * @param text The string to search
     * @return the list of match-sets in the string
     */
    public ArrayList<String[]> extractAll(String text) {
        ArrayList<String[]> results = new ArrayList<>();
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

    /**
     * Extract a list of single matches in the string. Each element in the list is the capture of
     * the first group specified in the constructor.
     *
     * @param text  The string to search
     * @return A list of the extracted capture groups in the string
     */
    public ArrayList<String> extractAllSingle(String text) {
        ArrayList<String> results = new ArrayList<>();
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

    /**
     * Extract the first regex match from a string.
     *
     * @param text  The string to search
     * @return The first regex match from a string, or null if none was found.
     */
    public String extractSingle(String text) {
        return extractSingle(text, null);
    }

    /**
     * Extract the first regex match from a string.
     *
     * @param text  The string to search
     * @param defaultValue  A value to return if no match was found.
     * @return The first regex match from a string, or defaultValue if none was found.
     */
    public String extractSingle(String text, String defaultValue) {
        if (text == null)
            return defaultValue;
        Matcher m = pattern.matcher(text);
        if (!m.find())
            return defaultValue;
        return m.group(groups[0]);
    }

    /**
     * Extract all specified groups from the first regex match in a string.
     *
     * @param text  The string to search
     * @return All specified groups from the first match in a string, or null if none were found.
     */
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

    /**
     * Check if the provided string matches this regex.
     *
     * @param text  The string to search
     * @return true if the regex matches, otherwise false
     */
    public boolean matches(String text) {
        Matcher m = pattern.matcher(text);
        return m.find();
    }

    /**
     * Replace all matches inside the provided string with a new (regex-interpreted) value.
     * This new value can refer to capture groups with $0, $1, $2, ...
     *
     * @param text  The string to replace
     * @param with  The string to replace any matches with.
     * @return The provided string with all matches replaced.
     */
    public String replaceAll(String text, String with) {
        if (text == null)
            return null;
        if (with == null)
            with = "";
        return pattern.matcher(text).replaceAll(with);
    }
}
