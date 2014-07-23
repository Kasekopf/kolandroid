package com.starfish.kol.android.game.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.starfish.kol.android.R;
import com.starfish.kol.android.game.BaseGameFragment;
import com.starfish.kol.android.util.ProgressBar;
import com.starfish.kol.model.LiveMessage;
import com.starfish.kol.model.models.StatsModel;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that
 * contain this fragment must implement the
 * {@link StatsFragment.OnFragmentInteractionListener} interface to handle
 * interaction events. Use the {@link StatsFragment#newInstance} factory method
 * to create an instance of this fragment.
 * 
 */
public class StatsFragment extends BaseGameFragment<LiveMessage, StatsModel> {
	public StatsFragment() {
		super(R.layout.fragment_stats_small);
	}

	public void refresh() {
		if (getModel() != null)
			getModel().update();
	}

	public void showQuests() {
		if (this.getModel() != null)
			getModel().loadQuests();
	}

	public static interface StatsCallbacks {
		public void update(String username, String subtext);
	}

	private TextView txtMuscle;
	private TextView txtMyst;
	private TextView txtMoxie;

	private TextView txtAdv;
	private TextView txtMeat;

	private ProgressBar barHP;
	private ProgressBar barMP;

	@Override
	public void onCreateSetup(View view, StatsModel base,
			Bundle savedInstanceState) {

		txtMuscle = (TextView) view.findViewById(R.id.stats_small_muscle);
		txtMyst = (TextView) view.findViewById(R.id.stats_small_myst);
		txtMoxie = (TextView) view.findViewById(R.id.stats_small_moxie);

		barHP = (ProgressBar) view.findViewById(R.id.stats_small_hp);
		barMP = (ProgressBar) view.findViewById(R.id.stats_small_mp);

		txtAdv = (TextView) view.findViewById(R.id.stats_small_adv);
		txtMeat = (TextView) view.findViewById(R.id.stats_small_meat);

		this.refresh();

		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Log.i("StatsFragment", "Clicked!");
				getModel().loadFull();
			}
		});
	}

	@Override
	protected void recieveProgress(LiveMessage message) {
		StatsModel model = getModel();

		StatsCallbacks activity = (StatsCallbacks) getActivity();
		if (activity == null)
			return; // can occur during orientation events, etc.

		activity.update(model.getUsername(), model.getCharInfo());

		String muscle = model.getMuscle() + " (" + model.getMuscleBase() + ")";
		txtMuscle.setText(muscle);
		String myst = model.getMyst() + " (" + model.getMystBase() + ")";
		txtMyst.setText(myst);
		String moxie = model.getMoxie() + " (" + model.getMoxieBase() + ")";
		txtMoxie.setText(moxie);

		barHP.setProgress(model.getHP(), model.getHPBase());
		barMP.setProgress(model.getMP(), model.getMPBase());

		txtAdv.setText(model.getAdv() + "");
		txtMeat.setText(model.getMeat() + "");
	}
}
