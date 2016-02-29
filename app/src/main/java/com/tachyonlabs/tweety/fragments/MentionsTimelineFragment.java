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

public class MentionsTimelineFragment extends TweetsListFragment {
    private TwitterClient client;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient(); // singleton client
        populateTimeline(1, 0);
    }

    // send an API request to get the timeline JSON
    // fill the listview by creating the tweet objects from the JSON
    public void populateTimeline(long since_id, long max_id) {
        client.getMentionsTimeline(since_id, max_id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                // deserialize JSON
                // create models
                // load the model data into the ListView
                addAll(Tweet.fromJsonArray(json));
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

    public void fetchTimelineAsync() {
        // Send the network request to fetch the updated data on Swipe-to-refresh
        // `client` here is an instance of Android Async HTTP
        long since_id;
        long max_id = 0;
        // get tweets newer than the current newest tweet
        Tweet newestDisplayedTweet = tweets.get(0);
        since_id = newestDisplayedTweet.getUid();
        client.getMentionsTimeline(since_id, max_id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                // add them to the ArrayList, notify the adapter, scroll back to show the new tweets
                insertAll(Tweet.fromJsonArray(json));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }
}
