package com.example.project_try;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class player extends AppCompatActivity {
    Button btnplay,btnfwd,btnrew,btnnext,btnprev;
    TextView txtname,txtstart,txtstop;
    ImageView songBG;
    SeekBar seekMusic;

    ArrayList songList;
    ArrayList songNames;
    String sname;
    static MediaPlayer mediaPlayer;
    public static final String EXTRA_NAME = "song_name";
    Thread updateSeekBar;
    int position;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home)
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        getSupportActionBar().setTitle("Now Playing");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);

        btnplay = findViewById(R.id.playButton);
        btnfwd = findViewById(R.id.fastForward);
        btnrew = findViewById(R.id.fastRewind);
        btnnext = findViewById(R.id.skipNext);
        btnprev = findViewById(R.id.skipPrev);

        txtname = findViewById(R.id.songText);
        txtstart = findViewById(R.id.timeStamp);
        txtstop = findViewById(R.id.timerEnd);

        seekMusic = findViewById(R.id.seekBar);

        songBG = findViewById(R.id.songImage);

        if(mediaPlayer!=null)       //a song already running
        {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        songNames = (ArrayList) bundle.getParcelableArrayList("SongNames");
        songList = (ArrayList) bundle.getParcelableArrayList("Songs");
        position = bundle.getInt("pos",0);
        txtname.setSelected(true);

        playSong(position);

        btnplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying())
                {
                    btnplay.setBackgroundResource(R.drawable.ic_play);
                    mediaPlayer.pause();
                }
                else
                {
                    btnplay.setBackgroundResource(R.drawable.ic_baseline_pause_24);
                    mediaPlayer.start();
                }
            }
        });

        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position+1)%songList.size());
                playSong(position);
                btnplay.setBackgroundResource(R.drawable.ic_baseline_pause_24);
                startAnimation(songBG);
            }
        });

        btnprev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if(position==0)
                {
                    position = songList.size()-1;
                }
                else
                {
                    position = position-1;
                }
                playSong(position);
                btnplay.setBackgroundResource(R.drawable.ic_baseline_pause_24);
                startAnimation(songBG);
            }
        });

        btnfwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying())
                {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+10000);
                }
            }
        });

        btnrew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()&&mediaPlayer.getCurrentPosition()>10000)
                {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-10000);
                }
            }
        });

    }

    public void startAnimation(View view)
    {
        ObjectAnimator animator = ObjectAnimator.ofFloat(songBG,"rotation",0f,360f);
        animator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();
    }

    public String timeConvert(int duartion)     //convert milliseconds to min:seconds
    {
        String time = "";
        int min = (duartion/1000)/60;
        int sec = (duartion/1000)%60;

        time+=min+":";
        if(sec<10)
            time+="0";
        time+=sec;

        return time;
    }

    public void playSong(int pos)
    {
        mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(songList.get(pos).toString()));
        mediaPlayer.start();
        sname = songNames.get(pos).toString();
        //Toast.makeText(this, songNames.get(2).getClass().getSimpleName(), Toast.LENGTH_SHORT).show();
        txtname.setText(sname);

        updateSeekBar = new Thread()
        {
            @Override
            public void run() {
                int duration = mediaPlayer.getDuration();
                int currentPosition = 0;
                while(currentPosition<duration)
                {
                    try {
                        sleep(500);         //update the seekbar every 500 milliseconds
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekMusic.setProgress(currentPosition);
                    }
                    catch (InterruptedException  | IllegalStateException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        };

        seekMusic.setMax(mediaPlayer.getDuration());
        updateSeekBar.start();
        seekMusic.getProgressDrawable().setColorFilter(getResources().getColor(R.color.design_default_color_primary), PorterDuff.Mode.MULTIPLY);
        seekMusic.getThumb().setColorFilter(getResources().getColor(R.color.design_default_color_primary),PorterDuff.Mode.SRC_IN);

        seekMusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
        String endTime = timeConvert(mediaPlayer.getDuration());
        txtstop.setText(endTime);

        final Handler handler = new Handler();
        final int delay = 1000;

        handler.postDelayed(new Runnable() {        //update time every second on seekBar
            @Override
            public void run() {
                String currentTime = timeConvert(mediaPlayer.getCurrentPosition());
                txtstart.setText(currentTime);
                handler.postDelayed(this,delay);
            }
        },delay);

        //to autoplay next song on completion
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                btnnext.performClick();
            }
        });
    }
}