package com.tachyonlabs.tweety.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tachyonlabs.tweety.R;
import com.tachyonlabs.tweety.fragments.ComposeFragment;
import com.tachyonlabs.tweety.fragments.HomeTimelineFragment;
import com.tachyonlabs.tweety.fragments.MentionsTimelineFragment;
import com.tachyonlabs.tweety.models.User;
import com.tachyonlabs.tweety.utils.TwitterApplication;
import com.tachyonlabs.tweety.utils.TwitterClient;

import org.apache.http.Header;
import org.json.JSONObject;

public class TimelineActivity extends AppCompatActivity {
    TwitterClient client;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // get the viewpager
        ViewPager vpPager = (ViewPager) findViewById(R.id.viewpager);
        // set the viewpager adapter
        vpPager.setAdapter(new TweetsPagerAdapter(getSupportFragmentManager()));
        // find the pager sliding tabstrip
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // attach the tabstrip to the viewpager
        tabStrip.setViewPager(vpPager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showComposeDialog();
            }
        });

        client = TwitterApplication.getRestClient();
        // get some account info
        client.getUserInfo(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                user = User.fromJSON(response);
            }
        });
    }

    // Inflate the menu; this adds items to the action bar if it is present.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    // bring up the dialogfragment for composing a new tweet
    private void showComposeDialog() {
        FragmentManager fm = getSupportFragmentManager();
        // pass in the URL for the user's profile image
        String myProfileImageUrl = user.getProfileImageUrl();
        ComposeFragment composeFragment = ComposeFragment.newInstance(myProfileImageUrl);
        composeFragment.show(fm, "fragment_compose");
    }

    public void onProfileView(MenuItem mi) {
        // launch the profile view
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    // return the order of the fragments in the ViewPager
    public class TweetsPagerAdapter extends FragmentPagerAdapter {
        private String tabTitles[] = { "Home", "Mentions" };

        // adapter gets the manager - insert or remove fragments from activity
        public TweetsPagerAdapter(FragmentManager fm) {
            super(fm);

        }

        // order and creation of fragments within the pager
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new HomeTimelineFragment();
            } else if (position == 1) {
                return new MentionsTimelineFragment();
            } else {
                return null;
            }
        }

        // returns the tab title
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        // how many fragments there are to swipe between
        @Override
        public int getCount() {
            return tabTitles.length;
        }
    }



}
