package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;

import java.util.List;

public class UserAdapter  extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    List<User> users;
    Context context;

    public UserAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);

        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder  extends RecyclerView.ViewHolder {

        ImageView ivProfileImg;
        TextView tvScreenName;
        TextView tvName;
        ImageView ivVerified;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProfileImg = itemView.findViewById(R.id.ivProfile);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvName = itemView.findViewById(R.id.tvName);
            ivVerified = itemView.findViewById(R.id.ivVerified);
        }

        public void bind(User user) {
            tvScreenName.setText("@" + user.screenName);
            Glide.with(context).load(user.publicImageUrl).circleCrop().into(ivProfileImg);
            tvName.setText(user.name);
            if (user.verified) {
                ivVerified.setVisibility(View.VISIBLE);
            } else {
                ivVerified.setVisibility(View.INVISIBLE);
            }
        }
    }
}
