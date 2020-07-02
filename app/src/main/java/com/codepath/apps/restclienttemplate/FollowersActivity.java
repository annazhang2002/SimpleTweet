package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.codepath.apps.restclienttemplate.databinding.ActivityFollowersBinding;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class FollowersActivity extends AppCompatActivity {

    RecyclerView rvUsers;
    List<User> users;
    UserAdapter adapter;
    TwitterClient client;

    long userId;
    String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityFollowersBinding binding = ActivityFollowersBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        userId = Parcels.unwrap(getIntent().getParcelableExtra("user_id"));
        userType =  getIntent().getStringExtra("user_type");

        client = TwitterApp.getRestClient(this);
        rvUsers = binding.rvUsers;
        users = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new UserAdapter(this, users);
        rvUsers.setLayoutManager(layoutManager);
        rvUsers.setAdapter(adapter);

        getUserList(userType, userId);

    }

    private void getUserList(String userType, long userId) {
        if (userType.equals("followers")) {
            client.getFollowers(userId, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    Log.i("FollowersActivity", "onSuccess getUserList");
                    JSONObject object = json.jsonObject;
                    try {
                        JSONArray array = object.getJSONArray("users");
                        Log.i("FollowerActivity", "users: " + array);
                        users.addAll(User.fromJsonArray(array));
                        Log.i("FollowerActivity", "users: " + users);
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Log.i("FollowerActivity", "bad bad bad");
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Log.i("FollowersActivity", "onFailure getUserList");
                }
            });
        } else if (userType.equals("following")) {
            client.getFollowing(userId, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    Log.i("FollowersActivity", "onSuccess getFollowing");
                    JSONObject object = json.jsonObject;
                    try {
                        JSONArray array = object.getJSONArray("users");
                        Log.i("FollowerActivity", "users: " + array);
                        users.addAll(User.fromJsonArray(array));
                        Log.i("FollowerActivity", "users: " + users);
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Log.i("FollowerActivity", "bad bad bad");
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Log.i("FollowersActivity", "onFailure getUserList");
                }
            });
        }

    }
}