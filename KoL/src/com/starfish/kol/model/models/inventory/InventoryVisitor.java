package com.starfish.kol.model.models.inventory;


public interface InventoryVisitor<Result> {
	public Result display(InventoryPocketModel model);
	public Result display(EquipmentPocketModel model);
}
