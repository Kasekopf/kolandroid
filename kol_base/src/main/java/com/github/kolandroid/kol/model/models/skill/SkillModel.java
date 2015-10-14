package com.github.kolandroid.kol.model.models.skill;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.data.DataCache;
import com.github.kolandroid.kol.data.RawSkill;
import com.github.kolandroid.kol.model.Model;
import com.github.kolandroid.kol.model.elements.OptionElement;
import com.github.kolandroid.kol.model.elements.interfaces.SubtextElement;
import com.github.kolandroid.kol.model.models.WebModel;
import com.github.kolandroid.kol.request.Request;
import com.github.kolandroid.kol.request.ResponseHandler;
import com.github.kolandroid.kol.session.Session;
import com.github.kolandroid.kol.util.Callback;
import com.github.kolandroid.kol.util.Regex;

public class SkillModel extends Model implements SubtextElement {
    private static final Regex ICON_SKILL_ID = new Regex("<div[^>]*rel=[\"']?(\\d+)[\"']>", 1);
    private static final Regex ICON_NAME = new Regex("title=\"([^\"]*)\"", 1);
    private static final Regex ICON_IMG = new Regex("src=\"([^\"]*)\"", 1);
    private static final Regex ICON_COST = new Regex("<div[^>]*cost[^>]*>([^<]*)</div>", 1);
    private static final Regex ICON_DISABLED = new Regex("skill *disabled", 1);
    private static final Regex OPTION_MP = new Regex("\\(.*?\\)", 0);
    protected final String id;
    private final String name;
    private final String cost;
    private final boolean disabled;
    private final String descriptionUrl;
    private final String castAction;
    private WebModel description;
    private String image;
    private SkillModel.Type type;

    /**
     * Create a new model in the provided session.
     *
     * @param s Session to use in all future requests by this model.
     */
    public SkillModel(Session s, String pwd, String yourself, String skillInfo) {
        super(s);
        name = ICON_NAME.extractSingle(skillInfo, "");
        image = ICON_IMG.extractSingle(skillInfo, "");
        cost = ICON_COST.extractSingle(skillInfo, "");
        disabled = ICON_DISABLED.matches(skillInfo);

        id = ICON_SKILL_ID.extractSingle(skillInfo, "0");
        descriptionUrl = "desc_skill.php?whichskill=" + id;

        castAction = "runskillz.php?pwd=" + pwd + "&action=Skillz&targetplayer=" + yourself + "&whichskill=" + id;
        type = Type.BUFF; // default to buff
    }

    public SkillModel(Session s, String pwd, String yourself, OptionElement base) {
        super(s);
        name = OPTION_MP.replaceAll(base.text, "");
        cost = OPTION_MP.extractSingle(base.text, "");
        image = (base.img == null) ? "" : base.img;
        disabled = base.disabled;
        id = base.value;
        descriptionUrl = "desc_skill.php?whichskill=" + base.value;

        castAction = "runskillz.php?pwd=" + pwd + "&action=Skillz&targetplayer=" + yourself + "&whichskill=" + base.value;
        type = Type.BUFF; // default to buff
    }

    @Override
    public String getSubtext() {
        return cost;
    }

    @Override
    public String getText() {
        return name;
    }

    @Override
    public String getImage() {
        return image;
    }

    public void cast(String quantity, String onPlayer) {
        String url = castAction;
        if (!onPlayer.equals("")) {
            url += "&specificplayer=" + onPlayer;
        }
        url += "&quantity=" + quantity;
        this.makeRequest(new Request(url));
    }

    public SkillModel.Type getType() {
        return type;
    }

    public boolean getDisabled() {
        return disabled;
    }

    public WebModel getDescription() {
        return description;
    }

    public void searchCache(DataCache<String, RawSkill> cache) {
        RawSkill match = cache.find(this.id);
        if (match != null) {
            if (this.image.equals("")) this.image = match.getImage();
            if (match.isBuff) {
                this.type = Type.BUFF;
            } else if (match.isPassive) {
                this.type = Type.PASSIVE;
            } else if (match.isCombat) {
                this.type = Type.COMBAT;
            } else {
                this.type = Type.NONCOMBAT;
            }
        }

        RawSkill newCacheValue = RawSkill.create(id, image, type == Type.BUFF, type == Type.PASSIVE, type == Type.COMBAT, name);
        cache.store(newCacheValue);
    }

    public void loadDescription(final Callback<SkillModel> onResult) {
        if (description != null || descriptionUrl.equals("")) {
            onResult.execute(this);
        } else {
            this.makeRequest(new Request(descriptionUrl), new ResponseHandler() {
                @Override
                public void handle(Session session, ServerReply response) {
                    if (response != null && response.url.contains(descriptionUrl)) {
                        // Attempt to determine the type of the skill from the html
                        for (Type t : Type.values()) {
                            if (t.matches(response)) {
                                type = t;
                                break;
                            }
                        }

                        description = new WebModel(session, new ServerReply(response, response.html));
                    }
                    onResult.execute(SkillModel.this);
                }
            });
        }
    }

    @Override
    public String toString() {
        return getText();
    }

    public enum Type {
        NONCOMBAT("<b>Type:</b> Noncombat"), COMBAT("<b>Type:</b> Combat"), PASSIVE("<b>Type:</b> Passive"), BUFF("<b>Type:</b> Buff");

        private final String indicator;

        Type(String indicator) {
            this.indicator = indicator;
        }

        public boolean matches(ServerReply reply) {
            return reply.html.contains(indicator);
        }
    }
}
