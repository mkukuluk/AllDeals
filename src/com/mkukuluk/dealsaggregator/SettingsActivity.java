/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.mkukuluk.dealsaggregator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Toast;

import com.mkukuluk.dealsaggregator.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


/**
 * Change settings
 */
public class SettingsActivity extends PreferenceActivity
        implements
            OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Loads the XML preferences file.
        LocationManager locationManager = (LocationManager)
                getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        double lat, lon;
        try {
            lat = location.getLatitude();
            lon = location.getLongitude();
        } catch (NullPointerException e) {
            lat = -1.0;
            lon = -1.0;
        }
//        lat = 24.473635;
//
//
//        lon = 54.36533;
        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(lat, lon, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size() > 0){
            if(addresses.get(0).getCountryCode().equalsIgnoreCase("US")){
                addPreferencesFromResource(R.xml.preferences);
            }else if(addresses.get(0).getCountryCode().equalsIgnoreCase("AE")){
                addPreferencesFromResource(R.xml.preferencesuae);
            }
//                System.out.println(addresses.get(0).getLocality());
            Toast.makeText(SettingsActivity.this, "Latitude " + lat + " Longitude " + lon + " " + addresses.get(0).getCountryCode(), Toast.LENGTH_LONG).show();
        }else{
            addPreferencesFromResource(R.xml.preferences);
            Toast.makeText(SettingsActivity.this, "Latitude "+lat+" Longitude "+lon+" ", Toast.LENGTH_LONG).show();
        }


        Preference preferences=findPreference("editUserSites");
        preferences.setIntent(new Intent(getBaseContext(), ManageMySitesActivity.class));

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Registers a callback to be invoked whenever a user changes a preference.
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Unregisters the listener set in onResume().
        // It's best practice to unregister listeners when your app isn't using them to cut down on
        // unnecessary system overhead. You do this in onPause().
        getPreferenceScreen()
                .getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    // Fires when the user changes a preference.
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Sets refreshDisplay to true so that when the user returns to the main
        // activity, the display refreshes to reflect the new settings.

        FetchDeals.refreshDisplay = true;
        finish();
    }
}
