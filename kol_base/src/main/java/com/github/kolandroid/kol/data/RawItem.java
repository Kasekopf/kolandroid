package com.github.kolandroid.kol.data;

import java.util.Map;

public class RawItem implements RawData {
    public final String id;
    public final String image;
    public final String descriptionId;
    public final String name;

    private RawItem(String id, String image, String descriptionId, String name) {
        this.id = id;
        this.image = image;
        this.descriptionId = descriptionId;
        this.name = name;
    }

    public static RawItem create(String id, String image, String descriptionId, String name) {
        if (id == null) id = "";
        if (image == null) image = "";
        if (descriptionId == null) descriptionId = "";
        if (name == null) name = "";

        id = id.replace("\n", "");
        image = image.replace("\n", "");
        if (image.contains("/")) {
            image = image.substring(image.lastIndexOf("/") + 1);
        }
        descriptionId = descriptionId.replace("\n", "");

        if (image.equals("") || descriptionId.equals("")) return null;
        name = name.replace("\n", "");

        return new RawItem(id, image, descriptionId, name);
    }

    public static RawItem parse(Map<String, Integer> headers, String cacheLine) {
        String[] sections = cacheLine.split("\t");
        if (headers == null) return null;
        if (sections.length != headers.size()) return null;

        String image = headers.containsKey("IMAGE") ? sections[headers.get("IMAGE")] : "";
        String description = headers.containsKey("DESCID") ? sections[headers.get("DESCID")] : "";
        String name = headers.containsKey("NAME") ? sections[headers.get("NAME")] : "";

        return new RawItem(sections[0], image, description, name);
    }

    public String toString() {
        return "$item[" + name + "]";
    }

    public String getImage() {
        return "http://images.kingdomofloathing.com/itemimages/" + image;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof RawItem)) {
            return false;
        }

        RawItem otherItem = (RawItem) other;
        return (id.equalsIgnoreCase(otherItem.id) &&
                image.equalsIgnoreCase(otherItem.image) &&
                descriptionId.equalsIgnoreCase(otherItem.descriptionId) &&
                name.equalsIgnoreCase(otherItem.name));
    }

    @Override
    public String output() {
        return id + "\t" + image + "\t" + descriptionId + "\t" + name + "\n";
    }

    @Override
    public String getId() {
        return id;
    }
}
