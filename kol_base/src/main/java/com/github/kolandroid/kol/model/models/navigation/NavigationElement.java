package com.github.kolandroid.kol.model.models.navigation;

import com.github.kolandroid.kol.model.Model;
import com.github.kolandroid.kol.model.elements.interfaces.SubtextElement;
import com.github.kolandroid.kol.request.Request;
import com.github.kolandroid.kol.session.Session;
import com.github.kolandroid.kol.util.Callback;

public class NavigationElement extends Model implements SubtextElement {
    protected final String url;
    private final String name;
    private final String image;

    public NavigationElement(Session session, String name, String image, String url) {
        super(session);
        this.name = name;
        this.image = image;
        this.url = url;
    }

    @Override
    public String getText() {
        return name;
    }

    @Override
    public String getImage() {
        return image;
    }

    public void submit(Callback<String> forClarification) {
        this.makeRequest(new Request(url));
    }

    public NavigationElement fillArgument(String argument, String value) {
        return this;
    }

    public boolean urlMatches(NavigationElement other) {
        return (other != null) && (other.url.equals(url));
    }

    @Override
    public String getSubtext() {
        return "";
    }
}
