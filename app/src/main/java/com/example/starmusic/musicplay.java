package com.example.starmusic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class musicplay extends AppCompatActivity {
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        updateSeek.interrupt();
    }
SeekBar seekBar;
    ImageView pre,next,play;
TextView textView,ct,tt;
ArrayList<File> songs;
int position;
MediaPlayer mediaPlayer;
Thread updateSeek;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musicplay);
        pre=findViewById(R.id.previous);
        play=findViewById(R.id.play);
        next=findViewById(R.id.next);
        textView=findViewById(R.id.textView);
        seekBar=findViewById(R.id.seekBar);
        ct=findViewById(R.id.currenttime);
        tt=findViewById(R.id.totaltime);
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        songs=(ArrayList)bundle.getParcelableArrayList("songs");
        String currents=intent.getStringExtra("currents");
        textView.setText(currents);
        textView.setSelected(true);
        position=intent.getIntExtra("position",0);
        Uri uri=Uri.parse(songs.get(position).toString());
        mediaPlayer=MediaPlayer.create(this,uri);
        mediaPlayer.start();
        tt.setText(formatTime(mediaPlayer.getDuration()));
        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setProgress(0);
        startSeekBarThread();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                 mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
        startSeekBarThread();
        mediaPlayer.setOnCompletionListener(mp -> {
            mediaPlayer.stop();
            mediaPlayer.release();

            if (position == songs.size() - 1) {
                position = 0; // Loop to the first song
            } else {
                position++;
            }

            Uri ur = Uri.parse(songs.get(position).toString());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), ur);
            mediaPlayer.setOnCompletionListener(this::onCompletion);
            play.setImageResource(R.drawable.pause);
            seekBar.setMax(mediaPlayer.getDuration());
            textView.setText(songs.get(position).getName().replace(".m4a", ""));
            mediaPlayer.start();
            tt.setText(formatTime(mediaPlayer.getDuration()));
            seekBar.setProgress(0);
            // Restart the SeekBar thread for the new song
            startSeekBarThread();
        });

        // paly and pause

    play.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mediaPlayer.isPlaying()){
                mediaPlayer.pause();
                play.setImageResource(R.drawable.play);
            }
            else {
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause);
                startSeekBarThread();
                mediaPlayer.setOnCompletionListener(mp -> onCompletion(mp));
            }
        }
    });

    // on click previous button

    pre.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mediaPlayer.stop();
            mediaPlayer.release();
            if(position==0) position=songs.size()-1;
            else position=position-1;
            Uri uri=Uri.parse(songs.get(position).toString());
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            mediaPlayer.setOnCompletionListener(mp -> onCompletion(mp));
            play.setImageResource(R.drawable.pause);
            seekBar.setMax(mediaPlayer.getDuration());
            mediaPlayer.start();
            tt.setText(formatTime(mediaPlayer.getDuration()));
            textView.setText(songs.get(position).getName().replace(".m4a",""));
            seekBar.setProgress(0);
            startSeekBarThread();
        }


    });

    // on click next button

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if(position==songs.size()-1) position=0;
                else position=position+1;
                Uri uri=Uri.parse(songs.get(position).toString());
                mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
                mediaPlayer.setOnCompletionListener(mp -> onCompletion(mp));
                play.setImageResource(R.drawable.pause);
                seekBar.setMax(mediaPlayer.getDuration());
                mediaPlayer.start();
                tt.setText(formatTime(mediaPlayer.getDuration()));
                textView.setText(songs.get(position).getName().replace(".m4a",""));
              seekBar.setProgress(0);
              startSeekBarThread();

            }


        });

    }

    //  seekbar manage
    private void startSeekBarThread() {

        updateSeek = new Thread() {
            @Override
            public void run() {
                try {
                    while (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        int currentPosition = mediaPlayer.getCurrentPosition();
                        ct.setText(formatTime(currentPosition));
                        runOnUiThread(() -> seekBar.setProgress(currentPosition));
                        sleep(1000);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        updateSeek.start();

    }

    // if song becomes end then automatic jump next song
    public void onCompletion(MediaPlayer mp) {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;

        // Update position for the next song
        if (position == songs.size() - 1) {
            position = 0; // Loop to the first song
        } else {
            position++;
        }

        // Load the next song
        Uri ur = Uri.parse(songs.get(position).toString());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), ur);

        // Re-attach the listener
        mediaPlayer.setOnCompletionListener(this::onCompletion);

        // Update UI
        play.setImageResource(R.drawable.pause);
        seekBar.setMax(mediaPlayer.getDuration());
        textView.setText(songs.get(position).getName().replace(".m4a", ""));
        seekBar.setProgress(0);

        // Start playback
        mediaPlayer.start();

        // Restart the SeekBar thread
        startSeekBarThread();
    }

    // for showing current duration and total duration of song
    private String formatTime(int milliseconds) {
        int hour=(milliseconds/1000)/3600;
        int minutes = (milliseconds / 1000) / 60 -hour*60;
        int seconds = (milliseconds / 1000) % 60;
        return String.format("%02d:%02d:%02d",hour, minutes, seconds);
    }
}
