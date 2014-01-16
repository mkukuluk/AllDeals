package com.mkukuluk.dealsaggregator;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.webkit.WebView;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;


/**
 * Created by MKULUKKALLUR on 12/19/13.
 */





public class MySitesHelpActivity extends Activity {

    private WebView webView;
    private AdView adView;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_rel);

        webView = (WebView) findViewById(R.id.webview);
//        webView.loadData(getResources().getString(R.string.help_string),
//                "text/html", "UTF-8");
        String helpText =  "<!DOCTYPE html><html><body><font face=\"verdana\" color=\"white\">This screen shows the custom feeds that you have entered. To enter a new site or edit or delete a site, you " +
                "can go to Menu > Settings > Manage My Site Feeds option. You will need to put in a name and the RSS url.</font></body></html>";
        webView.loadData(helpText,"text/html", "UTF-8");
        webView.setBackgroundColor(Color.rgb(0,0,0));

        adView = (AdView) findViewById(R.id.adView);

        AdRequest request = new AdRequest();
        adView.loadAd(request);

    }

}