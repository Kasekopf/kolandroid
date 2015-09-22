package com.github.kolandroid.kol.model.models.skill;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.connection.Session;
import com.github.kolandroid.kol.model.elements.OptionElement;
import com.github.kolandroid.kol.model.elements.OptionElement.OptionElementParser;
import com.github.kolandroid.kol.model.elements.basic.BasicGroup;
import com.github.kolandroid.kol.model.models.inventory.ItemModel;
import com.github.kolandroid.kol.model.models.inventory.ItemPocketModel;
import com.github.kolandroid.kol.util.Regex;

import java.util.ArrayList;

public class ItemRestorersModel extends ItemPocketModel implements SkillsSubmodel {
    /**
     * Autogenerated by eclipse.
     */
    private static final long serialVersionUID = 2345497919395304279L;

    private static final Regex ITEMS_FORM = new Regex(
            "<form[^>]*restorerform[^>]*>.*?</form>", 0);

    private static final Regex PWD = new Regex("<input[^>]*pwd[^>]*>", 0);
    private static final Regex EXTRACT_VALUE = new Regex(
            "value=[\"']?([0-9a-fA-F]*)", 1);

    public ItemRestorersModel(Session s, ServerReply base) {
        super("Restorers", s, base);
    }

    @Override
    protected void loadContent(ServerReply reply) {
        this.items = new ArrayList<>();
        String all_items = ITEMS_FORM.extractSingle(reply.html, "");
        final String pwd = EXTRACT_VALUE.extractSingle(PWD.extractSingle(all_items), "0");

        OptionElementParser<ItemModel> itemsParser = new OptionElementParser<ItemModel>(
                "(select a skill)") {
            @Override
            public ItemModel make(OptionElement base) {
                String action = "inv_use.php";
                action += "?pwd=" + pwd;
                action += "&action=useitem&bounce=skillz.php%3Faction%3Duseditem";
                action += "&whichitem=" + base.value;
                action += "&itemquantity=";

                return new ItemModel(getSession(), pwd, base, action);
            }
        };
        ArrayList<ItemModel> itemList = OptionElement.extractObjects(all_items, itemsParser);
        items.add(new BasicGroup<>("Restorers", itemList));
    }

    @Override
    public <Result> Result execute(SkillsVisitor<Result> visitor) {
        return visitor.execute(this);
    }

}
