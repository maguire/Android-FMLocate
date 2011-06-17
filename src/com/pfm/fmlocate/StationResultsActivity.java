package com.pfm.fmlocate;

import java.util.ArrayList;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yes.api.YesAPI;
import com.yes.api.model.Song;
import com.yes.api.model.Station;
import com.yes.api.model.StationsQuery;

public class StationResultsActivity extends ListActivity {
	private static final String TAG = "StationResultsActivity";
	
	private ProgressDialog m_ProgressDialog = null;
	private ArrayList<Station> stationResults = null;
	private ResultsAdapter m_adapter;
	private Runnable viewResults;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.results);

		this.stationResults = new ArrayList<Station>();

		LayoutInflater inflater = getLayoutInflater();
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		final Intent intent = getIntent();
		final Song currentSong = (Song) intent.getSerializableExtra("selected_song");
		if (currentSong == null){
			Log.e(TAG,"No Song was passed through from the previous activity.");
			return;
		}
		View header = inflater.inflate(R.layout.results_header, null);
		TextView cityTextView = (TextView) header
				.findViewById(R.id.location_text);
		String headerViewTxt = "Stations that play "
				+ Html.fromHtml(currentSong.getTitle()).toString() + " by "
				+ Html.fromHtml(currentSong.getBy()) + "\n"
				+ getIntent().getStringExtra("city_state");
		cityTextView.setText(headerViewTxt);
		lv.addHeaderView(header);

		this.m_adapter = new ResultsAdapter(this, inflater);
		setListAdapter(this.m_adapter);

		viewResults = new Runnable() {
			@Override
			public void run() {
				getResults(intent.getStringExtra("zip_code"), currentSong.getId(),currentSong.getSimilarSongIDs());
			}
		};
		Thread thread = new Thread(null, viewResults, "YesAPIBackground");
		thread.start();
		m_ProgressDialog = ProgressDialog.show(StationResultsActivity.this,
				"Please wait...", "Retrieving Stations ...", true);
	}

	private Runnable returnRes = new Runnable() {

		@Override
		public void run() {
			if (stationResults != null && stationResults.size() > 0) {
				m_adapter.notifyDataSetChanged();
				for (int i = 0; i < stationResults.size(); i++) {
					Station m = stationResults.get(i);
					m_adapter.add(new Pair<String, String>(m.getName(), m
							.getDesc()));
				}
			}
			m_ProgressDialog.dismiss();
			m_adapter.notifyDataSetChanged();
		}
	};
	


	private void getResults(final String zipCode, final Integer mediaID, ArrayList<Integer> similarMediaIDs) {
		try {
			YesAPI yesapi = new YesAPI();
			if (zipCode != null) {
				StationsQuery exactResults = yesapi.getLocalStationsByMID(zipCode,
						mediaID);
				if (exactResults != null){
					stationResults.addAll(exactResults.getStations());
				}
				if (similarMediaIDs.size() > 0) {

					for (Integer mid : similarMediaIDs) {
						StationsQuery similarResults = yesapi.getLocalStationsByMID(zipCode,
								mid);
						stationResults.addAll(similarResults.getStations());
					}
				}
				Log.i("ARRAY", "" + stationResults.size());
			} else {
				throw new NullPointerException("No Zip Code Found");
			}
		} catch (final Exception e) {
			Runnable errorToast = new Runnable(){
				@Override
				public void run() {	
					Toast.makeText(StationResultsActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
				}
			};
			
			runOnUiThread(errorToast);
			Log.e("BACKGROUND_PROC", e.getMessage());
		}
		runOnUiThread(returnRes);
	}
}
