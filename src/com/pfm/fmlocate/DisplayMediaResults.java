package com.pfm.fmlocate;

import java.util.ArrayList;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.yes.api.YesAPI;
import com.yes.api.model.MediaQuery;
import com.yes.api.model.Song;

public class DisplayMediaResults extends ListActivity {

	private ProgressDialog m_ProgressDialog = null;
	private ArrayList<Song> mediaResults = null;
	private ResultsAdapter m_adapter;
	private Runnable viewResults;

	private MyLocationListener myLocListener;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.results);

		/* start listening for GPS Location */
		String context = Context.LOCATION_SERVICE;
		LocationManager locationManager = (LocationManager) getSystemService(context);
		myLocListener = new MyLocationListener(locationManager,this);
		myLocListener.startListening();

		
		this.mediaResults = new ArrayList<Song>();

		LayoutInflater inflater = getLayoutInflater();
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		
		View header = inflater.inflate(R.layout.results_header, null);
		TextView txtView = (TextView) header.findViewById(R.id.location_text);
		txtView.setText(R.string.media_display_instructions);
		lv.addHeaderView(header);
		this.m_adapter = new ResultsAdapter(this, getLayoutInflater());
		
		setListAdapter(this.m_adapter);

		//when clicked, look up the song id in the media results, 
		// and get the postal code of the current location
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(DisplayMediaResults.this,
						DisplayStationResults.class);
				
				Song song = mediaResults.get(position);
				intent.putExtra("media_id", song.getId());
				Address address = myLocListener.getAddress();
				intent.putExtra("city_state", address.getLocality() + ", " + address.getAdminArea());
				intent.putExtra("zip_code", myLocListener.getAddress().getPostalCode());
				myLocListener.stopListening();
				startActivity(intent);
			}
		});

		// grab the results of the YesAPI query in a new thread, while putting up 
		// a progress dialog
		viewResults = new Runnable() {
			@Override
			public void run() {
				getResults(getIntent().getStringExtra(Intent.EXTRA_TEXT));
			}
		};
		Thread thread = new Thread(null, viewResults, "YesAPIBackground");
		thread.start();
		m_ProgressDialog = ProgressDialog.show(DisplayMediaResults.this,
				"Please wait...", "Retrieving Songs ...", true);
	}

	/**
	 * state handlers besides onCreate() 
	 */
	@Override
	protected void onDestroy() {
		myLocListener.stopListening();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		myLocListener.stopListening();
		super.onPause();
	}

	@Override
	protected void onResume() {
		myLocListener.startListening();
		super.onResume();
	}

	// to be run in the UI Thread to dismiss the progress dialog once
	// the results of the YesAPI call are added to the custom ResultsAdapter
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

	// perform the YesAPI call 
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
