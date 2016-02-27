package com.tachyonlabs.tweety.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.tachyonlabs.tweety.R;
import com.tachyonlabs.tweety.activities.ProfileActivity;
import com.tachyonlabs.tweety.adapters.TweetsAdapter;
import com.tachyonlabs.tweety.models.Tweet;
import com.tachyonlabs.tweety.models.User;
import com.tachyonlabs.tweety.utils.TwitterApplication;
import com.tachyonlabs.tweety.utils.TwitterClient;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public abstract class TweetsListFragment extends Fragment{
    RecyclerView rvTweets;
    ArrayList<Tweet> tweets;
    TweetsAdapter adapter;
    TwitterClient client;
    User user;
    User userProfileImageClicked;

    // inflation logic

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list, parent, false);
        rvTweets = (RecyclerView) v.findViewById(R.id.rvTweets);
        rvTweets.setAdapter(adapter);
        // Set layout manager to position the items
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.addOnScrollListener(new com.tachyonlabs.tweety.utils.EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                long max_id = tweets.get(tweets.size() - 1).getUid() - 1;
                populateTimeline(1, max_id);
            }
        });

        // hook up listener for tweet tap to view tweet detail
        adapter.setOnItemClickListener(new TweetsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (view instanceof ImageView) {
                    showUserProfile(view.getTag().toString());
                } else {
                    showTweetDetailDialog(position);
                }
            }
        });

        return v;
    }

    // creation lifecycle

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // create the ArrayList (data source)
        tweets = new ArrayList<>();
        // construct the adapter from the data source
        adapter = new TweetsAdapter(tweets);
        client = TwitterApplication.getRestClient();
        // get some account info
        client.getUserInfo(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                user = User.fromJSON(response);
            }
        });
    }

    // Abstract method to be overridden by fragments extending it
    protected abstract void populateTimeline(long since_id, long max_id);

    // bring up the dialogfragment for showing a detailed view of a tweet
    private void showTweetDetailDialog(int position) {
        // pass in the user's profile image and the tweet
        FragmentManager fm = getActivity().getSupportFragmentManager();
        String myProfileImageUrl = user.getProfileImageUrl();
        TweetDetailFragment tweetDetailFragment = TweetDetailFragment.newInstance(myProfileImageUrl, tweets.get(position));
        tweetDetailFragment.show(fm, "fragment_tweet_detail");
    }

    public void addAll(List<Tweet> initialOrOlderTweets) {
        final int previousTweetsLength = tweets.size();
        tweets.addAll(initialOrOlderTweets);
        adapter.notifyItemRangeInserted(previousTweetsLength, initialOrOlderTweets.size());
    }

    public void insertAll(List<Tweet> newTweets) {
        tweets.addAll(0, newTweets);
        adapter.notifyItemRangeInserted(0, newTweets.size());
        //scrollToTop();
    }

    public void onTweetButtonClicked(String myTweetText) {
        // when the user composes a new tweet and taps the Tweet button, post it
        client.postTweet(myTweetText, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                // get the new tweet and add it to the ArrayList
                Tweet myNewTweet = Tweet.fromJSON(json);
                tweets.add(0, myNewTweet);
                // notify the adapter
                adapter.notifyItemInserted(0);
                // scroll back to display the new tweet
                //scrollToTop();
                // display a success Toast
                Toast toast = Toast.makeText(getActivity(), "Tweet posted!", Toast.LENGTH_SHORT);
                View view = toast.getView();
                view.setBackgroundColor(0xC055ACEE);
                TextView textView = (TextView) view.findViewById(android.R.id.message);
                textView.setTextColor(0xFFFFFFFF);
                toast.show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

    public void showUserProfile(String screenName) {
        client.getOtherUserInfo(screenName, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                // get the new tweet and add it to the ArrayList
                User user = User.fromJSON(json);
                // launch the profile view
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

}
