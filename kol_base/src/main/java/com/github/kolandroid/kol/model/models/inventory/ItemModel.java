package com.github.kolandroid.kol.model.models.inventory;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.connection.Session;
import com.github.kolandroid.kol.data.DataCache;
import com.github.kolandroid.kol.data.RawItem;
import com.github.kolandroid.kol.model.Model;
import com.github.kolandroid.kol.model.elements.OptionElement;
import com.github.kolandroid.kol.model.elements.interfaces.SubtextElement;
import com.github.kolandroid.kol.model.models.WebModel;
import com.github.kolandroid.kol.request.Request;
import com.github.kolandroid.kol.request.ResponseHandler;
import com.github.kolandroid.kol.util.Callback;
import com.github.kolandroid.kol.util.Regex;

import java.util.ArrayList;

public class ItemModel extends Model implements SubtextElement {
    /**
     * Autogenerated by eclipse.
     */
    private static final long serialVersionUID = 8203542965489207042L;

    private static final Regex ITEM_IMG = new Regex(
            "<img[^>]*src=[\"']?([^\"' >]*)[\"' >]", 1);
    private static final Regex ITEM_DESCID = new Regex(
            "<img[^>]*descitem\\((\\d+)[,\\)]", 1);
    private static final Regex ITEM_NAME = new Regex("<b[^>]*>(.*?)</b>", 1);
    private static final Regex ITEM_QNTY = new Regex(
            "<b[^>]*ircm[^>]*>.*?</b>&nbsp;<span>\\((\\d+)\\)</span>", 1);

    private static final Regex ITEM_ID = new Regex("<td[^>]*id=['\"]?i(\\d+)['\"]?'", 1);

    private static final Regex ITEM_SLOT = new Regex("<a[^>]*>([^<]*?)</a>:", 1);

    private static final Regex ITEM_ACTION = new Regex("<a.*?</a>", 0);
    private static final Regex ITEM_ACTION_NAME = new Regex(
            "<a[^>]*>.*?\\[([^\\]]*?)\\].*?</a>", 1);
    private static final Regex ITEM_ACTION_LINK = new Regex(
            "<a[^>]*href=[\"']?(.*?)[\"' >]", 1);
    private static final Regex ITEM_SUBTEXT = new Regex(
            "<font[^>]*size=[\"']?1[^>]*>.*?(\\([^<]*\\))</font>", 1);

    private static final Regex DESCRIPTION_END = new Regex("</blockquote>");
    private static final Regex OPTION_QUANTITY = new Regex(" \\([^\\)]*?\\)$", 0);
    private final ArrayList<InventoryAction> actions;

    private final String id;
    private final String name;
    private final String subtext;
    private final String quantity;
    private final String displayName;
    private WebModel description;


    // Might be updated from cache
    private String image;
    private String descriptionId;

    public ItemModel(Session s, String pwd, String itemInfo) {
        super(s);

        image = ITEM_IMG.extractSingle(itemInfo, "");
        subtext = ITEM_SUBTEXT.extractSingle(itemInfo, "").replace("&nbsp;", "");

        // Determine the name of the item
        String partialName = ITEM_NAME.extractSingle(itemInfo, "");
        name = partialName;

        String slot = ITEM_SLOT.extractSingle(itemInfo, "");
        String number = ITEM_QNTY.extractSingle(itemInfo, "");
        if (number.equals("")) {
            quantity = "1";
        } else {
            quantity = number;
            partialName += " (" + number + ")";
        }

        if (!slot.equals("")) {
            partialName = slot + ": " + partialName;
        }
        displayName = partialName;

        id = ITEM_ID.extractSingle(itemInfo, "-1");
        descriptionId = ITEM_DESCID.extractSingle(itemInfo, "0");

        actions = new ArrayList<>();
        for (String action : ITEM_ACTION.extractAllSingle(itemInfo)) {
            InventoryAction parsed = parseAction(pwd, action);
            if (parsed != null)
                actions.add(parsed);
        }
    }

    public ItemModel(Session s, String pwd, OptionElement base, String baseAction) {
        super(s);

        this.displayName = base.text;
        this.name = OPTION_QUANTITY.replaceAll(base.text, "");
        this.quantity = OPTION_QUANTITY.extractSingle(base.text, "1");
        this.image = base.img;
        this.id = base.value;
        this.subtext = "";
        descriptionId = "";

        this.actions = new ArrayList<>();
        actions.add(new InventoryAction.ImmediateItemAction(getSession(), "Use", baseAction + "1"));

        if (!quantity.equals("1")) {
            actions.add(new InventoryAction.MultiuseItemAction(getSession(), this, baseAction));
        }
    }

    public void searchCache(DataCache<String, RawItem> cache) {
        RawItem match = cache.find(this.id);
        if (match != null) {
            if (this.image.equals("")) this.image = match.getImage();
            if (this.descriptionId.equals("")) this.descriptionId = match.descriptionId;
        }

        RawItem newCacheValue = RawItem.create(id, image, descriptionId, name);
        cache.store(newCacheValue);
    }

    private InventoryAction parseAction(String pwd, String action) {
        String actName = ITEM_ACTION_NAME.extractSingle(action);
        String actDest = ITEM_ACTION_LINK.extractSingle(action);
        if (actName == null || actDest == null)
            return null;

        String lowername = actName.toLowerCase();
        if (lowername.contains("use multiple")) {
            return new InventoryAction.MultiuseItemAction(getSession(), this, actDest, pwd);
        } else if (lowername.contains("take some") || lowername.contains("store some")) {
            return new InventoryAction.MultiClosetItemAction(getSession(), this, actName, actDest, pwd);
        } else if (lowername.contains("eat some")
                || lowername.contains("drink some")) {
            // do nothing for now
            return null;
        } else {
            actName = actName.substring(0, 1).toUpperCase()
                    + actName.substring(1);

            return new InventoryAction.ImmediateItemAction(getSession(), actName, actDest);
        }
    }

    public WebModel getDescription() {
        return description;
    }

    public void loadDescription(final Callback<ItemModel> onResult) {
        if (description != null || descriptionId.equals("")) {
            onResult.execute(this);
        } else {
            this.makeRequest(new Request("desc_item.php?whichitem=" + descriptionId),
                    new ResponseHandler() {
                @Override
                public void handle(Session session, ServerReply response) {
                    if (response == null) return;
                    if (!response.url.contains(descriptionId)) return;

                    String html = response.html;
                    html = DESCRIPTION_END.replaceAll(html, "<br>You Have: <b>" + quantity + "</b>$0");
                    description = new WebModel(session, new ServerReply(response, html));
                    onResult.execute(ItemModel.this);
                }
            });
        }
    }

    public ArrayList<InventoryAction> getActions() {
        return actions;
    }

    @Override
    public String getSubtext() {
        return subtext;
    }

    @Override
    public String getText() {
        return displayName;
    }

    @Override
    public String getImage() {
        return image;
    }

    @Override
    public String toString() {
        return getText();
    }
}
