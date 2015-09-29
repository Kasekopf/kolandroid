package com.github.kolandroid.kol.model.models.fight;

import com.github.kolandroid.kol.connection.Session;
import com.github.kolandroid.kol.model.Model;
import com.github.kolandroid.kol.request.Request;

public class FightBasicAction extends Model implements FightAction {
    private final String name;
    private final String image;
    private final String action;

    private final String identifier;

    private final FightActionHistory<? super FightBasicAction> storage;

    /**
     * Create a new model in the provided session.
     *
     * @param s Session to use in all future requests by this model.
     */
    public FightBasicAction(Session s, String name, String image, String action, FightActionHistory<? super FightBasicAction> storage) {
        super(s);

        this.name = name;
        this.image = image;
        this.action = action;

        this.identifier = "basic:" + name.toLowerCase();
        this.storage = storage;
    }

    @Override
    public void use() {
        storage.store(this, getSettings());
        this.makeRequest(new Request(action));
    }

    @Override
    public String getIdentifier() {
        return identifier;
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