package com.mkukuluk.dealsaggregator;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.webkit.WebView;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

/**
 * Created by mkulukkallur on 3/31/14.
 */
public class SettingsHelpActivity extends Activity {

    private WebView webView;
    private AdView adView;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_rel);

        webView = (WebView) findViewById(R.id.webview);
//        webView.loadData(getResources().getString(R.string.help_string),
//                "text/html", "UTF-8");
        String helpText =  "<!DOCTYPE html><html><body><font face=\"verdana\" color=\"white\">'Select Your Deal Sites' - to pick the deal sites you are interested in. Choose 'All' to display deals from all the sites listed." +
                "<br><br>" +
                "'Search Option' - to turn the Search box ON or OFF." +
                "<br><br>" +
                "'Display Deal Options' - to display the entire text of the deal or limit it to 1 line for ease of reading." +
                "<br><br>" +
                "'Manage Your Site Feeds' - to define your own custom site feed. If you don't see the deal site you like listed in the list, you can define your own feed from this menu. Just get the RSS URL and you're good to go!</b></font></body></html>";
        webView.loadData(helpText,"text/html", "UTF-8");
        webView.setBackgroundColor(Color.rgb(0, 0, 0));

        adView = (AdView) findViewById(R.id.adView);

        AdRequest request = new AdRequest();
        adView.loadAd(request);

    }

}
