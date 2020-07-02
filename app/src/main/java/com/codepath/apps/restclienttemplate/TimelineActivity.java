package com.codepath.apps.restclienttemplate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.TweetDao;
import com.codepath.apps.restclienttemplate.models.TweetWithUser;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity implements ComposeDialogFragment.EditNameDialogListener {

    public static final String TAG = "TimelineActivity";
    private static final int REQUEST_CODE = 20;

    TwitterClient client;
    TweetDao tweetDao;
    public static String userID;

    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetsAdapter adapter;
    SwipeRefreshLayout swipeContainer;
    MenuItem miActionProgressItem;
    boolean loaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityTimelineBinding binding = ActivityTimelineBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        client = TwitterApp.getRestClient(this);
        tweetDao = ((TwitterApp) getApplicationContext()).getMyDatabase().tweetDao();


        rvTweets = binding.rvTweets;
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets);

        getUserId();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        rvTweets.setLayoutManager(layoutManager);
        rvTweets.setAdapter(adapter);

        swipeContainer = (SwipeRefreshLayout) binding.swipeContainer;
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                refresh();
            }

        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        loaded = false;
        populateHomeTimeline();

        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG, "onLoadMore: " + tweets.get(tweets.size()-1).id);
                Log.i(TAG, "page: " + page);
                updateHomeTimeline(tweets.get(tweets.size()-1).id);
            }
        });
    }

    private void refresh() {
        tweets.clear();
        populateHomeTimeline();
    }

    private void updateHomeTimeline(long max_id) {
        loaded = true;
        client.updateHomeTimeline( max_id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess updateHomeTimeline");
                final JSONArray array = json.jsonArray;
                try {
                    final List<Tweet> tweetsFromNetwork = Tweet.fromJsonArray(array);
                    tweets.addAll(tweetsFromNetwork);
                    hideProgressBar();
                    swipeContainer.setRefreshing(false);
                    Log.d(TAG, "all tweets: " + tweets.toString());

//                    Integer lowestLocal = 0;
//                    for (int i = 0; i < tweets.size(); i++) {
//                        Log.i(TAG, "id is for the tweet " + i + ": " + tweets.get(i).id);
//                        if (tweets.get(i).id < lowestLocal) {
//                            lowestLocal = tweets.get(i).id;
//                        }
//                    }
//                    lowestId = tweets.get(tweets.size()-1).id;
//                    Log.i(TAG, "lowestID is  " +  lowestId);
                    // Query for existing tweets
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(TAG, "Saving data into database");

                            List<User> usersFromNetwork = User.fromJsonTweetArray(tweetsFromNetwork);
                            tweetDao.insertModel(usersFromNetwork.toArray(new User[0]));
                            Log.i(TAG, "Users from network: " + usersFromNetwork);

                            tweetDao.insertModel(tweetsFromNetwork.toArray(new Tweet[0]));
                            Log.i(TAG, "Users from network: " + tweetsFromNetwork);

                        }
                    });

                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    Log.d(TAG, "JSON exception: updateHomeTimeline", e);

                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure updateHomeTimeline" + throwable + " response: "+ response + " statusCode: " + statusCode);
                // Query for existing tweets
                showFromDatabase();

            }
        });
    }

    public void showFromDatabase() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "Showing data from database");
                List<TweetWithUser> tweetWithUsers = tweetDao.recentItems();
                Log.i(TAG, "TweetswithUsers: " + tweetWithUsers);
                List<Tweet> tweetsFromDB = TweetWithUser.getTweetList(tweetWithUsers);
                tweets.clear();
                adapter.notifyDataSetChanged();
                tweets.addAll(tweetsFromDB);
                Log.i(TAG, "Tweets from db: " + tweetsFromDB + " and the tweets: " + tweets);
                adapter.notifyDataSetChanged();
                loaded = true;
            }
        });
    }


    private void populateHomeTimeline() {
        loaded = true;
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess populateHomeTimeline");
                JSONArray array = json.jsonArray;
                try {
                    Log.i(TAG, "jsonarray: " + array);
                    final List<Tweet> tweetsFromNetwork = Tweet.fromJsonArray(array);
                    tweets.addAll(tweetsFromNetwork);

                    // Query for existing tweets
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(TAG, "Saving data into database");

                            List<User> usersFromNetwork = User.fromJsonTweetArray(tweetsFromNetwork);
                            tweetDao.insertModel(usersFromNetwork.toArray(new User[0]));
                            Log.i(TAG, "Users from network: " + usersFromNetwork);

                            tweetDao.insertModel(tweetsFromNetwork.toArray(new Tweet[0]));
                            Log.i(TAG, "Users from network: " + tweetsFromNetwork);

                        }
                    });

                    hideProgressBar();
                    swipeContainer.setRefreshing(false);
                    Log.d(TAG, "all tweets: " + tweets.toString());

//                    Integer lowestLocal = 0;
//                    for (int i = 0; i < tweets.size(); i++) {
//                        Log.i(TAG, "id is for the tweet " + i + ": " + tweets.get(i).id);
//                        if (tweets.get(i).id < lowestLocal) {
//                            lowestLocal = tweets.get(i).id;
//                        }
//                    }
//                    lowestId = tweets.get(tweets.size()-1).id;
//                    Log.i(TAG, "lowestID is  " +  lowestId);

                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    Log.d(TAG, "JSON exception: populateHomeTimeline", e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure populateHomeTimeline" + throwable + " response: "+ response + " statusCode: " + statusCode);
                showFromDatabase();

            }
        });
    }

    public void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();
        ComposeDialogFragment composeDialogFragment = ComposeDialogFragment.newInstance(this, userID);
        composeDialogFragment.show(fm, "fragment_compose");
        hideProgressBar();
    }

    public void getUserId() {
        client.getUserTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess populateHomeTimeline");
                JSONArray array = json.jsonArray;
                try {
                    userID = array.getJSONObject(0).getJSONObject("user").getString("profile_image_url_https");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure getUserId" + throwable + " response: "+ response + " statusCode: " + statusCode);
            }
        });
    }

    @Override
    public void onFinishEditDialog(String inputText) {
        refresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        Log.i(TAG, "createOptionsMenu");
        if (loaded) {
            hideProgressBar();
        } else {
            showProgressBar();
        }
        return super.onPrepareOptionsMenu(menu);
    }

    public void showProgressBar() {
        // Show progress item
        miActionProgressItem.setVisible(true);
        Log.i(TAG, "showprogressbar");
    }

    public void hideProgressBar() {
        // Hide progress item
        Log.i(TAG, "Hideprogressbar");
        miActionProgressItem.setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.compose) {
//            Intent intent = new Intent(this, ComposeActivity.class);
//            startActivityForResult(intent, REQUEST_CODE);
            showEditDialog();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));

            tweets.add(0, tweet);
            adapter.notifyItemInserted(0);
            rvTweets.smoothScrollToPosition(0);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /* Within the RecyclerView.Adapter class */

    // Clean all elements of the recycler
    public void clear() {
        tweets.clear();
        adapter.notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        adapter.notifyDataSetChanged();
    }
}