package com.github.kolandroid.kol.model.models.chat.raw;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@SuppressWarnings("CanBeFinal")
public class RawAction implements Serializable {
    public static final RawAction SHOW_PROFILE = new RawAction("Show Profile", "showplayer.php", "who", 1);

    public String title;
    public int action;
    public String arg;
    public String entry; // key of action

    @SerializedName("useid")
    public boolean useId;
    public boolean submit;

    private RawAction(String title, String entry, String arg, int action) {
        this.title = title;
        this.entry = entry;
        this.arg = arg;
        this.action = action;
    }

    protected void setEntry(String key) {
        this.entry = key;
    }
}
