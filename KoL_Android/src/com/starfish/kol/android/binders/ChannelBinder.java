package com.starfish.kol.android.binders;

import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.starfish.kol.android.R;
import com.starfish.kol.model.ProgressHandler;
import com.starfish.kol.model.models.chat.ChannelModel;

public class ChannelBinder implements Binder<ChannelModel> {
	private ProgressHandler<ChannelModel> channelHandler;

	public ChannelBinder(ProgressHandler<ChannelModel> channelHandler) {
		this.channelHandler = channelHandler;
	}

	@Override
	public int getView() {
		return R.layout.list_chat_channel_item;
	}

	@Override
	public void bind(View view, final ChannelModel model) {
		TextView text = (TextView) view.findViewById(R.id.list_item_text);
		text.setText(Html.fromHtml(model.getName()));

		Button enter = (Button) view.findViewById(R.id.chat_channel_enter);
		Button leave = (Button) view.findViewById(R.id.chat_channel_leave);

		if (model.isActive()) {
			text.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (channelHandler != null)
						channelHandler.reportProgress(model);
				}
			});

			enter.setEnabled(false);
			leave.setEnabled(true);
			leave.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					model.leave();
				}
			});
		} else {
			enter.setEnabled(true);
			leave.setEnabled(false);
			enter.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					model.enter();
				}
			});
		}
	}

}
