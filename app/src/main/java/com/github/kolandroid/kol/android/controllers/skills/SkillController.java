package com.github.kolandroid.kol.android.controllers.skills;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.kolandroid.kol.android.R;
import com.github.kolandroid.kol.android.controller.ModelController;
import com.github.kolandroid.kol.android.controllers.web.WebController;
import com.github.kolandroid.kol.android.screen.Screen;
import com.github.kolandroid.kol.android.screen.ScreenSelection;
import com.github.kolandroid.kol.android.screen.ViewScreen;
import com.github.kolandroid.kol.android.util.ImageDownloader;
import com.github.kolandroid.kol.model.models.WebModel;
import com.github.kolandroid.kol.model.models.skill.SkillModel;

public class SkillController extends ModelController<SkillModel> {
    /**
     * Autogenerated by eclipse.
     */
    private static final long serialVersionUID = 8802030177173483415L;

    public SkillController(SkillModel model) {
        super(model);
    }

    @Override
    public int getView() {
        return R.layout.skill_view;
    }

    @Override
    public void connect(final View view, SkillModel model, final Screen host) {
        WebModel description = model.getDescription();
        if (description == null) {
            // Display image/name of the item as a backup

            TextView text = (TextView) view.findViewById(R.id.skill_name);
            text.setText(model.getText());
            text.setVisibility(View.VISIBLE);

            TextView subtext = (TextView) view.findViewById(R.id.skill_cost);
            subtext.setText(model.getSubtext());
            subtext.setVisibility(View.VISIBLE);

            if (model.getImage() != null && !model.getImage().equals("")) {
                ImageView img = (ImageView) view.findViewById(R.id.skill_image);
                img.setVisibility(View.VISIBLE);
                ImageDownloader.loadFromUrl(img, model.getImage());
            }
        } else {
            ViewScreen desc = (ViewScreen) view.findViewById(R.id.dialog_skill_description);
            desc.display(new WebController(description), host);
        }

        if (!model.isBuff()) {
            View player = view.findViewById(R.id.skill_player_input);
            View playerLabel = view.findViewById(R.id.skill_player_input_label);
            player.setVisibility(View.GONE);
            playerLabel.setVisibility(View.GONE);
        }

        Button submit = (Button) view.findViewById(R.id.skill_submit);
        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                EditText number = (EditText) view.findViewById(R.id.skill_number_input);
                EditText player = (EditText) view.findViewById(R.id.skill_player_input);

                String num = number.getText().toString();
                String play = player.getText().toString();
                if (num.equals(""))
                    return;
                if (play.equalsIgnoreCase("(yourself)"))
                    play = "";
                getModel().cast(num, play);
                host.close();
            }
        });
    }

    @Override
    public void chooseScreen(ScreenSelection choice) {
        choice.displayDialog(this);
    }

}
