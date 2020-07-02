package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.databinding.ActivityTweetDetailsBinding;
import com.codepath.apps.restclienttemplate.databinding.ActivityUserBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;

import org.parceler.Parcels;
import org.w3c.dom.Text;

public class UserActivity extends AppCompatActivity {

    ImageView ivProfile;
    TextView tvName;
    TextView tvScreenName;
    TextView tvBio;
    TextView tvFollowerCount;
    TextView tvFollowingCount;
    TextView tvLocation;
    TextView tvUrl;
    TextView tvCreatedAt;
    ImageView ivBanner;
    ImageView ivVerified;

    User user;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUserBinding binding = ActivityUserBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        context = this;

        ivProfile = binding.ivProfile;
        tvName = binding.tvName;
        tvScreenName = binding.tvScreenName;
        tvBio = binding.tvBio;
        tvFollowerCount = binding.tvFollowerCount;
        tvFollowingCount = binding.tvFollowingCount;
        tvLocation = binding.tvLocation;
        tvCreatedAt = binding.tvCreatedAt;
        tvUrl = binding.tvUrl;
        ivBanner = binding.ivBanner;
        ivVerified = binding.ivVerified;

        user = (User) Parcels.unwrap(getIntent().getParcelableExtra("user"));
        Log.i("UserActivity", "user: " + user);

        tvBio.setText(user.bio);
        tvScreenName.setText("@" + user.screenName);
        Glide.with(this).load(user.publicImageUrl).circleCrop().into(ivProfile);
        tvName.setText(user.name);
        tvFollowerCount.setText(user.followerCount + "");
        tvFollowingCount.setText(user.followingCount + "");

        tvFollowerCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FollowersActivity.class);
                intent.putExtra("user_id", Parcels.wrap(user.id));
                intent.putExtra("user_type", "followers");
                context.startActivity(intent);
            }
        });


        if (user.location != null && !user.location.isEmpty()){
            tvLocation.setText(user.location);
        } else {
            tvLocation.setText("N/A");
        }
        tvCreatedAt.setText(user.createdAt);
        Log.i("User", "userurl: "+ user.url);
        if (user.url != null && !user.url.isEmpty() && !user.url.equals("null")){
            tvUrl.setText(user.url);
        } else {
            tvUrl.setText("N/A");
        }
        if (!user.verified) {
            ivVerified.setVisibility(View.INVISIBLE);
            ivBanner.setBackgroundColor(getResources().getColor(R.color.gray));
        } else {
            Glide.with(this).load(user.bannerImg).into(ivBanner);
            ivBanner.setBackgroundColor(Color.TRANSPARENT);
        }
    }
}