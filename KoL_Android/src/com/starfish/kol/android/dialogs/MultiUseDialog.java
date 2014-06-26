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
import com.starfish.kol.android.util.searchlist.OnListSelection;
import com.starfish.kol.model.Model;
import com.starfish.kol.model.interfaces.MultiUseableItem;

public class MultiUseDialog extends DialogFragment {
	public static MultiUseDialog create(final Model<?> context, final MultiUseableItem item) {
		MultiUseDialog dialog = MultiUseDialog.create(item);
		dialog.setOnSelection(new OnListSelection<String>() {
			@Override
			public boolean selectItem(String quantity) {
				item.use(context, quantity);
				return true;
			}
		});
		return dialog;
	}
	
	private static MultiUseDialog create(MultiUseableItem base) {
		MultiUseDialog dialog = new MultiUseDialog();
		Bundle args = new Bundle();
		args.putSerializable("item", base);
		dialog.setArguments(args);
		return dialog;
	}

	private OnListSelection<String> selector;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		return dialog;
	}

	public void setOnSelection(OnListSelection<String> select) {
		this.selector = select;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View rootView = (View)inflater.inflate(R.layout.dialog_multiuse_screen,
				container, false);

		MultiUseableItem item = (MultiUseableItem) this.getArguments().getSerializable("item");

	    TextView text = (TextView)rootView.findViewById(R.id.dialog_multiuse_name);
	    text.setText(item.getText());
	    
	    ImageView img = (ImageView)rootView.findViewById(R.id.dialog_multiuse_image);
		if(item.getImage() == null || item.getImage().length() == 0) {
			img.setVisibility(View.GONE);
		} else {
			img.setVisibility(View.VISIBLE);
			ImageDownloader.loadFromUrl(img, item.getImage());
		}
	    

	    Button submit = (Button)rootView.findViewById(R.id.dialog_multiuse_submit);
	    submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
		    	EditText number = (EditText)rootView.findViewById(R.id.dialog_multiuse_number);
		    	
		    	String num = number.getText().toString();
		    	if(num == null || num.length() == 0)
		    		return;
		    	
		    	if(selector != null) {
		    		selector.selectItem(num);
		    	}

				MultiUseDialog.this.dismiss();
			}	    	
	    });
		return rootView;
	}
}
