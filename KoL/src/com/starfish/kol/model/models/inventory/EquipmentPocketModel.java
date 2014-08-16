package com.starfish.kol.model.models.inventory;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import com.starfish.kol.connection.ServerReply;
import com.starfish.kol.connection.Session;
import com.starfish.kol.model.elements.ActionElement;
import com.starfish.kol.model.elements.OptionElement;
import com.starfish.kol.model.elements.basic.BasicAction;
import com.starfish.kol.model.elements.basic.BasicGroup;
import com.starfish.kol.model.elements.interfaces.DeferredGameAction;
import com.starfish.kol.model.elements.interfaces.ModelGroup;
import com.starfish.kol.util.Regex;

public class EquipmentPocketModel extends InventoryPocketModel {
	/**
	 * Autogenerated by eclipse.
	 */
	private static final long serialVersionUID = -4393723486745747928L;

	private static final Regex EQUIPMENT = new Regex(
			"<table[^>]*curequip[^>]*>.*?</table>", 0);
	private static final Regex ITEM_EQUIPPED = new Regex("<tr>.*?(?=</?tr>)", 0);
	
	private static final Regex OUTFITS = OptionElement.regexFor("whichoutfit");
	
	private ArrayList<ModelGroup<ActionElement>> outfits = new ArrayList<ModelGroup<ActionElement>>();
	
	public EquipmentPocketModel(Session s, String updateUrl) {
		super(s, updateUrl);
	}

	protected void loadContent(ServerReply reply) {
		super.loadContent(reply);

		String pwd = PWD.extractSingle(reply.html);
		
		String equipment = EQUIPMENT.extractSingle(reply.html);
		if (equipment != null) {
			ModelGroup<InventoryItem> equipped = parseItems("Equipped",
					ITEM_EQUIPPED.extractAllSingle(equipment), pwd);
			if (equipped.size() > 0)
				items.add(0, equipped);
		}

		String baseaction = "inv_equip.php?action=outfit&which=2&whichoutfit=";
		
		outfits = new ArrayList<ModelGroup<ActionElement>>();
		String outfit_select = OUTFITS.extractSingle(reply.html);
		ArrayList<ModelGroup<OptionElement>> outfit_options = OptionElement.extractOptionGroups(outfit_select, "Outfits");
		for(ModelGroup<OptionElement> outfit_group : outfit_options) {
			BasicGroup<ActionElement> group = new BasicGroup<ActionElement>(outfit_group.getName());
			for(OptionElement option : outfit_group) {
				if(option.text.contains("(select an outfit)")) continue;
				
				group.add(new ActionElement(getSession(), option.text, option.img, baseaction + option.value));
			}
			outfits.add(group);
		}
	}
	
	public ArrayList<ModelGroup<ActionElement>> getOutfits() {
		this.access();
		return outfits;
	}

	public <Result> Result execute(InventoryVisitor<Result> visitor) {
		return visitor.display(this);
	}
	
	public CustomOutfitBuilder saveOutfit() {
		return new CustomOutfitBuilder();
	}
	
	public class CustomOutfitBuilder implements Serializable
	{
		/**
		 * Autogenerated by eclipse.
		 */
		private static final long serialVersionUID = 4232930772541155466L;

		public DeferredGameAction saveOutfit(String name) {
			String baseAction = "inv_equip.php?which=2&action=customoutfit&outfitname=";
			
			try {
				name = URLEncoder.encode(name, "UTF-8")
				        .replaceAll("\\+", "%20")
				        .replaceAll("\\%21", "!")
				        .replaceAll("\\%27", "'")
				        .replaceAll("\\%28", "(")
				        .replaceAll("\\%29", ")")
				        .replaceAll("\\%7E", "~");
			} catch (UnsupportedEncodingException e) {
				System.out.println("Unable to URL-Encode outfit name");
				e.printStackTrace();
			}
			
			return new BasicAction(getSession(), baseAction + name);
		}
	}
}
