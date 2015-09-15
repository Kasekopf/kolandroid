package com.github.kolandroid.kol.model.models.inventory;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.connection.Session;
import com.github.kolandroid.kol.model.GroupModel;
import com.github.kolandroid.kol.model.models.WebModel;
import com.github.kolandroid.kol.util.Regex;

public class InventoryModel extends GroupModel<InventoryPocketModel> {
    /**
     * Autogenerated by eclipse.
     */
    private static final long serialVersionUID = 5853274439517430160L;

    private static final Regex CHOSEN_CONSUME = new Regex("\\[consumables\\]");
    private static final Regex CHOSEN_EQUIP = new Regex("\\[equipment\\]");
    private static final Regex CHOSEN_MISC = new Regex("\\[miscellaneous\\]");
    private static final Regex CHOSEN_RECENT = new Regex("\\[recent items\\]");
    private final InventoryPocketModel consume;
    private final EquipmentPocketModel equip;
    private final InventoryPocketModel misc;
    private final InventoryPocketModel recent;
    private int chosen;
    private WebModel resultsPane;

    public InventoryModel(Session s, ServerReply text) {
        super(s);

        consume = new InventoryPocketModel("Consume", s, "inventory.php?which=1");
        equip = new EquipmentPocketModel("Equip", s, "inventory.php?which=2");
        misc = new InventoryPocketModel("Misc", s, "inventory.php?which=3");
        recent = new InventoryPocketModel("Recent", s, "inventory.php?which=f-1");

        loadContent(text);
    }

    protected void loadContent(ServerReply text) {
        if (!text.url.contains("inventory.php")) {
            System.out
                    .println("Attempted to load non-inventory page into InventoryModel: "
                            + text.url);
            return;
        }

        resultsPane = extractResultsPane(getSession(), text);

        if (CHOSEN_CONSUME.matches(text.html)) {
            chosen = 1;
        } else if (CHOSEN_EQUIP.matches(text.html)) {
            chosen = 2;
        } else if (CHOSEN_MISC.matches(text.html)) {
            chosen = 3;
        } else if (CHOSEN_RECENT.matches(text.html)) {
            chosen = 0;
        } else
            throw new RuntimeException(
                    "Unable to determine current inventory pane");

        InventoryPocketModel[] children = this.getChildren();
        children[chosen].process(text);
        System.out.println("Loaded into slot " + chosen);
    }

    public WebModel getResultsPane() {
        return resultsPane;
    }

    @Override
    public InventoryPocketModel[] getChildren() {
        return new InventoryPocketModel[]{recent, consume, equip, misc};
    }

    @Override
    public int getActiveChild() {
        return chosen;
    }
}
