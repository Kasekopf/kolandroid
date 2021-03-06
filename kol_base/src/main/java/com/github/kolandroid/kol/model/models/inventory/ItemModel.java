package com.github.kolandroid.kol.model.models.inventory;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.data.DataCache;
import com.github.kolandroid.kol.data.RawItem;
import com.github.kolandroid.kol.gamehandler.ViewContext;
import com.github.kolandroid.kol.model.Model;
import com.github.kolandroid.kol.model.elements.MultiActionElement;
import com.github.kolandroid.kol.model.elements.OptionElement;
import com.github.kolandroid.kol.model.elements.interfaces.SubtextElement;
import com.github.kolandroid.kol.model.models.WebModel;
import com.github.kolandroid.kol.request.Request;
import com.github.kolandroid.kol.request.ResponseHandler;
import com.github.kolandroid.kol.session.Session;
import com.github.kolandroid.kol.session.data.PwdData;
import com.github.kolandroid.kol.util.Callback;
import com.github.kolandroid.kol.util.Logger;
import com.github.kolandroid.kol.util.Regex;
import com.github.kolandroid.kol.util.StringUtils;

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

    private static final Regex NAME_END = new Regex("</center><p><blockquote>");
    private static final Regex OPTION_QUANTITY = new Regex(" \\(([^\\)]*)\\)$", 1);
    private static final Regex NAME_FROM_DESCRIPTION = new Regex("<b>(.*?)</b>", 1);
    private static final Regex ID_FROM_DESCRIPTION = new Regex("<!-- itemid: (\\d+) -->", 1);
    private static final Regex TYPE_FROM_DESCRIPTION = new Regex("<br>Type: <b>([A-Za-z\\- ]*)( ?<| \\()", 1);
    protected final String id;
    private final ArrayList<MultiActionElement> actions;
    private final String name;
    private final String subtext;
    private final boolean disabled;
    private final String slotName;
    private final int quantity;
    private WebModel description;
    // Might be updated from cache
    private String image;
    private String descriptionId;

    /**
     * Create an item using information from an inventory or other storage page.
     *
     * @param s                 The current session
     * @param pwd               The current password hash
     * @param itemInfo          The information to parse into an item
     * @param additionalActions Any additional actions to be added to the item, if required.
     */
    public ItemModel(Session s, String pwd, String itemInfo, Iterable<InventoryActionFactory> additionalActions) {
        super(s);

        if (itemInfo.contains("<font size=-1>none</font>")) {
            this.id = "0";
            this.actions = new ArrayList<>();
            this.name = "None";
            String[] slotInfo = ITEM_SLOT.extract(itemInfo);
            if (slotInfo == null || slotInfo.length == 0) {
                this.slotName = "None";
            } else {
                this.slotName = slotInfo[0] + ": " + "None";
            }
            this.subtext = "";

            this.quantity = 1;
            this.image = "http://images.kingdomofloathing.com/itemimages/blank.gif";
            this.disabled = true;
            this.descriptionId = "";
            return;
        }

        this.disabled = false;

        image = ITEM_IMG.extractSingle(itemInfo, "");
        subtext = StringUtils.htmlDecode(ITEM_SUBTEXT.extractSingle(itemInfo, ""));

        // Determine the name of the item
        String partialName = ITEM_NAME.extractSingle(itemInfo, "");
        name = partialName;

        // Determine the quantity of the item
        int amount;
        try {
            amount = Integer.parseInt(ITEM_QUANTITY.extractSingle(itemInfo, "1"));
        } catch (NumberFormatException e) {
            amount = 1;
        }
        quantity = amount;

        // Determine the slot of the item
        String[] slotInfo = ITEM_SLOT.extract(itemInfo);
        if (slotInfo == null) {
            //No slot
            slotName = partialName;
        } else if (slotInfo[1] == null || slotInfo[1].isEmpty()) {
            //Non-accessory slot
            slotName = slotInfo[0] + ": " + partialName;
        } else {
            //Accessory slot
            slotName = slotInfo[0] + " " + slotInfo[1] + ": " + partialName;
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
        for (InventoryActionFactory factory : additionalActions) {
            if (factory.appliesTo(relMap)) {
                actions.add(factory.make(getSession(), quantity == 1, id, pwd));
            }
        }
    }

    /**
     * Create an item using information from a dropdown.
     *
     * @param s     The current session
     * @param pwd   The current password hash
     * @param base  The dropdown option to parse into an item.
     * @param baseActions Any additional actions to be added to the item, if required.
     */
    public ItemModel(Session s, String pwd, OptionElement base, InventoryActionFactory... baseActions) {
        super(s);

        this.name = OPTION_QUANTITY.replaceAll(base.text, "");
        this.slotName = name;

        int amount;
        try {
            amount = Integer.parseInt(OPTION_QUANTITY.extractSingle(base.text, "1"));
        } catch (NumberFormatException e) {
            amount = 1;
        }
        this.quantity = amount;

        this.image = base.img;
        this.id = base.value;
        this.subtext = "";
        this.disabled = base.disabled;
        descriptionId = "";

        boolean single = (quantity == 1);
        this.actions = new ArrayList<>();

        for (InventoryActionFactory action : baseActions) {
            actions.add(action.make(s, single, id, pwd));
        }
    }

    /**
     * Create an item model which only changes the quantity of an existing item.
     * @param base  The item model to copy properties from
     * @param quantityChange    The delta to change quantity by
     */
    public ItemModel(ItemModel base, int quantityChange) {
        super(base.getSession());

        this.slotName = base.slotName;
        this.name = base.name;
        this.image = base.image;
        this.id = base.id;
        this.subtext = base.subtext;
        this.disabled = base.disabled;
        descriptionId = base.descriptionId;
        this.actions = base.actions;

        this.quantity = base.quantity + quantityChange;
    }

    /**
     * Create an item model around an item description.
     *
     * @param s      The current session
     * @param loader The view context loading this model
     * @param reply  An item description page
     */
    public ItemModel(final Session s, ViewContext loader, ServerReply reply) {
        super(s);

        this.description = new WebModel(s, reply);
        this.name = NAME_FROM_DESCRIPTION.extractSingle(reply.html, "");
        this.subtext = "";
        this.disabled = false;
        this.slotName = "";
        this.quantity = 1;

        this.id = ID_FROM_DESCRIPTION.extractSingle(reply.html, "0");
        this.actions = new ArrayList<>();

        // Add actions based on the type
        final String type = TYPE_FROM_DESCRIPTION.extractSingle(reply.html, "").toLowerCase();
        if (!type.isEmpty()) {
            loader.getDataContext().getSessionCache(s).access(PwdData.class, new Callback<PwdData>() {
                @Override
                public void execute(PwdData item) {
                    String pwd = item.getPwd();
                    switch (type) {
                        case "food":
                            actions.add(new MultiActionElement(s, "Eat", true, "inv_eat.php?pwd=" + pwd + "&which=1&whichitem=" + id));
                            break;
                        case "booze":
                            actions.add(new MultiActionElement(s, "Drink", true, "inv_booze.php?pwd=" + pwd + "&which=1&whichitem=" + id));
                            break;
                        case "spleen":
                            actions.add(new MultiActionElement(s, "Use", true, "inv_spleen.php?pwd=" + pwd + "&which=1&whichitem=" + id));
                            break;
                        case "hat":
                        case "back":
                        case "pants":
                        case "weapon":
                        case "ranged weapon":
                        case "off-hand":
                        case "familiar equipment":
                            actions.add(new MultiActionElement(s, "Equip", true, "inv_equip.php?pwd=" + pwd + "&which=2&action=equip&whichitem=" + id));
                            break;
                        case "accessory":
                            actions.add(new MultiActionElement(s, "Equip in Slot 1", true, "inv_equip.php?pwd=" + pwd + "&which=2&action=equip&slot=1&whichitem=" + id));
                            actions.add(new MultiActionElement(s, "Equip in Slot 2", true, "inv_equip.php?pwd=" + pwd + "&which=2&action=equip&slot=2&whichitem=" + id));
                            actions.add(new MultiActionElement(s, "Equip in Slot 3", true, "inv_equip.php?pwd=" + pwd + "&which=2&action=equip&slot=3&whichitem=" + id));
                            break;
                        case "usable":
                        case "potion":
                            actions.add(new MultiActionElement(s, "Use", true, "inv_use.php?pwd=" + pwd + "&which=3&whichitem=" + id));
                            break;
                        default:
                            //Unable to determine type or type has no associated action
                            break;
                    }
                }
            }, new Callback<Void>() {
                @Override
                public void execute(Void item) {
                    Logger.log("ItemModel", "Unable to locate pwd hash");
                }
            });
        }
    }

    /**
     * Check if this item's id matches the specified id.
     * @param id    The id to match
     * @return True if id is the item id of this item.
     */
    public boolean matches(String id) {
        return this.id.equals(id);
    }

    /**
     * Check if this model represents more than 0 of an item.
     * @return True if the model represents more than 0 of an item, otherwise false.
     */
    public boolean moreThanZero() {
        return quantity > 0;
    }

    /**
     * Check the provided cache for this item and update any internal item properties
     *  using properties from the cache.
     * Update the cache with any properties discovered using THIS item.
     * @param cache The cache to check for additional information
     */
    public void searchCache(DataCache<String, RawItem> cache) {
        RawItem match = cache.find(this.id);
        if (match != null) {
            if (this.image.equals("")) this.image = match.getImage();
            if (this.descriptionId.equals("")) this.descriptionId = match.descriptionId;
        }

        RawItem newCacheValue = RawItem.create(id, image, descriptionId, name);
        cache.store(newCacheValue);
    }

    /**
     * Parse an action on an item storage page (i.e. [use] or [store] or [eat]).
     *
     * @param pwd   The pwdhash of the current session
     * @param action    The string name of the action
     * @param fullInfo  The full information provided to reconstruct the item
     * @return A MultiActionElement representing an action, or null if the action is redundant
     */
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

        if (quantity != 1) {
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
                actDest = actDest.replace("&howmany1=1", "&howmany1=#");
                restrictSingle = false;
            }
        }

        return new MultiActionElement(getSession(), actName, restrictSingle, actDest);
    }

    /**
     * Get the item description.
     *
     * @return A WebModel for the item description, or null if no description is loaded.
     */
    public WebModel getDescription() {
        return description;
    }

    /**
     * Trigger the ItemModel to load the description of an item.
     * @param onResult  A callback to be called when the loading is complete.
     */
    public void loadDescription(final Callback<ItemModel> onResult) {
        if (disabled) return;

        if (description != null || descriptionId.equals("")) {
            onResult.execute(this);
        } else {
            this.makeRequest(new Request("desc_item.php?whichitem=" + descriptionId),
                    new ResponseHandler() {
                @Override
                public void handle(Session session, ServerReply response) {
                    if (response != null && response.url.contains(descriptionId)) {
                        String html = response.html;
                        if (quantity > 1) {
                            html = NAME_END.replaceAll(html, " (" + quantity + ")$0");
                        }
                        description = new WebModel(session, new ServerReply(response, html));
                    }
                    onResult.execute(ItemModel.this);
                }
                    });
        }
    }

    /**
     * Get the possible actions associated with this item.
     * @return A list of possible actions for this item
     */
    public ArrayList<MultiActionElement> getActions() {
        return actions;
    }

    @Override
    public String getSubtext() {
        return subtext;
    }

    @Override
    public String getText() {
        if (quantity == 1) {
            return slotName;
        } else {
            return slotName + " (" + quantity + ")";
        }
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
