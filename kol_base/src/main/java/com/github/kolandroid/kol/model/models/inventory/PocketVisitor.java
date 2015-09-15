package com.github.kolandroid.kol.model.models.inventory;


public interface PocketVisitor<Result> {
    Result display(ItemPocketModel model);

    Result display(EquipmentPocketModel model);
}
