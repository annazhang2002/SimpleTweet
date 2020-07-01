package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
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
    ImageView ivImgMedia1;

    ImageView ivLike;
    ImageView ivRetweet;
    TextView tvLikeCount;
    TextView tvRetweetCount;
    ImageView ivReply;

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
        ivImgMedia1 = findViewById(R.id.ivMedia1);

        ivLike = findViewById(R.id.ivLike);
        ivRetweet = findViewById(R.id.ivRetweet);
        tvLikeCount = findViewById(R.id.tvLikeCount);
        tvRetweetCount = findViewById(R.id.tvRetweetCount);
        ivReply = findViewById(R.id.ivReply);

        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        tvBody.setText(tweet.body);
        tvScreenName.setText("@" + tweet.user.screenName);
        Glide.with(this).load(tweet.user.publicImageUrl).circleCrop().into(ivProfileImg);
        tvName.setText(tweet.user.name);
        tvTimeStamp.setText(tweet.createdAt);

        if (tweet.mediaUrl1 != null){
            Glide.with(context)
                    .load(tweet.mediaUrl1)
                    .override(600, 400).
                    transform(new RoundedCornersTransformation(30, 0)).
                    into(ivImgMedia1);
            ivImgMedia1.setVisibility(View.VISIBLE);
        } else {
            Glide.with(context).load(tweet.mediaUrl1).override(0, 0).into(ivImgMedia1);
            ivImgMedia1.setVisibility(View.INVISIBLE);
        }

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

        ivReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("TweetsDetailsActivity", "onClick ivReply");
                TweetDetailsActivity activity = (TweetDetailsActivity) (context);
                FragmentManager fm = activity.getSupportFragmentManager();
                ComposeDialogFragment alertDialog = ComposeDialogFragment.newInstanceReply(context, TimelineActivity.userID, tweet);
                alertDialog.show(fm, "fragment_alert");
            }
        });



        tvLikeCount.setText(tweet.likeCount + "");
        tvRetweetCount.setText(tweet.retweetCount + "");

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
                            tweet.likeCount -= 1;
                            tvLikeCount.setText(tweet.likeCount + "");
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
                            tweet.likeCount += 1;
                            tvLikeCount.setText(tweet.likeCount + "");
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
                            tweet.retweetCount -= 1;
                            tvRetweetCount.setText(tweet.retweetCount + "");
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
                            tweet.retweetCount += 1;
                            tvRetweetCount.setText(tweet.retweetCount + "");
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.i("TweetDetailsActivity", "failed to retweet", throwable);

                        }
                    });
                }
                Log.i("TweetsAdapter", "here");
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