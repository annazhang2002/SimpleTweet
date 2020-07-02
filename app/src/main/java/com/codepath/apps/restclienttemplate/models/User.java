package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Entity
@Parcel
public class User {

    @ColumnInfo
    @PrimaryKey(autoGenerate=true)
    public long id;

    @ColumnInfo
    public String screenName;
    @ColumnInfo
    public String name;
    @ColumnInfo
    public String publicImageUrl;
    @ColumnInfo
    public String bio;
    @ColumnInfo
    public int followerCount;
    @ColumnInfo
    public int followingCount;
    @ColumnInfo
    public boolean verified;
    @ColumnInfo
    public String createdAt;
    @ColumnInfo
    public String bannerImg;
    @ColumnInfo
    public String location;
    @ColumnInfo
    public String url;

    public User() {}

    public static User fromJson(JSONObject object) throws JSONException {
        User user = new User();
        user.name = object.getString("name");
        user.screenName = object.getString("screen_name");
        user.publicImageUrl = object.getString("profile_image_url_https");
        user.id = object.getLong("id");
        user.bio = object.getString("description");
        Log.i("User", "userbio: " + user.bio);
        user.followerCount = object.getInt("followers_count");
        user.followingCount = object.getInt("friends_count");
        user.verified = object.getBoolean("verified");
        user.createdAt = object.getString("created_at");
        user.bannerImg = object.getString("profile_banner_url");
        user.location = object.getString("location");
        user.url = object.getString("url");
        Log.i("User", "followers: " + user.followingCount);
        Log.i("User", "following: " + user.followerCount);

        return user;
    }

    public static List<User> fromJsonTweetArray(List<Tweet> tweetsFromNetwork) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < tweetsFromNetwork.size(); i++) {
            users.add(tweetsFromNetwork.get(i).user);
        }
        return users;
    }
}
