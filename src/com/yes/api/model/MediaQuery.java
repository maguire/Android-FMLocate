package com.yes.api.model;

import java.util.ArrayList;

public class MediaQuery {

	private String _yes;
	
	private ArrayList<Song> _songs;

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
	
}
