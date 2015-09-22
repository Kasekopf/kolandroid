package com.github.kolandroid.kol.android.util.searchlist;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.kolandroid.kol.android.R;
import com.github.kolandroid.kol.android.binders.Binder;
import com.github.kolandroid.kol.android.util.adapters.ListGroupAdapter;
import com.github.kolandroid.kol.model.elements.basic.BasicGroup;
import com.github.kolandroid.kol.model.elements.interfaces.ModelGroup;
import com.github.kolandroid.kol.util.Regex;

import java.util.ArrayList;
import java.util.List;

public class HighlightableListGroupAdapter<F> extends ListGroupAdapter<ModelGroup<F>, F> {
    private final List<ModelGroup<F>> displayed;
    private List<ModelGroup<F>> base;
    private Regex filter;

    public HighlightableListGroupAdapter(Context context, List<ModelGroup<F>> base, Binder<? super ModelGroup<F>> groupBinder, Binder<? super F> childBinder) {
        super(context, new ArrayList<ModelGroup<F>>(), groupBinder, childBinder);

        this.displayed = new ArrayList<>();
        this.filter = new Regex("");
        this.setElements(base);
    }

    public void changeFilter(String newValue) {
        filter = new Regex("(?i)\\Q" + newValue + "\\E");
        displayed.clear();
        this.setElements(base);
    }

    @Override
    public void setElements(List<ModelGroup<F>> base) {
        this.base = base;
        displayed.clear();
        for (ModelGroup<F> elem : base) {
            BasicGroup<F> newgroup = new BasicGroup<>(elem.getName());
            for (int i = 0; i < elem.size(); i++) {
                F item = elem.get(i);
                if (filter.matches(item.toString()))
                    newgroup.add(item);
            }

            if (newgroup.size() > 0)
                displayed.add(newgroup);
        }
        super.setElements(displayed);
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        View view = super.getChildView(groupPosition, childPosition, isLastChild, convertView, parent);

        TextView text = (TextView) view.findViewById(R.id.generic_element_text);
        String highlighted = filter.replaceAll(text.getText().toString(), "<b>$0</b>");
        text.setText(Html.fromHtml(highlighted));
        return view;
    }
}
