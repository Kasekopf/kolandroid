package com.github.kolandroid.kol.session.data;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.session.Session;
import com.github.kolandroid.kol.session.cache.LiveCacheLine;
import com.github.kolandroid.kol.session.cache.SessionCache;
import com.github.kolandroid.kol.util.Callback;
import com.github.kolandroid.kol.util.Regex;

import java.io.Serializable;

public class CharacterPaneData implements CharacterBasicData, Serializable {
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
                    1, 2, 3)};

    private static final Regex[] STATS_BUFFED = {
            // If stat is actually buffed
            new Regex(
                    "<font color=[\"']?blue[\"']?>(\\d+)</font>&nbsp;\\((\\d+)\\)",
                    1),
            // If stat is not buffed
            new Regex("\\d+", 0)};

    private static final Regex[] STATS_UNBUFFED = {
            // If stat is actually buffed
            new Regex(
                    "<font color=[\"']?blue[\"']?>(\\d+)</font>&nbsp;\\((\\d+)\\)",
                    2),
            // If stat is not buffed
            new Regex("\\d+", 0)};

    private static final Regex[] MEAT = {
            // Slim&Normal mode
            new Regex("/(?:slim)?meat\\.gif.*?<span.*?>([\\d,]+?)</span>", 1),
            // Compact mode
            new Regex("Meat.*?<b>([\\d,]+?)</b>", 1)};

    private static final Regex[] ADV = {
            // Slim&Normal mode
            new Regex("/(?:slim)?hourglass\\.gif.*?<span.*?>(\\d+?)</span>", 1),
            // Compact mode
            new Regex("Adv.*?<b>(\\d+?)</b>", 1)};

    private static final Regex[] HP_CURR = {
            // Slim&Normal mode
            new Regex(
                    "/(?:slim)?hp\\.gif.*?<span.*?>(\\d+?)&nbsp;/&nbsp;(\\d+?)</span>",
                    1),
            // Compact mode
            new Regex("HP.*?>(\\d+?)/(\\d+?)<", 1)};

    private static final Regex[] HP_BASE = {
            // Slim&Normal mode
            new Regex(
                    "/(?:slim)?hp\\.gif.*?<span.*?>(\\d+?)&nbsp;/&nbsp;(\\d+?)</span>",
                    2),
            // Compact mode
            new Regex("HP.*?>(\\d+?)/(\\d+?)</", 2)};

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
            new Regex("Horde: (\\d+)", 1)};

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
            new Regex("Horde: (\\d+)", 1)};

    private static final Regex[] NAME = {new Regex(
            "<a[^<>]*?charsheet\\.php[^<>]*>(?:<b>)?([\\w ]+)(?:</b>)?", 1)};

    private static final Regex[] CUSTOM_TITLE = {new Regex(
            "<a[^<>]*?charsheet\\.php[^<>]*><b>.*?</b></a>(.*?)(</td>|<table)", 1),
            new Regex(
                    "<b><a[^<>]*?charsheet\\.php[^<>]*>.*?</a></b>(.*?)(<hr|<table)", 1)};

    private static final Regex PWD_HASH = new Regex("var pwdhash ?= ?[\"']([a-fA-F0-9]*)[\"'];", 1);

    private static final Regex AVATAR = new Regex("<a[^>]*charsheet.php[^>]*><img[^>]*src=[\"']([^\"']*)[\"']", 1);

    private final ServerReply lastUpdate;
    private final String[] currentStats;

    public CharacterPaneData(ServerReply charPane) {
        this.lastUpdate = charPane;
        this.currentStats = extractAll(lastUpdate.html,
                new String[]{"-2", "-2", "-2"}, STATS_PREPROCESSING);
    }

    public ServerReply getPage() {
        return lastUpdate;
    }

    @Override
    public String getPwdHash() {
        return PWD_HASH.extractSingle(lastUpdate.html);
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

    @Override
    public int getCurrentHP() {
        return extractInt(lastUpdate.html, -1, HP_CURR);
    }

    @Override
    public int getCurrentMP() {
        return extractInt(lastUpdate.html, -1, MP_CURR);
    }

    @Override
    public int getMaxHP() {
        return extractInt(lastUpdate.html, -1, HP_BASE);
    }

    @Override
    public int getMaxMP() {
        return extractInt(lastUpdate.html, -1, MP_BASE);
    }

    @Override
    public int getMeat() {
        String meat = extractString(lastUpdate.html, "-1", MEAT);
        try {
            return Integer.parseInt(meat.replace(",", ""));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    @Override
    public int getAdventures() {
        String adv = extractString(lastUpdate.html, "-1", ADV);
        try {
            return Integer.parseInt(adv.replace(",", ""));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    @Override
    public int getBaseMuscle() {
        return extractInt(currentStats[0], -1, STATS_UNBUFFED);
    }

    @Override
    public int getBuffedMuscle() {
        return extractInt(currentStats[0], -1, STATS_BUFFED);
    }

    @Override
    public int getBaseMyst() {
        return extractInt(currentStats[1], -1, STATS_UNBUFFED);
    }

    @Override
    public int getBuffedMyst() {
        return extractInt(currentStats[1], -1, STATS_BUFFED);
    }

    @Override
    public int getBaseMoxie() {
        return extractInt(currentStats[2], -1, STATS_UNBUFFED);
    }

    @Override
    public int getBuffedMoxie() {
        return extractInt(currentStats[2], -1, STATS_BUFFED);
    }

    @Override
    public String getName() {
        return extractString(lastUpdate.html, "", NAME);
    }

    private boolean inAstralPlane() {
        return lastUpdate.html.contains("otherimages/spirit.gif") || lastUpdate.html.contains("<br>Lvl. <img");
    }

    @Override
    public String getTitle() {
        if (inAstralPlane()) {
            return "Level Infinity Astral Spirit";
        }

        return extractString(lastUpdate.html, "", CUSTOM_TITLE).replace("<br>", " ").replace("Lvl.", "Level");
    }

    @Override
    public String getAvatar() {
        return AVATAR.extractSingle(lastUpdate.html, "");
    }

    public static class Cache extends LiveCacheLine<CharacterPaneData> {
        /**
         * Create a new LiveCacheLine in the provided session.
         *
         * @param s The session to use for any requests.
         */
        public Cache(Session s) {
            super(s);
        }

        @Override
        protected CharacterPaneData process(ServerReply reply) {
            return new CharacterPaneData(reply);
        }

        @Override
        protected void computeUrl(SessionCache cache, Callback<String> callback, Callback<Void> failure) {
            callback.execute("charpane.php");
        }
    }
}
