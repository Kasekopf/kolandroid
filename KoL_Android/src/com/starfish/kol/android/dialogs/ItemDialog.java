package com.starfish.kol.android.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.starfish.kol.android.R;
import com.starfish.kol.android.util.ImageDownloader;
import com.starfish.kol.android.util.adapters.ListAdapter;
import com.starfish.kol.android.util.listbuilders.DefaultBuilder;
import com.starfish.kol.android.util.searchlist.OnListSelection;
import com.starfish.kol.model.interfaces.ActionItem;
import com.starfish.kol.model.interfaces.ModelItem;
import com.starfish.kol.model.models.InventoryModel.InvItem;

public class ItemDialog extends DialogFragment {
	public static ItemDialog create(InvItem base) {
		ItemDialog dialog = new ItemDialog();
		Bundle args = new Bundle();
		args.putSerializable("item", base);
		dialog.setArguments(args);
		return dialog;
	}

	private OnListSelection<ActionItem> selector;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		return dialog;
	}

	public void setOnSelection(OnListSelection<ActionItem> select) {
		this.selector = select;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = (View)inflater.inflate(R.layout.dialog_item_screen,
				container, false);

		InvItem item = (InvItem) this.getArguments().getSerializable("item");

	    ListAdapter<ModelItem> adapter = new ListAdapter<ModelItem>(this.getActivity(), item.getActions(), new DefaultBuilder<ModelItem>());
	    
	    ListView list = (ListView)rootView.findViewById(R.id.dialog_item_list);
	    list.setAdapter(adapter);
	    list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> ad, View list, int pos,
					long arg3) {
				ActionItem select = (ActionItem)ad.getItemAtPosition(pos);
				if(select != null) {
					if(selector != null)
						selector.selectItem(select);
					ItemDialog.this.dismiss();
				}
			}
	    });
		
	    TextView text = (TextView)rootView.findViewById(R.id.dialog_item_text);
	    text.setText(item.getText());
	    
	    ImageView img = (ImageView)rootView.findViewById(R.id.dialog_item_image);
	    ImageDownloader.loadFromUrl(img, item.getImage());
		return rootView;
	}
}
