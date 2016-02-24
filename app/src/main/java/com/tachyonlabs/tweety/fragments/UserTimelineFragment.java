package com.tachyonlabs.tweety.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.tachyonlabs.tweety.models.Tweet;
import com.tachyonlabs.tweety.utils.TwitterApplication;
import com.tachyonlabs.tweety.utils.TwitterClient;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

public class UserTimelineFragment extends TweetsListFragment {
    private TwitterClient client;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient(); // singleton client
        populateTimeline();
    }

    public static UserTimelineFragment newInstance(String screenName) {
        UserTimelineFragment userFragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString("screen_name", screenName);
        userFragment.setArguments(args);
        return userFragment;
    }

    // send an API request to get the timeline JSON
    // fill the listview by creating the tweet objects from the JSON
    private void populateTimeline() {
        String screenName = getArguments().getString("screen_name");
        client.getUserTimeline(screenName, new JsonHttpResponseHandler() {
            // Success

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                // deserialize JSON
                // create models
                // load the model data into the ListView
                Log.d("DEBUG", json.toString());
                addAll(Tweet.fromJsonArray(json));
            }

            // Failure
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

}
