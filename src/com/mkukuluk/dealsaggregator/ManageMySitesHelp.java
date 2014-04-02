package com.mkukuluk.dealsaggregator;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.webkit.WebView;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

/**
 * Created by MKULUKKALLUR on 4/2/2014.
 */
public class ManageMySitesHelp extends Activity {
    private WebView webView;
    private AdView adView;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_rel);

        webView = (WebView) findViewById(R.id.webview);
//        webView.loadData(getResources().getString(R.string.help_string),
//                "text/html", "UTF-8");
        String helpText =  "<!DOCTYPE html><html><body><font face=\"verdana\" color=\"white\">"+
                "'Manage Your Site Feeds' - to define your own custom site feed. If you don't see the deal site you like listed in the list, you can define your own feed here. " +
                "Enter the RSS feed url of the site you are interested in and give it a name and hit \"Save\". <br><br>" +
                "You can edit or delete your feeds in the View/Edit Sites Tab.</b></font></body></html>";
        webView.loadData(helpText,"text/html", "UTF-8");
        webView.setBackgroundColor(Color.rgb(0, 0, 0));

        adView = (AdView) findViewById(R.id.adView);

        AdRequest request = new AdRequest();
        adView.loadAd(request);

    }
}
