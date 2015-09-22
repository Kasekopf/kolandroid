package com.github.kolandroid.kol.android.controllers.inventory;

import android.view.View;
import android.widget.Button;

import com.github.kolandroid.kol.android.R;
import com.github.kolandroid.kol.android.controller.Controller;
import com.github.kolandroid.kol.android.controllers.MultiuseController;
import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.model.elements.ActionElement;
import com.github.kolandroid.kol.model.elements.MultiuseElement;
import com.github.kolandroid.kol.model.models.inventory.ClosetModel;

public class ClosetController extends ItemStorageController<ClosetModel> {
    /**
     * Autogenerated by eclipse.
     */
    private static final long serialVersionUID = -275856461187273887L;

    public ClosetController(ClosetModel model) {
        super(model, R.color.closet_header);
    }

    @Override
    public int getView() {
        return R.layout.closet_view;
    }

    @Override
    public void connect(View view, ClosetModel model, final Screen host) {
        super.connect(view, model, host);

        Button switchButton = (Button) view.findViewById(R.id.closet_switch_fill);
        final ActionElement changeState = model.getChangeState();
        switchButton.setText(changeState.getText());
        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeState.submit(host.getViewContext());
            }
        });

        Button meatButton = (Button) view.findViewById(R.id.closet_meat);
        final MultiuseElement manageMeat = model.getManageMeat();
        final String meatText = model.getMeatText();
        meatButton.setText(manageMeat.getText());
        meatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Controller controller = new MultiuseController(manageMeat, meatText);
                host.getViewContext().getPrimaryRoute().execute(controller);
            }
        });
    }
}
