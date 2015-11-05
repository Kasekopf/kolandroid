package com.github.kolandroid.kol.model.models.inventory.pockets;

public interface ItemPocketVisitor<Result> {
    Result display(ItemPocketModel model);

    Result display(EquipmentPocketModel model);

    Result display(HagnkStatusPocketModel model);
}
