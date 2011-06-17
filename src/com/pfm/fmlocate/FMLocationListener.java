package com.pfm.fmlocate;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class FMLocationListener implements LocationListener {
	private LocationManager myManager;
	private Location bestLocation;
	private Context context;

	public FMLocationListener(final LocationManager myManager,
			final Context context) {
		this.bestLocation = myManager.getLastKnownLocation(myManager
				.getBestProvider(new Criteria(), true));
		this.myManager = myManager;
		this.context = context;
	}

	/**********************************************************************
	 * helpers for starting/stopping monitoring of GPS changes below
	 **********************************************************************/
	public void startListening() {
		myManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,
				0, this);
		myManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
				this);
	}

	public void stopListening() {
		if (myManager != null)
			myManager.removeUpdates(this);
	}

	private static final int TWO_MINUTES = 1000 * 60 * 2;

	/**
	 * Determines whether one Location reading is better than the current
	 * Location fix
	 * 
	 * @param location
	 *            The new Location that you want to evaluate
	 * @param currentBestLocation
	 *            The current Location fix, to which you want to compare the new
	 *            one
	 */
	protected boolean isBetterLocation(Location location,
			Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use
		// the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be
			// worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider) {
			return true;
		}
		return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	/**********************************************************************
	 * LocationListener overrides below
	 **********************************************************************/
	@Override
	public void onLocationChanged(Location location) {
		if (this.isBetterLocation(location, this.bestLocation)) {
			this.bestLocation = location;
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	public Address getAddress() {
		Address address = new Address(Locale.getDefault());
		if (this.bestLocation != null) {
			double lat = this.bestLocation.getLatitude();
			double lng = this.bestLocation.getLongitude();
			Geocoder geo = new Geocoder(this.context);
			try {
				List<Address> addresses = geo.getFromLocation(lat, lng, 1);

				if (addresses != null && addresses.size() > 0) {
					address = addresses.get(0);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return address;
	}
}
