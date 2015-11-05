package com.github.kolandroid.kol.model.models.inventory.pockets;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.model.GroupModel;

public interface ItemPocket extends GroupModel.ChildModel {
    void process(ServerReply reply);

    boolean apply(String itemId, int amountDifference);

    void refreshIfLoaded();

    <Result> Result execute(ItemPocketVisitor<Result> visitor);
}
