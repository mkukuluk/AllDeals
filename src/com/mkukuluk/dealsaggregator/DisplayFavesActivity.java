package com.mkukuluk.dealsaggregator;


import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.Toast;


import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
* Created by MKULUKKALLUR on 12/19/13.
*/
public class DisplayFavesActivity extends ListActivity {

    private List<String> dealList = new ArrayList<String>();
    private List<String>dealURLList = new ArrayList<String>();
    private ArrayAdapter arrayAdapter ;
    EditText inputSearch = null;
    private ShareActionProvider mShareActionProvider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.players);
        SharedPreferences prefs = getSharedPreferences("com.mkukuluk.dealaggregator.faves", Context.MODE_PRIVATE);
        Map<String, ?> temp = new HashMap();
        temp = prefs.getAll();
        Iterator it = temp.entrySet().iterator();
        if(temp.size()==0){
            Toast.makeText(DisplayFavesActivity.this, "No Favorites Saved. Long Press Items you like and press the "+Html.fromHtml("&#8853;").toString()+" button to Save to Favorites.", Toast.LENGTH_LONG).show();
        finish();
        }
        while(it.hasNext()){
            Map.Entry pairs = (Map.Entry)it.next();
//            String tempList = (String) pairs.getKey();
//            tempList =Html.fromHtml("&#9829;").toString()+"   "+  tempList.substring(1,tempList.length()).trim();

            dealList.add(Html.fromHtml("&#9829;").toString()+"   "+  (String) pairs.getKey());

            dealURLList.add((String) pairs.getValue());

        }


        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_activated_1,dealList);


        setListAdapter(arrayAdapter);
        ListView listView = getListView();
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new ModeCallback());
        int[] colors = {0, 0xffff0000, 0};
        listView.setDivider(new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, colors));
        listView.setDividerHeight(2);


         // Assign adapter to ListView
        listView.setAdapter(arrayAdapter);
        listView.setTextFilterEnabled(true);

        AdView adView = new AdView(this, AdSize.SMART_BANNER, "a152b47c1cca8a4");
        AdRequest request = new AdRequest();
        //request.addTestDevice("241F8D71AA9866EE0EA185442F777C5F");
        adView.loadAd(request);

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


       protected void onListItemClick(ListView l, View v, int position, long id) {

//        Uri feedUri = Uri.parse(myRssFeed.getItem(position).getLink());
        String urlofdeal = dealURLList.get(position);
        Uri feedUri = Uri.parse(urlofdeal);
        Intent myIntent = new Intent(Intent.ACTION_VIEW, feedUri);
        startActivity(myIntent);

    }

    private void refreshDisplay(){


        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_activated_1,dealList);


        setListAdapter(arrayAdapter);
        ListView listView = getListView();


        // Assign adapter to ListView
        listView.setAdapter(arrayAdapter);

    }



    //ModeCallBack

    private class ModeCallback implements ListView.MultiChoiceModeListener {

        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.favedelmenu, menu);
            MenuItem item = menu.findItem(R.id.menu_item_share);
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

            SharedPreferences prefs = getSharedPreferences("com.mkukuluk.dealaggregator.faves", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            SparseBooleanArray checkedItems = getListView().getCheckedItemPositions();
            int len = dealList.size();

            switch (item.getItemId()) {
//                case R.id.share:
//                    Toast.makeText(FetchDeals.this, "Shared " + getListView().getCheckedItemCount() +
//                            " items", Toast.LENGTH_SHORT).show();
//                    mShareActionProvider = (ShareActionProvider) item.getActionProvider();
//                    mode.finish();
//                    break;

                case R.id.favedel:
                       for (int i = 0; i < len; i++)
                        if (checkedItems.get(i)) {
//                            String tempDl = dealList.get(i);
                           
//                            editor.remove(Html.fromHtml("&#10032;").toString() + "   " + tempDl.substring(1,tempDl.length()).trim());
                           editor.remove(dealList.get(i).substring(dealList.get(i).lastIndexOf(Html.fromHtml("&#9829;").toString()+"   ")+1).trim());

                        }


                    editor.commit();
//                    onStart();

                    Toast.makeText(DisplayFavesActivity.this, "Removed " + getListView().getCheckedItemCount() +
                            " items from Favorites", Toast.LENGTH_SHORT).show();
                    dealList.clear();
//                    refreshDisplay();

                    mode.finish();
                    finish();
                    break;

                case R.id.menu_item_share:

                    //get all the checked items and put them in a string builder:
                    StringBuilder allCheckedDeals = new StringBuilder();
                    for (int i = 0; i < len; i++)
                        if (checkedItems.get(i)) {
                            allCheckedDeals.append(dealList.get(i).substring(dealList.get(i).lastIndexOf(Html.fromHtml("&#9829;").toString()+"   ")+1).trim()+" - "+dealURLList.get(i)+"\n\n");

                        }

                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "These are some of my favorite deals from 'All Deals' Android App (https://play.google.com/store/apps/details?id=com.mkukuluk.dealsaggregator) \n\n"+allCheckedDeals);
                    sendIntent.setType("text/plain");
                    startActivity(Intent.createChooser(sendIntent, "How do you want to share?"));
                    break;
                default:
                    Toast.makeText(DisplayFavesActivity.this, "Clicked " + item.getTitle(),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
            return true;
        }

//        private void setShareIntent(Intent shareIntent) {
//            if (mShareActionProvider != null) {
//                mShareActionProvider.setShareIntent(shareIntent);
//            }
//        }

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

}
