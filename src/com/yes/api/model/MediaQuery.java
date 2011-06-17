package com.yes.api.model;

import java.util.ArrayList;

public class MediaQuery {

	private String _yes;
	
	private ArrayList<Song> _songs;

	private String _err;
	
	public void setYes(String _yes) {
		this._yes = _yes;
	}

	public String getYes() {
		return _yes;
	}

	public void setSongs(ArrayList<Song> _songs) {
		this._songs = _songs;
	}

	public ArrayList<Song> getSongs() {
		return _songs;
	}

	public void setErr(String _err) {
		this._err = _err;
	}

	public String getErr() {
		return _err;
	}
	
}
