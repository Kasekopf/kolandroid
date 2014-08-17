package com.starfish.kol.android.binders;

import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.starfish.kol.android.R;
import com.starfish.kol.model.ProgressHandler;
import com.starfish.kol.model.elements.interfaces.DeferredAction;
import com.starfish.kol.model.models.chat.ChatChannel;
import com.starfish.kol.model.models.chat.ChatModel;

public class ChannelBinder implements Binder<ChatChannel> {
	private ProgressHandler<ChatChannel> channelHandler;
	private ProgressHandler<DeferredAction<ChatModel>> actionHandler;
	
	public ChannelBinder(ProgressHandler<ChatChannel> channelHandler, ProgressHandler<DeferredAction<ChatModel>> actionHandler) {
		this.channelHandler = channelHandler;
		this.actionHandler = actionHandler;
	}
	
	@Override
	public int getView() {
		return R.layout.list_chat_channel_item;
	}

	@Override
	public void bind(View view, final ChatChannel model) {
		TextView text = (TextView)view.findViewById(R.id.list_item_text);
		text.setText(Html.fromHtml(model.getName()));
		text.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(channelHandler != null)
					channelHandler.reportProgress(model);
			}			
		});
		
		Button enter = (Button)view.findViewById(R.id.chat_channel_enter);
		Button leave = (Button)view.findViewById(R.id.chat_channel_leave);
		
		if(model.isActive()) {
			enter.setEnabled(false);
			leave.setEnabled(true);
			leave.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if(actionHandler != null)
						actionHandler.reportProgress(model.leave());
				}
			});
		} else {
			enter.setEnabled(true);
			leave.setEnabled(false);
			enter.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if(actionHandler != null)
						actionHandler.reportProgress(model.enter());
				}
			});
		}
	}

}
