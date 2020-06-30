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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import java.util.List;


public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    Context context;
    List<Tweet> tweets;

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
    // define viewholder
    public class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{

        ImageView ivProfileImg;
        TextView tvBody;
        TextView tvScreenName;
        TextView tvName;
        TextView tvTimestamp;
        ImageView ivImgMedia1;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProfileImg = itemView.findViewById(R.id.ivProfileImg);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvName = itemView.findViewById(R.id.tvName);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            ivImgMedia1 = itemView.findViewById(R.id.ivImgMedia1);

            itemView.setOnClickListener(this);
        }

        public void bind(Tweet tweet) {
            tvBody.setText(tweet.body);
            tvScreenName.setText("@" + tweet.user.screenName);
            Glide.with(context).load(tweet.user.publicImageUrl).circleCrop().into(ivProfileImg);
            tvName.setText(tweet.user.name);
            tvTimestamp.setText(tweet.timeAgo);
            if (tweet.mediaUrl1 != null){
                Glide.with(context).load(tweet.mediaUrl1).into(ivImgMedia1);
                ivImgMedia1.setVisibility(View.VISIBLE);
            } else {
                ivImgMedia1.setVisibility(View.INVISIBLE);
            }
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
