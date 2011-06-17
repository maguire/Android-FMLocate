package com.yes.api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.*;
import org.codehaus.jackson.io.CharacterEscapes;
import org.codehaus.jackson.io.InputDecorator;
import org.codehaus.jackson.map.ObjectMapper;

import com.yes.api.model.MediaQuery;
import com.yes.api.model.Song;
import com.yes.api.model.StationsQuery;

public class YesAPI {

	private static final int MAX_MEDIA_RESULTS = 50;

	private static final String API_URL = "http://api.yes.com";

	private static final String API_VERSION = "1";

	private final JsonFactory jsonFactory = new JsonFactory();

	private static final int MAX_LOCAL_STATIONS = 20;

	public StationsQuery getLocalStationsByMID(final String zipCode,
			final Integer mid) throws MalformedURLException, IOException {
		return this.getLocalStationsByMID(zipCode, mid, MAX_LOCAL_STATIONS);
	}

	public StationsQuery getLocalStationsByMID(final String zipCode,
			final Integer mid, final Integer max) throws MalformedURLException,
			IOException {
		Map<String, String> args = new HashMap<String, String>() {
			{
				put("loc", zipCode);
				put("mid", mid.toString());
				put("max", max.toString());
			}
		};

		return this.query("stations", args, StationsQuery.class);
	}
	/**
	 * Returns a MediaQuery object that contains the results of a call to api.yes.com  
	 * @param mediaQuery the string to search the Yes DB for songs/artists that match
	 * @param removeDuplicates attempts to remove the duplicate song entries in the MediaQuery
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public MediaQuery getMedia(final String mediaQuery, boolean removeDuplicates)
			throws MalformedURLException, IOException {
		return this.getMedia(mediaQuery, MAX_MEDIA_RESULTS, removeDuplicates);
	}

	public MediaQuery getMedia(final String queryString, final Integer max,
			boolean removeDuplicates) throws MalformedURLException, IOException {
		Map<String, String> args = new HashMap<String, String>() {
			{
				put("q", queryString);
				put("max", max.toString());
			}
		};

		MediaQuery mediaQuery = query("media", args, MediaQuery.class);

		if (removeDuplicates) {
			mediaQuery = removeMediaQueryDuplicates(mediaQuery);
		}
		return mediaQuery;
	}

	/**
	 * Returns a MediaQuery with duplicates removed.
	 * a duplicate is defined as a song that when 
	 * 	_ the artist and title are concatenated  
	 *  - converted to lowercase
	 *  - removed non-alphanumeric characters
	 *  - removed secondary artists (eg. "featuring Rihanna") 
	 *  and compared to other strings (with the same above rules applied) are equal
	 *  
	 *  This method may not be particularly fast 
	 * @param mediaQuery
	 * @return
	 */
	private MediaQuery removeMediaQueryDuplicates(MediaQuery mediaQuery) {
		//TODO look into speeding up the checking of duplicates
		//TODO "w/" does not work 
		HashMap<String, Song> artistMediaID = new HashMap<String, Song>();
		ArrayList<Song> nonDuplicateSongs = new ArrayList<Song>();
		String nonAlphanumericRegEx = "[^a-z0-9]";
		String featRegEx = "(?:ft|feat|featuring|with|w/).+$";
		for (Song song : mediaQuery.getSongs()) {
			String lowercaseArtist = song.getBy().toLowerCase().replaceAll(
					nonAlphanumericRegEx, "").replaceAll(
					featRegEx, "");
			String lowercaseTitle = song.getTitle().toLowerCase().replaceAll(
					nonAlphanumericRegEx, "").replaceAll(
					featRegEx, "");
			Song existingSong = artistMediaID.get(lowercaseArtist
					+ lowercaseTitle);
			if (existingSong == null) {
				artistMediaID.put(lowercaseArtist + lowercaseTitle, song);
				nonDuplicateSongs.add(song);
			} else {
				existingSong.addSimilarSongID(song.getId());
			}
		}
		mediaQuery.setSongs(nonDuplicateSongs);
		return mediaQuery;
	}

	/**
	 * Returns a object of the type of class that is passed in by the cls variable 
	 * by populating it with results from the Yes API and converting the json to object
	 * @param <T>
	 * @param apiCall
	 * @param args
	 * @param cls
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private <T> T query(String apiCall, final Map<String, String> args,
			Class<T> cls) throws MalformedURLException, IOException {
		String constructedURL = API_URL + "/" + API_VERSION + "/" + apiCall
				+ "?";

		for (Map.Entry<String, String> value : args.entrySet()) {
			constructedURL += value.getKey() + "="
					+ URLEncoder.encode(value.getValue()) + "&";
		}

		URL url = new URL(constructedURL);
		
		ObjectMapper mapper = new ObjectMapper(jsonFactory);
		return mapper.readValue(url, cls);

	}
}
