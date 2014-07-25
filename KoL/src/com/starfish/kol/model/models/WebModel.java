package com.starfish.kol.model.models;

import com.starfish.kol.connection.ServerReply;
import com.starfish.kol.connection.Session;
import com.starfish.kol.model.Model;
import com.starfish.kol.request.Request;
import com.starfish.kol.util.Regex;

public class WebModel extends Model<Void> {
	/**
	 * Autogenerated by eclipse.
	 */
	private static final long serialVersionUID = -1723871679147309734L;

	/**
	 * Determine if a URL actually points to KoL
	 */
	private static final Regex URL_FIND = new Regex(
			"^https?://www(\\d*).kingdomofloathing.com/(.*)$", 2);
	
	private static final Regex URL_BASE_FIND = new Regex(
			"^(?:.*/)?([^/?]*)(?:\\?.*)?$", 1);

	/**
	 * Regexes for fixing item descriptions.
	 */
	private static final Regex ITEM_DESC = new Regex(
			"<img[^>]*descitem\\((\\d+)(, event)?\\)[^>]*>");
	private static final Regex ITEM_WHICH_DESC = new Regex(
			"<img[^>]*descitem\\((\\d+),(\\d+)(, event)?\\)[^>]*>");

	/**
	 * Regexes for replacing static buttons.
	 */
	private static final Regex FIND_FORM = new Regex(
			"<form[^>]*>(<input[^>]*type=[\"']?hidden[^>]*>)*<input[^>]*button[^>]*></form>",
			0);
	private static final Regex FORM_ACTION = new Regex(
			"<form[^>]*action=[\"']?([^\"'> ]*)[\"'> ]", 1);
	private static final Regex HIDDEN_INPUT = new Regex(
			"<input[^>]*type=[\"']?hidden[^>]*>", 0);
	private static final Regex GET_NAME = new Regex(
			"name=[\"']?([^\"'> ]*)[\"'> ]", 1);
	private static final Regex GET_VALUE = new Regex(
			"value=[\"']?([^\"'> ]*)[\"'> ]", 1);

	private static final Regex INPUT_BUTTON = new Regex(
			"<input[^>]*button[^>]*>", 0);
	private static final Regex GET_TEXT = new Regex("value=\"([^>]*)\">", 1);

	private static final Regex FORM_REPLACER = new Regex("<form([^>]*)action=([\"']?[^\"' >]*[\"']?)([^>]*)>");
	private static final Regex FORM_REPLACER2 = new Regex("<form([^>]*)method=[\"']?[^\"' >]*[\"']?([^>]*)>");
	
	private static final Regex TABLE_FIXER = new Regex("(</td>)(.*?)(</td>|</tr>|</table>|<td[^>]*>)");
	/**
	 * Remove code which redirects when no frames are detected.
	 */
	private static final Regex FRAME_REDIRECT = new Regex("if\\s*\\(parent\\.frames\\.length\\s*==\\s*0\\)\\s*location.href\\s*=\\s*[\"']?game\\.php[\"']?;", 0);
	
	private String url;
	private String html;

	public WebModel(Session s, ServerReply text) {
		super(s);

		System.out.println("Loading webmodel for " + text.url);
		this.setHTML(text.html);
		this.url = text.url;
	}

	public String getURL() {
		return url;
	}

	protected final void setHTML(String html) {
		// Replace item description javascript with working html links
		html = ITEM_DESC.replaceAll(html,
				"<a href=\"desc_item.php?whichitem=$1\">$0</a>");
		html = ITEM_WHICH_DESC.replaceAll(html,
				"<a href=\"desc_item.php?whichitem=$1&otherplayer=$2\">$0</a>");
		//html = replaceButtons(html);
		html = replaceForms(html);
		html = FRAME_REDIRECT.replaceAll(html, "");
		html = doHacks(html);
		
		this.html = html;
	}
	
	private final String doHacks(String html) {
		/**
		 * Hacks for account.php
		 */		
		//stop removing the submit button on account.php
		html = html.replace("document.write('<style type=\"text/css\">#submit {display: none; }</style>');", "");
		//remove all the blue "Saving..." text on account.php 
		html = html.replace("<span class=\"saving\">Saving...</span>", "");
		//remove the fancy tab ajax calls on account.php; they do not have the proper cookie
		html = html.replace("$('#tabs li').click(changeTab);", "");
		
		return html;
	}
	
	private static final Regex HEAD_TAG = new Regex("<head>");

    private final static String jsInjectCode = 
            "function customParseForm(form) { " +
            "    var inputs = form.getElementsByTagName('input');" +
            "    var data = form.totallyrealaction ? form.totallyrealaction.value : '';" +
            "    var tobegin = (data.indexOf('?') == -1);" +
            "    for (var i = 0; i < inputs.length; i++) {" +
            "         var field = inputs[i];" +
            "         if(field.name && field.name==='totallyrealaction') continue; " +
            "         if(field.type == 'radio' && !field.checked) continue; " +
            "         if(field.type == 'checkbox' && !field.checked) continue; " + 
            "         if (field.type != 'reset' && field.name) {" +
            "             data += (tobegin ? '?' : '&');" +
            "             tobegin = false;" +
            "             data += encodeURIComponent(field.name) + '=' + encodeURIComponent(field.value);" +
            "         }" +
            "    }" +
            "    var select = form.getElementsByTagName('select');" +
            "    for (var i = 0; i < select.length; i++) {" +
            "         var field = select[i];" +
            "         data += (tobegin ? '?' : '&');" +
            "         tobegin = false;" +
            "         data += encodeURIComponent(field.name) + '=' + encodeURIComponent(field.options[field.selectedIndex].value);" +
            "    }" +
            "    window.FORMOUT.processFormData(data);" +
            "}";
    
	private String replaceForms(String html) {
		html = FORM_REPLACER.replaceAll(html, "<form$1$3><input type=hidden name=totallyrealaction value=$2>");
		html = FORM_REPLACER2.replaceAll(html, "<form$1action=\"\" onsubmit=\"customParseForm(this);\"$2>");
		
		html = TABLE_FIXER.replaceAll(html, "$1$3$2");
		html = HEAD_TAG.replaceAll(html, "$0 <script>" + jsInjectCode + "</script>");
		return html;
	}

	/**
	 * If the submission behavior of a button is entirely static, we can replace
	 * the entire button with a static link.
	 * 
	 * Examples: Accepting the artist/untinkerer quest; Recieving friar
	 * blessing; Bounty hunter hunter
	 * 
	 * Choice adventures are implemented with this, but the special interface is
	 * probably more useful.
	 * 
	 * @param html
	 *            The html of the page
	 * @return Html of the page with all static buttons replaced with a link.
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private String replaceButtons(String html) {
		if (FIND_FORM.matches(html)) {
			System.out.println("Found convertable form!");
		}

		for (String form : FIND_FORM.extractAllSingle(html)) {
			String action = FORM_ACTION.extractSingle(form);
			if (action == null)
				continue;

			for (String hidden : HIDDEN_INPUT.extractAllSingle(form)) {
				String name = GET_NAME.extractSingle(hidden);
				String value = GET_VALUE.extractSingle(hidden);

				action += (action.contains("?") ? "&" : "?");
				action += name + "=" + value;
			}

			String text = GET_TEXT.extractSingle(INPUT_BUTTON
					.extractSingle(html));
			html = html.replace(form, "<a href=\"" + action + "\">" + text
					+ "</a>");
		}
		return html;
	}
	
	public final String getHTML() {
		return this.html;
	}

	public boolean makeRequest(String url) {
		if(url == null || url.length() < 1) return false;
		
		if(url.contains("totallyrealaction")) {
			System.out.println("Ignoring duplicate form request");
			return true;
		}
		
		if (url.contains("http://") || url.contains("https://")) {
			url = URL_FIND.extractSingle(url);
			if (url == null)
				return false;
		}
		
		if (url.charAt(0) == '?') {
			String currentBase = URL_BASE_FIND.extractSingle(this.url);
			if(currentBase == null) currentBase = "main.php";
			url = currentBase + url;
		}

		Request req = new Request(url, this.getGameHandler());
		this.makeRequest(req);
		return true;
	}
	
	public boolean isSmall() {
		return this.url.contains("small_");
	}
}
