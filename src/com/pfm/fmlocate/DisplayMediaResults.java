package com.pfm.fmlocate;

import java.util.ArrayList;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.yes.api.YesAPI;
import com.yes.api.model.MediaQuery;
import com.yes.api.model.Song;

public class DisplayMediaResults extends ListActivity {

	private ProgressDialog m_ProgressDialog = null;
	private ArrayList<Song> mediaResults = null;
	private ResultsAdapter m_adapter;
	private Runnable viewResults;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.media_results);

		String context = Context.LOCATION_SERVICE;
		LocationManager locationManager = (LocationManager) getSystemService(context);
		mll = new MyLocationListener(locationManager,this);
		mll.startListening();

		this.mediaResults = new ArrayList<Song>();
		this.m_adapter = new ResultsAdapter(this, getLayoutInflater());
		setListAdapter(this.m_adapter);

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(DisplayMediaResults.this,
						DisplayStationResults.class);
				
				Song song = mediaResults.get(position);
				intent.putExtra("media_id", song.getId());
				intent.putExtra("zip_code", mll.getAddress().getPostalCode());
				mll.stopListening();
				startActivity(intent);
			}
		});

		viewResults = new Runnable() {
			@Override
			public void run() {
				getResults(getIntent().getStringExtra(Intent.EXTRA_TEXT));
			}
		};
		Thread thread = new Thread(null, viewResults, "YesAPIBackground");
		thread.start();
		m_ProgressDialog = ProgressDialog.show(DisplayMediaResults.this,
				"Please wait...", "Retrieving data ...", true);
	}

	@Override
	protected void onDestroy() {
		mll.stopListening();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		mll.stopListening();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mll.startListening();
		super.onResume();
	}


	private Runnable returnRes = new Runnable() {

		@Override
		public void run() {
			if (mediaResults != null && mediaResults.size() > 0) {
				m_adapter.notifyDataSetChanged();
				for (int i = 0; i < mediaResults.size(); i++) {
					Song m = mediaResults.get(i);
					m_adapter.add(new Pair<String, String>(m.getBy(), m
							.getTitle()));
				}
			}
			m_ProgressDialog.dismiss();
			m_adapter.notifyDataSetChanged();
		}
	};
	private MyLocationListener mll;

	private void getResults(final String qString) {
		try {
			YesAPI yesapi = new YesAPI();
			MediaQuery results = yesapi.getMedia(qString);
			for (Song song : results.getSongs()) {
				mediaResults.add(song);
			}
			Log.i("ARRAY", "" + mediaResults.size());
		} catch (Exception e) {
			Log.e("BACKGROUND_PROC", e.getMessage());
		}
		runOnUiThread(returnRes);
	}

}
