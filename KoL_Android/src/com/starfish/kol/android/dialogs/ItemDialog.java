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
import com.starfish.kol.gamehandler.ViewContext;
import com.starfish.kol.model.elements.interfaces.DeferredGameAction;
import com.starfish.kol.model.elements.interfaces.Multiuseable;
import com.starfish.kol.model.models.inventory.InventoryAction;
import com.starfish.kol.model.models.inventory.InventoryActionVisitor;
import com.starfish.kol.model.models.inventory.InventoryItem;

public class ItemDialog extends DialogFragment {
	public static ItemDialog create(InventoryItem base) {
		ItemDialog dialog = new ItemDialog();
		Bundle args = new Bundle();
		args.putSerializable("item", base);
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
		View rootView = (View)inflater.inflate(R.layout.dialog_item_screen,
				container, false);

		InventoryItem item = (InventoryItem) this.getArguments().getSerializable("item");

	    ListAdapter<InventoryAction> adapter = new ListAdapter<InventoryAction>(this.getActivity(), item.getActions(), new DefaultBuilder<InventoryAction>());
	    
	    final InventoryActionVisitor visitor = new InventoryActionVisitor() {
			@Override
			public void executeRequest(DeferredGameAction action) {
				action.submit((ViewContext)getActivity());
			}

			@Override
			public void displayAutosell(Multiuseable item) {
				MultiUseDialog.create(item, "Sell").show(getFragmentManager(),
						"multiuseitem");
			}

			@Override
			public void displayMultiuse(Multiuseable item) {
				MultiUseDialog.create(item, "Use").show(getFragmentManager(),
						"multiuseitem");
			}	    	
	    };
	    
	    ListView list = (ListView)rootView.findViewById(R.id.dialog_item_list);
	    list.setAdapter(adapter);
	    list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> ad, View list, int pos,
					long arg3) {
				InventoryAction select = (InventoryAction)ad.getItemAtPosition(pos);
				
				if(select != null) {
					select.select(visitor);
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
