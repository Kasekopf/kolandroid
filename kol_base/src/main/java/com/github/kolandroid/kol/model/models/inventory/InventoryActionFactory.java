package com.github.kolandroid.kol.model.models.inventory;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.connection.Session;
import com.github.kolandroid.kol.model.elements.MultiActionElement;

import java.util.Map;

public enum InventoryActionFactory {
    USE("Use", "THISISNEVERONTHEPAGE", "multiuse.php?whichitem=IID&action=useitem&ajax=1&pwd=PWD&quantity=#") {
        @Override
        public boolean appliesTo(Map<String, String> rel) {
            return true;
        }
    },
    USE_RESTORER("Use", "THISISNEVERONTHISPAGE", "inv_use.php?pwd=PWD&action=useitem&bounce=skillz.php%3Faction%3Duseditem&whichitem=IID&itemquantity=#") {
        public boolean appliesTo(Map<String, String> res) {
            return false;
        }
    },
    AUTOSELL("Autosell", "", "sellstuff.php?action=sell&ajax=1&type=quant&whichitem%5B%5D=IID&howmany=#&pwd=PWD") {
        @Override
        public boolean appliesTo(Map<String, String> rel) {
            if (!rel.containsKey("d") || !rel.containsKey("s")) return false;

            if (!rel.get("d").equals("1")) return false;
            try {
                return Integer.parseInt(rel.get("s")) > 0;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    },
    CLOSET("Closet", "inventory.php?action=closetpush&ajax=1", "inventory.php?action=closetpush&ajax=1&whichitem=IID&qty=#&pwd=PWD") {
        @Override
        public boolean appliesTo(Map<String, String> rel) {
            return rel.containsKey("q") && rel.get("q").equals("0");
        }
    },
    MALL("Stock in Mall", "managestore.php?action=additem", "managestore.php?action=additem&qty#=NUM&item1=IID&price1=&limit1=&ajax=1&pwd=PWD") {
        @Override
        public boolean appliesTo(Map<String, String> rel) {
            return rel.containsKey("q") && rel.get("q").equals("0")
                    && rel.containsKey("g") && rel.get("g").equals("0")
                    && rel.containsKey("t") && rel.get("t").equals("1");
        }
    },
    DISPLAY("Add to Display Case", "managecollection.php?action=put", "managecollection.php?action=put&ajax=1&whichitem1=IID&howmany1=#&pwd=PWD") {
        @Override
        public boolean appliesTo(Map<String, String> rel) {
            return rel.containsKey("q") && rel.get("q").equals("0");
        }
    },
    CLAN("Contribute to Clan", "clan_stash.php?action=addgoodies", "clan_stash.php?action=addgoodies&ajax=1&item1=IID&qty1=#&pwd=PWD") {
        @Override
        public boolean appliesTo(Map<String, String> rel) {
            return rel.containsKey("q") && rel.get("q").equals("0")
                    && rel.containsKey("t") && rel.get("t").equals("1");
        }
    },
    PULVERIZE("Pulverize", "craft.php?action=pulverize", "craft.php?action=pulverize&ajax=1&smashitem=IID&qty=#&pwd=PWD") {
        @Override
        public boolean appliesTo(Map<String, String> rel) {
            //i.q==0 && i.p==0 && i.u=="q" && i.d==1 && i.t==1)
            return rel.containsKey("q") && rel.get("q").equals("0")
                    && rel.containsKey("p") && rel.get("p").equals("0")
                    && rel.containsKey("u") && rel.get("u").equals("q")
                    && rel.containsKey("d") && rel.get("d").equals("1")
                    && rel.containsKey("t") && rel.get("t").equals("1");
        }
    };

    private final String name;
    private final String url;
    private final String identifier;

    InventoryActionFactory(String name, String identifier, String url) {
        this.name = name;
        this.identifier = identifier;
        this.url = url;
    }

    public boolean findOnPage(ServerReply page) {
        return (page != null) && page.html.contains(identifier);
    }

    public MultiActionElement make(Session session, boolean restrictSingle, String id, String pwd) {
        String newUrl = url.replace("IID", id).replace("PWD", pwd);
        return new MultiActionElement(session, name, restrictSingle, newUrl);
    }

    public abstract boolean appliesTo(Map<String, String> rel);
}
