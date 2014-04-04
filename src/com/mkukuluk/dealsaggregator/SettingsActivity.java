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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.mkukuluk.dealsaggregator.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * Change settings
 */
public class SettingsActivity extends PreferenceActivity
        implements
            OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        SharedPreferences prefs = getSharedPreferences("com.mkukuluk.dealaggregator.usersite", Context.MODE_PRIVATE);
//        Map<String, ?> tempMap = new HashMap();
//        tempMap = prefs.getAll();
//        Iterator it = tempMap.entrySet().iterator();
//        if(tempMap.size()==0){



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
        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(lat, lon, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        if (addresses.size() > 0){
//            if(addresses.get(0).getCountryCode().equalsIgnoreCase("US")){
//                addPreferencesFromResource(R.xml.preferences);
//            }else if(addresses.get(0).getCountryCode().equalsIgnoreCase("AE")){
//                addPreferencesFromResource(R.xml.preferencesuae);
//            }
////            Toast.makeText(SettingsActivity.this, "Latitude " + lat + " Longitude " + lon + " " + addresses.get(0).getCountryCode(), Toast.LENGTH_LONG).show();
//        }else{
//            addPreferencesFromResource(R.xml.preferences);
//
////            Toast.makeText(SettingsActivity.this, "Latitude "+lat+" Longitude "+lon+" ", Toast.LENGTH_LONG).show();
//        }

        addPreferencesFromResource(R.xml.preferences);
        Preference preferences=findPreference("editUserSites");
        preferences.setIntent(new Intent(getBaseContext(), ManageMySitesActivity.class));

//        }
//        else {
//            ArrayList<String> siteNames = new ArrayList<String>();
//            ArrayList<String> siteURLs = new ArrayList<String>();
//            while(it.hasNext()){
//                Map.Entry pairs = (Map.Entry)it.next();
//                siteNames.add(pairs.getValue().toString());
//                siteURLs.add(pairs.getKey().toString());
//
//
//            }
//            //Test code
//            PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(this);
//
//            PreferenceCategory category = new PreferenceCategory(this);
//            category.setTitle("category name");
//
//            screen.addPreference(category);
//
//            ListPreference searchPref = new ListPreference(this);
//            searchPref.setTitle("Search Option");
//            searchPref.setSummary("Turn the Search box ON or OFF");
//            searchPref.setKey("searchPref");
//            searchPref.setDefaultValue("detailsON");
//            String[] temp = getResources().getStringArray(R.array.searchArray);
//            String[] tempVals = getResources().getStringArray(R.array.searchValues);
//            searchPref.setEntries(temp);
//            searchPref.setEntryValues(tempVals);
//
//
//            String[] urlNames = new String[siteNames.size()];
//            siteNames.toArray(urlNames);
//            String[] urlArray = new String[siteURLs.size()];
//            siteURLs.toArray(urlArray);
//
//
//            MultiSelectListPreference searchPref2 = new MultiSelectListPreference(this);
//            searchPref2.setTitle("Sites");
//            searchPref2.setSummary("Sites");
//            searchPref2.setKey("msSitePref");
//            searchPref2.setEntries(urlNames);
//            searchPref2.setEntryValues(urlArray);
//
//            Intent intent = new Intent(this, ManageMySitesActivity.class);
//            Preference pref = new Preference(this);
//            pref.setTitle("Manage sites");
//            pref.setSummary("Manage Sites");
//            pref.setKey("editUserSites");
//            pref.setIntent(intent);
//
//
//            category.addPreference(searchPref);
//            category.addPreference(searchPref2);
//            category.addPreference(pref);
//
//
//            setPreferenceScreen(screen);
//            FetchDeals.refreshDisplay = true;
////            finish();
//
//        }
        //end test code

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


    //Help screen menu
    // Populates the activity's options menu.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settingscreenmenu, menu);
        return true;
    }

    // Handles the user's menu selection.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.help:
                Intent mainActivity = new Intent(getBaseContext(), SettingsHelpActivity.class);
                startActivity(mainActivity);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
