package com.yes.api.model;

import java.util.ArrayList;

public class StationsQuery {

	private String _yes;
	
	private ArrayList<Station> _stations;

	private String _err;
	
	public void setYes(String _yes) {
		this._yes = _yes;
	}

	public String getYes() {
		return _yes;
	}

	public void setStations(ArrayList<Station> _stations) {
		this._stations = _stations;
	}

	public ArrayList<Station> getStations() {
		return _stations;
	}

	public void setErr(String _err) {
		this._err = _err;
	}

	public String getErr() {
		return _err;
	}
}
