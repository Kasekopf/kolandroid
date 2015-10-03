package com.github.kolandroid.kol.model.elements;

import com.github.kolandroid.kol.model.Model;
import com.github.kolandroid.kol.request.Request;
import com.github.kolandroid.kol.session.Session;

public class MultiActionElement extends Model {
    private final String url;
    private final String name;
    private final boolean restrictSingle;

    public MultiActionElement(Session session, String name, boolean restrictSingle, String url) {
        super(session);

        this.url = url;
        this.name = name;
        this.restrictSingle = restrictSingle;
    }

    public boolean allowMultiple() {
        return true;
    }

    public String getName() {
        return name;
    }

    public boolean trigger(String quantity) {
        try {
            int res = Integer.parseInt(quantity);
            return trigger(res);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean trigger(int quantity) {
        if (quantity <= 0) return false;
        if (restrictSingle && quantity != 1) return false;
        String activeUrl = url.replace("#", "" + quantity);
        this.makeRequest(new Request(activeUrl));
        return true;
    }

    public boolean allowSingleOnly() {
        return restrictSingle;
    }

    public String toString() {
        return "$multiaction[" + url + "]";
    }
}
