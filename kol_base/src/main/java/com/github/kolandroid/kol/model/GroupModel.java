package com.github.kolandroid.kol.model;

import com.github.kolandroid.kol.connection.Session;
import com.github.kolandroid.kol.model.GroupModel.ChildModel;

/**
 * A model responsible for many additional child models, with a single active child at a given time.
 *
 * @param <Child> The type of children to possess
 */
public abstract class GroupModel<Child extends ChildModel> extends Model {
    /**
     * Autogenerated by eclipse.
     */
    private static final long serialVersionUID = 8057138180603838561L;
    private int chosen = 0;

    /**
     * Create a new GroupModel in the specified game session.
     * @param s The session to use.
     */
    public GroupModel(Session s) {
        super(s);
    }

    /**
     * Get the child currently active in the model.
     * @return The child currently active in the model.
     */
    public int getActiveChild() {
        return chosen;
    }

    /**
     * Set the child currently active in the model.
     *
     * @param child The child to be active in the model.
     */
    public void setActiveChild(int child) {
        if (child >= 0 && child < getChildren().length) {
            this.chosen = child;
        }
    }

    /**
     * Get an array of all children possessed by the model.
     * @return All children possessed by the model.
     */
    public abstract Child[] getChildren();

    /**
     * A simple interface for any children of a GroupModel. Children must have a title.
     */
    public interface ChildModel {
        /**
         * Get the title for the child.
         * @return The title of this child.
         */
        String getTitle();
    }
}
