package com.github.kolandroid.kol.model.models.inventory;


public interface ItemPocketVisitor<Result> {
    Result display(ItemPocketModel model);

    Result display(EquipmentPocketModel model);

    Result display(HagnkStatusPocketModel model);
}
