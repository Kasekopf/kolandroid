package com.github.kolandroid.kol.model.models.fight;

import com.github.kolandroid.kol.connection.Session;
import com.github.kolandroid.kol.model.Model;
import com.github.kolandroid.kol.request.Request;

public class FightBasicAction extends Model implements FightAction {
    private final String name;
    private final String image;
    private final String action;

    /**
     * Create a new model in the provided session.
     *
     * @param s Session to use in all future requests by this model.
     */
    public FightBasicAction(Session s, String name, String image, String action) {
        super(s);

        this.name = name;
        this.image = image;
        this.action = action;
    }

    @Override
    public void use() {
        this.makeRequest(new Request(action));
    }

    @Override
    public String getSubtext() {
        return "";
    }

    @Override
    public String getText() {
        return name;
    }

    @Override
    public String getImage() {
        return image;
    }
}
