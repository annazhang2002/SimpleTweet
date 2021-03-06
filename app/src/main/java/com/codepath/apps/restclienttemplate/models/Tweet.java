package com.codepath.apps.restclienttemplate.models;

import android.nfc.Tag;
import android.text.format.DateUtils;
import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.codepath.apps.restclienttemplate.TwitterApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Parcel
@Entity(foreignKeys = @ForeignKey(entity=User.class, parentColumns="id", childColumns="userId"))
public class Tweet {

    @ColumnInfo
    @PrimaryKey(autoGenerate=true)
    public long id;

    public static final String TAG = "Tweet";

    @ColumnInfo
    public String body;
    @ColumnInfo
    public  String createdAt;
    @ColumnInfo
    public String timeAgo;
    @ColumnInfo
    public long userId;
    @Ignore
    public User user;
    @ColumnInfo
    public String mediaUrl1;
    @ColumnInfo
    public boolean liked;
    @ColumnInfo
    public boolean retweeted;
    @ColumnInfo
    public int likeCount;
    @ColumnInfo
    public int retweetCount;
    @ColumnInfo
    public int replyCount;

    public Tweet() {}

    public static Tweet fromJson(JSONObject object) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = object.getString("text");
        tweet.createdAt = getSimpleDate(object.getString("created_at"));
        tweet.timeAgo = getRelativeTimeAgo(object.getString("created_at"));
        tweet.user = User.fromJson(object.getJSONObject("user"));
        tweet.userId = tweet.user.id;
        tweet.liked = object.getBoolean("favorited");
        tweet.retweeted = object.getBoolean("retweeted");
        tweet.likeCount = object.getInt("favorite_count");
        tweet.retweetCount = object.getInt("retweet_count");
//        tweet.replyCount = object.getInt("reply_count");
        JSONObject entities = object.getJSONObject("entities");
        if (entities.has("media")) {
            JSONArray media = entities.getJSONArray("media");
            Log.i(TAG, "Media: " + media);
            tweet.mediaUrl1 = media.getJSONObject(0).getString("media_url_https");
            Log.i(TAG, "media URL: " + tweet.mediaUrl1);
        }
        tweet.id = object.getLong("id");
        return tweet;
    }

    public static List<Tweet> fromJsonArray(JSONArray array) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for (int i =0; i< array.length(); i++) {
            tweets.add(fromJson(array.getJSONObject(i)));
        }

        return tweets;
    }

    public static String getSimpleDate(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        String newFormat = "h:mm a · MMM dd, yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.applyPattern(newFormat);
        String newDate = sf.format(new Date(rawJsonDate));
        Log.i("Tweet", "newDate: " + newDate);
        return newDate;
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);

        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();

            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE ).toString();
        } catch (ParseException e) {
            Log.e("Tweet", "gelRelativeTimeAgo", e);
            e.printStackTrace();
        }



        return relativeDate;
    }
}
