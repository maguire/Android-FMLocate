package com.pfm.fmlocate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class FMLocateActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Capture our button from layout
		Button button = (Button) findViewById(R.id.searchBtn);
		// Register the onClick listener with the implementation above
		button.setOnClickListener(searchBtnListener);

	}

	// Create an anonymous implementation of OnClickListener
	private OnClickListener searchBtnListener = new OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent(FMLocateActivity.this, MediaResultsActivity.class);
			EditText et = (EditText) findViewById(R.id.entry);
			intent.putExtra(Intent.EXTRA_TEXT, et.getText().toString());
			startActivity(intent);
		}
	};

}
