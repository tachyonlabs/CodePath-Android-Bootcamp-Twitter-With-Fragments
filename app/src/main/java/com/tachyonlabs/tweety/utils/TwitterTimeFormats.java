package com.tachyonlabs.tweety.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TwitterTimeFormats {

    public static String relativeDate(String createdAt) {
        // get a Twitter-app-style string of how long ago the tweet was tweeted
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);
        String dateString;
        long msDate = 0;
        try {
            msDate = sf.parse(createdAt).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // the Math.max is because occasionally I see a newest tweet from Twitter with a value like
        // negative three seconds
        long relativeDate = Math.max(System.currentTimeMillis() - msDate, 0);
        if (relativeDate > 604800000L) {
            dateString = (relativeDate / 604800000L) + "w";
        } else if (relativeDate > 86400000L) {
            dateString = (relativeDate / 86400000L) + "d";
        } else if (relativeDate > 3600000L) {
            dateString = (relativeDate / 3600000L) + "h";
        } else if (relativeDate > 60000) {
            dateString = (relativeDate / 60000) + "m";
        } else {
            dateString = (relativeDate / 1000) + "s";
        }

        return dateString;
    }

    public static String tweetDetailDateFormat(String rawJsonDate) {
        // display the tweet's timestamp using the format that the Twitter app uses when you
        // tap a tweet to bring up a detailed view
        Date date;
        long dateMillis = 0;
        date = new Date(dateMillis);
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        try {
            dateMillis = sf.parse(rawJsonDate).getTime();
            date = new Date(dateMillis);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String twitterDetailDateFormat = "h:mm a • d MMM yy";
        SimpleDateFormat stdf = new SimpleDateFormat(twitterDetailDateFormat, Locale.ENGLISH);
        stdf.setLenient(true);

        return stdf.format(date);
    }

}
