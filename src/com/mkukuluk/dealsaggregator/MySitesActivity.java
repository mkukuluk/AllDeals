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

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import android.app.ListActivity;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;


/**
 * Main class that gets the urls and fetches the rss feeds.
 */
public class MySitesActivity extends ListActivity {
    public static final String WIFI = "Wi-Fi";
    public static final String ANY = "Any";

    //variables for sites to be pulled

    public static final String woot = "Woot";
    public static final String slickdeals = "Slick Deals";
    public static final String doa = "Deals Of America";
    public static final String all = "All";
    public static final String fatwallet = "Fat Wallet";
    public static final String deals2buy = "Deals2Buy";
    public static final String tigerDirect = "Tiger Direct";

    //all the URLs here
    public static final String dealsOfAmericaSite = "http://www.dealsofamerica.com/arss.xml";
    public static final String wootSite = "http://api.woot.com/1/sales/current.rss";
    public static final String slickDealsSite = "http://feeds.feedburner.com/SlickdealsnetFP?format=xml";
    public static final String fatWalletSite = "http://feeds2.feedburner.com/Fatwallet?format=xml";
    public static final String deals2BuySite = "http://feeds2.feedburner.com/deals2buy?format=xml";
    public static final String tigerDirectSite = "http://www.tigerdirect.com/email/retro/rss.xml";

    public static StringBuffer sitePrefString = new StringBuffer();


    private static  ArrayList<String> URLs = new ArrayList<String>();

    private  String dealInitial = "";


    private static Set multiSitePref= new HashSet();

    private static String detailsPref = "";

    private static String newUserSite = "";


    // Whether there is a Wi-Fi connection.
    private static boolean wifiConnected = false;
    // Whether there is a mobile connection.
    private static boolean mobileConnected = false;
    // Whether the display should be refreshed.
    public static boolean refreshDisplay = true;

    // The user's current network preference setting.
//    public static String sPref = null;

    //Search box pref
    public static String searchBoxPref = null;
    //site preferences
//    public static String sitesPref = null;
    // The BroadcastReceiver that tracks network connectivity changes.
    private NetworkReceiver receiver = new NetworkReceiver();

    private List <ParseXMLFeeds.Entry>entryList = new ArrayList<ParseXMLFeeds.Entry>();

    private List<String>dealList = new ArrayList<String>();
    private List<String>dealURLList = new ArrayList<String>();
    private List<String>dealListDetails = new ArrayList<String>();
    private HashMap dealMap = new HashMap();


    private ArrayList sizeList = new ArrayList();
    private ArrayList nameList = new ArrayList();

    private ArrayAdapter arrayAdapter ;

    private ShareActionProvider mShareActionProvider;


    EditText inputSearch= null;


    private static List<String>faveList = new ArrayList<String>();
    SharedPreferences prefs;
    private AdView adView;

    //Variables for restoring list position.
    int index = 0;
    int top = 0;




    @Override
    public void onCreate(Bundle savedInstanceState) {
//        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//                .detectAll() // or .detectAll() for all detectable problems
//                .penaltyLog()
//                .build());
//        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//                .detectLeakedSqlLiteObjects()
//                .detectLeakedClosableObjects()
//                .penaltyLog()
//                .penaltyDeath()
//                .build());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.players);

        // Register BroadcastReceiver to track connection changes.
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);
        index = 0;
        top = 0;


    }


    // Refreshes the display if the network connection and the
    // pref settings allow it.
    @Override
    public void onStart() {

        super.onStart();
        entryList = new ArrayList<ParseXMLFeeds.Entry>();
        dealList.clear();
        dealURLList.clear();
        dealList = new ArrayList<String>();
        dealURLList = new ArrayList<String>();

        dealMap.clear();
        dealMap = new HashMap();

        URLs = new ArrayList<String>();
        sizeList = new ArrayList();
        nameList = new ArrayList();

        sitePrefString = new StringBuffer();

        // Gets the user's network preference settings

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Retrieves a string value for the preferences. The second parameter
        // is the default value to use if a preference value is not found.

        searchBoxPref = sharedPrefs.getString("searchPref", "ON");
        multiSitePref = sharedPrefs.getStringSet("msSitePref", null);
        detailsPref = sharedPrefs.getString("detailsPref", "detailsON");
        newUserSite = sharedPrefs.getString("newSite", "");
        updateConnectedFlags();


        if (refreshDisplay) {
            entryList = new ArrayList<ParseXMLFeeds.Entry>();
            URLs = new ArrayList<String>();
            sizeList = new ArrayList();
            nameList = new ArrayList();

            sitePrefString = new StringBuffer();
            dealList = new ArrayList<String>();
            dealURLList = new ArrayList<String>();

            dealMap = new HashMap();

            if(inputSearch!=null){
                inputSearch.setText("");
            }
//            dealList.clear();
//            dealURLList.clear();


            sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
            multiSitePref = sharedPrefs.getStringSet("msSitePref",null);
            detailsPref = sharedPrefs.getString("detailsPref", "detailsON");


            loadPage();
            arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_activated_1,dealList);

            // setListAdapter(arrayAdapter);
            ListView listView = getListView();
            listView.setAdapter(arrayAdapter);

            //experimental - search box?
//            setContentView(R.layout.main);
            if(searchBoxPref.equals("ON")){
                inputSearch = (EditText) findViewById(R.id.inputSearch1);
                inputSearch.setVisibility(View.VISIBLE);

                inputSearch.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                        // When user changed the Text
                        arrayAdapter.getFilter().filter(cs);
                    }

                    @Override
                    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                                  int arg3) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void afterTextChanged(Editable arg0) {
                        // TODO Auto-generated method stub
                    }
                });
            }
            else{
                inputSearch = (EditText) findViewById(R.id.inputSearch1);
                inputSearch.setVisibility(View.INVISIBLE);
            }
            //experimental - search box?

            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            listView.setMultiChoiceModeListener(new ModeCallback());
            listView.setBackgroundResource(R.drawable.shape);
            int[] colors = {0, 0xffffff00, 0};
            listView.setDivider(new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, colors));
            listView.setDividerHeight(2);


            AdView adView = new AdView(this, AdSize.SMART_BANNER, "a152b47c1cca8a4");
            AdRequest request = new AdRequest();
            //request.addTestDevice("241F8D71AA9866EE0EA185442F777C5F");
            adView.loadAd(request);

        }
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        String temp = ((TextView) v).getText().toString();
        index = l.getFirstVisiblePosition();
        View w = l.getChildAt(0);
        top = (w == null) ? 0 : w.getTop();
        String urlofdeal = "";
        if(inputSearch.getText().toString().length() > 0){
            String segments[] = temp.split(" - ");

            String tempdl = temp.replace(segments[0] + " - ", "");
            urlofdeal = (String) dealMap.get(tempdl);


        }else{
            urlofdeal = dealURLList.get(position);
        }

        Uri feedUri = Uri.parse(urlofdeal);
        Intent myIntent = new Intent(Intent.ACTION_VIEW, feedUri);
        startActivity(myIntent);

    }


    private class ModeCallback implements ListView.MultiChoiceModeListener {

        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.list_select_menu, menu);
            MenuItem item = menu.findItem(R.id.menu_item_share);

            // Fetch and store ShareActionProvider
            mShareActionProvider = (ShareActionProvider) item.getActionProvider();
            mode.setTitle("Select Items");
            return true;
        }

        private void setShareIntent(Intent shareIntent) {
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(shareIntent);
            }
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            SparseBooleanArray checkedItems = getListView().getCheckedItemPositions();
            int len = dealList.size();
            switch (item.getItemId()) {

                case R.id.addToFave:

                    if(inputSearch.getText().toString().length()>0){
                        Toast.makeText(MySitesActivity.this, "Feature unavailable for the time being. Please clear search and try. ", Toast.LENGTH_LONG).show();

                    }
                    else
                    {
                        SharedPreferences prefs = getSharedPreferences("com.mkukuluk.dealaggregator.faves", Context.MODE_PRIVATE);

                        //we need to check if the same deal has been added with a shorter line length setting
                        Map<String, ?> temp = new HashMap();
                        temp = prefs.getAll();
                        Iterator it = temp.entrySet().iterator();
                        if(temp.size()>0){
                            while(it.hasNext()){
                                Map.Entry pairs = (Map.Entry)it.next();


                            }
                        }

                        //end check



                        SharedPreferences.Editor editor = prefs.edit();

                        Boolean addDealToFave = true;

                        for (int i = 0; i < len; i++){
                            if (checkedItems.get(i)) {
                                String segments[] = dealList.get(i).split(" - ");

                                String tempdl = dealList.get(i).replace(segments[0] + " - ","");

                                if(temp.size()>0){
                                    while(it.hasNext()){
                                        Map.Entry pairs = (Map.Entry)it.next();
                                        if(pairs.getKey().toString().contains(tempdl)){
                                            addDealToFave = false;
                                        }else if(tempdl.contains(pairs.getKey().toString())){
                                            addDealToFave = false;
                                        }


                                    }
                                }

                                if(addDealToFave){
                                    editor.putString(tempdl, dealURLList.get(i));
                                    faveList.add(tempdl);
                                }

                            }

                        }
                        editor.commit();
                        Toast.makeText(MySitesActivity.this, "Added " + getListView().getCheckedItemCount() +
                                " items to Favorites", Toast.LENGTH_SHORT).show();

                        mode.finish();
                    }
                    break;

                //share
                case R.id.menu_item_share:

                    //get all the checked items and put them in a string builder:
                    StringBuilder allCheckedDeals = new StringBuilder();
                    for (int i = 0; i < len; i++)
                        if (checkedItems.get(i)) {
                            allCheckedDeals.append(dealList.get(i).substring(dealList.get(i).lastIndexOf(Html.fromHtml("&#9829;").toString()+"   ")+1).trim()+" - "+dealURLList.get(i)+"\n\n");

                        }

                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out these great deals from 'All Deals' Android App (https://play.google.com/store/apps/details?id=com.mkukuluk.dealsaggregator) \n\n"+allCheckedDeals);
                    sendIntent.setType("text/plain");
                    startActivity(Intent.createChooser(sendIntent, "How do you want to share?"));
                    break;
                default:
                    Toast.makeText(MySitesActivity.this, "Clicked " + item.getTitle(),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
            return true;
        }


        public void onDestroyActionMode(ActionMode mode) {
        }

        public void onItemCheckedStateChanged(ActionMode mode,
                                              int position, long id, boolean checked) {
            final int checkedCount = getListView().getCheckedItemCount();
            switch (checkedCount) {
                case 0:
                    mode.setSubtitle(null);
                    break;
                case 1:
                    mode.setSubtitle("One item selected");
                    break;
                default:
                    mode.setSubtitle("" + checkedCount + " items selected");
                    break;
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            this.unregisterReceiver(receiver);
        }
    }

    // Checks the network connection and sets the wifiConnected and mobileConnected
    // variables accordingly.
    private void updateConnectedFlags() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        } else {
            wifiConnected = false;
            mobileConnected = false;
        }
    }


    private void loadPage() {
        if (wifiConnected || mobileConnected) {

            // AsyncTask subclass

            //select only sites that are selected.

            //multi
            URLs.clear();
            sizeList.clear();
            nameList.clear();

            //end multi


            SharedPreferences prefs = getSharedPreferences("com.mkukuluk.dealaggregator.usersite", Context.MODE_PRIVATE);
            Map<String, ?> temp = new HashMap();
            temp = prefs.getAll();
            Iterator it = temp.entrySet().iterator();
            if(temp.size()==0){
                Toast.makeText(MySitesActivity.this, "No User Defined Feeds. Go to Menu > Settings > Manage Your Site Feeds", Toast.LENGTH_LONG).show();
                finish();
            }
            while(it.hasNext()){
                Map.Entry pairs = (Map.Entry)it.next();
                    URLs.add(pairs.getValue().toString());
                nameList.add(pairs.getKey().toString());


            }


            for(int i =0; i < URLs.size(); i ++){

                new DownloadXmlTask().execute(URLs.get(i));



            }


        }
        else {
            showErrorPage();
        }
    }

    // Displays an error if the app is unable to load content.
    private void showErrorPage() {
        setContentView(R.layout.main);

        // The specified network connection is not available. Displays error message.
        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.loadData(getResources().getString(R.string.connection_error),
                "text/html", null);
    }



    // Populates the activity's options menu.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    // Handles the user's menu selection.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.displayFaves:
                Intent displayfavesActivity = new Intent(getBaseContext(), DisplayFavesActivity.class);
                startActivity(displayfavesActivity);

                return true;

            case R.id.mySites:
                Intent mySitesActivity = new Intent(getBaseContext(), MySitesActivity.class);
                startActivity(mySitesActivity);
                return true;
            case R.id.settings:

                Intent settingsActivity = new Intent(getBaseContext(), SettingsActivity.class);
                startActivity(settingsActivity);
                return true;
            case R.id.refresh:

                refreshDisplay = true;
                entryList = new ArrayList<ParseXMLFeeds.Entry>();
                URLs = new ArrayList<String>();
                sizeList = new ArrayList();
                nameList = new ArrayList();

                sitePrefString = new StringBuffer();
                dealList = new ArrayList<String>();
                dealURLList = new ArrayList<String>();
                dealMap = new HashMap();
                dealList.clear();
                dealURLList.clear();
                dealMap.clear();
                loadPage();
                return true;

            case R.id.help:
//                isFaves = false;
//                showHelpPage();
//                return true;
                Intent helpActivity = new Intent(getBaseContext(), MySitesHelpActivity.class);
                startActivity(helpActivity);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Implementation of AsyncTask used to download XML feed from deal sites.
    private class DownloadXmlTask extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog = new ProgressDialog(MySitesActivity.this);


        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait while we grab all your deals...");
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            try {


                dealList.clear();
                dealURLList.clear();
                dealMap.clear();
                return loadXmlFromNetwork(urls[0]);


            } catch (IOException e) {
                return getResources().getString(R.string.connection_error);
            } catch (XmlPullParserException e) {
                return getResources().getString(R.string.xml_error);
            }
        }

        @Override
        protected void onPostExecute(String result) {

            //new code

            setListAdapter(arrayAdapter);
            ListView listView = getListView();

            listView.setSelectionFromTop(index, top);
            listView.setTextFilterEnabled(true);
            listView.setBackgroundResource(R.drawable.shape);
            //end new code



            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }


    }

    // Uploads XML from deal sites, parses it, and combines it with
    // HTML markup. Returns HTML string.
    private String loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        ParseXMLFeeds parseXMLFeeds = new ParseXMLFeeds();
        List<ParseXMLFeeds.Entry> entries = null;
        String title = null;
        String url = null;
        String summary = null;
        Calendar rightNow = Calendar.getInstance();
        DateFormat formatter = new SimpleDateFormat("MMM dd h:mmaa");


        // Checks whether the user set the preference to include summary text
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
//        boolean pref = sharedPrefs.getBoolean("summaryPref", false);

        StringBuilder htmlString = new StringBuilder();
        htmlString.append("<h3>" + getResources().getString(R.string.page_title) + " "+sitePrefString+" "+"</h3>");
        htmlString.append("<em>" + getResources().getString(R.string.updated) + " " +
                formatter.format(rightNow.getTime()) + "</em>");

        try {
            stream = downloadUrl(urlString);
            entries = parseXMLFeeds.parse(stream);
            sizeList.add(entries.size());
            entryList.addAll(entries);

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        // ParseXMLFeeds returns a List (called "entries") of Entry objects.
        // Each Entry object represents a single post in the XML feed.
        // This section processes the entries list to combine each entry with HTML markup.
        // Each entry is displayed in the UI as a link that optionally includes
        // a text summary.
        int i = 0;
        int j = 0;
        for (ParseXMLFeeds.Entry entry : entryList) {
            Integer x= (Integer) sizeList.get(i);
            String y = (String) nameList.get(i);
            if(j < x ){
                dealInitial = y;
            }else{
                i++;
                j = 0;
                x = 0;
                dealInitial = y;
            }
            htmlString.append(entry.title.toString());
            if(detailsPref.equals("detailsOFF")){
                if(entry.title.toString().length() > 40){
                    dealList.add(dealInitial+" - "+entry.title.toString().substring(0,40));
                }
                else{
                    dealList.add(dealInitial+" - "+entry.title.toString());
                }
            }else{
                dealList.add(dealInitial+" - "+entry.title.toString());
            }
            dealURLList.add(entry.link.toString());
            dealMap.put(entry.title.toString(),entry.link.toString());

            j++;
        }
        return htmlString.toString();
    }

    // Given a string representation of a URL, sets up a connection and gets
    // an input stream.
    private InputStream downloadUrl(String urlString) throws IOException {

        InputStream in = null;
        try{
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            in = conn.getInputStream();

        }catch(Exception e){

            AssetManager assetManager = getAssets();

            try {
                in = assetManager.open("dealswoot.xml");
//                return in;
            } catch (IOException ioe) {
                Log.e("tag", ioe.getMessage());
            }
        }

        return in;

    }

    /**
     *
     * This BroadcastReceiver intercepts the android.net.ConnectivityManager.CONNECTIVITY_ACTION,
     * which indicates a connection change. It checks whether the type is TYPE_WIFI.
     * If it is, it checks whether Wi-Fi is connected and sets the wifiConnected flag in the
     * main activity accordingly.
     *
     */
    public class NetworkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connMgr =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if(networkInfo != null){
                refreshDisplay = true;
            }else{
                refreshDisplay = false;
            }
//            if (WIFI.equals(sPref) && networkInfo != null
//                    && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
//
//                refreshDisplay = true;
//                Toast.makeText(context, R.string.wifi_connected, Toast.LENGTH_SHORT).show();
//
//
//            } else if (ANY.equals(sPref) && networkInfo != null) {
//                refreshDisplay = true;
//
//
//            } else {
//                refreshDisplay = false;
//                Toast.makeText(context, R.string.lost_connection, Toast.LENGTH_SHORT).show();
//            }
        }
    }



    //custom view

    private class CustomListAdapter extends ArrayAdapter {

        private Context mContext;
        private int id;
        private List <String>items ;

        public CustomListAdapter(Context context, int textViewResourceId , List<String> list )
        {
            super(context, textViewResourceId, list);
            mContext = context;
            id = textViewResourceId;
            items = list ;
        }

        @Override
        public View getView(int position, View v, ViewGroup parent)
        {
            View mView = v ;
            if(mView == null){
                LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                mView = vi.inflate(id, null);
            }

            TextView text = (TextView) mView.findViewById(R.id.textView);

            if(items.get(position) != null )
            {
                text.setTextColor(Color.WHITE);
                text.setText(items.get(position));
                text.setBackgroundColor(Color.RED);
                int color = Color.argb( 200, 255, 64, 64 );
                text.setBackgroundColor( color );

            }

            return mView;
        }

    }

}


