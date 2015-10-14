package com.github.kolandroid.kol.data;

import java.util.Map;

public class RawSkill implements RawData {
    public final String id;
    public final String image;
    public final boolean isBuff;
    public final boolean isPassive;
    public final boolean isCombat;
    public final String name;

    private RawSkill(String id, String image, boolean isBuff, boolean isPassive, boolean isCombat, String name) {
        this.id = id;
        this.image = image;
        this.isBuff = isBuff;
        this.isPassive = isPassive;
        this.isCombat = isCombat;
        this.name = name;
    }

    public static RawSkill create(String id, String image, boolean isBuff, boolean isPassive, boolean isCombat, String name) {
        id = id.replace("\n", "");
        image = image.replace("\n", "");
        if (image.contains("/")) {
            image = image.substring(image.lastIndexOf("/") + 1);
        }

        if (image.equals("")) return null;
        name = name.replace("\n", "");

        return new RawSkill(id, image, isBuff, isPassive, isCombat, name);
    }

    public static RawSkill parse(Map<String, Integer> headers, String cacheLine) {
        String[] sections = cacheLine.split("\t");
        if (headers == null) return null;
        if (sections.length != headers.size()) return null;

        String image = headers.containsKey("IMAGE") ? sections[headers.get("IMAGE")] : "";
        boolean isBuff = headers.containsKey("ISBUFF") ? sections[headers.get("ISBUFF")].equals("true") : true;
        boolean isPassive = headers.containsKey("ISPASSIVE") ? sections[headers.get("ISPASSIVE")].equals("true") : true;
        boolean isCombat = headers.containsKey("ISCOMBAT") ? sections[headers.get("ISCOMBAT")].equals("true") : true;
        String name = headers.containsKey("NAME") ? sections[headers.get("NAME")] : "";

        return new RawSkill(sections[0], image, isBuff, isPassive, isCombat, name);
    }

    public String toString() {
        return "$skill[" + name + "]";
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof RawSkill)) {
            return false;
        }

        RawSkill otherSkill = (RawSkill) other;
        return (id.equalsIgnoreCase(otherSkill.id) &&
                image.equalsIgnoreCase(otherSkill.image) &&
                (isBuff == otherSkill.isBuff) &&
                name.equalsIgnoreCase(otherSkill.name));
    }

    public String getImage() {
        return "http://images.kingdomofloathing.com/itemimages/" + image;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String output() {
        return id + "\t" + image + "\t" + isBuff + "\t" + name + "\n";
    }
}
