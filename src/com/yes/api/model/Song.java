package com.yes.api.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Song implements Serializable {

	private String _by;
	private String _genre;
	private String _cover;
	private int _id;
	private int _artist;
	private int _rank;
	private String _title;
	private String _link;
	private String _video;
	
	private ArrayList<Integer> _similarSongIDs = new ArrayList<Integer>();
	
	public void setBy(String _by) {
		this._by = _by;
	}
	public String getBy() {
		return _by;
	}
	public void setGenre(String _genre) {
		this._genre = _genre;
	}
	public String getGenre() {
		return _genre;
	}
	public void setCover(String _cover) {
		this._cover = _cover;
	}
	public String getCover() {
		return _cover;
	}
	public void setId(int _id) {
		this._id = _id;
	}
	public int getId() {
		return _id;
	}
	public void setArtist(int _artist) {
		this._artist = _artist;
	}
	public int getArtist() {
		return _artist;
	}
	public void setRank(int _rank) {
		this._rank = _rank;
	}
	public int getRank() {
		return _rank;
	}
	public void setTitle(String _title) {
		this._title = _title;
	}
	public String getTitle() {
		return _title;
	}
	public void setLink(String _link) {
		this._link = _link;
	}
	public String getLink() {
		return _link;
	}
	public void setVideo(String _video) {
		this._video = _video;
	}
	public String getVideo() {
		return _video;
	}
	public void addSimilarSongID(int id) {
		this._similarSongIDs.add(id);
	}
	
	public ArrayList<Integer> getSimilarSongIDs(){
		return this._similarSongIDs;
	}


}
