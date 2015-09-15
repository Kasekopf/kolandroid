package com.github.kolandroid.kol.model.models;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.connection.Session;
import com.github.kolandroid.kol.model.elements.ActionElement;
import com.github.kolandroid.kol.util.Regex;

import java.util.ArrayList;

public class ChoiceModel extends WebModel {
    /**
     * Autogenerated by eclipse.
     */
    private static final long serialVersionUID = -9107242519455349408L;

    private static final Regex OPTIONS = new Regex("<form[^>]*>(.*?)</form>", 1);

    private static final Regex PWD_INPUT = new Regex("(<input[^>]*name=[\"']?pwd[^>]*>)", 1);
    private static final Regex WHICH_INPUT = new Regex("(<input[^>]*name=[\"']?whichchoice[^>]*>)", 1);
    private static final Regex OPTION_INPUT = new Regex("(<input[^>]*name=[\"']?option[^>]*>)", 1);
    private static final Regex SUBMIT_INPUT = new Regex("(<input[^>]*type=[\"']?submit[^>]*>)", 1);

    private static final Regex ALPHANUM_VALUE = new Regex("value=[\"']?([a-f0-9]+)[\"'>]", 1);
    private static final Regex NUM_VALUE = new Regex("value=[\"']?(\\d+)[\"'>]", 1);
    private static final Regex VALUE = new Regex("value=[\"]?(.*?)[\">]", 1);

    private ArrayList<ActionElement> options;

    public ChoiceModel(Session s, ServerReply response) {
        super(s, new ServerReply(response, filterHtml(response.html)));

        this.extractOptions(response.html);
    }

    private static String filterHtml(String html) {
        return OPTIONS.replaceAll(html, "");
    }

    private void extractOptions(String html) {
        this.options = new ArrayList<ActionElement>();
        for (String form : OPTIONS.extractAllSingle(html)) {
            System.out.println("Found option: " + form);

            String pwd = ALPHANUM_VALUE.extractSingle(PWD_INPUT.extractSingle(form));
            String whichchoice = NUM_VALUE.extractSingle(WHICH_INPUT.extractSingle(form));
            String option = NUM_VALUE.extractSingle(OPTION_INPUT.extractSingle(form));
            String text = VALUE.extractSingle(SUBMIT_INPUT.extractSingle(form));

            if (pwd == null || whichchoice == null || option == null) continue;

            text = text.replace("&quot;", "\"");

            String action = "choice.php?pwd=" + pwd + "&whichchoice=" + whichchoice + "&option=" + option;
            options.add(new ActionElement(getSession(), text, action));
        }
    }

    public ArrayList<ActionElement> getOptions() {
        return this.options;
    }
}
