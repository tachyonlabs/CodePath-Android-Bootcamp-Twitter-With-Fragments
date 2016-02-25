package com.tachyonlabs.tweety.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.tachyonlabs.tweety.R;
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
                showTweetDetailDialog(position);
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
}
