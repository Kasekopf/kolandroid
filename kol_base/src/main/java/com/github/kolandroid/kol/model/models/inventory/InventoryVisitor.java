package com.github.kolandroid.kol.model.models.inventory;


public interface InventoryVisitor<Result> {
    Result display(InventoryPocketModel model);

    Result display(EquipmentPocketModel model);
}
