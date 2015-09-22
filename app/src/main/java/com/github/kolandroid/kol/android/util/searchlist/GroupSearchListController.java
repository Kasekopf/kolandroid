package com.github.kolandroid.kol.android.util.searchlist;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

import com.github.kolandroid.kol.android.R;
import com.github.kolandroid.kol.android.binders.Binder;
import com.github.kolandroid.kol.android.controller.Controller;
import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;
import com.github.kolandroid.kol.model.elements.ActionElement;
import com.github.kolandroid.kol.model.elements.interfaces.ModelGroup;

import java.util.ArrayList;

public class GroupSearchListController<F> implements Controller {
    /**
     * Autogenerated by eclipse.
     */
    private static final long serialVersionUID = -3034860712134386719L;

    private final ListSelector<F> selector;
    private final Binder<? super ModelGroup<F>> groupBinder;
    private final Binder<? super F> childBinder;

    private ArrayList<ModelGroup<F>> base;

    private transient HighlightableListGroupAdapter<F> adapter;
    private transient ExpandableListView list;

    public GroupSearchListController(ArrayList<ModelGroup<F>> items, Binder<? super ModelGroup<F>> groupBinder, Binder<? super F> childBinder, ListSelector<F> selector) {
        this.base = items;
        this.groupBinder = groupBinder;
        this.childBinder = childBinder;
        this.selector = selector;
    }

    public static <F extends ActionElement> GroupSearchListController<F> create(ArrayList<ModelGroup<F>> items, Binder<? super ModelGroup<F>> groupBinder, Binder<? super F> childBinder) {
        return new GroupSearchListController<>(items, groupBinder, childBinder, new ActionSelector<F>());
    }

    @Override
    public int getView() {
        return R.layout.fragment_group_list;
    }

    @Override
    public void connect(View view, final Screen host) {
        adapter = new HighlightableListGroupAdapter<>(view.getContext(), base, groupBinder, childBinder);
        list = (ExpandableListView) view.findViewById(R.id.list_display_list);
        list.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView arg0, View arg1,
                                        int groupPosition, int childPosition, long id) {
                @SuppressWarnings("unchecked")
                F choice = (F) adapter.getChild(groupPosition, childPosition);
                if (selector != null)
                    if (selector.selectItem(host, choice)) {
                        host.close();
                    }
                return true;
            }

        });
        list.setAdapter(adapter);
        for (int i = 0; i < adapter.getGroupCount(); i++)
            list.expandGroup(i);

        final EditText text = (EditText) view
                .findViewById(R.id.list_display_text);
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                adapter.changeFilter(s.toString());
                for (int i = 0; i < adapter.getGroupCount(); i++)
                    list.expandGroup(i);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // ignored
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // ignored
            }
        });
    }

    public void setItems(ArrayList<ModelGroup<F>> base) {
        this.base = base;
        if (adapter != null && list != null) {
            adapter.setElements(base);
            for (int i = 0; i < adapter.getGroupCount(); i++)
                list.expandGroup(i);
        }
    }

    @Override
    public void disconnect(Screen host) {
        // do nothing
    }

    @Override
    public void chooseScreen(ScreenSelection choice) {
        choice.displayPrimary(this, false);
    }

}
