package com.tachyonlabs.tweety.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tachyonlabs.tweety.R;
import com.tachyonlabs.tweety.models.Tweet;
import com.tachyonlabs.tweety.utils.TwitterTimeFormats;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TweetDetailFragment extends android.support.v4.app.DialogFragment {
    private String myProfileImageUrl;
    private Tweet tweet;

    public TweetDetailFragment() {
        // Required empty public constructor
    }

    public static TweetDetailFragment newInstance(String myProfileImageUrl, Tweet tweet) {
        // get the tweet to display and the logged-in user's profile image URL
        TweetDetailFragment fragment = new TweetDetailFragment();
        Bundle args = new Bundle();
        args.putString("myProfileImageUrl", myProfileImageUrl);
        args.putSerializable("tweet", tweet);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tweet_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        // Get fields from view
        ImageView ivClose = (ImageView) view.findViewById(R.id.ivClose);
        ImageView ivMyProfileImage = (ImageView) view.findViewById(R.id.ivMyProfileImage);
        ImageView ivProfileImage = (ImageView) view.findViewById(R.id.ivProfileImage);
        TextView tvUserName = (TextView) view.findViewById(R.id.tvUserName);
        TextView tvScreenName = (TextView) view.findViewById(R.id.tvScreenName);
        TextView tvTweetBody = (TextView) view.findViewById(R.id.tvTweetBody);
        TextView tvTimeStamp = (TextView) view.findViewById(R.id.tvTimeStamp);
        // Fetch arguments from bundle
        // display the logged-in user's profile image
        myProfileImageUrl = getArguments().getString("myProfileImageUrl");
        Picasso.with(view.getContext()).load(myProfileImageUrl).fit().centerCrop().into(ivMyProfileImage);

        // and display the tweet
        tweet = (Tweet) getArguments().getSerializable("tweet");
        Picasso.with(view.getContext()).load(tweet.getUser().getProfileImageUrl()).fit().centerCrop().into(ivProfileImage);
        tvUserName.setText(tweet.getUser().getName());
        tvScreenName.setText("@" + tweet.getUser().getScreenName());
        tvTweetBody.setText(tweet.getBody());
        String tweetTimeStamp = TwitterTimeFormats.tweetDetailDateFormat(tweet.getCreatedAt());
        tvTimeStamp.setText(tweetTimeStamp);

        // the X in the top-left corner closes the fragment
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
    }

}
