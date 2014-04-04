/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mkukuluk.dealsaggregator;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ManageMySitesActivity extends FragmentActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
     * three primary sections of the app. We use a {@link android.support.v4.app.FragmentPagerAdapter}
     * derivative, which will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    AppSectionsPagerAdapter mAppSectionsPagerAdapter;

    private static String prefilledname = "";
    private static String prefilledURL = "";
    private static boolean isedit = false;
    /**
     * The {@link ViewPager} that will display the three primary sections of the app, one at a
     * time.
     */
    ViewPager mViewPager;
    private ListView thisListView;
    private ArrayList <String> dealList = new ArrayList <String>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();

        // Specify that the Home/Up button should not be enabled, since there is no hierarchical
        // parent.
        actionBar.setHomeButtonEnabled(false);

        // Specify that we will be displaying tabs in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mAppSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public  class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    Fragment fragment = new AddUserSiteFragment();
                    Bundle args = new Bundle();
                    args.putInt(AddUserSiteFragment.ARG_SECTION_NUMBER, i + 1);
                    fragment.setArguments(args);
                    return fragment;
                case 1:

                    Fragment fragment2 = new MyListFragment();
                    Bundle args2 = new Bundle();
                    fragment2.setArguments(args2);
                    return fragment2;

                default:
                    Fragment fragment1 = new AddUserSiteFragment();
                    Bundle args1 = new Bundle();
                    args1.putInt(AddUserSiteFragment.ARG_SECTION_NUMBER, i + 1);
                    fragment1.setArguments(args1);
                    return fragment1;

            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(position == 0){
                return "Add your site";
            }else if(position == 1){
                return "View/Edit Sites";
            }else{
                return "Section " + (position + 1);
            }

        }
    }

    //Help Menu
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
                Intent mainActivity = new Intent(getBaseContext(), ManageMySitesHelp.class);
                startActivity(mainActivity);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * A fragment that launches other parts of the demo application.
     */
    public static class LaunchpadSectionFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_section_launchpad, container, false);

            // Demonstration of a collection-browsing activity.
//            rootView.findViewById(R.id.demo_collection_button)
//                    .setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            Intent intent = new Intent(getActivity(), EditTextActivity.class);
//                            startActivity(intent);
//                        }
//                    });

            // Demonstration of navigating to external activities.
            rootView.findViewById(R.id.demo_external_activity)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Create an intent that asks the user to pick a photo, but using
                            // FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET, ensures that relaunching
                            // the application from the device home screen does not return
                            // to the external activity.
                            Intent externalActivityIntent = new Intent(Intent.ACTION_PICK);
                            externalActivityIntent.setType("image/*");
                            externalActivityIntent.addFlags(
                                    Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                            startActivity(externalActivityIntent);
                        }
                    });

            return rootView;
        }
    }

    /**
     * A dummy fragment representing a section of the app, but that simply displays dummy text.
     */
    public class AddUserSiteFragment extends Fragment {

        public static final String ARG_SECTION_NUMBER = "section_number";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.edittext_layout, container, false);
            Bundle args = getArguments();
            ((EditText) rootView.findViewById(R.id.editTextSiteFeedName)).setText(
                    "");
            final EditText editTextSiteNameInput = (EditText) rootView.findViewById(R.id.editTextSiteFeedName);
            final EditText editTextSiteURLInput = (EditText) rootView.findViewById(R.id.editTextSiteURL);
            
            if(isedit){
                editTextSiteNameInput.setText(prefilledname);
                editTextSiteURLInput.setText(prefilledURL);
                isedit = false;prefilledname= "";prefilledURL ="";
            }
            View.OnClickListener handler = new View.OnClickListener() {
                public void onClick(View v) {
                    // we will use switch statement and just
                    // get thebutton's id to make things easier

                    SharedPreferences prefs = getSharedPreferences("com.mkukuluk.dealaggregator.usersite", Context.MODE_PRIVATE);
                    final SharedPreferences.Editor editor = prefs.edit();
                    switch (v.getId()) {
                        case R.id.addToNew:


                            editor.putString(editTextSiteNameInput.getText().toString(), editTextSiteURLInput.getText().toString());

                            editor.commit();
                            Toast.makeText(ManageMySitesActivity.this, "Site Feed Saved - "+editTextSiteNameInput.getText().toString(), Toast.LENGTH_SHORT).show();
                            finish();
                            break;
                        // the EditText for plain text input will be cleared
                        case R.id.clearTextInput:
                            AlertDialog.Builder builder = new AlertDialog.Builder(ManageMySitesActivity.this);
                            builder.setTitle("Confirm");
                            builder.setMessage("Are you sure you want to clear all values?");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    editTextSiteNameInput.setText("");
                                    editTextSiteURLInput.setText("");

                                }
                            })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                            builder.show();

                            break;




                    }
                }
            };

            // we will set the listeners of our buttons
            rootView.findViewById(R.id.addToNew).setOnClickListener(handler);
            rootView.findViewById(R.id.clearTextInput).setOnClickListener(handler);

            return rootView;

        }
    }

//new code below:

    public class MyListFragment extends ListFragment {

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            SharedPreferences prefs = getSharedPreferences("com.mkukuluk.dealaggregator.usersite", Context.MODE_PRIVATE);
            Map<String, ?> temp = new HashMap();
            temp = prefs.getAll();
            Iterator it = temp.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry pairs = (Map.Entry)it.next();
                dealList.add((String) pairs.getKey());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_multiple_choice, dealList);
            setListAdapter(adapter);
            thisListView = getListView();
            ListView listView = getListView();
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            listView.setMultiChoiceModeListener(new ModeCallback());

        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
			Toast.makeText(ManageMySitesActivity.this, "Press and Hold to Edit or Delete", Toast.LENGTH_SHORT).show();

        }

    }


    private class ModeCallback implements ListView.MultiChoiceModeListener {

        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.usersitemenu, menu);
            mode.setTitle("Select Items");
            return true;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            SharedPreferences prefs = getSharedPreferences("com.mkukuluk.dealaggregator.usersite", Context.MODE_PRIVATE);

            SparseBooleanArray checkedItems = thisListView.getCheckedItemPositions();
            int len = dealList.size();

            switch (item.getItemId()) {

                case R.id.useredit:
                    if(checkedItems.size()>1){
                        Toast.makeText(ManageMySitesActivity.this, "Please select only 1 item to edit.", Toast.LENGTH_SHORT).show();

                        mode.finish();
                        break;
                    }
                    getActionBar().setSelectedNavigationItem(0);
                   isedit = true;
                    Map<String, ?> temp = new HashMap();
                    temp = prefs.getAll();
                    for (int i = 0; i < len; i++){
                        if (checkedItems.get(i)) {
                     prefilledname = dealList.get(i);
                            prefilledURL = temp.get(dealList.get(i)).toString();
                        }
            }

                    mode.finish();
                    break;
                case R.id.userdel:

                    SharedPreferences.Editor editor = prefs.edit();
                    for (int i = 0; i < len; i++)
                        if (checkedItems.get(i)) {
//                            String tempDl = dealList.get(i);

                            editor.remove(dealList.get(i));

                        }


                    editor.commit();
					Toast.makeText(ManageMySitesActivity.this, "Removed ", Toast.LENGTH_SHORT).show();
                    dealList.clear();
                    mode.finish();
                    finish();
                    break;
                default:
                    Toast.makeText(ManageMySitesActivity.this, "Clicked " + item.getTitle(),
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
            final int checkedCount = thisListView.getCheckedItemCount();
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
