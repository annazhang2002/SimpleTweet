package com.codepath.apps.restclienttemplate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.restclienttemplate.models.Tweet;
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
    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetsAdapter adapter;
    SwipeRefreshLayout swipeContainer;
    MenuItem miActionProgressItem;
    Integer lowestId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        client = TwitterApp.getRestClient(this);

        rvTweets = findViewById(R.id.rvTweets);
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets);

        lowestId = 0;

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        rvTweets.setLayoutManager(layoutManager);
        rvTweets.setAdapter(adapter);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
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
        client.updateHomeTimeline( max_id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess updateHomeTimeline");
                JSONArray array = json.jsonArray;
                try {
                    tweets.addAll(Tweet.fromJsonArray(array));
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
                    Log.d(TAG, "JSON exception: updateHomeTimeline", e);

                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure updateHomeTimeline" + throwable + " response: "+ response + " statusCode: " + statusCode);

            }
        });
    }


    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess populateHomeTimeline");
                JSONArray array = json.jsonArray;
                try {
                    tweets.addAll(Tweet.fromJsonArray(array));
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

            }
        });
    }

    private void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();
        ComposeDialogFragment composeDialogFragment = ComposeDialogFragment.newInstance(this, "");
        composeDialogFragment.show(fm, "fragment_compose");
    }

    @Override
    public void onFinishEditDialog(String inputText) {
        refresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        showProgressBar();
        return super.onPrepareOptionsMenu(menu);
    }

    public void showProgressBar() {
        // Show progress item
        miActionProgressItem.setVisible(true);
    }

    public void hideProgressBar() {
        // Hide progress item
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