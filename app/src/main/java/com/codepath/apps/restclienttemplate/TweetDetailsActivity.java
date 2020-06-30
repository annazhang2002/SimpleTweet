package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import okhttp3.Headers;

import static com.codepath.apps.restclienttemplate.R.drawable.*;

public class TweetDetailsActivity extends AppCompatActivity {

    TwitterClient client;
    Context context;

    ImageView ivProfileImg;
    TextView tvName;
    TextView tvScreenName;
    TextView tvBody;
    TextView tvTimeStamp;

    ImageView ivLike;
    ImageView ivRetweet;

    Tweet tweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);

        client = TwitterApp.getRestClient(this);
        context = this;

        ivProfileImg = findViewById(R.id.ivProfileImg);
        tvName = findViewById(R.id.tvName);
        tvScreenName = findViewById(R.id.tvScreenName);
        tvBody = findViewById(R.id.tvBody);
        tvTimeStamp = findViewById(R.id.tvTimeStamp);

        ivLike = findViewById(R.id.ivLike);
        ivRetweet = findViewById(R.id.ivRetweet);

        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        tvBody.setText(tweet.body);
        tvScreenName.setText("@" + tweet.user.screenName);
        Glide.with(this).load(tweet.user.publicImageUrl).circleCrop().into(ivProfileImg);
        tvName.setText(tweet.user.name);
        tvTimeStamp.setText(tweet.createdAt);

        if (tweet.liked) {
            Glide.with(context).load(ic_vector_heart).into(ivLike);
        } else {
            Glide.with(context).load(ic_vector_heart_stroke).into(ivLike);
        }

        if (tweet.retweeted) {
            Glide.with(context).load(ic_vector_retweet_stroke_green).into(ivRetweet);
        } else {
            Glide.with(context).load(ic_vector_retweet_stroke).into(ivRetweet);
        }

        ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tweet.liked) {
                    client.unlikeTweet(tweet.id, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i("TweetDetailsActivity", "unliked");
                            Glide.with(context).load(ic_vector_heart_stroke).into(ivLike);
                            tweet.liked = false;

                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.i("TweetDetailsActivity", "failed to unlike", throwable);

                        }
                    });
                } else {
                    client.likeTweet(tweet.id, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i("TweetDetailsActivity", "liked!!");
                            Glide.with(context).load(ic_vector_heart).into(ivLike);
                            tweet.liked = true;
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.i("TweetDetailsActivity", "failed to like", throwable);

                        }
                    });
                }

            }
        });

        ivRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tweet.retweeted) {
                    client.unretweet(tweet.id, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i("TweetDetailsActivity", "unretweeted");
                            Glide.with(context).load(ic_vector_retweet_stroke).into(ivRetweet);
                            tweet.retweeted = false;

                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.i("TweetDetailsActivity", "failed to unretweet", throwable);

                        }
                    });
                } else {
                    client.retweet(tweet.id, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i("TweetDetailsActivity", "retweeted!!");
                            Glide.with(context).load(ic_vector_retweet_stroke_green).into(ivRetweet);
                            tweet.retweeted = true;
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.i("TweetDetailsActivity", "failed to retweet", throwable);

                        }
                    });
                }

            }
        });
//        if (tweet.mediaUrl1 != null){
//            Glide.with(this).load(tweet.mediaUrl1).into(ivImgMedia1);
//            ivImgMedia1.setVisibility(View.VISIBLE);
//        } else {
//            ivImgMedia1.setVisibility(View.INVISIBLE);
//        }

    }
}