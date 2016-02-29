package com.tachyonlabs.tweety.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class User  implements Serializable {
    // attributes
    private String name;
    private long uid;
    private String screenName;
    private String profileImageUrl;
    private String profileBannerUrl;
    private String tagLine;
    private int followersCount;
    private int followingsCount;

    public String getName() {
        return name;
    }

    public long getUid() {
        return uid;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getProfileBannerUrl() {
        return profileBannerUrl;
    }

    public String getTagLine() {
        return tagLine;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public int getFriendsCount() {
        return followingsCount;
    }

    // deserialize user JSON
    public static User fromJSON(JSONObject json) {
        User u = new User();
        try {
            u.name = json.getString("name");
            u.uid = json.getLong("id");
            u.screenName = json.getString("screen_name");
            u.profileImageUrl = json.getString("profile_image_url");
            u.tagLine = json.getString("description");
            u.followersCount = json.getInt("followers_count");
            u.followingsCount = json.getInt("friends_count");
            u.profileBannerUrl = json.getString("profile_banner_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return u;
    }
}
