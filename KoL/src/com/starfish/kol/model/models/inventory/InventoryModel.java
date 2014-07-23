package com.starfish.kol.model.models.inventory;

import com.starfish.kol.connection.ServerReply;
import com.starfish.kol.connection.Session;
import com.starfish.kol.model.Model;
import com.starfish.kol.model.ParentModel;
import com.starfish.kol.model.models.WebModel;
import com.starfish.kol.util.Regex;

public class InventoryModel extends ParentModel<Void> {
	/**
	 * Autogenerated by eclipse.
	 */
	private static final long serialVersionUID = 5853274439517430160L;

	private static final Regex CHOSEN_CONSUME = new Regex("\\[consumables\\]");
	private static final Regex CHOSEN_EQUIP = new Regex("\\[equipment\\]");
	private static final Regex CHOSEN_MISC = new Regex("\\[miscellaneous\\]");
	private static final Regex CHOSEN_RECENT = new Regex("\\[recent items\\]");

	private int chosen;

	private static final Regex RESULTS_PANE = new Regex(
			"<div[^>]*id=[\"']?effdiv.*?</div>", 0);
	private static final Regex PAGE_BODY = new Regex(
			"(<body[^>]*>)(.*?)(</body>)");

	private String resultsPane;

	private InventoryPocketModel consume;
	private EquipmentPocketModel equip;
	private InventoryPocketModel misc;
	private InventoryPocketModel recent;

	public InventoryModel(Session s, ServerReply text) {
		super(s, text);

		consume = new InventoryPocketModel(s, "inventory.php?which=1");
		equip = new EquipmentPocketModel(s, "inventory.php?which=2");
		misc = new InventoryPocketModel(s, "inventory.php?which=3");
		recent = new InventoryPocketModel(s, "inventory.php?which=f-1");

		loadContent(text);
	}

	protected boolean loadContent(ServerReply text) {
		if (!text.url.contains("inventory.php")) {
			System.out
					.println("Attempted to load non-inventory page into InventoryModel: "
							+ text.url);
			return false;
		}

		resultsPane = RESULTS_PANE.extractSingle(text.html);

		if (CHOSEN_CONSUME.matches(text.html)) {
			chosen = 0;
		} else if (CHOSEN_EQUIP.matches(text.html)) {
			chosen = 1;
		} else if (CHOSEN_MISC.matches(text.html)) {
			chosen = 2;
		} else if (CHOSEN_RECENT.matches(text.html)) {
			chosen = 3;
		} else
			throw new RuntimeException(
					"Unable to determine current inventory pane");

		getPocket(chosen).process(text);
		System.out.println("Loaded into slot " + chosen);
		return true;
	}

	public WebModel getResultsPane() {
		if (resultsPane == null)
			return null;

		ServerReply base = this.getBase();
		String html = PAGE_BODY.replaceAll(base.html, "$1<center>"
				+ resultsPane + "</center>$3");
		ServerReply newRep = new ServerReply(base.responseCode,
				base.redirectLocation, base.date, html, "small/invresults.php",
				base.cookie);
		return new WebModel(getSession(), newRep);
	}

	public int getInitialChosen() {
		return chosen;
	}

	public InventoryPocketModel getConsume() {
		return consume;
	}

	public EquipmentPocketModel getEquip() {
		return equip;
	}

	public InventoryPocketModel getMisc() {
		return misc;
	}

	public InventoryPocketModel getRecent() {
		return recent;
	}
	
	public InventoryPocketModel getPocket(int slot) {
		switch (slot) {
		case 0:
			return consume;
		case 1:
			return equip;
		case 2:
			return misc;
		case 3:
			return recent;
		}

		throw new RuntimeException("Attempted to load unknown slot");
	}

	@Override
	protected Model<?>[] getChildren() {
		return new Model<?>[]{consume, equip, misc, recent};
	}
}
