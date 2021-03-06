package com.github.kolandroid.kol.android.binders;

import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.kolandroid.kol.android.R;

import java.io.Serializable;

public class TextBinder implements Binder<Object>, Serializable {
    public static final TextBinder ONLY = new TextBinder();
    /**
     * Autogenerated by eclipse.
     */
    private static final long serialVersionUID = 5777375171095970607L;

    private TextBinder() {

    }

    @Override
    public int getView() {
        return R.layout.generic_element_view;
    }

    @Override
    public void bind(View view, Object model) {
        TextView text = (TextView) view.findViewById(R.id.generic_element_text);
        text.setText(Html.fromHtml(model.toString()));

        ImageView img = (ImageView) view.findViewById(R.id.generic_element_image);
        img.setVisibility(View.GONE);
    }
}
