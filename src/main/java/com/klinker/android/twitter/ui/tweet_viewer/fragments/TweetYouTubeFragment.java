package com.klinker.android.twitter.ui.tweet_viewer.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerView;
import com.klinker.android.twitter.R;
import com.klinker.android.twitter.settings.AppSettings;
import com.klinker.android.twitter.manipulations.widgets.HoloTextView;


public class TweetYouTubeFragment extends YouTubePlayerFragment implements
        YouTubePlayer.OnInitializedListener {

    private AppSettings settings;
    private Context context;
    public static View layout;
    private String url;

    private static YouTubePlayerView player;
    private static HoloTextView error;
    private static YouTubePlayer realPlayer;
    private static YouTubePlayer.OnInitializedListener listener;

    private static boolean videoLoaded = false;

    public TweetYouTubeFragment(AppSettings settings, String url) {
        this.settings = settings;
        this.url = url;
    }

    public TweetYouTubeFragment() {
        this.settings = null;
        this.url = "";
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (realPlayer != null) {
            realPlayer.release();
        }

        realPlayer = null;

        TweetYouTubeFragment.videoLoaded = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        layout = inflater.inflate(R.layout.youtube_fragment, null, false);
        player = (YouTubePlayerView) layout.findViewById(R.id.youtube_view);
        error = (HoloTextView) layout.findViewById(R.id.error);

        player.initialize(AppSettings.YOUTUBE_API_KEY, this);

        listener = this;

        return layout;
    }

    private static String video = "";
    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {

        try {
            if (url.contains("youtube")) { // normal youtube link
                // first get the youtube video code
                int start = url.indexOf("v=") + 2;
                int end;
                if (url.substring(start).contains("&")) {
                    end = url.indexOf("&");
                    video = url.substring(start, end);
                } else if (url.substring(start).contains("?")) {
                    end = url.indexOf("?");
                    video = url.substring(start, end);
                } else {
                    video = url.substring(start);
                }
            } else { // shortened youtube link
                // first get the youtube video code
                int start = url.indexOf(".be/") + 4;
                int end;
                if (url.substring(start).contains("&")) {
                    end = url.indexOf("&");
                    video = url.substring(start, end);
                } else if (url.substring(start).contains("?")) {
                    end = url.indexOf("?");
                    video = url.substring(start, end);
                } else {
                    video = url.substring(start);
                }
            }
        } catch (Exception e) {
            video = "";
        }

        //youTubePlayer.loadVideo(video, 0);
        youTubePlayer.setShowFullscreenButton(false);

        realPlayer = youTubePlayer;
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        player.setVisibility(View.GONE);
        error.setVisibility(View.VISIBLE);

        realPlayer = null;
    }

    public static void pause() {
        try {
            player.setVisibility(View.GONE);
            layout.setVisibility(View.GONE);
        } catch (Exception e) {

        }

        if (realPlayer != null) {
            realPlayer.pause();
        }
    }

    public static void resume() {
        try {
            player.setVisibility(View.VISIBLE);
            layout.setVisibility(View.VISIBLE);
        } catch (Exception e) {

        }

        if (realPlayer != null) {
            if (videoLoaded) {
                realPlayer.play();
            } else {
                realPlayer.loadVideo(video);
                videoLoaded = true;
            }
        }
    }
}
