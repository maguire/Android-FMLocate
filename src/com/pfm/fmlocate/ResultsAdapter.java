package com.pfm.fmlocate;

import java.util.ArrayList;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ResultsAdapter extends ArrayAdapter<Pair<String, String>> {

	private final LayoutInflater layoutInflater;
	private static final int textViewResourceId = R.layout.row;
	
	public ResultsAdapter(Context context, LayoutInflater vi) {
		super(context, textViewResourceId, new ArrayList<Pair<String,String>>());
		this.layoutInflater = vi;
		
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			v = this.layoutInflater.inflate(textViewResourceId, null);
		}
		Pair<String,String> item = this.getItem(position);
		if (item != null) {
			TextView tt = (TextView) v.findViewById(R.id.toptext);
			TextView bt = (TextView) v.findViewById(R.id.bottomtext);
			
			if (tt != null) {
				tt.setText(Html.fromHtml(item.first).toString());
			}
			if (bt != null) {
				bt.setText(Html.fromHtml(item.second).toString());
			}
		}
		return v;
	}
}
