package com.github.kolandroid.kol.model.models.skill;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.gamehandler.ViewContext;
import com.github.kolandroid.kol.model.GroupModel;
import com.github.kolandroid.kol.session.Session;

public class SkillsModel extends GroupModel<SkillsSubmodel> {
    /**
     * Autogenerated by eclipse.
     */
    private static final long serialVersionUID = 295714863597L;
    private final SkillsListModel skills;
    private final ItemRestorersModel items;

    public SkillsModel(Session s, ServerReply text) {
        super(s);

        this.skills = new SkillsListModel(s, text);
        this.items = new ItemRestorersModel(s, text);
        this.setActiveChild(0);
    }

    @Override
    public void attachView(ViewContext context) {
        super.attachView(context);

        skills.attachView(context);
    }

    @Override
    public SkillsSubmodel[] getChildren() {
        return new SkillsSubmodel[]{skills, items};
    }
}
