package com.starfish.kol.model.models;

import com.starfish.kol.connection.ServerReply;
import com.starfish.kol.connection.Session;
import com.starfish.kol.model.LiveModel;
import com.starfish.kol.request.Request;
import com.starfish.kol.request.SimulatedRequest;
import com.starfish.kol.util.Regex;

public class StatsModel extends LiveModel {
	/**
	 * Autogenerated by eclipse
	 */
	private static final long serialVersionUID = -2175870608547445653L;

	/**
	 * Pretty much all of the Regex were stolen from KoLMafia. Specifically,
	 * from CharPaneRequest
	 */
	private static final Regex[] STATS_PREPROCESSING = {
	/*
	 * //Default Character Pane (Caught by next one too...) new Regex(
	 * "Muscle.*?<b>(.*?)</b>.*?Mysticality.*?<b>(.*?)</b>.*?Moxie.*?<b>(.*?)</b>"
	 * , 1, 2, 3),
	 */
	// Compact Character Pane
	new Regex("Mus.*?<b>(.*?)</b>.*?Mys.*?<b>(.*?)</b>.*?Mox.*?<b>(.*?)</b>",
			1, 2, 3) };

	private static final Regex[] STATS_BUFFED = {
			// If stat is actually buffed
			new Regex(
					"<font color=[\"']?blue[\"']?>(\\d+)</font>&nbsp;\\((\\d+)\\)",
					1),
			// If stat is not buffed
			new Regex("\\d+", 0) };

	private static final Regex[] STATS_UNBUFFED = {
			// If stat is actually buffed
			new Regex(
					"<font color=[\"']?blue[\"']?>(\\d+)</font>&nbsp;\\((\\d+)\\)",
					2),
			// If stat is not buffed
			new Regex("\\d+", 0) };

	private static final Regex[] MEAT = {
			// Slim&Normal mode
			new Regex("/(?:slim)?meat\\.gif.*?<span.*?>([\\d,]+?)</span>", 1),
			// Compact mode
			new Regex("Meat.*?<b>([\\d,]+?)</b>", 1) };

	private static final Regex[] ADV = {
			// Slim&Normal mode
			new Regex("/(?:slim)?hourglass\\.gif.*?<span.*?>(\\d+?)</span>", 1),
			// Compact mode
			new Regex("Adv.*?<b>(\\d+?)</b>", 1) };

	private static final Regex[] HP_CURR = {
			// Slim&Normal mode
			new Regex(
					"/(?:slim)?hp\\.gif.*?<span.*?>(\\d+?)&nbsp;/&nbsp;(\\d+?)</span>",
					1),
			// Compact mode
			new Regex("HP.*?>(\\d+?)/(\\d+?)<", 1) };

	private static final Regex[] HP_BASE = {
			// Slim&Normal mode
			new Regex(
					"/(?:slim)?hp\\.gif.*?<span.*?>(\\d+?)&nbsp;/&nbsp;(\\d+?)</span>",
					2),
			// Compact mode
			new Regex("HP.*?>(\\d+?)/(\\d+?)</", 2) };

	private static final Regex[] MP_CURR = {
			// Slim&Normal mode
			new Regex(
					"/(?:slim)?mp\\.gif.*?<span.*?>(\\d+?)&nbsp;/&nbsp;(\\d+?)</span>",
					1),
			// Compact mode
			new Regex("MP.*?>(\\d+?)/(\\d+?)</", 1),
			// Slim&Normal (Zombiecore)
			new Regex("/(?:slim)?zombies/horde.*?\\.gif.*?Horde: (\\d+)", 1),
			// Compact mode (Zombiecore)
			new Regex("Horde: (\\d+)", 1) };

	private static final Regex[] MP_BASE = {
			// Slim&Normal mode
			new Regex(
					"/(?:slim)?mp\\.gif.*?<span.*?>(\\d+?)&nbsp;/&nbsp;(\\d+?)</span>",
					2),
			// Compact mode
			new Regex("MP.*?>(\\d+?)/(\\d+?)</", 2),
			// Slim&Normal (Zombiecore)
			new Regex("/(?:slim)?zombies/horde.*?\\.gif.*?Horde: (\\d+)", 1),
			// Compact mode (Zombiecore)
			new Regex("Horde: (\\d+)", 1) };

	private static final Regex[] NAME = { new Regex(
			"<a[^<>]*?charsheet\\.php[^<>]>(?:<b>)?([\\w ]+)(?:</b>)?", 1) };

	private static final Regex[] LEVEL = {
			new Regex("<br>Level (\\d+)<br>", 1),
			new Regex("<br>Lvl. (\\d+)<table", 1) };

	private static final Regex[] LEVEL_TEXT = { new Regex(
			"<br>Level \\d+<br>([\\w ]+)<", 1) };

	private static final Regex PAGE_BODY = new Regex(
			"(<body[^>]*>)(.*?)(</body>)");
	private static final Regex QUEST_LOG = new Regex(
			"<center id=[\"']?nudgeblock[\"']?>(.*?)<p></center>", 1);

	private ServerReply lastUpdate;
	private String[] currentStats = null;

	public StatsModel(Session s) {
		super(s, "charpane.php", false);
		
		this.lastUpdate = new ServerReply(200, "", "", "", "", "");
	}

	@Override
	protected void loadContent(ServerReply reply) {
		currentStats = extractAll(reply.html,
				new String[] { "-2", "-2", "-2" }, STATS_PREPROCESSING);

		this.lastUpdate = reply;
	}

	private String[] extractAll(String text, String[] default_value,
			Regex... toTry) {
		for (Regex r : toTry) {
			String[] res = r.extract(text);
			if (res != null)
				return res;
		}
		return default_value;
	}

	private String extractString(String text, String default_value,
			Regex... toTry) {
		for (Regex r : toTry) {
			String res = r.extractSingle(text);
			if (res != null)
				return res;
		}
		return default_value;
	}

	private int extractInt(String text, int default_value, Regex... toTry) {
		String res = extractString(text, default_value + "", toTry);
		return Integer.parseInt(res);
	}

	public String getUsername() {
		return extractString(lastUpdate.html, "", NAME);
	}

	public String getCharInfo() {
		String level = "Level " + extractInt(lastUpdate.html, 0, LEVEL);
		String text = extractString(lastUpdate.html, null, LEVEL_TEXT);
		if (text == null)
			return level;
		else
			return level + " " + text;
	}

	public int getMuscle() {
		return extractInt(currentStats[0], -1, STATS_BUFFED);
	}

	public int getMyst() {
		return extractInt(currentStats[1], -1, STATS_BUFFED);
	}

	public int getMoxie() {
		return extractInt(currentStats[2], -1, STATS_BUFFED);
	}

	public int getMuscleBase() {
		return extractInt(currentStats[0], -1, STATS_UNBUFFED);
	}

	public int getMystBase() {
		return extractInt(currentStats[1], -1, STATS_UNBUFFED);
	}

	public int getMoxieBase() {
		return extractInt(currentStats[2], -1, STATS_UNBUFFED);
	}

	public int getHP() {
		return extractInt(lastUpdate.html, -1, HP_CURR);
	}

	public int getHPBase() {
		return extractInt(lastUpdate.html, -1, HP_BASE);
	}

	public int getMP() {
		return extractInt(lastUpdate.html, -1, MP_CURR);
	}

	public int getMPBase() {
		return extractInt(lastUpdate.html, -1, MP_BASE);
	}

	public int getAdv() {
		return extractInt(lastUpdate.html, -1, ADV);
	}

	public String getMeat() {
		return extractString(lastUpdate.html, "-1", MEAT);
	}

	public void loadQuests() {
		String sideLog = QUEST_LOG.extractSingle(lastUpdate.html);
		
		Request req;
		if (sideLog != null) {
			String body = PAGE_BODY.replaceAll(lastUpdate.html, "$1" + sideLog
					+ "$3");

			if(this.lastUpdate == null)
				return;
			
			req = new SimulatedRequest(this.lastUpdate,
					"http://www.kingdomofloathing.com/questsidebar.php", body);
		} else {
			req = new Request("http://www.kingdomofloathing.com/questlog.php");
		}

		this.makeRequest(req);
	}

	public void loadFull() {
		String body = QUEST_LOG.replaceAll(lastUpdate.html, "");
		Request r = new SimulatedRequest(lastUpdate,
				"http://www.kingdomofloathing.com/fullsidebar.php", body);
		this.makeRequest(r);
	}
}
