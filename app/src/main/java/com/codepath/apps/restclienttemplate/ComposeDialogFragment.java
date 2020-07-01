package com.codepath.apps.restclienttemplate;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeDialogFragment extends DialogFragment {

    public static final String TAG = "ComposeDialogFragment";
    public static final int MAX_TWEET_LENGTH = 140;
    private static Context context1;

    EditText etCompose;
    Button btnTweet;
    TextView tvCharCount;
    ImageView ivClose;
    ImageView ivProfileImg;

    TwitterClient client;

    public ComposeDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static ComposeDialogFragment newInstance(Context context, String profileUrl) {
        ComposeDialogFragment frag = new ComposeDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", "Compose");
        frag.setArguments(args);
        context1 = context;
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose, container);
    }

    public interface EditNameDialogListener {
        void onFinishEditDialog(String inputText);
    }

    // Call this method to send the data back to the parent fragment
    public void sendBackResult(Tweet tweet) {
        // Notice the use of `getTargetFragment` which will be set when the dialog is displayed
        EditNameDialogListener listener = (EditNameDialogListener) getTargetFragment();
        listener.onFinishEditDialog("refresh");
        dismiss();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        etCompose = view.findViewById(R.id.etCompose);
        btnTweet = view.findViewById(R.id.btnTweet);
        tvCharCount = view.findViewById(R.id.tvCharCount);
        ivClose = view.findViewById(R.id.ivClose);
        ivProfileImg = view.findViewById(R.id.ivProfileImg);

        tvCharCount.setText("0/" + MAX_TWEET_LENGTH);
//        Glide.with(context1).load(tweet.user.publicImageUrl).circleCrop().into(ivProfileImg);


        client = TwitterApp.getRestClient(context1);

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String tweetContent = etCompose.getText().toString();
                if (tweetContent.isEmpty()) {
                    Toast.makeText(context1, "Sorry, your tweet cannot be empty", Toast.LENGTH_LONG).show();
                    return;
                }
                if (tweetContent.length() > MAX_TWEET_LENGTH) {
                    Toast.makeText(context1, "Sorry, your tweet is too long" , Toast.LENGTH_LONG).show();
                    return;

                }

                client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "onSuccess to publish tweet");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i(TAG, "Published tweet: " + tweet.body);
//                            Intent intent = new Intent();
//                            intent.putExtra("tweet", Parcels.wrap(tweet));
                            Toast.makeText(context1, "Tweeted!" , Toast.LENGTH_LONG).show();
                            sendBackResult(tweet);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "onFailure to publish tweet", throwable);
                    }
                });
            }
        });


        etCompose.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tvCharCount.setText(etCompose.getText().toString().length() + "/" + MAX_TWEET_LENGTH);
                if (etCompose.getText().toString().length() > MAX_TWEET_LENGTH) {
                    tvCharCount.setTextColor(getResources().getColor(R.color.RED));
                } else {
                    tvCharCount.setTextColor(getResources().getColor(R.color.BLACK));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                tvCharCount.setText(etCompose.getText().toString().length() + "/" + MAX_TWEET_LENGTH);
            }
        });

        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle("hellloooo");
        // Show soft keyboard automatically and request focus to field
        etCompose.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

}
