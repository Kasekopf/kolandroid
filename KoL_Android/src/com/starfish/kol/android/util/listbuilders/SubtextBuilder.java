package com.starfish.kol.android.util.listbuilders;

import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.starfish.kol.android.R;
import com.starfish.kol.android.util.ImageDownloader;
import com.starfish.kol.android.util.adapters.ListFullBuilder;
import com.starfish.kol.model.interfaces.ModelGroup;
import com.starfish.kol.model.interfaces.SubtextItem;

public class SubtextBuilder<F extends SubtextItem> implements
		ListFullBuilder<ModelGroup<F>, F> {
	/**
	 * Autogenerated by eclipse.
	 */
	private static final long serialVersionUID = -3461934770250185253L;

	@Override
	public int getGroupLayout() {
		return R.layout.list_generic_header;
	}

	@Override
	public int getChildLayout() {
		return R.layout.list_subtext_item;
	}

	@Override
	public void fillGroup(View view, ModelGroup<F> group) {
		TextView text = (TextView) view.findViewById(R.id.list_header_text);
		text.setText(group.getName());
	}

	@Override
	public void fillChild(View view, F child) {
		TextView text = (TextView) view.findViewById(R.id.list_item_text);
		text.setText(Html.fromHtml(child.getText()));

		TextView subtext = (TextView) view.findViewById(R.id.list_item_subtext);
		subtext.setText(Html.fromHtml(child.getSubtext()));
		subtext.setVisibility((child.getSubtext().length() <= 2) ? View.GONE : View.VISIBLE);

		ImageView img = (ImageView) view.findViewById(R.id.list_item_image);
		if (child.getImage() == null || child.getImage().length() == 0) {
			img.setVisibility(View.GONE);
		} else {
			img.setVisibility(View.VISIBLE);
			ImageDownloader.loadFromUrl(img, child.getImage());
		}
	}

}
