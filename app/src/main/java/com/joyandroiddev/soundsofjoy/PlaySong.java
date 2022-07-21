package com.joyandroiddev.soundsofjoy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gauravk.audiovisualizer.visualizer.BarVisualizer;

import java.io.File;
import java.util.ArrayList;

public class PlaySong extends AppCompatActivity {
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        updateSeek.interrupt();
    }

    TextView textView,currentTimer,totalTimer;
    ImageView previous,play,next,imageView6;
    ArrayList<File> songs;
    MediaPlayer mediaPlayer;
    String textContent;
    int position;
    SeekBar seekBar;
    Thread updateSeek;
    BarVisualizer visualizer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);
        textView = findViewById(R.id.textView);
        totalTimer = findViewById(R.id.totalTimer);
        currentTimer = findViewById(R.id.currentTimer);
        play = findViewById(R.id.play);
        imageView6 = findViewById(R.id.imageView6);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        seekBar = findViewById(R.id.seekBar);
        visualizer = findViewById(R.id.blast);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songs = (ArrayList) bundle.getParcelableArrayList("songList");
        textContent = intent.getStringExtra("currentSong");
        textView.setText(textContent);
        textView.setSelected(true);
        position = intent.getIntExtra("position",0);
        Uri uri = Uri.parse(songs.get(position).toString());
        mediaPlayer = MediaPlayer.create(this,uri);
        mediaPlayer.start();
        RotateAnimation rotateAnimation = new RotateAnimation(0,36000,RotateAnimation.RELATIVE_TO_SELF,.5f,
                RotateAnimation.RELATIVE_TO_SELF,.5f);
        rotateAnimation.setDuration(600000);
        rotateAnimation.setRepeatCount(RotateAnimation.INFINITE);
        imageView6.startAnimation(rotateAnimation);
        seekBar.setMax(mediaPlayer.getDuration());




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


        String endTime = createTime(mediaPlayer.getDuration());
        totalTimer.setText(endTime);

        final Handler handler = new Handler();
        final int delay = 1000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currentTime = createTime(mediaPlayer.getCurrentPosition());
                currentTimer.setText(currentTime);
                handler.postDelayed(this,delay);

            }
        },delay);


       updateSeek = new Thread(){
           @Override
           public void run() {
               int currentPosition = 0;
               try{
                   while (currentPosition<mediaPlayer.getDuration()){
                       currentPosition = mediaPlayer.getCurrentPosition();
                       seekBar.setProgress(currentPosition);

                   }sleep(800);
               }

               catch(Exception e){
                   e.printStackTrace();
               }
           }
    };
    updateSeek.start();


        int audiosessionId = mediaPlayer.getAudioSessionId();
        if(audiosessionId != -1)
        {
            visualizer.setAudioSessionId(audiosessionId);
        }


    play.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
             //Animation rotate = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate);
             //imageView6.startAnimation(rotate);


            if(mediaPlayer.isPlaying()){
                play.setImageResource(R.drawable.play);
                mediaPlayer.pause();
                imageView6.clearAnimation();

            }
            else{
                play.setImageResource(R.drawable.pause);
                mediaPlayer.start();
                RotateAnimation rotateAnimation = new RotateAnimation(0,36000,RotateAnimation.RELATIVE_TO_SELF,.5f,
                        RotateAnimation.RELATIVE_TO_SELF,.5f);
                rotateAnimation.setDuration(600000);
                rotateAnimation.setRepeatCount(RotateAnimation.INFINITE);
                imageView6.startAnimation(rotateAnimation);

            }


        }
    });


    previous.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mediaPlayer.stop();
            mediaPlayer.release();
            if(position!=0){
                position = position - 1;
            }
            else {
                position = songs.size() -1;
            }
            Uri uri = Uri.parse(songs.get(position).toString());
            mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
            mediaPlayer.start();
            play.setImageResource(R.drawable.pause);
            seekBar.setMax(mediaPlayer.getDuration());
            textContent = songs.get(position).getName().toString();
            textView.setText(textContent);
            int audio_sessionId = mediaPlayer.getAudioSessionId();
            if(audio_sessionId != -1)
            {
                visualizer.setAudioSessionId(audio_sessionId);
            }
        }
    });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if(position!=songs.size()-1){
                    position = position + 1;
                }
                else {
                    position = 0;
                }
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause);
                seekBar.setMax(mediaPlayer.getDuration());
                textContent = songs.get(position).getName().toString();
                textView.setText(textContent);
                int audio_sessionId = mediaPlayer.getAudioSessionId();
                if(audio_sessionId != -1)
                {
                    visualizer.setAudioSessionId(audio_sessionId);
                }
            }
        });


    }
    public String createTime(int duration)
    {
        String time = "";
        int min =duration/1000/60;
        int sec = duration/1000%60;

        time += min+ ":";

        if(sec<10) {time += "0";}
        time +=sec;

        return time;
    }

}