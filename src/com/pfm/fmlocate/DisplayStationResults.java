package com.pfm.fmlocate;

import java.util.ArrayList;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.yes.api.YesAPI;
import com.yes.api.model.Station;
import com.yes.api.model.StationsQuery;

public class DisplayStationResults extends ListActivity {
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

		View header = inflater.inflate(R.layout.results_header, null);
		TextView cityTextView = (TextView) header.findViewById(R.id.location_text);
		String cityTextViewTxt = "Stations for "  + getIntent().getStringExtra("city_state");
		cityTextView.setText(cityTextViewTxt);
		lv.addHeaderView(header);

		this.m_adapter = new ResultsAdapter(this, inflater);
		setListAdapter(this.m_adapter);

		viewResults = new Runnable() {
			@Override
			public void run() {
				getResults(getIntent().getStringExtra("zip_code"), getIntent()
						.getIntExtra("media_id", 0));
			}
		};
		Thread thread = new Thread(null, viewResults, "YesAPIBackground");
		thread.start();
		m_ProgressDialog = ProgressDialog.show(DisplayStationResults.this,
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

	private void getResults(final String zipCode, final Integer mediaID) {
		try {
			YesAPI yesapi = new YesAPI();
			StationsQuery results = yesapi.getLocalStationsByMID(zipCode,
					mediaID);
			if (results != null) {

				for (Station station : results.getStations()) {
					stationResults.add(station);
				}
				Log.i("ARRAY", "" + stationResults.size());
			}
			else {
				throw new Exception("No Results were found");
			}
		} catch (Exception e) {
			Log.e("BACKGROUND_PROC", e.getMessage());
		}
		runOnUiThread(returnRes);
	}
}
