package com.starfish.kol.android.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.starfish.kol.android.R;
import com.starfish.kol.android.util.ImageDownloader;
import com.starfish.kol.gamehandler.ViewContext;
import com.starfish.kol.model.elements.interfaces.DeferredGameAction;
import com.starfish.kol.model.elements.interfaces.MultiUseableItem;

public class MultiUseDialog extends DialogFragment {
	public static MultiUseDialog create(MultiUseableItem base, String buttonText) {
		MultiUseDialog dialog = new MultiUseDialog();
		Bundle args = new Bundle();
		args.putSerializable("item", base);
		args.putString("button", buttonText);
		dialog.setArguments(args);
		return dialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		return dialog;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View rootView = (View) inflater.inflate(
				R.layout.dialog_multiuse_screen, container, false);

		final MultiUseableItem item = (MultiUseableItem) this.getArguments()
				.getSerializable("item");

		TextView text = (TextView) rootView
				.findViewById(R.id.dialog_multiuse_name);
		text.setText(item.getText());

		ImageView img = (ImageView) rootView
				.findViewById(R.id.dialog_multiuse_image);
		if (item.getImage() == null || item.getImage().length() == 0) {
			img.setVisibility(View.GONE);
		} else {
			img.setVisibility(View.VISIBLE);
			ImageDownloader.loadFromUrl(img, item.getImage());
		}

		Button submit = (Button) rootView
				.findViewById(R.id.dialog_multiuse_submit);
		submit.setText(this.getArguments().getString("button"));
		
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				EditText number = (EditText) rootView
						.findViewById(R.id.dialog_multiuse_number);

				String num = number.getText().toString();
				if (num == null || num.length() == 0)
					return;

				DeferredGameAction action = item.use(num);
				action.submit((ViewContext)getActivity());

				MultiUseDialog.this.dismiss();
			}
		});
		return rootView;
	}
}
