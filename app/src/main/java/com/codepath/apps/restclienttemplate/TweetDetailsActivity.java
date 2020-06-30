package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

public class TweetDetailsActivity extends AppCompatActivity {

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
//        if (tweet.mediaUrl1 != null){
//            Glide.with(this).load(tweet.mediaUrl1).into(ivImgMedia1);
//            ivImgMedia1.setVisibility(View.VISIBLE);
//        } else {
//            ivImgMedia1.setVisibility(View.INVISIBLE);
//        }

    }
}