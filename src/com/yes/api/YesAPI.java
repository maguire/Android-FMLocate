package com.yes.api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.*;
import org.codehaus.jackson.map.ObjectMapper;

import com.yes.api.model.MediaQuery;
import com.yes.api.model.StationsQuery;

public class YesAPI {

	private static final int MAX_MEDIA_RESULTS = 50;

	private static final String API_URL = "http://api.yes.com";

	private static final String API_VERSION = "1";

	private final JsonFactory jsonFactory = new JsonFactory();

	private static final int MAX_LOCAL_STATIONS = 20;

	public YesAPI() {

	}

	public StationsQuery getLocalStationsByMID(final String zipCode,
			final Integer mid) {
		return this.getLocalStationsByMID(zipCode, mid, MAX_LOCAL_STATIONS);
	}

	public StationsQuery getLocalStationsByMID(final String zipCode,
			final Integer mid, final Integer max) {
		Map<String, String> args = new HashMap<String, String>() {
			{
				put("loc", zipCode);
				put("mid", mid.toString());
				put("max", max.toString());
			}
		};

		return this.query("stations", args, StationsQuery.class);
	}

	public MediaQuery getMedia(final String artistName) {
		return this.getMedia(artistName, MAX_MEDIA_RESULTS);
	}

	public MediaQuery getMedia(final String artistName, final Integer max) {
		Map<String, String> args = new HashMap<String, String>() {
			{
				put("q", artistName);
				put("max", max.toString());
			}
		};

		return query("media", args, MediaQuery.class);
	}

	private <T> T query(String apiCall, final Map<String, String> args, Class<T> cls) {
		String constructedURL = API_URL + "/" + API_VERSION + "/" + apiCall
				+ "?";

		for (Map.Entry<String, String> value : args.entrySet()) {
			constructedURL += value.getKey() + "=" + value.getValue() + "&";
		}
		try {
			System.out.println(constructedURL);
			URL url = new URL(constructedURL);
			
			ObjectMapper mapper = new ObjectMapper(jsonFactory);
			return mapper.readValue(url, cls);

		} catch (MalformedURLException urlExc) {
			System.out.println("We constructed a malformed API URL"
					+ constructedURL);
		} catch (IOException ioException) {
			System.out.println("Could not open URL" + constructedURL);
		}
		return null;
	}
}
