package com.example.secucapture.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.secucapture.R;
import com.example.secucapture.Utils.DbHelper;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class DriversLessonsVideosActivity extends AppCompatActivity {

    Activity act;
    Context ctx;
    DbHelper doa;

    public Button btnProceedToQuiz, fetch_video_btn;

    String lessonLink ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drivers_lessons_videos);

        act = this;
        ctx = this;
        doa = new DbHelper(ctx, act);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
//        lessonLink = bundle.getString("lesson_link");

//        Log.e("lessonLink", "onCreate: "+lessonLink);

//        lessonLink = "QFuvieqha2oVxV9D";
        lessonLink = "u8_wZM71iT4";

        final YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_player);
        getLifecycle().addObserver(youTubePlayerView);
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                super.onReady(youTubePlayer);

                youTubePlayer.loadVideo(lessonLink, 0);
            }

            @Override
            public void onStateChange(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerState state) {
                super.onStateChange(youTubePlayer, state);
            }
        });

        fetch_video_btn = (Button) findViewById(R.id.fetch_video_btn);
        fetch_video_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_player);
                getLifecycle().addObserver(youTubePlayerView);
                youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                        super.onReady(youTubePlayer);

                        youTubePlayer.loadVideo(lessonLink, 0);
                    }

                    @Override
                    public void onStateChange(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerState state) {
                        super.onStateChange(youTubePlayer, state);
                    }
                });

//                _loadVideo(lessonLink);

            }
        });


        btnProceedToQuiz = (Button) findViewById(R.id.btnProceedToQuiz);
        btnProceedToQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), DriversLessonsQuizesActivity.class));

            }
        });

    }

    public void _loadVideo(String videoID) {

        Log.e("youtubeVid", "_loadVideo: "+"YOU CLICKED ME" );
        //youtube player
        final YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_player);
        getLifecycle().addObserver(youTubePlayerView);
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                super.onReady(youTubePlayer);

                youTubePlayer.loadVideo(videoID, 0);
            }

            @Override
            public void onStateChange(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerState state) {
                super.onStateChange(youTubePlayer, state);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(getApplicationContext(), DriverAssessmentActivity.class);
        DriversLessonsVideosActivity.this.finish();
        startActivity(i);
    }

}