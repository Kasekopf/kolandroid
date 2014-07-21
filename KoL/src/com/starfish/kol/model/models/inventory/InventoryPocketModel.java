package com.starfish.kol.model.models.inventory;

import java.util.ArrayList;

import com.starfish.kol.connection.Connection.ServerReply;
import com.starfish.kol.connection.Session;
import com.starfish.kol.model.basic.ActionItem;
import com.starfish.kol.model.basic.BasicGroup;
import com.starfish.kol.model.interfaces.ModelGroup;
import com.starfish.kol.model.util.LiveModel;
import com.starfish.kol.util.Regex;


public class InventoryPocketModel extends LiveModel {
	/**
	 * Autogenerated by eclipse.
	 */
	private static final long serialVersionUID = 2109624705245532662L;

	private static final Regex SECTION = new Regex(
			"<a[^>]*><table.*?(</table>(?=.?<a)|$)", 0);
	private static final Regex SECTION_NAME = new Regex(
			"<font color=[\"']?white[\"']?>(.*?):</font>", 1);

	private static final Regex ITEM = new Regex(
			"<table class=[\"']?item.*?</table>", 0);

	private static final Regex ITEM_IMG = new Regex(
			"<img[^>]*src=[\"']?([^\"' >]*)[\"' >]", 1);
	private static final Regex ITEM_DESCID = new Regex(
			"<img[^>]*descitem\\((\\d+)[,\\)]", 1);
	private static final Regex ITEM_NAME = new Regex("<b[^>]*>(.*?)</b>", 1);
	private static final Regex ITEM_QNTY = new Regex(
			"<b class=\"ircm\">.*?</b>&nbsp;<span>\\((\\d+)\\)</span>", 1);

	private static final Regex ITEM_SLOT = new Regex("<a[^>]*>([^<]*?)</a>:", 1);

	private static final Regex ITEM_ACTION = new Regex("<a.*?</a>", 0);
	private static final Regex ITEM_ACTION_NAME = new Regex(
			"<a[^>]*>.*?\\[([^\\]]*?)\\].*?</a>", 1);
	private static final Regex ITEM_ACTION_LINK = new Regex(
			"<a[^>]*href=[\"']?(.*?)[\"' >]", 1);
	private static final Regex ITEM_SUBTEXT = new Regex(
			"<font[^>]*size=[\"']?1[^>]*>.*?(\\([^<]*\\))</font>", 1);
	
	protected ArrayList<ModelGroup<InventoryItem>> items;
	
	public InventoryPocketModel(Session s, String updateUrl) {
		super(s, updateUrl, true);
		this.items = new ArrayList<ModelGroup<InventoryItem>>();
	}

	public ArrayList<ModelGroup<InventoryItem>> getItems() {
		this.access();
		return items;
	}

	protected ModelGroup<InventoryItem> parseItems(String sectionName,
			ArrayList<String> items) {
		BasicGroup<InventoryItem> newsection = new BasicGroup<InventoryItem>(
				sectionName);
		for (String item : items) {

			String img = ITEM_IMG.extractSingle(item);
			String descid = ITEM_DESCID.extractSingle(item);
			String name = ITEM_NAME.extractSingle(item);
			String subtext = ITEM_SUBTEXT.extractSingle(item);
			String slot = ITEM_SLOT.extractSingle(item);

			String number = ITEM_QNTY.extractSingle(item);
			if (name == null)
				continue;
			if (subtext == null)
				subtext = "";

			subtext = subtext.replace("&nbsp;", "");
			if (number != null)
				name += " (" + number + ")";
			if (slot != null) {
				name = slot + ": " + name;
			}

			ArrayList<ActionItem> actions = new ArrayList<ActionItem>();
			actions.add(new ActionItem(getSession(), "Description", "",
					"desc_item.php?whichitem=" + descid));
			for (String action : ITEM_ACTION.extractAllSingle(item)) {
				String actName = ITEM_ACTION_NAME.extractSingle(action);
				String actDest = ITEM_ACTION_LINK.extractSingle(action);
				if (actName == null || actDest == null)
					continue;

				actName = actName.substring(0, 1).toUpperCase()
						+ actName.substring(1);
				actions.add(new ActionItem(getSession(), actName, "", actDest));
			}

			newsection.add(new InventoryItem(name, img, subtext, actions));
		}
		return newsection;
	}

	protected void loadContent(ServerReply reply) {
		this.items = new ArrayList<ModelGroup<InventoryItem>>();

		for (String section : SECTION.extractAllSingle(reply.html)) {
			String sectionName = SECTION_NAME.extractSingle(section);
			ModelGroup<InventoryItem> newsection = parseItems(sectionName,
					ITEM.extractAllSingle(section));

			if (newsection.size() > 0)
				this.items.add(newsection);
		}
	}
}