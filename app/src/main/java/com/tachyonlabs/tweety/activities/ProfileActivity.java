package com.tachyonlabs.tweety.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tachyonlabs.tweety.R;
import com.tachyonlabs.tweety.fragments.UserTimelineFragment;
import com.tachyonlabs.tweety.models.User;

import java.text.DecimalFormat;

public class ProfileActivity extends AppCompatActivity {
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        user = (User) getIntent().getSerializableExtra("user");

        populateProfileHeader(user);

        if (savedInstanceState == null) {
            // create the user timeline fragment
            UserTimelineFragment fragmentUserTimeline = UserTimelineFragment.newInstance(user);
            // display user timeline fragment within this activity dynamically
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, fragmentUserTimeline);
            ft.commit();
        }
    }

    private void populateProfileHeader(User user) {
        TextView tvFullName = (TextView) findViewById(R.id.tvFullName);
        TextView tvScreenName = (TextView) findViewById(R.id.tvScreenName);
        TextView tvTagLine = (TextView) findViewById(R.id.tvTagLine);
        TextView tvFollowersCount = (TextView) findViewById(R.id.tvFollowersCount);
        TextView tvFollowingCount = (TextView) findViewById(R.id.tvFollowingCount);
        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        ImageView ivProfileBannerImage = (ImageView) findViewById(R.id.ivProfileBannerImage);
        tvFullName.setText(user.getName());
        tvScreenName.setText("@" + user.getScreenName());
        tvTagLine.setText(user.getTagLine());
        DecimalFormat insertCommas = new DecimalFormat("###,###,###,###");
        tvFollowersCount.setText(insertCommas.format(user.getFollowersCount()));
        tvFollowingCount.setText(insertCommas.format(user.getFriendsCount()));
        Picasso.with(this).load(user.getProfileImageUrl()).into(ivProfileImage);
        String profileBannnerUrl = user.getProfileBannerUrl();
        if (profileBannnerUrl != null) {
            Picasso.with(this).load(profileBannnerUrl).into(ivProfileBannerImage);
        } else {
            ivProfileBannerImage.setBackgroundColor(0xFF55acee);
        }
    }
}
