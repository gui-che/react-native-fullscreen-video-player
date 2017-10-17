package com.favoritevideorn;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
//import android.view.View;
import android.content.Intent;
import android.view.ViewGroup;
//import android.widget.FrameLayout;
import android.widget.ImageView;

//import android.content.Context;
//import android.media.AudioManager;
import android.os.Handler;
//import android.os.Message;
import android.text.TextUtils;
//import android.util.Log;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
//import com.google.android.exoplayer2.ExoPlaybackException;
//import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
//import com.google.android.exoplayer2.Format;
//import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
//import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
//import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
//import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
//import com.google.android.exoplayer2.metadata.Metadata;
//import com.google.android.exoplayer2.metadata.MetadataRenderer;
//import com.google.android.exoplayer2.source.BehindLiveWindowException;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
//import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
//import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
//import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
//import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

//import static java.security.AccessController.getContext;

@SuppressLint("Registered")
public class ExoActivity extends AppCompatActivity {

    private final String STATE_RESUME_WINDOW = "resumeWindow";
    private final String STATE_RESUME_POSITION = "resumePosition";
    private final String STATE_PLAYER_FULLSCREEN = "playerFullscreen";

    private SimpleExoPlayerView mExoPlayerView;
    private MediaSource mVideoSource;
    private boolean mExoPlayerFullscreen = true;
    private ImageView mFullScreenIcon;
    private Dialog mFullScreenDialog;
	private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();

    private int mResumeWindow;
    private long mResumePosition;
	private String videoPath;

	private DataSource.Factory mediaDataSourceFactory;
	private Handler mainHandler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.exo_fullscreen);

        if (savedInstanceState != null) {
            mResumeWindow = savedInstanceState.getInt(STATE_RESUME_WINDOW);
            mResumePosition = savedInstanceState.getLong(STATE_RESUME_POSITION);
            mExoPlayerFullscreen = savedInstanceState.getBoolean(STATE_PLAYER_FULLSCREEN);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putInt(STATE_RESUME_WINDOW, mResumeWindow);
        outState.putLong(STATE_RESUME_POSITION, mResumePosition);
        outState.putBoolean(STATE_PLAYER_FULLSCREEN, mExoPlayerFullscreen);

        super.onSaveInstanceState(outState);
    }


    private void initFullscreenDialog() {

        mFullScreenDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
            public void onBackPressed() {
                // if (mExoPlayerFullscreen)
                //     closeFullscreenDialog();
                super.onBackPressed();
				finish();
            }
        };
    }


//    private void openFullscreenDialog() {
//
//        ((ViewGroup) mExoPlayerView.getParent()).removeView(mExoPlayerView);
//        mFullScreenDialog.addContentView(mExoPlayerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(ExoActivity.this, R.drawable.ic_fullscreen_skrink));
//        mExoPlayerFullscreen = true;
//        mFullScreenDialog.show();
//    }


//    private void closeFullscreenDialog() {
//
//        ((ViewGroup) mExoPlayerView.getParent()).removeView(mExoPlayerView);
//        ((FrameLayout) findViewById(R.id.main_media_frame)).addView(mExoPlayerView);
//        mExoPlayerFullscreen = false;
//        mFullScreenDialog.dismiss();
//        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(ExoActivity.this, R.drawable.ic_fullscreen_expand));
//    }


     private void initFullscreenButton() {

        PlaybackControlView controlView;
        controlView = (PlaybackControlView) mExoPlayerView.findViewById(R.id.exo_controller);
        mFullScreenIcon = (ImageView) controlView.findViewById(R.id.exo_fullscreen_icon);
        // FrameLayout mFullScreenButton = (FrameLayout) controlView.findViewById(R.id.exo_fullscreen_button);
        // mFullScreenButton.setOnClickListener(new View.OnClickListener() {
        //     @Override
        //     public void onClick(View v) {
        //         if (!mExoPlayerFullscreen)
        //             openFullscreenDialog();
        //         else
        //             closeFullscreenDialog();
        //     }
        // });
     }


    private void initExoPlayer() {

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        LoadControl loadControl = new DefaultLoadControl();
        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(this), trackSelector, loadControl);
        mExoPlayerView.setPlayer(player);

        boolean haveResumePosition = mResumeWindow != C.INDEX_UNSET;

        if (haveResumePosition) {
            mExoPlayerView.getPlayer().seekTo(mResumeWindow, mResumePosition);
        }

        mExoPlayerView.getPlayer().prepare(mVideoSource);
        mExoPlayerView.getPlayer().setPlayWhenReady(true);
    }


    @Override
    protected void onResume() {

        super.onResume();

        if (mExoPlayerView == null) {

        	mainHandler = new Handler();

            mExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.exoplayer);
            initFullscreenDialog();
            initFullscreenButton();

            String userAgent = Util.getUserAgent(ExoActivity.this, getApplicationContext().getApplicationInfo().packageName);
            DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(userAgent, null, DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS, DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS, true);
            mediaDataSourceFactory = new DefaultDataSourceFactory(ExoActivity.this, null, httpDataSourceFactory);

			Intent i = getIntent();
			if(i != null){
				videoPath = i.getStringExtra("VIDEO_URL");
				Uri daUri = Uri.parse(videoPath);
				mVideoSource = buildMediaSource(daUri,"");
			}

        }

        initExoPlayer();

        if (mExoPlayerFullscreen) {
            ((ViewGroup) mExoPlayerView.getParent()).removeView(mExoPlayerView);
            mFullScreenDialog.addContentView(mExoPlayerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(ExoActivity.this, R.drawable.ic_fullscreen_skrink));
            mFullScreenDialog.show();
        }
    }


	private MediaSource buildMediaSource(Uri uri, String overrideExtension) {
	        int type = Util.inferContentType(!TextUtils.isEmpty(overrideExtension) ? "." + overrideExtension
	                : uri.getLastPathSegment());
	        switch (type) {
	            case C.TYPE_SS:
	                return new SsMediaSource(uri, mediaDataSourceFactory,
	                        new DefaultSsChunkSource.Factory(mediaDataSourceFactory), mainHandler, null);
	            case C.TYPE_DASH:
	                return new DashMediaSource(uri, mediaDataSourceFactory,
	                        new DefaultDashChunkSource.Factory(mediaDataSourceFactory), mainHandler, null);
	            case C.TYPE_HLS:
	                return new HlsMediaSource(uri, mediaDataSourceFactory, mainHandler, null);
	            case C.TYPE_OTHER:
	                return new ExtractorMediaSource(uri, mediaDataSourceFactory, new DefaultExtractorsFactory(),
	                        mainHandler, null);
	            default: {
	                throw new IllegalStateException("Unsupported type: " + type);
	            }
	        }
	    }

    @Override
    protected void onPause() {

        super.onPause();

        mResumeWindow = mExoPlayerView.getPlayer().getCurrentWindowIndex();
        mResumePosition = Math.max(0, mExoPlayerView.getPlayer().getContentPosition());

        if (mExoPlayerView != null && mExoPlayerView.getPlayer() != null) {
            mExoPlayerView.getPlayer().release();
        }

        if (mFullScreenDialog != null)
            mFullScreenDialog.dismiss();
    }

}
