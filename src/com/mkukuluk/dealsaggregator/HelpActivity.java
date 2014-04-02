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





public class HelpActivity extends Activity {

    private WebView webView;
    private AdView adView;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_rel);

        webView = (WebView) findViewById(R.id.webview);
//        webView.loadData(getResources().getString(R.string.help_string),
//                "text/html", "UTF-8");
        String helpText =  "<!DOCTYPE html><html><body><font face=\"verdana\" color=\"white\">\"All Deals\" pulls the latest deals from several sites and displays them in a simple list." +
                "<br/><br/><br/>Selecting by a particular deal touching will launch that deal\'s webpage, and hitting the \"Back\" button will get you back to the app. " +
                "<br/><br/><br/>You can select your favorite deal sites by going to " +
                "<i><b>Menu > Settings > Select your deal sites.</b></i><br/><br/><br/>Long press on any item in the list will enable you to select multiple deals which you can add to your favorites by selecting the " +
                "<b>&#8853;</b> button on the top right. Favorites can be accessed from the Menu. They can be deleted by selecting by Long press and pressing the 'Delete' button on top right. "
                +"<br/><br/><br/><b>Disclaimer - The copyright for all the deals mentioned here belong to the respective sites.</b></font></body></html>";
        webView.loadData(helpText,"text/html", "UTF-8");
        webView.setBackgroundColor(Color.rgb(0,0,0));

        adView = (AdView) findViewById(R.id.adView);

        AdRequest request = new AdRequest();
        adView.loadAd(request);

    }

}