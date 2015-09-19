package com.github.kolandroid.kol.model.models.skill;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.connection.Session;
import com.github.kolandroid.kol.model.Model;
import com.github.kolandroid.kol.model.elements.OptionElement;
import com.github.kolandroid.kol.model.elements.interfaces.SubtextElement;
import com.github.kolandroid.kol.model.models.WebModel;
import com.github.kolandroid.kol.request.Request;
import com.github.kolandroid.kol.request.ResponseHandler;
import com.github.kolandroid.kol.util.Callback;
import com.github.kolandroid.kol.util.Regex;

public class SkillModel extends Model implements SubtextElement {
    private static final Regex ICON_SKILLID = new Regex("<div[^>]*rel=[\"']?(\\d+)[\"']>", 1);
    private static final Regex ICON_NAME = new Regex("title=\"([^\"]*)\"", 1);
    private static final Regex ICON_IMG = new Regex("src=\"([^\"]*)\"", 1);
    private static final Regex ICON_COST = new Regex("<div[^>]*cost[^>]*>([^<]*)</div>", 1);
    private static final Regex ICON_DISABLED = new Regex("skill *disabled", 1);
    private static final Regex OPTION_MP = new Regex("\\(.*?\\)", 0);
    private final String name;
    private final String cost;
    private final String image;
    private final boolean disabled;
    private final String descriptionUrl;
    private final String castAction;
    private WebModel description;
    private boolean isBuff;

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

        String id = ICON_SKILLID.extractSingle(skillInfo, "0");
        descriptionUrl = "desc_skill.php?whichskill=" + id;

        castAction = "runskillz.php?pwd=" + pwd + "&action=Skillz&targetplayer=" + yourself + "&whichskill=" + id;
        isBuff = true;
    }

    public SkillModel(Session s, String pwd, String yourself, OptionElement base) {
        super(s);
        name = OPTION_MP.replaceAll(base.text, "");
        cost = OPTION_MP.extractSingle(base.text, "");
        image = base.img;
        disabled = base.disabled;
        descriptionUrl = "desc_skill.php?whichskill=" + base.value;

        castAction = "runskillz.php?pwd=" + pwd + "&action=Skillz&targetplayer=" + yourself + "&whichskill=" + base.value;
        isBuff = true;
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

    public boolean isBuff() {
        return false;
    }

    public boolean getDisabled() {
        return disabled;
    }

    public WebModel getDescription() {
        return description;
    }

    public void loadDescription(final Callback<SkillModel> onResult) {
        if (description != null || descriptionUrl.equals("")) {
            onResult.execute(this);
        } else {
            this.makeRequest(new Request(descriptionUrl), new ResponseHandler() {
                @Override
                public void handle(Session session, ServerReply response) {
                    if (response == null) return;
                    if (!response.url.contains(descriptionUrl)) return;

                    isBuff = (response.html.contains("<b>Type:</b> Buff<br>"));
                    description = new WebModel(session, new ServerReply(response, response.html));
                    onResult.execute(SkillModel.this);
                }
            });
        }
    }

    @Override
    public String toString() {
        return getText();
    }
}
