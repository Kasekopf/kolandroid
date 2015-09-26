package com.github.kolandroid.kol.model.models.inventory;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.connection.Session;
import com.github.kolandroid.kol.data.DataCache;
import com.github.kolandroid.kol.data.RawItem;
import com.github.kolandroid.kol.model.Model;
import com.github.kolandroid.kol.model.elements.MultiActionElement;
import com.github.kolandroid.kol.model.elements.OptionElement;
import com.github.kolandroid.kol.model.elements.interfaces.SubtextElement;
import com.github.kolandroid.kol.model.models.WebModel;
import com.github.kolandroid.kol.request.Request;
import com.github.kolandroid.kol.request.ResponseHandler;
import com.github.kolandroid.kol.util.Callback;
import com.github.kolandroid.kol.util.Regex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ItemModel extends Model implements SubtextElement {
    /**
     * Autogenerated by eclipse.
     */
    private static final long serialVersionUID = 8203542965489207042L;

    private static final Regex ITEM_IMG = new Regex(
            "<img[^>]*src=[\"']?([^\"' >]*)[\"' >]", 1);
    private static final Regex ITEM_DESCRIPTION_ID = new Regex(
            "<img[^>]*descitem\\((\\d+)[,\\)]", 1);
    private static final Regex ITEM_NAME = new Regex("<b[^>]*>(.*?)</b>", 1);
    private static final Regex ITEM_QUANTITY = new Regex(
            "<b[^>]*ircm[^>]*>.*?</b>&nbsp;<span>\\((\\d+)\\)</span>", 1);

    private static final Regex ITEM_ID = new Regex("<td[^>]*id=['\"]?i(\\d+)['\"]?'", 1);
    private static final Regex ITEM_REL = new Regex("<table[^>]*rel=['\"]?([^\"'>]*)['\">]", 1);

    private static final Regex ITEM_SLOT = new Regex("<a[^>]*>([^<]*?)</a>(&nbsp;(\\d))?:", 1, 3);

    private static final Regex ITEM_ACTION = new Regex("<a.*?</a>", 0);
    private static final Regex ITEM_ACTION_NAME = new Regex(
            "<a[^>]*>.*?\\[([^\\]]*?)\\].*?</a>", 1);
    private static final Regex ITEM_ACTION_LINK = new Regex(
            "<a[^>]*href=[\"']?(.*?)[\"' >]", 1);
    private static final Regex ITEM_SUBTEXT = new Regex(
            "<font[^>]*size=[\"']?1[^>]*>.*?(\\([^<]*\\))</font>", 1);

    private static final Regex DESCRIPTION_START = new Regex("<!-- itemid:[^>]*><br>");
    private static final Regex OPTION_QUANTITY = new Regex(" \\([^\\)]*?\\)$", 0);
    private final ArrayList<MultiActionElement> actions;

    private final String id;
    private final String name;
    private final String subtext;
    private final String quantity;
    private final String displayName;
    private WebModel description;


    // Might be updated from cache
    private String image;
    private String descriptionId;

    private MultiActionElement test;

    public ItemModel(Session s, String pwd, String itemInfo, Iterable<InventoryActionFactory> additonalActions) {
        super(s);

        image = ITEM_IMG.extractSingle(itemInfo, "");
        subtext = ITEM_SUBTEXT.extractSingle(itemInfo, "").replace("&nbsp;", "");

        // Determine the name of the item
        String partialName = ITEM_NAME.extractSingle(itemInfo, "");
        name = partialName;

        // Determine the quantity of the item
        String number = ITEM_QUANTITY.extractSingle(itemInfo, "");
        if (number.equals("")) {
            quantity = "1";
        } else {
            quantity = number;
            partialName += " (" + number + ")";
        }

        // Determine the slot of the item
        String[] slotInfo = ITEM_SLOT.extract(itemInfo);
        if (slotInfo == null) {
            //No slot
            displayName = partialName;
        } else if (slotInfo[1] == null || slotInfo[1].isEmpty()) {
            //Non-accessory slot
            displayName = slotInfo[0] + ": " + partialName;
        } else {
            //Accessory slot
            displayName = slotInfo[0] + " " + slotInfo[1] + ": " + partialName;
        }


        id = ITEM_ID.extractSingle(itemInfo, "-1");
        descriptionId = ITEM_DESCRIPTION_ID.extractSingle(itemInfo, "0");

        // Parse and add all visible actions
        actions = new ArrayList<>();
        for (String action : ITEM_ACTION.extractAllSingle(itemInfo)) {
            MultiActionElement parsed = parseAction(pwd, action, itemInfo);
            if (parsed != null)
                actions.add(parsed);
        }

        // Extract the rel="..." phrase and break into key/value pairs
        String rel = ITEM_REL.extractSingle(itemInfo, "");
        Map<String, String> relMap = new HashMap<>();
        for (String pair : rel.split("&")) {
            String[] splitPair = pair.split("=");
            if (splitPair.length != 2) continue;
            relMap.put(splitPair[0], splitPair[1]);
        }

        // Add all applicable right-click actions
        for (InventoryActionFactory factory : additonalActions) {
            if (factory.appliesTo(relMap)) {
                actions.add(factory.make(getSession(), quantity.equals("1"), id, pwd));
            }
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

        boolean single = quantity.equals("1");
        this.actions = new ArrayList<>();
        actions.add(InventoryActionFactory.USE.make(s, single, id, pwd));
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

    private MultiActionElement parseAction(String pwd, String action, String fullInfo) {
        String actName = ITEM_ACTION_NAME.extractSingle(action);
        String actDest = ITEM_ACTION_LINK.extractSingle(action);
        if (actName == null || actDest == null)
            return null;

        String lowerName = actName.toLowerCase();
        if (lowerName.contains("use multiple") || lowerName.contains("some") || lowerName.contains("all")) {
            return null;
        }

        boolean restrictSingle = true;

        if (!quantity.equals("1")) {
            if (lowerName.contains("use") && fullInfo.contains("use multiple") && !actDest.contains("inv_spleen.php")) {
                return InventoryActionFactory.USE.make(getSession(), false, id, pwd);
            }

            if ((lowerName.contains("eat") && fullInfo.contains("eat some"))
                    || (lowerName.contains("drink") && fullInfo.contains("drink some"))
                    || (lowerName.contains("use") && fullInfo.contains("use some"))) {
                actDest += "&ajax=1&quantity=#";
                restrictSingle = false;
            }

            if (lowerName.contains("store") || lowerName.contains("take")) {
                actDest = actDest.replace("&qty=1", "&qty=#");
                restrictSingle = false;
            }
        }

        return new MultiActionElement(getSession(), actName, restrictSingle, actDest);
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
                    html = DESCRIPTION_START.replaceAll(html, "$0<br>You Have: <b>" + quantity + "</b>");
                    description = new WebModel(session, new ServerReply(response, html));
                    onResult.execute(ItemModel.this);
                }
            });
        }
    }

    public ArrayList<MultiActionElement> getActions() {
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
