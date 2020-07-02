package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

import static com.codepath.apps.restclienttemplate.R.drawable.ic_vector_heart;
import static com.codepath.apps.restclienttemplate.R.drawable.ic_vector_heart_stroke;
import static com.codepath.apps.restclienttemplate.R.drawable.ic_vector_retweet_stroke;
import static com.codepath.apps.restclienttemplate.R.drawable.ic_vector_retweet_stroke_green;


public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> implements ComposeDialogFragment.EditNameDialogListener {

    Context context;
    List<Tweet> tweets;
    TwitterClient client;

    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tweet tweet = tweets.get(position);

        holder.bind(tweet);


    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    @Override
    public void onFinishEditDialog(String inputText) {

    }

//    private void showEditDialog() {
//        FragmentManager fm = getSupportFragmentManager();
//        ComposeDialogFragment composeDialogFragment = ComposeDialogFragment.newInstance(this, userID);
//        composeDialogFragment.show(fm, "fragment_compose");
//    }

    // define viewholder
    public class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{

        ImageView ivProfileImg;
        TextView tvBody;
        TextView tvScreenName;
        TextView tvName;
        TextView tvTimestamp;
        ImageView ivImgMedia1;
        ImageView ivLike;
        ImageView ivRetweet;
        TextView tvLikeCount;
        TextView tvRetweetCount;
        ImageView ivReply;
        ImageView ivVerified;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProfileImg = itemView.findViewById(R.id.ivProfileImg);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvName = itemView.findViewById(R.id.tvName);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            ivImgMedia1 = itemView.findViewById(R.id.ivImgMedia1);
            ivLike = itemView.findViewById(R.id.ivLike);
            ivRetweet = itemView.findViewById(R.id.ivRetweet);
            tvLikeCount =itemView.findViewById(R.id.tvLikeCount);
            tvRetweetCount =itemView.findViewById(R.id.tvRetweetCount);
            ivReply = itemView.findViewById(R.id.ivReply);
            ivVerified = itemView.findViewById(R.id.ivVerified);

            itemView.setOnClickListener(this);

            client = new TwitterClient(context);
        }

        public void bind(final Tweet tweet) {
            tvBody.setText(tweet.body);
            tvScreenName.setText("@" + tweet.user.screenName);
            Glide.with(context).load(tweet.user.publicImageUrl).circleCrop().into(ivProfileImg);
            tvName.setText(tweet.user.name);
            tvTimestamp.setText(tweet.timeAgo);
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

            if (tweet.user.verified) {
                ivVerified.setVisibility(View.VISIBLE);
            } else {
                ivVerified.setVisibility(View.INVISIBLE);
            }

            tvLikeCount.setText(tweet.likeCount + "");
            tvRetweetCount.setText(tweet.retweetCount + "");

            ivProfileImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, UserActivity.class);
                    intent.putExtra("user", Parcels.wrap(tweet.user));
                    Log.i("TweetDetailsActivity", "userbio: " + tweet.user.bio);
                    context.startActivity(intent);
                }
            });

            ivReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("TweetsAdapter", "onClick ivReply");
                    TimelineActivity activity = (TimelineActivity) (context);
                    FragmentManager fm = activity.getSupportFragmentManager();
                    ComposeDialogFragment alertDialog = ComposeDialogFragment.newInstanceReply(context, TimelineActivity.userID, tweet);
                    alertDialog.show(fm, "fragment_alert");
                }
            });

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
        }

        @Override
        public void onClick(View view) {
            Log.i("TweetsAdapter", "onClick");

            int position = getAdapterPosition();

            // making sure the position is valid
            if (position != RecyclerView.NO_POSITION) {
                Tweet tweet = tweets.get(position);

                // creating a new intent to go to the new activity
                Intent intent = new Intent(context, TweetDetailsActivity.class);

                // pass information to the intent with the parceler
                intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));

                context.startActivity(intent);
            }
        }

    }
}
